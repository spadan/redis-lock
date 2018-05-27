package com.example.springbootdemo.commons.lock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {

    /**
     * 获取分布式锁
     * 不断尝试获取分布式锁，直至获取成功，否则线程会一直阻塞
     *
     * @param lockName 锁名称
     */
    void lock(String lockName);

    /**
     * 获取分布式锁
     * 非阻塞式，获取成功立刻返回true,获取失败则立即返回false
     *
     * @param lockName 锁名称
     * @return 获取锁成功返回true，获取锁失败返回false
     */
    boolean tryLock(String lockName);

    /**
     * 获取分布式锁
     * 获取成功立刻返回true，否则不断重试直至获取锁成功或等待时间耗尽
     *
     * @param lockName 锁名称
     * @param time     最长等待时间
     * @param unit     时间单位，秒、毫秒等
     * @return 获取锁成功返回true，获取锁失败返回false
     */
    boolean tryLock(String lockName, long time, TimeUnit unit);

    /**
     * 释放分布式锁
     *
     * @param lockName 锁名称
     * @return 释放成功true, 失败false
     */
    boolean unlock(String lockName);

}
