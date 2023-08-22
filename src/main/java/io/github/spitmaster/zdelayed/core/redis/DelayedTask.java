package io.github.spitmaster.zdelayed.core.redis;

import java.lang.reflect.Method;

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


    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
