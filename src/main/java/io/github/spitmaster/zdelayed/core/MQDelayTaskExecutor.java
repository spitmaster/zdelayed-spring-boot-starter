package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.concurrent.Future;

public class MQDelayTaskExecutor implements DelayTaskExecutor{

    @Override
    public Future scheduleTask(@Nonnull MethodInvocation methodInvocation, Duration delayTime) throws Throwable {
        return null;
    }
}
