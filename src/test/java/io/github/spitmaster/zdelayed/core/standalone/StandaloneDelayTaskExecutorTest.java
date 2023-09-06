package io.github.spitmaster.zdelayed.core.standalone;

import com.google.common.base.Stopwatch;
import io.github.spitmaster.zdelayed.TestApplication;
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
    public void a1() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Integer> a = standaloneDelayTaskSample.a(); //假设代码执行花费50ms以内
        a.get();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("a elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

    //不等待执行结果
    @Test
    public void a2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        standaloneDelayTaskSample.a();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("a elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 5);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

    //等待执行结果
    @Test
    public void b1() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Integer> b = standaloneDelayTaskSample.b(2000L);
        b.get();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("b elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 2050);
        Assertions.assertTrue(elapsed.toMillis() >= 2000);
    }

    //不等待执行结果
    @Test
    public void b2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        standaloneDelayTaskSample.b(2000L);
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("b elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

    //等待执行结果
    @Test
    public void c1() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Integer> c = standaloneDelayTaskSample.c(2000L);
        c.get();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("c elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 2050);
        Assertions.assertTrue(elapsed.toMillis() >= 2000);
    }

    //不等待执行结果
    @Test
    public void c2() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        standaloneDelayTaskSample.c(2000L);
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("c elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

    //等待执行结果
    @Test
    public void d1() throws ExecutionException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Future<Integer> d = standaloneDelayTaskSample.d(Duration.ofMillis(2000));
        d.get();
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("d elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 2050);
        Assertions.assertTrue(elapsed.toMillis() >= 2000);
    }


    //不等待执行结果
    @Test
    public void d2() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        standaloneDelayTaskSample.d(Duration.ofMillis(2000));
        Duration elapsed = stopwatch.elapsed();
        LOGGER.info("d elapsed={}", elapsed);
        Assertions.assertTrue(elapsed.toMillis() < 50);
        Assertions.assertTrue(elapsed.toMillis() >= 0);
    }

}