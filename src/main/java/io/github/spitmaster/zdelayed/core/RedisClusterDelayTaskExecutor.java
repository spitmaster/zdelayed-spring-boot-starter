package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;

import java.time.Duration;
import java.util.concurrent.Future;

public class RedisClusterDelayTaskExecutor implements DelayTaskExecutor{

    @Override
    public Future scheduleTask(MethodInvocation methodInvocation, Duration delayTime) throws Throwable {
        return null;
    }
}
