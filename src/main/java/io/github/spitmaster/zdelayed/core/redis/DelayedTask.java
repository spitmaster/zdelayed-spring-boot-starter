package io.github.spitmaster.zdelayed.core.redis;

/**
 * 延迟任务的信息
 * methodClass + methodName + parameterTypes 可以确定唯一的方法
 *
 * @author zhouyijin
 */
public class DelayedTask {

    /**
     * 方法所在的class
     */
    private String methodClass;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法的参数类型
     */
    private String[] parameterTypes;

    /**
     * 延时方法的参数
     */
    private String[] args;

    public String getMethodClass() {
        return methodClass;
    }

    public void setMethodClass(String methodClass) {
        this.methodClass = methodClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
