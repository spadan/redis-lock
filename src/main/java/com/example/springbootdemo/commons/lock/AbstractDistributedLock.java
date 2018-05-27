package com.example.springbootdemo.commons.lock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * 基于redis的分布式锁
 *
 * @author xiongLiang
 * @date 2018/5/24 14:17
 */

public abstract class AbstractDistributedLock implements DistributedLock, InitializingBean {

    @Override
    public void lock(String lockName) {
        Assert.isTrue(StringUtils.isNotBlank(lockName), "lock name can not be null");
        while (true) {
            if (acquireLock(getKey(lockName))) {
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean tryLock(String lockName) {
        Assert.isTrue(StringUtils.isNotBlank(lockName), "lock name can not be null");
        return acquireLock(getKey(lockName));
    }

    @Override
    public boolean tryLock(String lockName, long time, TimeUnit unit) {
        Assert.isTrue(StringUtils.isNotBlank(lockName), "lock name can not be null");
        Assert.isTrue(time > 0, "timeout should be positive");
        Assert.notNull(unit, "unit can not be null");
        long begin = System.currentTimeMillis();
        final long end = begin + unit.toMillis(time);
        while (begin < end) {
            if (acquireLock(getKey(lockName))) {
                return true;
            }
            try {
                // 100毫秒后重试
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            begin = System.currentTimeMillis();
        }
        return false;
    }

    @Override
    public boolean unlock(String lockName) {
        Assert.isTrue(StringUtils.isNotBlank(lockName), "lock name can not be null");
        return releaseLock(getKey(lockName));
    }

    /**
     * 统一分布式锁名称前缀
     *
     * @param lockName 原始锁名称
     * @return 在redis中的锁名称
     */
    private String getKey(String lockName) {
        return "distributedLock:" + lockName;
    }

    protected abstract boolean acquireLock(String lockName);

    protected abstract boolean releaseLock(String lockName);


}
