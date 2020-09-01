package me.lolico.blog.lang.aspect;

import com.google.common.collect.ImmutableList;
import me.lolico.blog.lang.SpelAnnotationResolver;
import me.lolico.blog.lang.annotation.DistributedLock;
import me.lolico.blog.web.vo.ApiResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Lolico Li
 */
@Aspect
@Component
public final class DistributedLockAspect {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLockAspect.class);
    private final StringRedisTemplate redisTemplate;
    private BeanFactory beanFactory;

    public DistributedLockAspect(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(lock)")
    private void pointcut(DistributedLock lock) {

    }

    @Around(value = "pointcut(lock)", argNames = "pjp,lock")
    public Object around(ProceedingJoinPoint pjp, DistributedLock lock) throws Throwable {
        String key = generateKey(pjp, lock);
        String value = acquireLock(key, lock.timeout(), lock.timeUnit());
        if (!StringUtils.hasLength(value)) {
            // Return directly may cause ClassCastException, should throw an exception
            // include the status code and message here and then handle it better in
            // exception handler of controller advice.
            return ApiResult.status(HttpStatus.LOCKED, "interface is locked");
        }
        // acquire Lock successful
        try {
            return pjp.proceed();
        } finally {
            // release lock
            releaseLock(key, value);
        }

    }

    private String generateKey(JoinPoint jp, DistributedLock lock) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        String key = lock.key();
        if (!StringUtils.isEmpty(key)) {
            key = SpelAnnotationResolver.getValue(key, jp, String.class);
        }
        if (StringUtils.isEmpty(key)) {
            key = method.getName();
        }
        return key;
    }

    private String acquireLock(String key, long timeout, TimeUnit timeUnit) {
        try {
            // It is better to use the snowflake algorithm to generate a globally unique id
            String value = UUID.randomUUID().toString();
            Boolean lockStat = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
            if (lockStat == null || !lockStat) {
                // acquire Lock failed
                return null;
            }
            return value;
        } catch (Exception e) {
            // acquire Lock failed because of an exception occurred, log it
            logger.error("获取分布式锁失败，key={}", key, e);
            return null;
        }
    }

    private void releaseLock(String key, String value) {
        // just try to release this lock and the result we dont care
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>(script, Boolean.class);
            Boolean unLockStat = redisTemplate.execute(redisScript, ImmutableList.of(key), value);
            if (unLockStat == null || !unLockStat) {
                logger.error("释放分布式锁失败，key={}，已自动超时，其他线程可能已经重新获取锁", key);
            }
        } catch (Exception e) {
            logger.error("释放分布式锁失败，key={}", key, e);
        }
    }
}
