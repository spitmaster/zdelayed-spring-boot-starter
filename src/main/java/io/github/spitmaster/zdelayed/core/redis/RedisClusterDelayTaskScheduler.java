package io.github.spitmaster.zdelayed.core.redis;

import io.github.spitmaster.zdelayed.core.DelayTaskExecutor;
import org.aopalliance.intercept.MethodInvocation;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RedisClusterDelayTaskScheduler implements DelayTaskExecutor {

    private final RedissonClient redissonClient;

    public RedisClusterDelayTaskScheduler(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Future scheduleTask(MethodInvocation methodInvocation, Duration delayTime) throws Throwable {
        String queueName = DelayQueueNameProvider.queueName(methodInvocation.getMethod());
        RBlockingQueue blockingQueue = redissonClient.getBlockingQueue(queueName);
        RDelayedQueue delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        delayedQueue.offer(getDelayTask(methodInvocation), delayTime.toMillis(), TimeUnit.MILLISECONDS);
        return null;
    }

    private DelayedTask getDelayTask(MethodInvocation methodInvocation) {
        DelayedTask delayedTask = new DelayedTask();
        delayedTask.setMethod(methodInvocation.getMethod());
        delayedTask.setArgs(methodInvocation.getArguments());
        return delayedTask;
    }


    public static void main(String[] args) throws NoSuchMethodException {
        Method method = RedisClusterDelayTaskScheduler.class.getMethod("queueName", MethodInvocation.class);
        Class<?> declaringClass = method.getDeclaringClass();
//        System.out.println(declaringClass);
        for (Parameter parameter : method.getParameters()) {
            System.out.println(parameter.getType().getName());
        }
    }
}
