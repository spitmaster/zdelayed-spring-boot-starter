package io.github.spitmaster.zdelayed.config;

import io.github.spitmaster.zdelayed.core.redis.RedisClusterDelayTaskExecutor;
import io.github.spitmaster.zdelayed.core.redis.RedisClusterDelayTaskScheduler;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

/**
 * zdelayed的默认配置
 *
 * @author zhouyijin
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "zdelayed.enabled", matchIfMissing = true)
@ConditionalOnClass(RedissonClient.class)
public class ZdelayedRedisAutoConfiguration {

    @Bean
    @ConditionalOnBean(RedissonClient.class) //没有使用redisson的情况下不加载
    @ConditionalOnClass(RedissonClient.class)
    public RedisClusterDelayTaskScheduler redisClusterDelayTaskScheduler(
            RedissonClient redissonClient) {
        return new RedisClusterDelayTaskScheduler(redissonClient);
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class) //没有使用redisson的情况下不加载
    @ConditionalOnClass(RedissonClient.class)
    public RedisClusterDelayTaskExecutor redisClusterDelayTaskExecutor(
            RedissonClient redissonClient,
            @Qualifier(ZdelayedAutoConfiguration.ZDELAYED_CLUSTER_EXECUTOR) ExecutorService zdelayedExecutor
    ) {
        return new RedisClusterDelayTaskExecutor(redissonClient, zdelayedExecutor);
    }

}
