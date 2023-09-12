package io.github.spitmaster.zdelayed.core.standalone;

import io.github.spitmaster.zdelayed.annotation.DelayTime;
import io.github.spitmaster.zdelayed.annotation.Zdelayed;
import io.github.spitmaster.zdelayed.enums.DelayedTaskScope;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Future;

@Component
public class StandaloneDelayTaskSample {

    @Zdelayed(taskScope = DelayedTaskScope.STANDALONE)
    public Future<Integer> a() {
        System.out.println("a");
        return AsyncResult.forValue(0);
    }

    @Zdelayed(taskScope = DelayedTaskScope.STANDALONE)
    public Future<Integer> b(@DelayTime Long delayTimeMills) {
        System.out.println("b");
        return AsyncResult.forValue(0);
    }

    @Zdelayed(taskScope = DelayedTaskScope.STANDALONE)
    public Future<Integer> c(@DelayTime long delayTimeMills) {
        System.out.println("c");
        return AsyncResult.forValue(0);
    }

    @Zdelayed(taskScope = DelayedTaskScope.STANDALONE)
    public Future<Integer> d(@DelayTime Duration delayTime) {
        System.out.println("d");
        return AsyncResult.forValue(0);
    }

    //由于设置了fixedDelayTime, 所以直接无视 @DelayTime
    @Zdelayed(fixedDelayTime = 3)
    public Future<Integer> e(@DelayTime Duration delayTime) {
        System.out.println("e");
        return AsyncResult.forValue(0);
    }


}
