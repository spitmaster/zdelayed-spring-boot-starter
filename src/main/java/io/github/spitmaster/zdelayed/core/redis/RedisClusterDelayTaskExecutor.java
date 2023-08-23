package io.github.spitmaster.zdelayed.core.redis;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        RBlockingQueue<DelayedTask> delayedTaskRBlockingQueue = redissonClient.getBlockingQueue(
                RedisClusterDelayTaskScheduler.ZDELAYED_QUEUE_NAME,
                RedisClusterDelayTaskScheduler.DELAY_TASK_CODEC
        );
        while (true) {
            if (isShutdown) {
                return;
            }
            DelayedTask delayedTask = null;
            try {
                delayedTask = delayedTaskRBlockingQueue.take();
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
                //1. 找到method和spring环境下的bean
                MethodBean methodBean = getMethodBean(delayedTask);
                //2. 通过反射执行方法调用
                methodBean.invoke(delayedTask.getArgs());
            } catch (Exception e) {
                LOGGER.info("RedisClusterDelayTaskExecutor execute task failed; delayedTask={}", delayedTask, e);
            }
        });
    }

    private MethodBean getMethodBean(DelayedTask delayedTask) throws ClassNotFoundException, NoSuchMethodException {
        String methodClass = delayedTask.getMethodClass();
        String methodName = delayedTask.getMethodName();
        String[] parameterTypes = delayedTask.getParameterTypes();

        Class<?> methodClazz = Class.forName(methodClass);
        Object bean = beanFactory.getBean(methodClazz);
        Class[] parameterClasses = null;
        if (parameterTypes != null) {
            parameterClasses = new Class[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterClasses[i] = Class.forName(parameterTypes[i]);
            }
        }
        Method method = methodClazz.getMethod(methodName, parameterClasses);
        MethodBean methodBean = new MethodBean();
        methodBean.setMethod(method);
        methodBean.setBean(bean);
        return methodBean;
    }

    private static class MethodBean {
        private Method method;
        private Object bean;

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Object getBean() {
            return bean;
        }

        public void setBean(Object bean) {
            this.bean = bean;
        }

        private void invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {
            method.invoke(bean, args);
        }
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