package io.github.spitmaster.zdelayed.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * 标记方法参数, 用于告诉延时任务的延迟时间多长 (如果@Zdelayed设置了fixedDelayedTime, 则无视@DelayTime注解)
 * ---
 * 可用于 java.time.Duration 类型的参数上, 用于表示延迟时长
 * ---
 * 也可用于 java.lang.Number 类型的参数上, 一律当做毫秒表示延迟时长进行处理
 * java.lang.Number#longValue()
 * ---
 * 也可以用于基础类型上, 一律强转成 long 当做毫秒表示延迟时长进行处理
 *
 * @author zhouyijin
 * @see java.time.Instant
 * @see java.lang.Number
 * @see java.time.Duration
 * @see Zdelayed
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DelayTime {

    /**
     * 如果是数字类型或者基础类型, 可以指定时间单位
     * 默认使用毫秒
     */
    ChronoUnit timeunit() default ChronoUnit.MILLIS;
}
