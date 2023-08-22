package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.Future;

public class RedisClusterDelayTaskExecutor implements DelayTaskExecutor {

    private final RedissonClient redissonClient;

    public RedisClusterDelayTaskExecutor(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Future scheduleTask(MethodInvocation methodInvocation, Duration delayTime) throws Throwable {


        return null;
    }
}
