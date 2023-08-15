package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * 真正执行延时任务的执行器
 */
public interface DelayTaskExecutor {

    /**
     * 延时执行器执行延时任务
     *
     * @param methodInvocation 被代理的方法
     * @param delayTime        延迟的时间
     * @return 返回值, 只有两种, 一种是null表示原方法返回值是void, 一种是Future
     * @throws Throwable 异常
     */
    Object scheduleTask(@Nonnull MethodInvocation methodInvocation, Duration delayTime) throws Throwable;

}
