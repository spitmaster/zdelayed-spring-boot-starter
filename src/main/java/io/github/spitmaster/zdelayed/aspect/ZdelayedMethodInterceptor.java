package io.github.spitmaster.zdelayed.aspect;

import io.github.spitmaster.zdelayed.annotation.Zdelayed;
import io.github.spitmaster.zdelayed.core.MQDelayTaskExecutor;
import io.github.spitmaster.zdelayed.core.RedisClusterDelayTaskExecutor;
import io.github.spitmaster.zdelayed.core.StandaloneDelayTaskExecutor;
import io.github.spitmaster.zdelayed.enums.DelayedTaskScope;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotatedElementUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.time.Duration;

/**
 * 切面的执行
 *
 * @author zhouyijin
 */
public class ZdelayedMethodInterceptor implements MethodInterceptor {

    public ZdelayedMethodInterceptor(StandaloneDelayTaskExecutor standaloneDelayTaskExecutor,
                                     RedisClusterDelayTaskExecutor redisClusterDelayTaskExecutor,
                                     MQDelayTaskExecutor mqDelayTaskExecutor,
                                     DelayTimeResolver delayTimeResolver) {
        this.standaloneDelayTaskExecutor = standaloneDelayTaskExecutor;
        this.redisClusterDelayTaskExecutor = redisClusterDelayTaskExecutor;
        this.mqDelayTaskExecutor = mqDelayTaskExecutor;
        this.delayTimeResolver = delayTimeResolver;
    }

    private final StandaloneDelayTaskExecutor standaloneDelayTaskExecutor;
    private final RedisClusterDelayTaskExecutor redisClusterDelayTaskExecutor;
    private final MQDelayTaskExecutor mqDelayTaskExecutor;
    private final DelayTimeResolver delayTimeResolver;

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        //1. 检查是否是延时任务
        Zdelayed zdelayed = AnnotatedElementUtils.findMergedAnnotation(method, Zdelayed.class);
        if (zdelayed == null) {
            return methodInvocation.proceed();
        }
        //2. 获取延时任务的延时时长
        Duration delayTime = delayTimeResolver.getDelayTime(methodInvocation);
        if (delayTime == null) {
            //没有延迟时间, 则不当做延时任务执行
            return methodInvocation.proceed();
        }
        //3. 找到对应的延时任务执行器进行执行
        DelayedTaskScope delayedTaskScope = zdelayed.taskScope();
        switch (delayedTaskScope) {
            case STANDALONE:
                return standaloneDelayTaskExecutor.scheduleTask(methodInvocation, delayTime);
            case REDIS_CLUSTER:
                //分布式场景下的延时任务, Future返回值没有意义, 直接返回null
                redisClusterDelayTaskExecutor.scheduleTask(methodInvocation, delayTime);
                return null;
            case MQ:
                //分布式场景下的延时任务, Future返回值没有意义, 直接返回null
                mqDelayTaskExecutor.scheduleTask(methodInvocation, delayTime);
                return null;
        }
        //没有
        return methodInvocation.proceed();
    }
}
