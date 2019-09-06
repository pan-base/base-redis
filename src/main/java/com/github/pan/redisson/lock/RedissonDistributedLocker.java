package com.github.pan.redisson.lock;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 分布式锁
 * 
 * @author huangfupan
 *
 */
public class RedissonDistributedLocker {

	private RedissonClient redissonClient;

	public RedissonDistributedLocker(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	/**
	 * 一直尝试获取锁直到成功加锁，锁失效时间为leaseTime,单位为毫秒
	 * 
	 * @param lockkey
	 * @param leaseTime
	 * @return
	 */
	public RLock lock(String lockkey, long leaseTime) {
		RLock lock = redissonClient.getLock(lockkey);
		lock.lock(leaseTime, TimeUnit.MILLISECONDS);
		return lock;
	}

	/**
	 * 尝试获取锁，直到waitTime仍未获得锁则加锁失败 锁失效时间为leaseTime 时间单位毫秒
	 * 
	 * @param lockkey
	 * @param leaseTime
	 * @param waitTime
	 * @return
	 */
	public boolean tryLock(String lockkey, long leaseTime, long waitTime) {
		RLock lock = redissonClient.getLock(lockkey);
		try {
			return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * 解锁，只能解当前线程加的锁
	 * 
	 * @param lockkey
	 */
	public void unlock(String lockkey) {
		RLock lock = redissonClient.getLock(lockkey);
		lock.unlock();
	}

	/**
	 * 解锁，只能解当前线程加的锁
	 * 
	 * @param lock
	 */
	public void unlock(RLock lock) {
		lock.unlock();
	}

}
