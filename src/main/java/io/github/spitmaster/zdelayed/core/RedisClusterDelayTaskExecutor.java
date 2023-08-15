package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import java.time.Duration;

public class RedisClusterDelayTaskExecutor implements DelayTaskExecutor{

    @Override
    public Object scheduleTask(@Nonnull MethodInvocation methodInvocation, Duration delayTime) throws Throwable {
        return null;
    }
}
