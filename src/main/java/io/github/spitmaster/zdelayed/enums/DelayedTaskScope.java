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
     */
    REDIS_CLUSTER,

//    /**
//     * 通过MQ的方式传递任务
//     * not implemented
//     */
//    MQ,

}
