package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;

public class MQDelayTaskExecutor implements DelayTaskExecutor{
    @Override
    public Object scheduleTask(@Nonnull MethodInvocation methodInvocation) throws Throwable {
        return null;
    }
}
