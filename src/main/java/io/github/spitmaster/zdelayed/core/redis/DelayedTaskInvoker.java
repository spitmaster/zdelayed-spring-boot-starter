package io.github.spitmaster.zdelayed.core.redis;

import com.google.common.primitives.Primitives;
import org.apache.commons.lang3.ArrayUtils;
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
     * json序列化后的 方法调用需要的参数
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
        Class<?>[] parameterClasses = null;
        Object[] methodArgs = ArrayUtils.EMPTY_OBJECT_ARRAY; //null情况下的默认值
        if (parameterTypes != null) {
            methodArgs = new Object[parameterTypes.length];
            parameterClasses = new Class[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                String parameterTypeName = parameterTypes[i];
                Class<?> parameterType = matchPrimitiveType(parameterTypeName); //先看看这个参数的类型是不是基础类型
                if (parameterType == null) {
                    parameterType = Class.forName(parameterTypeName);
                }
                //args的类型是json序列化过的数据, 可能与真实的数据不一样, 这里需要处理这些参数, 让类型与方法的参数匹配上
                parameterClasses[i] = parameterType;
                //参数要恢复成方法真正调用使用的类型的对象
                Object methodArg = JsonSerializer.parseObject(delayedTask.getArgs()[i], parameterType);
                methodArgs[i] = methodArg;
            }
        }
        Method method = methodClazz.getMethod(methodName, parameterClasses);
        DelayedTaskInvoker methodBean = new DelayedTaskInvoker();
        methodBean.method = method;
        methodBean.bean = bean;
        methodBean.args = methodArgs;
        return methodBean;
    }

    private static Class<?> matchPrimitiveType(String parameterTypeName) {
        for (Class<?> primitiveType : Primitives.allPrimitiveTypes()) {
            if (primitiveType.getName().equals(parameterTypeName)) {
                return primitiveType;
            }
        }
        return null;
    }

    /**
     * 调用业务方法
     */
    void invoke() throws InvocationTargetException, IllegalAccessException {
        method.invoke(bean, args);
    }
}
