package com.github.pan.redisson.limit;

import java.util.Arrays;
import java.util.UUID;

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
		String uuid = UUID.randomUUID().toString();
		RFuture<Integer> result = redisson.getCommandExecutor().evalWriteAsync(key, LongCodec.INSTANCE,
				RedisCommands.EVAL_INTEGER,
				"redis.call('zadd',KEYS[1],ARGV[1],ARGV[2]) " + "redis.call('zremrangeByScore',KEYS[1],0,ARGV[3]) "
						+ "local counter = redis.call('zcard',KEYS[1]) " + "redis.call('expire',KEYS[1],ARGV[4]) "
						+ "return counter",
				Arrays.<Object>asList("ip:" + key), nowTs, uuid, nowTs - period * 1000, period + 1);
		int count = redisson.getCommandExecutor().get(result);
		return count <= maxcount;
	}
}
