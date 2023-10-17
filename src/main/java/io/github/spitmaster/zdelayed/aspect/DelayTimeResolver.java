package io.github.spitmaster.zdelayed.aspect;

import io.github.spitmaster.zdelayed.annotation.DelayTime;
import io.github.spitmaster.zdelayed.annotation.Zdelayed;
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
     * @param zdelayed         切点上的 @Zdelayed 注解
     * @param methodInvocation 切点
     * @return 延时时长
     */
    public Duration getDelayTime(Zdelayed zdelayed, MethodInvocation methodInvocation) {
        long fixedDelayTime = zdelayed.fixedDelayTime();
        if (fixedDelayTime > 0) {
            //如果Zdelayed注解设置了固定的延迟时间, 则使用固定的延迟时间, 否则使用@DelayTime标记的时间参数
            return Duration.of(fixedDelayTime, zdelayed.timeunit());
        }
        validateMethod(methodInvocation);
        DelayTimeParameterInfo delayTimeParameterInfo = getDelayTimeParameterInfo(methodInvocation);
        if (delayTimeParameterInfo != null) {
            Object[] args = methodInvocation.getArguments();
            int paramIndex = delayTimeParameterInfo.paramIndex;
            Object delayTimeArg = args[paramIndex];
            Class<?> delayTimeArgClass = delayTimeArg.getClass();
            if (delayTimeArg instanceof Duration) {
                return (Duration) delayTimeArg;
            } else if (delayTimeArg instanceof Number) {
                return Duration.of(((Number) delayTimeArg).longValue(), zdelayed.timeunit());
            } else if (delayTimeArgClass.isPrimitive()) {
                //基础类型, 可能走不到这里, 基础类型可能会被转成包装类型
                return Duration.of((long) delayTimeArg, zdelayed.timeunit());
            }
        }
        return null;
    }

    //校验这个方法上的注解是否合法
    private void validateMethod(MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        Parameter[] parameters = method.getParameters();
        long matchedParameterCount = Arrays.stream(Optional.ofNullable(parameters).orElse(new Parameter[]{})).map(parameter -> AnnotatedElementUtils.findMergedAnnotation(parameter, DelayTime.class)).filter(Objects::nonNull).count();
        if (matchedParameterCount > 1) {
            //如果有两个以上的参数指定 @DelayTime 注解, 则直接报错
            String errorMessage = String.format("this method:[%s] more than one parameter has @DelayTime annotation", method.getName());
            throw new ZdelayedException(errorMessage);
        }
    }

    private DelayTimeParameterInfo getDelayTimeParameterInfo(MethodInvocation methodInvocation) {
        return DELAY_TIME_PARAMETER_INFO_MAP.computeIfAbsent(methodInvocation.getMethod(), method -> {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            int paramCount = parameterAnnotations.length;
            for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
                for (Annotation annotation : parameterAnnotations[paramIndex]) {
                    if (annotation instanceof DelayTime) {
                        DelayTimeParameterInfo delayTimeParameterInfo = new DelayTimeParameterInfo();
                        delayTimeParameterInfo.paramIndex = paramIndex;
                        return delayTimeParameterInfo;
                    }
                }
            }
            return null;
        });
    }

    private static class DelayTimeParameterInfo {
        private int paramIndex;
    }
}
