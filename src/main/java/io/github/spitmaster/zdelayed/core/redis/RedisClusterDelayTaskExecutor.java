package io.github.spitmaster.zdelayed.core.redis;

import com.alibaba.fastjson.JSON;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * taskScope = REDIS_CLUSTER 的延时任务 的执行器
 * 使用redisson的延时队列实现
 *
 * @author zhouyijin
 */
public class RedisClusterDelayTaskExecutor implements BeanFactoryAware, InitializingBean, Runnable, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClusterDelayTaskExecutor.class);

    /**
     * 1个线程, 用于监听延时队列, 并调度拿到的所有任务
     */
    private static final Executor QUEUE_LISTENER = Executors.newFixedThreadPool(1);

    private final RedissonClient redissonClient;

    /**
     * 业务代码在这个执行器中执行
     */
    private final Executor zdelayedExecutor;

    /**
     * Spring环境
     */
    private BeanFactory beanFactory;
    private boolean isShutdown = false;

    public RedisClusterDelayTaskExecutor(RedissonClient redissonClient, Executor zdelayedExecutor) {
        this.redissonClient = redissonClient;
        this.zdelayedExecutor = zdelayedExecutor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        QUEUE_LISTENER.execute(this);
    }

    @Override
    public void run() {
        RBlockingQueue<String> delayedTaskRBlockingQueue = redissonClient.getBlockingQueue(
                RedisClusterDelayTaskScheduler.ZDELAYED_QUEUE_NAME,
                RedisClusterDelayTaskScheduler.DELAY_TASK_CODEC
        );
        while (true) {
            if (isShutdown) {
                return;
            }
            DelayedTask delayedTask = null;
            try {
                String delayedTaskStr = delayedTaskRBlockingQueue.take();
                delayedTask = JSON.parseObject(delayedTaskStr, DelayedTask.class);
            } catch (InterruptedException e) {
                LOGGER.info("RedisClusterDelayTaskExecutor get delayed task failed", e);
                Thread.currentThread().interrupt();
            }
            if (delayedTask != null) {
                try {
                    executeDelayedTask(delayedTask);
                } catch (Exception e) {
                    LOGGER.info("RedisClusterDelayTaskExecutor submit task failed; delayedTask={}", delayedTask, e);
                }
            }
        }
    }

    private void executeDelayedTask(DelayedTask delayedTask) {
        zdelayedExecutor.execute(() -> {
            try {
                //1. 标记该任务是来资源延时任务触发的, 这样Scheduler不会再将任务放入延时队列中, 而是直接执行
                DelayedTaskChecker.markFromDelayedTask();
                //2. 找到method和spring环境下的bean
                DelayedTaskInvoker delayedTaskInvoker = DelayedTaskInvoker.from(delayedTask, beanFactory);
                //3. 通过反射执行方法调用
                delayedTaskInvoker.invoke();
            } catch (Exception e) {
                LOGGER.info("RedisClusterDelayTaskExecutor execute task failed; delayedTask={}", delayedTask, e);
            } finally {
                //4. 执行结束, 清除标识, 以便下一次调用能识别出是不是来资源延时任务
                DelayedTaskChecker.clearMark();
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        isShutdown = true;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}