package com.example.springbootdemo;

import com.example.springbootdemo.commons.Semaphore.DistributedSemaphore;
import com.example.springbootdemo.commons.lock.DistributedLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDemoApplicationTests {

    @Autowired
    private DistributedLock lock;

    @Autowired
    private DistributedSemaphore countLimiter;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testRedis() {
        if (lock.tryLock("xl")) {
            System.out.println("获取锁成功");
        } else {
            System.out.println("获取锁失败");
        }
        if (lock.unlock("xl")) {
            System.out.println("解锁成功");
        } else {
            System.out.println("解锁失败");

        }
    }

    @Test
    public void testRedis2() throws Exception {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(50);
        final CountDownLatch countDownLatch = new CountDownLatch(50);
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 50; i++) {
            executorService.submit(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                if (lock.tryLock("xl")) {
                    System.out.println(Thread.currentThread().getName() + "获取锁成功11111111111111111111111111");
                } else {
                    System.out.println(Thread.currentThread().getName() + "获取锁失败");
                }
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                if (lock.unlock("xl")) {
                    System.out.println(Thread.currentThread().getName() + "解锁成功222222222222222222222222222222");
                } else {
                    System.out.println(Thread.currentThread().getName() + "解锁失败");
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("over");
    }

    @Test
    public void testRedis3() {
        System.out.println("尝试获取锁");
        try {
            lock.lock("xl");
            System.out.println("我获得锁啦");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock("xl");
        }
    }


}
