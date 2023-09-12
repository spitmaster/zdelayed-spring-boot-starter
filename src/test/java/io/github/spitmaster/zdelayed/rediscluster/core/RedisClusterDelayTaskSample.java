package io.github.spitmaster.zdelayed.rediscluster.core;

import io.github.spitmaster.zdelayed.annotation.DelayTime;
import io.github.spitmaster.zdelayed.annotation.Zdelayed;
import io.github.spitmaster.zdelayed.enums.DelayedTaskScope;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Future;

@Component
public class RedisClusterDelayTaskSample {

    @Zdelayed(taskScope = DelayedTaskScope.REDIS_CLUSTER)
    public Future<Integer> a() {
        System.out.println("a");
        return AsyncResult.forValue(0);
    }

    @Zdelayed(taskScope = DelayedTaskScope.REDIS_CLUSTER)
    public void b(@DelayTime Long delayTimeMills) {
        System.out.println("b");
    }

    @Zdelayed(taskScope = DelayedTaskScope.REDIS_CLUSTER)
    public void c(@DelayTime long delayTimeMills) {
        System.out.println("c");
    }

    @Zdelayed(taskScope = DelayedTaskScope.REDIS_CLUSTER)
    public void d(@DelayTime Duration delayTime) {
        System.out.println("d");
    }

    //由于设置了fixedDelayTime, 所以直接无视 @DelayTime
    @Zdelayed(taskScope = DelayedTaskScope.REDIS_CLUSTER, fixedDelayTime = 3)
    public void e(@DelayTime Duration delayTime) {
        System.out.println("e");
    }


}
