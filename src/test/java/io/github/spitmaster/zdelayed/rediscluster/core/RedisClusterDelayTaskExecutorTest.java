package io.github.spitmaster.zdelayed.rediscluster.core;

import com.google.common.base.Stopwatch;
import io.github.spitmaster.zdelayed.rediscluster.TestWithRedisApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = TestWithRedisApplication.class)
class RedisClusterDelayTaskExecutorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClusterDelayTaskExecutorTest.class);

    @Autowired
    RedisClusterDelayTaskSample redisClusterDelayTaskSample;

    //等待执行结果
    @Test
    void a1() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Integer> a = redisClusterDelayTaskSample.a(); //假设代码执行花费50ms以内
        a.get();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("a elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

    //不等待执行结果
    @Test
    void a2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        redisClusterDelayTaskSample.a();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("a elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 5);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }


    //不等待执行结果
    @Test
    void b2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        redisClusterDelayTaskSample.b(2000L);
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("b elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 500);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }


    //不等待执行结果
    @Test
    void c2() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        redisClusterDelayTaskSample.c(2000L);
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("c elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }


    //不等待执行结果
    @Test
    void d2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        redisClusterDelayTaskSample.d(Duration.ofMillis(2000));
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("d elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }


    //不等待执行结果
    @Test
    void e2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        redisClusterDelayTaskSample.e(Duration.ofSeconds(2000));
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("e elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

    @AfterAll
    static void waitForDelayedTask() throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
    }
}