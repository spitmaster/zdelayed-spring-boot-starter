package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import java.time.Duration;
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
    public Object scheduleTask(@Nonnull MethodInvocation methodInvocation, Duration delayTime) throws Throwable {
        return scheduledExecutorService.schedule(() -> {
            try {
                return methodInvocation.proceed();
            } catch (Throwable th) {
                throw new Exception(th);
            }
        }, delayTime.toMillis(), TimeUnit.MILLISECONDS);
    }
}
