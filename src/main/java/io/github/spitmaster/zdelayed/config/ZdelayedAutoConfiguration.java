package io.github.spitmaster.zdelayed.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * zdelayed的默认配置
 *
 * @author zhouyijin
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "zdelayed.enabled", matchIfMissing = true)
public class ZdelayedAutoConfiguration {
}
