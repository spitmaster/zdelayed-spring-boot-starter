package io.github.spitmaster.zdelayed.enums;

/**
 * 延时任务传递的范围
 *
 * @author zhouyijin
 */
public enum DelayedTaskScope {

    /**
     * 默认延时任务是单机的
     */
    STANDALONE,

    /**
     * 在使用同一个redis集群的延时任务的服务之间传递任务
     * 使用有限制条件:
     * 1. 使用同一个redis实例的服务必须是同一套代码, 不然其他服务消费到延时任务, 没有这个method就无法执行
     * 2. 延时任务无法嵌套
     */
    REDIS_CLUSTER,

//    /**
//     * 通过MQ的方式传递任务
//     * not implemented
//     */
//    MQ,

}
