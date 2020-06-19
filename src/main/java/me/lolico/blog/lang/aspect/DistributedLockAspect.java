package me.lolico.blog.lang.aspect;

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
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
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
        String key = getKey(pjp, lock);
        String value = getLock(key, lock.timeout(), lock.timeUnit());
        if (!StringUtils.hasLength(value)) {
            // 获取锁失败
            return ApiResult.status(HttpStatus.LOCKED, "请勿频繁操作");
            // return ResponseEntity.status(HttpStatus.LOCKED); //HttpStatus.NO_CONTENT ErrorCode
        }
        // 获取锁成功
        try {
            return pjp.proceed();
            // } catch (Throwable throwable) {
            //     return ApiResult.status(HttpStatus.INTERNAL_SERVER_ERROR, "服务器出错");
            // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            // 释放锁
            releaseLock(key, value);
        }

    }

    private String getKey(JoinPoint jp, DistributedLock lock) {
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

    private String getLock(String key, long timeout, TimeUnit timeUnit) {
        try {
            String value = UUID.randomUUID().toString();
            Boolean lockStat = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                return connection.set(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8),
                        Expiration.from(timeout, timeUnit), RedisStringCommands.SetOption.SET_IF_ABSENT);
            });
            if (lockStat == null || !lockStat) {
                // 获取锁失败
                return null;
            }
            return value;
        } catch (Exception e) {
            logger.error("获取分布式锁失败，key={}", key, e);
            return null;
        }
    }

    private void releaseLock(String key, String value) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Boolean unLockStat = redisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.eval(script.getBytes(), ReturnType.BOOLEAN, 1,
                            key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8)));
            if (unLockStat == null || !unLockStat) {
                logger.error("释放分布式锁失败，key={}，已自动超时，其他线程可能已经重新获取锁", key);
            }
        } catch (Exception e) {
            logger.error("释放分布式锁失败，key={}", key, e);
        }
    }
}
