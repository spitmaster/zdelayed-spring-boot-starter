package io.github.spitmaster.zdelayed.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记方法参数, 用于告诉延时任务的延迟时间多长
 * ---
 * 可用于 java.time.Instant 类型的参数上, 用于指定执行时间
 * ---
 * 可用于 java.time.Duration 类型的参数上, 用于表示延迟时长
 * ---
 * 也可用于 java.lang.Number 类型的参数上, 一律当做毫秒表示延迟时长进行处理
 * java.lang.Number#longValue()
 * ---
 * 也可以用于基础类型上, 一律强转成 long 当做毫秒表示延迟时长进行处理
 *
 * @see java.time.Instant
 * @see java.lang.Number
 * @see java.time.Duration
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DelayTime {

}
