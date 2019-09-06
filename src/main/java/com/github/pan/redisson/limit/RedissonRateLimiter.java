package com.github.pan.redisson.limit;

import java.util.Arrays;

import org.redisson.Redisson;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.protocol.RedisCommands;

public class RedissonRateLimiter {

	private RedissonClient redissonClient;

	public RedissonRateLimiter(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	public boolean acquire(String key, int period, int maxcount) {
		Redisson redisson = (Redisson) redissonClient;
		long nowTs = System.currentTimeMillis();
		RFuture<Boolean> result = redisson.getCommandExecutor().evalWriteAsync(key, LongCodec.INSTANCE,
				RedisCommands.EVAL_BOOLEAN,
				"redis.call('zadd',KEYS[1],ARGV[1],ARGV[1]) " + "redis.call('zremrangeByScore',0,ARGV[2]) "
						+ "local counter = redis.call('zcard',KEYS[1] )" + "redis.call('expire',KEYS[1]),ARGV[3] "
						+ "return counter <= ARGV[4]",
				Arrays.<Object>asList("ip:" + key),
				Arrays.<Object>asList(nowTs, nowTs - period * 1000, period + 1, maxcount));
		return redisson.getCommandExecutor().get(result);
	}
}
