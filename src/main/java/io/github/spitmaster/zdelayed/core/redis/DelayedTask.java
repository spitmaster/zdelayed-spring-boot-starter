package io.github.spitmaster.zdelayed.core.redis;

/**
 * 延迟任务的信息
 * methodClass + methodName + parameterTypes 可以确定唯一的方法
 *
 * @author zhouyijin
 */
class DelayedTask {

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
    private Object[] args;

    String getMethodClass() {
        return methodClass;
    }

    void setMethodClass(String methodClass) {
        this.methodClass = methodClass;
    }

    String getMethodName() {
        return methodName;
    }

    void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    String[] getParameterTypes() {
        return parameterTypes;
    }

    void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    Object[] getArgs() {
        return args;
    }

    void setArgs(Object[] args) {
        this.args = args;
    }
}
