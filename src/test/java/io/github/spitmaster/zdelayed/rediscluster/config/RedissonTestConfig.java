package io.github.spitmaster.zdelayed.rediscluster.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 仅在单元测试使用的配置
 */
@Configuration
@PropertySource("classpath:redis.properties")
public class RedissonTestConfig {

    @Bean
    RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        //使用json序列化方式
        config.setCodec(new JsonJacksonCodec())
                .useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort() + "")
                .setPassword(redisProperties.getPassword());
        return Redisson.create(config);
    }

}
