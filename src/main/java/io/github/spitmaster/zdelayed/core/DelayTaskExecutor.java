package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;

/**
 * 真正执行延时任务的执行器
 */
public interface DelayTaskExecutor {

    /**
     *
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    Object scheduleTask(@Nonnull MethodInvocation methodInvocation) throws Throwable;

}
