package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import java.util.concurrent.ScheduledExecutorService;

public class StandaloneDelayTaskExecutor implements DelayTaskExecutor {

    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public Object scheduleTask(@Nonnull MethodInvocation methodInvocation) throws Throwable {
        return null;
    }
}
