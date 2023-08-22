package io.github.spitmaster.zdelayed.core.redis;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 封装redis延时队列的名称定义算法
 */
public class DelayQueueNameProvider {

    private DelayQueueNameProvider() {
    }

    /**
     * 延时队列的队列名称
     *
     * @param method 方法
     * @return 队列名称
     */
    public static String queueName(Method method) {
        Class<?> declaringClass = method.getDeclaringClass(); //method所在的class
        Parameter[] parameters = method.getParameters(); //method的参数
        StringBuilder sb = new StringBuilder();
        sb.append("zdelayed");
        sb.append(":");
        sb.append(declaringClass.getName());
        sb.append(":");
        sb.append(method.getName());
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                sb.append(":");
                sb.append(parameter.getType().getTypeName());
            }
        }
        return sb.toString();
    }
}
