package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ReflectionUtils;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 单机执行延时任务的executor
 */
public class StandaloneDelayTaskExecutor implements DelayTaskExecutor {

    private final ScheduledExecutorService scheduledExecutorService;

    public StandaloneDelayTaskExecutor(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public Future scheduleTask(MethodInvocation methodInvocation, Duration delayTime) throws Throwable {
        Callable<Object> task = () -> {
            try {
                Object result = methodInvocation.proceed();
                if (result instanceof Future) {
                    //@Zdelayed 注解标记的方实现法如果返回值是Future, 这里要解包不然 scheduledExecutorService 还会再包装一层
                    return ((Future<?>) result).get();
                }
            } catch (Throwable ex) {
                ReflectionUtils.rethrowException(ex);
            }
            return null;
        };
        return scheduledExecutorService.schedule(task, delayTime.toMillis(), TimeUnit.MILLISECONDS);
    }

}
