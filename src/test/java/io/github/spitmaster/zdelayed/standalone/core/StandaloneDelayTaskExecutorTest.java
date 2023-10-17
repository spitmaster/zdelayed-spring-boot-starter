package io.github.spitmaster.zdelayed.standalone.core;

import com.google.common.base.Stopwatch;
import io.github.spitmaster.zdelayed.standalone.TestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@SpringBootTest(classes = TestApplication.class)
class StandaloneDelayTaskExecutorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneDelayTaskExecutorTest.class);

    @Autowired
    StandaloneDelayTaskSample standaloneDelayTaskSample;

    //等待执行结果
    @Test
    void a1() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Integer> a = standaloneDelayTaskSample.a(); //假设代码执行花费50ms以内
        a.get();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("a1 elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

    //不等待执行结果
    @Test
    void a2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        standaloneDelayTaskSample.a();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("a2 elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 5);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

    //等待执行结果
    @Test
    void b1() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Integer> b = standaloneDelayTaskSample.b(2000L);
        b.get();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("b1 elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 2050);
        Assertions.assertTrue(elapsed.toMillis() >= 2000);
    }

    //不等待执行结果
    @Test
    void b2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        standaloneDelayTaskSample.b(2000L);
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("b2 elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

    //等待执行结果
    @Test
    void c1() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Integer> c = standaloneDelayTaskSample.c(2000L);
        c.get();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("c1 elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 2050);
        Assertions.assertTrue(elapsed.toMillis() >= 2000);
    }

    //不等待执行结果
    @Test
    void c2() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        standaloneDelayTaskSample.c(2000L);
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("c2 elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

    //等待执行结果
    @Test
    void d1() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Integer> d = standaloneDelayTaskSample.d(Duration.ofMillis(2000));
        d.get();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("d1 elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 2050);
        Assertions.assertTrue(elapsed.toMillis() >= 2000);
    }


    //不等待执行结果
    @Test
    void d2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        standaloneDelayTaskSample.d(Duration.ofMillis(2000));
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("d2 elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }


    //等待执行结果
    @Test
    void e1() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Integer> d = standaloneDelayTaskSample.e(Duration.ofSeconds(2000));
        d.get();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("e1 elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 3050);
        Assertions.assertTrue(elapsed.toMillis() >= 3000);
    }


    //不等待执行结果
    @Test
    void e2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        standaloneDelayTaskSample.e(Duration.ofSeconds(2000));
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("e2 elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }


}