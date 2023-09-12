package io.github.spitmaster.zdelayed.rediscluster;

import io.github.spitmaster.zdelayed.rediscluster.config.RedissonTestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

/**
 *
 */
@SpringBootApplication
@Import(RedissonTestConfig.class)
public class TestWithRedisApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TestWithRedisApplication.class, args);
    }
}
