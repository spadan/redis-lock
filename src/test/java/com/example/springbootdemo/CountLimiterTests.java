package com.example.springbootdemo;

import com.example.springbootdemo.commons.Semaphore.DistributedSemaphore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CountLimiterTests {

    @Autowired
    private DistributedSemaphore countLimiter;

    @Test
    public void contextLoads() {
    }


    @Test
    public void testCountLimiter2() {
        if (countLimiter.tryAcquire("daxiong", 1, TimeUnit.MINUTES, 1)) {
            System.out.println(Thread.currentThread().getName() + "获取资源成功11111111111111111111111111");
        } else {
            System.out.println(Thread.currentThread().getName() + "获取资源失败");
        }
    }


    @Test
    public void testCountLimiter() throws Exception {
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
                if (countLimiter.tryAcquire("panda", 1, TimeUnit.MINUTES, 5)) {
                    System.out.println(Thread.currentThread().getName() + "获取资源成功11111111111111111111111111");
                } else {
                    System.out.println(Thread.currentThread().getName() + "获取资源失败");
                }

                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                if (countLimiter.release("panda")) {
                    System.out.println(Thread.currentThread().getName() + "释放资源成功11111111111111111111111111");
                } else {
                    System.out.println(Thread.currentThread().getName() + "释放资源失败");
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("over");
    }


}
