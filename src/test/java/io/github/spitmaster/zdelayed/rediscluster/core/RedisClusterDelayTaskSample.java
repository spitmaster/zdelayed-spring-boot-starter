package io.github.spitmaster.zdelayed.rediscluster.core;

import io.github.spitmaster.zdelayed.annotation.DelayTime;
import io.github.spitmaster.zdelayed.annotation.Zdelayed;
import io.github.spitmaster.zdelayed.enums.DelayedTaskScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Future;

@Component
public class RedisClusterDelayTaskSample {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClusterDelayTaskSample.class);

    @Zdelayed(taskScope = DelayedTaskScope.REDIS_CLUSTER)
    public Future<Integer> a() {
        LOGGER.warn("------------------------------------------------------");
        LOGGER.warn("a");
        LOGGER.warn("------------------------------------------------------");
        return AsyncResult.forValue(0);
    }

    @Zdelayed(taskScope = DelayedTaskScope.REDIS_CLUSTER)
    public void b(@DelayTime Long delayTimeMills) {
        LOGGER.warn("------------------------------------------------------");
        LOGGER.warn("b");
        LOGGER.warn("------------------------------------------------------");
    }

    @Zdelayed(taskScope = DelayedTaskScope.REDIS_CLUSTER)
    public void c(@DelayTime long delayTimeMills) {
        LOGGER.warn("------------------------------------------------------");
        LOGGER.warn("c");
        LOGGER.warn("------------------------------------------------------");
    }

    @Zdelayed(taskScope = DelayedTaskScope.REDIS_CLUSTER)
    public void d(@DelayTime Duration delayTime) {
        LOGGER.warn("------------------------------------------------------");
        LOGGER.warn("d");
        LOGGER.warn("------------------------------------------------------");
    }

    //由于设置了fixedDelayTime, 所以直接无视 @DelayTime
    @Zdelayed(taskScope = DelayedTaskScope.REDIS_CLUSTER, fixedDelayTime = 3)
    public void e(@DelayTime Duration delayTime) {
        LOGGER.warn("------------------------------------------------------");
        LOGGER.warn("e");
        LOGGER.warn("------------------------------------------------------");
    }


}
