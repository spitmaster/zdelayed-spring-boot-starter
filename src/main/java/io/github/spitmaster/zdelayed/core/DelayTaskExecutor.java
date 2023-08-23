package io.github.spitmaster.zdelayed.core;

import org.aopalliance.intercept.MethodInvocation;

import java.time.Duration;
import java.util.concurrent.Future;

/**
 * 真正执行延时任务的执行器
 *
 * @author zhouyijin
 */
public interface DelayTaskExecutor {

    /**
     * 延时执行器执行延时任务
     *
     * @param methodInvocation 被代理的方法
     * @param delayTime        延迟的时间
     * @return 返回值, Future
     * @throws Throwable 异常
     */
    Future scheduleTask(MethodInvocation methodInvocation, Duration delayTime) throws Throwable;

}
