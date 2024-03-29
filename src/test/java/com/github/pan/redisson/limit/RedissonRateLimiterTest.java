package com.github.pan.redisson.limit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.pan.config.RedisBaseTestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisBaseTestApplication.class)
public class RedissonRateLimiterTest {

	@Autowired
	private RedissonRateLimiter redissonRateLimiter;

	@Test
	public void test() {
		for (int i = 0; i < 10; i++) {
			boolean valid = redissonRateLimiter.acquire("a01", 1, 2);
			System.out.println(String.format("%s:%s", i,valid));
		}

	}

}
