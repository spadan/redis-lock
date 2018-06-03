package com.example.springbootdemo.commons.Semaphore;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public abstract class AbstractDistributedSemaphore implements DistributedSemaphore, InitializingBean {


    @Override
    public void acquire(String sourceName, long timeRange, TimeUnit unit, int allowedAccessTimes) {
        Assert.isTrue(StringUtils.isNotBlank(sourceName), "source name can not be null");
        Assert.isTrue(timeRange > 0, "timeRange should be positive");
        Assert.notNull(unit, "unit can not be null");
        Assert.isTrue(allowedAccessTimes > 0, "allowed access time must be positive");
        for (; ; ) {
            if (acquirePermit(getKey(sourceName), unit.toMillis(timeRange), allowedAccessTimes)) {
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
    public boolean tryAcquire(String sourceName, long timeRange, TimeUnit unit, int allowedAccessTimes) {
        Assert.isTrue(StringUtils.isNotBlank(sourceName), "source name can not be null");
        Assert.isTrue(timeRange > 0, "timeRange should be positive");
        Assert.notNull(unit, "unit can not be null");
        Assert.isTrue(allowedAccessTimes > 0, "allowed access time must be positive");
        return acquirePermit(getKey(sourceName), unit.toMillis(timeRange), allowedAccessTimes);
    }

    @Override
    public boolean tryAcquire(String sourceName, long timeRange, TimeUnit unit, int allowedAccessTimes, int seconds) {
        Assert.isTrue(StringUtils.isNotBlank(sourceName), "source name can not be null");
        Assert.isTrue(timeRange > 0, "timeout should be positive");
        Assert.notNull(unit, "unit can not be null");
        Assert.isTrue(allowedAccessTimes > 0, "allowed access time must be positive");
        Assert.isTrue(seconds > 0, "seconds must be positive");
        long begin = System.currentTimeMillis();
        final long end = begin + TimeUnit.SECONDS.toMillis(timeRange);
        while (begin < end) {
            if (acquirePermit(getKey(sourceName), unit.toMillis(timeRange), allowedAccessTimes)) {
                return true;
            }
            try {
                // 10毫秒后重试
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            begin = System.currentTimeMillis();
        }
        return false;
    }

    @Override
    public boolean release(String sourceName) {
        Assert.isTrue(StringUtils.isNotBlank(sourceName), "source name can not be null");
        return releasePermit(getKey(sourceName));
    }

    private String getKey(String sourceName) {
        return "semaphore:" + sourceName;
    }

    protected abstract boolean acquirePermit(String sourceName, long millis, int allowedAccessTimes);

    protected abstract boolean releasePermit(String sourceName);

}
