package com.github.pan.config;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.pan.redisson.limit.RedissonRateLimiter;
import com.github.pan.redisson.lock.RedissonDistributedLocker;

@Configuration
@ConditionalOnProperty(name = "pan.redisson.enabled", matchIfMissing = false)
public class RedissonAutoConfiguration {
	@Value("${pan.redisson.configname:'redisson-config.yml'}")
	private String configYmlName;

	/**
	 * 采用yml格式配置redisson参数
	 * 参考 https://github.com/redisson/redisson/wiki/2.-Configuration
	 * @return
	 * @throws IOException
	 */
	@Bean
	public RedissonClient initRedissonClient() throws IOException {
		Config config = Config.fromYAML(RedissonAutoConfiguration.class.getClassLoader().getResource(configYmlName));
		return Redisson.create(config);
	}
	
	@Bean
	public RedissonDistributedLocker getRedissonDistributedLocker(RedissonClient client) {
		return new RedissonDistributedLocker(client);
	}
	
	@Bean
	public RedissonRateLimiter getRedissonRateLimiter(RedissonClient client) {
		return new RedissonRateLimiter(client);
	}
}
