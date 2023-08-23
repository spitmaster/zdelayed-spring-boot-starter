package io.github.spitmaster.zdelayed.aspect;

import io.github.spitmaster.zdelayed.annotation.Zdelayed;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.core.Ordered;

/**
 * 定义@Zdelayed需要使用的Pointcut和Advice
 *
 * @author zhouyijin
 */
public class ZdelayedAnnotationAdvisor extends AbstractPointcutAdvisor {

    private static final Pointcut ZDELAYED_POINTCUT = new AnnotationMatchingPointcut(null, Zdelayed.class, true);

    private final Advice advice;

    public ZdelayedAnnotationAdvisor(ZdelayedMethodInterceptor advice) {
        this.advice = advice;
    }

    @Override
    public Pointcut getPointcut() {
        return ZDELAYED_POINTCUT;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
