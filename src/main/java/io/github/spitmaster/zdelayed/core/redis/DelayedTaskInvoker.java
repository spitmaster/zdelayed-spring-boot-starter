package io.github.spitmaster.zdelayed.core.redis;

import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 封装DelayTask对应的 业务方法调用器
 * 将方法的调度 和 业务方法的调用隔离开
 *
 * @author zhouyijin
 */
class DelayedTaskInvoker {

    /**
     * 真正需要被调用的方法
     */
    private Method method;

    /**
     * Spring环境下的bean; 这个bean是被代理的, 所以又会被 io.github.spitmaster.zdelayed.core.redis.RedisClusterDelayTaskScheduler 执行
     */
    private Object bean;

    /**
     * 方法调用需要的参数
     */
    private Object[] args;

    /**
     * 构造执行业务方法需要的信息
     */
    public static DelayedTaskInvoker from(DelayedTask delayedTask, BeanFactory beanFactory) throws ClassNotFoundException, NoSuchMethodException {
        String methodClass = delayedTask.getMethodClass();
        String methodName = delayedTask.getMethodName();
        String[] parameterTypes = delayedTask.getParameterTypes();

        Class<?> methodClazz = Class.forName(methodClass);
        Object bean = beanFactory.getBean(methodClazz);
        Class[] parameterClasses = null;
        if (parameterTypes != null) {
            parameterClasses = new Class[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterClasses[i] = Class.forName(parameterTypes[i]);
            }
        }
        Method method = methodClazz.getMethod(methodName, parameterClasses);
        DelayedTaskInvoker methodBean = new DelayedTaskInvoker();
        methodBean.method = method;
        methodBean.bean = bean;
        methodBean.args = delayedTask.getArgs();
        return methodBean;
    }

    /**
     * 调用业务方法
     */
    void invoke() throws InvocationTargetException, IllegalAccessException {
        method.invoke(bean, args);
    }
}
