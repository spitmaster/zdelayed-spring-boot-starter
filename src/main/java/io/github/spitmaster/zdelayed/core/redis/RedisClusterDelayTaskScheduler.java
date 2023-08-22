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
        String queueName = this.queueName(methodInvocation);
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

    //未来可以加缓存
    public String queueName(MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        Class<?> declaringClass = method.getDeclaringClass(); //method所在的class
        Parameter[] parameters = method.getParameters(); //method的参数
        StringBuilder sb = new StringBuilder();
        sb.append(declaringClass.getName());
        sb.append(":");
        sb.append(method.getName());
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                sb.append(":");
                sb.append(parameter.getType().getTypeName());
            }
        }
        return sb.toString();
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
