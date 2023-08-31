package io.github.spitmaster.zdelayed.core.standalone;

import io.github.spitmaster.zdelayed.core.DelayTaskExecutor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 单机执行延时任务的executor
 *
 * @author zhouyijin
 */
public class StandaloneDelayTaskExecutor implements DelayTaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneDelayTaskExecutor.class);

    private final ScheduledExecutorService zdelayedScheduler;

    public StandaloneDelayTaskExecutor(ScheduledExecutorService zdelayedScheduler) {
        this.zdelayedScheduler = zdelayedScheduler;
    }

    @Override
    public Future scheduleTask(MethodInvocation methodInvocation, Duration delayTime) throws Throwable {
        return zdelayedScheduler.schedule(() -> {
                    try {
                        Object result = methodInvocation.proceed();
                        if (result instanceof Future) {
                            //@Zdelayed 注解标记的方实现法如果返回值是Future, 这里要解包不然 scheduledExecutorService 还会再包装一层
                            return ((Future<?>) result).get();
                        }
                    } catch (InterruptedException e) {
                        LOGGER.error("StandaloneDelayTaskExecutor Interrupted", e);
                        /* Clean up whatever needs to be handled before interrupting  */
                        Thread.currentThread().interrupt();
                    } catch (Throwable th) {
                        LOGGER.error("StandaloneDelayTaskExecutor execute error", th);
                        ReflectionUtils.rethrowException(th);
                    }
                    return null;
                },
                delayTime.toMillis(),
                TimeUnit.MILLISECONDS);
    }
}
