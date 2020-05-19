package com.elite.webdata.redisson.dao;

import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * redis dao
 */
@Resource
public class RedisDao {

    @Autowired
    private RedissonClient redissonClient;

    public String getString(String key) {
        RBucket<Object> result = this.redissonClient.getBucket(key);
        return result.get().toString();
    }

    public void setString(String key, Object value) {
        RBucket<Object> result = this.redissonClient.getBucket(key);
        if (!result.isExists()) {
            result.set(value, 5, TimeUnit.MINUTES);
        }
    }

    public boolean hasString(String key) {
        RBucket<Object> result = this.redissonClient.getBucket(key);
        if (result.isExists()) {
            return true;
        } else {
            return false;
        }
    }

    public long incr(String key, long delta) {
        return this.redissonClient.getAtomicLong(key).addAndGet(delta);
    }

    /**
     * 加锁
     */
    public void lock() {
        RLock lock = redissonClient.getLock("aa");
        RCountDownLatch countDown = redissonClient.getCountDownLatch("aa");
        countDown.trySetCount(1);
        try {
            countDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RCountDownLatch latch = redissonClient.getCountDownLatch("countDownLatchName");
        latch.countDown();
        RReadWriteLock rwlock = redissonClient.getReadWriteLock("lockName");
        rwlock.readLock().lock();
        rwlock.writeLock().lock();
        rwlock.readLock().lock(10, TimeUnit.SECONDS);
        rwlock.writeLock().lock(10, TimeUnit.SECONDS);
        try {
            boolean res = rwlock.readLock().tryLock(100, 10, TimeUnit.SECONDS);
            boolean res1 = rwlock.writeLock().tryLock(100, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
