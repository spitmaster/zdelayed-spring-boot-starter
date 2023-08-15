package io.github.spitmaster.zdelayed.config;

import io.github.spitmaster.zdelayed.aspect.DelayTimeResolver;
import io.github.spitmaster.zdelayed.aspect.ZdelayedAnnotationAdvisor;
import io.github.spitmaster.zdelayed.aspect.ZdelayedMethodInterceptor;
import io.github.spitmaster.zdelayed.core.MQDelayTaskExecutor;
import io.github.spitmaster.zdelayed.core.RedisClusterDelayTaskExecutor;
import io.github.spitmaster.zdelayed.core.StandaloneDelayTaskExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * zdelayed的默认配置
 *
 * @author zhouyijin
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "zdelayed.enabled", matchIfMissing = true)
public class ZdelayedAutoConfiguration {

    public static final String ZDELAYED_SCHEDULED_EXECUTOR_BEAN_NAME = "zdelayedScheduledExecutorService";

    @Bean
    public DelayTimeResolver delayTimeResolver() {
        return new DelayTimeResolver();
    }

    /**
     * 默认的standalone延时任务执行线程池
     * 你可以自定义名字为 ZDELAYED_SCHEDULED_EXECUTOR_BEAN_NAME 的ScheduledExecutorService替代这个默认的 scheduledExecutor
     */
    @Bean(ZDELAYED_SCHEDULED_EXECUTOR_BEAN_NAME)
    @ConditionalOnMissingBean
    public ScheduledExecutorService defaultScheduledExecutor() {
        return new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
    }

    @Bean
    public StandaloneDelayTaskExecutor standaloneDelayTaskExecutor(@Qualifier(ZDELAYED_SCHEDULED_EXECUTOR_BEAN_NAME) ScheduledExecutorService zdelayedScheduledExecutorService) {
        return new StandaloneDelayTaskExecutor(zdelayedScheduledExecutorService);
    }

    @Bean
    public RedisClusterDelayTaskExecutor redisClusterDelayTaskExecutor() {
        return new RedisClusterDelayTaskExecutor();
    }

    @Bean
    public MQDelayTaskExecutor mqDelayTaskExecutor() {
        return new MQDelayTaskExecutor();
    }

    @Bean
    public ZdelayedMethodInterceptor zdelayedMethodInterceptor(
            StandaloneDelayTaskExecutor standaloneDelayTaskExecutor,
            RedisClusterDelayTaskExecutor redisClusterDelayTaskExecutor,
            MQDelayTaskExecutor mqDelayTaskExecutor,
            DelayTimeResolver delayTimeResolver) {
        return new ZdelayedMethodInterceptor(standaloneDelayTaskExecutor, redisClusterDelayTaskExecutor, mqDelayTaskExecutor, delayTimeResolver);
    }

    @Bean
    public ZdelayedAnnotationAdvisor zdelayedAnnotationAdvisor(ZdelayedMethodInterceptor zdelayedMethodInterceptor) {
        return new ZdelayedAnnotationAdvisor(zdelayedMethodInterceptor);
    }

}
