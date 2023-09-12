package io.github.spitmaster.zdelayed.annotation;

import io.github.spitmaster.zdelayed.enums.DelayedTaskScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * 依赖于Spring框架
 * 标记该注解的方法, 会延迟执行
 * 被标记的方法, 只能返回Future类型的对象, 或者void
 * ---
 * 配合 io.github.spitmaster.zdelayed.annotation.DelayTime 使用
 * 使用 @DelayTime 标记在方法的参数上用于表示延时多长时间进行执行
 * 如果没有 @DelayTime 则不当做延时任务处理
 *
 * @author zhouyijin
 * @see DelayTime
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Zdelayed {

    /**
     * 延时任务的传递范围
     * 默认是本实例去执行延时任务
     */
    DelayedTaskScope taskScope() default DelayedTaskScope.STANDALONE;

    /**
     * 当这个值大于0的时候, 强制使用这个延迟时间, 无视@DelayTime注解
     *
     * @see DelayTime
     */
    long fixedDelayTime() default 0;

    /**
     * fixedDelayTime 使用的时间单位
     */
    ChronoUnit timeunit() default ChronoUnit.SECONDS;
}
