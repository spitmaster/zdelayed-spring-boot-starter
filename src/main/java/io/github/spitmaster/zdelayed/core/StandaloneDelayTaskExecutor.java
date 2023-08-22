package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ReflectionUtils;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 单机执行延时任务的executor
 */
public class StandaloneDelayTaskExecutor implements DelayTaskExecutor {

    private final ScheduledExecutorService zdelayedScheduler;
    private final ExecutorService zdelayedExecutor;

    public StandaloneDelayTaskExecutor(ScheduledExecutorService zdelayedScheduler, ExecutorService zdelayedExecutor) {
        this.zdelayedScheduler = zdelayedScheduler;
        this.zdelayedExecutor = zdelayedExecutor;
    }

    @Override
    public Future scheduleTask(MethodInvocation methodInvocation, Duration delayTime) throws Throwable {
        return zdelayedScheduler.schedule(
                        () -> this.executeTask(methodInvocation),
                        delayTime.toMillis(),
                        TimeUnit.MILLISECONDS)
                //zdelayedScheduler.schedule() 会再包装一层 Future, 我们需要.get() 才能获取到执行器zdelayedExecutor的Future
                .get();
    }

    private Future<Object> executeTask(MethodInvocation methodInvocation) {
        return zdelayedExecutor.submit(() -> {
            try {
                Object result = methodInvocation.proceed();
                if (result instanceof Future) {
                    //@Zdelayed 注解标记的方实现法如果返回值是Future, 这里要解包不然 scheduledExecutorService 还会再包装一层
                    return ((Future<?>) result).get();
                }
            } catch (Throwable ex) {
                ReflectionUtils.rethrowException(ex);
            }
            return null;
        });
    }
}
