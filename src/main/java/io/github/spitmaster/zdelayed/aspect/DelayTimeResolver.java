package io.github.spitmaster.zdelayed.aspect;

import io.github.spitmaster.zdelayed.annotation.DelayTime;
import io.github.spitmaster.zdelayed.exceptions.ZdelayedException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 计算延时任务延时时间的工具
 *
 * @author zhouyijin
 */
public class DelayTimeResolver {

    //缓存
    private static final Map<Method, DelayTimeParameterInfo> DELAY_TIME_PARAMETER_INFO_MAP = new ConcurrentHashMap<>();

    /**
     * 获取该延时任务, 需要延时的时长
     *
     * @param methodInvocation 切点
     * @return 延时时长
     */
    public Duration getDelayTime(MethodInvocation methodInvocation) {
        validateMethod(methodInvocation);
        DelayTimeParameterInfo delayTimeParameterInfo = getDelayTimeParameterInfo(methodInvocation);
        if (delayTimeParameterInfo != null) {
            Object[] args = methodInvocation.getArguments();
            int paramIndex = delayTimeParameterInfo.paramIndex;
            DelayTime delayTime = delayTimeParameterInfo.delayTime;
            Object delayTimeArg = args[paramIndex];
            Class<?> delayTimeArgClass = delayTimeArg.getClass();
            if (delayTimeArg instanceof Duration) {
                return (Duration) delayTimeArg;
            } else if (delayTimeArg instanceof Number) {
                return Duration.of(((Number) delayTimeArg).longValue(), delayTime.timeunit());
            } else if (delayTimeArgClass.isPrimitive()) {
                //基础类型, 可能走不到这里, 基础类型可能会被转成包装类型
                return Duration.of((long) delayTimeArg, delayTime.timeunit());
            }
        }
        return null;
    }

    //校验这个方法上的注解是否合法
    private void validateMethod(MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        Parameter[] parameters = method.getParameters();
        long matchedParameterCount = Arrays.stream(Optional.ofNullable(parameters)
                        .orElse(new Parameter[]{}))
                .map(parameter -> AnnotatedElementUtils.findMergedAnnotation(parameter, DelayTime.class))
                .filter(Objects::nonNull)
                .count();
        if (matchedParameterCount > 1) {
            //如果有两个以上的参数指定 @DelayTime 注解, 则直接报错
            String errorMessage = String.format("this method:[%s] more than one parameter has @DelayTime annotation",
                    method.getName());
            throw new ZdelayedException(errorMessage);
        }
    }

    private DelayTimeParameterInfo getDelayTimeParameterInfo(MethodInvocation methodInvocation) {
        return DELAY_TIME_PARAMETER_INFO_MAP.computeIfAbsent(
                methodInvocation.getMethod(),
                method -> {
                    DelayTimeParameterInfo delayTimeParameterInfo = null;
                    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                    int paramCount = parameterAnnotations.length;
                    SCAN:
                    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
                        for (Annotation annotation : parameterAnnotations[paramIndex]) {
                            if (annotation instanceof DelayTime) {
                                delayTimeParameterInfo = new DelayTimeParameterInfo();
                                delayTimeParameterInfo.method = method;
                                delayTimeParameterInfo.delayTime = (DelayTime) annotation;
                                delayTimeParameterInfo.paramIndex = paramIndex;
                                break SCAN;
                            }
                        }
                    }
                    return delayTimeParameterInfo;
                });
    }

    private static class DelayTimeParameterInfo {
        private Method method;
        private DelayTime delayTime;
        private int paramIndex;
    }

    public static void main(String[] args) {
        Object[] a = new Object[]{1, 1.2, 3.0f, "2"};
        System.out.println(a[1]);
        System.out.println(a[1].getClass()); //这里会变成包装类型
        System.out.println(a[1].getClass().isPrimitive()); //这里会变成包装类型
    }
}
