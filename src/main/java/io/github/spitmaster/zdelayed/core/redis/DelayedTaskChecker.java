package io.github.spitmaster.zdelayed.core.redis;

/**
 * 使用ThreadLocal来标记此次调用是否是来资源延时任务的触发, 防止递归进入发送延时任务的逻辑
 *
 * @author zhouyijin
 */
public class DelayedTaskChecker {

    private static final ThreadLocal<Integer> delayedTaskMarkerThreadLocal = new ThreadLocal<>();

    private DelayedTaskChecker(){

    }

    /**
     * 查询延时任务是否是来自redis的延时任务
     *
     * @return true表示调用是来自于延时任务
     */
    public static boolean isFromDelayedTask() {
        return delayedTaskMarkerThreadLocal.get() != null;
    }

    /**
     * 标记该线程正在执行延时任务
     * 被标记的线程, 不能再发送延时任务
     * 当执行到延时任务的method的切点的时候, 直接调用, 不延时
     */
    public static void markFromDelayedTask() {
        delayedTaskMarkerThreadLocal.set(1);
    }

    /**
     * 清理标记, 该线程可以正常发送延时任务
     */
    public static void clearMark() {
        delayedTaskMarkerThreadLocal.remove();
    }
}
