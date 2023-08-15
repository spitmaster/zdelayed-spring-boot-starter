package io.github.spitmaster.zdelayed.core;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * 延迟任务的信息
 *
 * @author zhouyijin
 */
public class DelayedTask {

    /**
     * 延时方法
     */
    private Method method;

    /**
     * 延时方法的参数
     */
    private Object[] args;

    /**
     * 延时方法返回值类型
     */
    private Class returnClass;

    /**
     * 延时任务需要延时的时间
     */
    private Duration delayTime;
}
