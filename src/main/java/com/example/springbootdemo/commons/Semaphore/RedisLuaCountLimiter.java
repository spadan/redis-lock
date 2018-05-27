package com.example.springbootdemo.commons.Semaphore;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RedisLuaCountLimiter extends AbstractDistributedSemaphore implements InitializingBean {

    private StringRedisTemplate redisTemplate;
    private RedisScript<Long> script;

    /**
     * 线程标识
     */
    private ThreadLocal<String> IDENTIFIER = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void afterPropertiesSet() {
        // lua脚本
        // 首先删除已过期的元素,然后判断元素数量是否达到限制,没有的话继续添加，代表获取信号成功，否则获取信号失败退出
        String scriptText = "redis.call('zremrangebyscore',KEYS[1],'-inf',ARGV[1]) if redis.call('zcard',KEYS[1]) < tonumber(ARGV[2]) " +
                "then  return redis.call('zadd',KEYS[1],ARGV[3],ARGV[4]) else return 0 end";
        script = new DefaultRedisScript<>(scriptText, Long.class);
    }

    @Override
    protected boolean acquirePermit(String sourceName, long millis, int allowedAccessTimes) {
        List<String> keyList = Lists.newArrayList(sourceName);
        // 当前时间
        long now = System.currentTimeMillis();
        // 当前时刻资源控制的开始时间，如限制2分钟访问1次，则2分钟前的时刻即为资源控制起始时间
        long beginTime = now - millis;
        Object[] argArray = new Object[]{beginTime + "", allowedAccessTimes + "", now + "", IDENTIFIER.get()};
        return redisTemplate.execute(script, keyList, argArray) == 1L;
    }

    @Override
    public boolean releasePermit(String sourceName) {
        return redisTemplate.opsForZSet().remove(sourceName, IDENTIFIER.get()) == 1L;
    }
}
