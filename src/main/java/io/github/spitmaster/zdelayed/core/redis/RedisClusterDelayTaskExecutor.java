package io.github.spitmaster.zdelayed.core.redis;

import io.github.spitmaster.zdelayed.annotation.Zdelayed;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

public class RedisClusterDelayTaskExecutor implements BeanPostProcessor, ApplicationRunner {

    private final RedissonClient redissonClient;
    private final Executor zdelayedExecutor;

    private MethodMatcher zdelayedMethodMatcher = new AnnotationMethodMatcher(Zdelayed.class, true);

    /**
     * 所有被@Delayed找到的Method都会被放到这里
     */
    private Set<Method> delayedMethods = new HashSet<>();

    public RedisClusterDelayTaskExecutor(RedissonClient redissonClient, Executor zdelayedExecutor) {
        this.redissonClient = redissonClient;
        this.zdelayedExecutor = zdelayedExecutor;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        /*
         * 过滤找到所有被@Delayed标记的method
         */
        Class<?> beanClass = bean.getClass();
        Method[] methods = beanClass.getMethods();
        if (methods != null) {
            for (Method method : methods) {
                if (zdelayedMethodMatcher.matches(method, beanClass)) {
                    delayedMethods.add(method);
                }
            }
        }
        Method[] declaredMethods = beanClass.getDeclaredMethods();
        if (declaredMethods != null) {
            for (Method method : declaredMethods) {
                if (zdelayedMethodMatcher.matches(method, beanClass)) {
                    delayedMethods.add(method);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /*
         * spring应用的refresh执行完之后被执行
         * 此时发起所有redis延时队列的监听
         */
        for (Method delayedMethod : delayedMethods) {
            String queueName = DelayQueueNameProvider.queueName(delayedMethod);
            RBlockingQueue blockingQueue = redissonClient.getBlockingQueue(queueName);
            RDelayedQueue delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
            // TODO: 2023/8/22
        }

    }
}
