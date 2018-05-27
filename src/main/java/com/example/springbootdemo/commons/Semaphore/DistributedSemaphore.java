package com.example.springbootdemo.commons.Semaphore;

import java.util.concurrent.TimeUnit;

/**
 * 计数控制器，用于实现规定时间内只能执行多少次等任务
 */
public interface DistributedSemaphore {

    void acquire(String sourceName, long time, TimeUnit unit, int allowedAccessTimes);

    boolean tryAcquire(String sourceName, long time, TimeUnit unit, int allowedAccessTimes);

    boolean tryAcquire(String sourceName, long time, TimeUnit unit, int allowedAccessTimes, int seconds);

    public boolean release(String sourceName);

}
