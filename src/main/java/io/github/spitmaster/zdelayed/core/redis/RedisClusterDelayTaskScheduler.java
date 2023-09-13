package io.github.spitmaster.zdelayed.core.redis;

import com.alibaba.fastjson.JSON;
import io.github.spitmaster.zdelayed.core.DelayTaskExecutor;
import org.aopalliance.intercept.MethodInvocation;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 分发延时任务的调度器
 *
 * @author zhouyijin
 */
public class RedisClusterDelayTaskScheduler implements DelayTaskExecutor {

    //延时队列的名字
    static final String ZDELAYED_QUEUE_NAME = "zdelayed:task";
    //使用jackson序列化 DelayedTask 对象
//    static final TypedJsonJacksonCodec DELAY_TASK_CODEC = new TypedJsonJacksonCodec(DelayedTask.class);
    static final Codec DELAY_TASK_CODEC = new StringCodec();

    private final RedissonClient redissonClient;

    public RedisClusterDelayTaskScheduler(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Future scheduleTask(MethodInvocation methodInvocation, Duration delayTime) throws Throwable {
        if (DelayedTaskChecker.isFromDelayedTask()) {
            //如果已经是来自于延时任务的调用了, 则直接执行
            methodInvocation.proceed();
            return null;
        }
        RBlockingQueue<String> blockingQueue = redissonClient.getBlockingQueue(ZDELAYED_QUEUE_NAME, DELAY_TASK_CODEC);
        RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        delayedQueue.offer(this.getDelayTask(methodInvocation), delayTime.toMillis(), TimeUnit.MILLISECONDS);
        return null;
    }

    private String getDelayTask(MethodInvocation methodInvocation) {
        DelayedTask delayedTask = new DelayedTask();
        //1. 找到需要发送给 redis 延时队列的数据
        Method method = methodInvocation.getMethod();
        Class<?> methodClass = method.getDeclaringClass();
        Parameter[] parameters = method.getParameters();
        Object[] arguments = methodInvocation.getArguments();
        //2. 组装
        delayedTask.setMethodClass(methodClass.getName());
        delayedTask.setMethodName(method.getName());
        if (parameters != null) {
            String[] parameterTypes = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                parameterTypes[i] = parameters[i].getType().getName();
            }
            delayedTask.setParameterTypes(parameterTypes);
        }
        //每个参数单独通过fastjson序列化成字符串, redis的queue回调的时候,再通过fastjson还原
        String[] args = Arrays.stream(arguments)
                .map(JSON::toJSONString)
                .toArray(String[]::new);
        delayedTask.setArgs(args);
        return JSON.toJSONString(delayedTask);
    }

}
