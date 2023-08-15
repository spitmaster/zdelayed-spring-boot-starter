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

/**
 * 切面的执行
 *
 * @author zhouyijin
 */
public class ZdelayedMethodInterceptor implements MethodInterceptor {

    public ZdelayedMethodInterceptor(
            StandaloneDelayTaskExecutor standaloneDelayTaskExecutor,
            RedisClusterDelayTaskExecutor redisClusterDelayTaskExecutor,
            MQDelayTaskExecutor mqDelayTaskExecutor) {
        this.standaloneDelayTaskExecutor = standaloneDelayTaskExecutor;
        this.redisClusterDelayTaskExecutor = redisClusterDelayTaskExecutor;
        this.mqDelayTaskExecutor = mqDelayTaskExecutor;
    }

    private final StandaloneDelayTaskExecutor standaloneDelayTaskExecutor;
    private final RedisClusterDelayTaskExecutor redisClusterDelayTaskExecutor;
    private final MQDelayTaskExecutor mqDelayTaskExecutor;

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        Zdelayed zdelayed = AnnotatedElementUtils.findMergedAnnotation(method, Zdelayed.class);
        if (zdelayed == null) {
            return methodInvocation.proceed();
        }
        DelayedTaskScope delayedTaskScope = zdelayed.taskScope();
        switch (delayedTaskScope) {
            case STANDALONE:
                return standaloneDelayTaskExecutor.scheduleTask(methodInvocation);
            case REDIS_CLUSTER:
                return redisClusterDelayTaskExecutor.scheduleTask(methodInvocation);
            case MQ:
                return mqDelayTaskExecutor.scheduleTask(methodInvocation);
        }
        return null;
    }
}
