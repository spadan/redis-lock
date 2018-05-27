package com.example.springbootdemo.commons.lock;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

/**
 * 基于redis的分布式锁
 *
 * @author xiongLiang
 * @date 2018/5/24 14:17
 */
@Component
public class RedisLuaDistributedLock extends AbstractDistributedLock implements InitializingBean {

    /**
     * 锁超时时间60s，超过该时间还未释放锁将自动释放
     */
    private static final String LOCK_TIMEOUT = "60";

    private StringRedisTemplate redisTemplate;
    private RedisScript<Long> lockScript;
    private RedisScript<Long> releaseScript;

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 线程标识
     */
    private ThreadLocal<String> IDENTIFIER = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

    @Override
    public void afterPropertiesSet() {
        final String lockScriptText = "if redis.call('setNX',KEYS[1],ARGV[1])==1 then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end";
        lockScript = new DefaultRedisScript<>(lockScriptText, Long.class);
        final String releaseScriptText = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        releaseScript = new DefaultRedisScript<>(releaseScriptText, Long.class);
    }

    @Override
    protected boolean acquireLock(String lockName) {
        Long result = redisTemplate.execute(lockScript,
                Collections.singletonList(lockName), IDENTIFIER.get(), LOCK_TIMEOUT);
        return result == 1L;
    }

    @Override
    protected boolean releaseLock(String lockName) {
        Long result = redisTemplate.execute(releaseScript,
                Collections.singletonList(lockName), IDENTIFIER.get());
        return result == 1L;
    }
}
