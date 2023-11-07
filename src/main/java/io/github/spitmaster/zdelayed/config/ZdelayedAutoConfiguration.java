package io.github.spitmaster.zdelayed.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.spitmaster.zdelayed.aspect.DelayTimeResolver;
import io.github.spitmaster.zdelayed.aspect.ZdelayedAnnotationAdvisor;
import io.github.spitmaster.zdelayed.aspect.ZdelayedMethodInterceptor;
import io.github.spitmaster.zdelayed.core.MQDelayTaskExecutor;
import io.github.spitmaster.zdelayed.core.redis.RedisClusterDelayTaskExecutor;
import io.github.spitmaster.zdelayed.core.redis.RedisClusterDelayTaskScheduler;
import io.github.spitmaster.zdelayed.core.standalone.StandaloneDelayTaskExecutor;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * zdelayed的默认配置
 *
 * @author zhouyijin
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "zdelayed.enabled", matchIfMissing = true)
public class ZdelayedAutoConfiguration {

    //单机延时任务的执行器
    public static final String ZDELAYED_STANDALONE_SCHEDULER = "zdelayed-standalone-scheduler";
    //分布式情况下的延时任务的执行器
    public static final String ZDELAYED_CLUSTER_EXECUTOR = "zdelayed-cluster-executor";

    @Bean
    public DelayTimeResolver delayTimeResolver() {
        return new DelayTimeResolver();
    }

    /**
     * 调度用的线程池, 只需要一个1线程即可
     * 你可以自定义名字为 ZDELAYED_SCHEDULER 的 bean 替代这个默认的 scheduledExecutor
     */
    @Bean(ZDELAYED_STANDALONE_SCHEDULER)
    @ConditionalOnMissingBean(name = ZDELAYED_STANDALONE_SCHEDULER)
    public ScheduledExecutorService defaultZdelayedScheduler() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true) //不阻止JVM关闭
                .setNameFormat("zdelayed-scheduler-%d")
                .build();
        return new ScheduledThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                threadFactory
        );
    }

    /**
     * 真正执行任务的线程池
     * 这里提供一个默认的
     * 你可以覆盖这个bean
     */
    @Bean(ZDELAYED_CLUSTER_EXECUTOR)
    @ConditionalOnMissingBean(name = ZDELAYED_CLUSTER_EXECUTOR)
    public ExecutorService defaultZdelayedExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors() * 2,
                Runtime.getRuntime().availableProcessors() * 2,
                10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(Integer.MAX_VALUE),
                new ThreadFactoryBuilder()
                        .setDaemon(true) //不阻止JVM关闭
                        .setNameFormat("zdelayed-executor%d")
                        .build(),
                new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    @Bean
    public StandaloneDelayTaskExecutor standaloneDelayTaskExecutor(
            @Qualifier(ZDELAYED_STANDALONE_SCHEDULER) ScheduledExecutorService zdelayedScheduler) {
        return new StandaloneDelayTaskExecutor(zdelayedScheduler);
    }

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
            @Qualifier(ZDELAYED_CLUSTER_EXECUTOR) ExecutorService zdelayedExecutor
    ) {
        return new RedisClusterDelayTaskExecutor(redissonClient, zdelayedExecutor);
    }

    @Bean
    public MQDelayTaskExecutor mqDelayTaskExecutor() {
        return new MQDelayTaskExecutor();
    }

    @Bean
    public ZdelayedMethodInterceptor zdelayedMethodInterceptor(
            StandaloneDelayTaskExecutor standaloneDelayTaskExecutor,
            @Autowired(required = false) RedisClusterDelayTaskScheduler redisClusterDelayTaskExecutor,
            @Autowired(required = false) MQDelayTaskExecutor mqDelayTaskExecutor,
            DelayTimeResolver delayTimeResolver) {
        return new ZdelayedMethodInterceptor(standaloneDelayTaskExecutor, redisClusterDelayTaskExecutor, mqDelayTaskExecutor, delayTimeResolver);
    }

    @Bean
    public ZdelayedAnnotationAdvisor zdelayedAnnotationAdvisor(ZdelayedMethodInterceptor zdelayedMethodInterceptor) {
        return new ZdelayedAnnotationAdvisor(zdelayedMethodInterceptor);
    }

}
