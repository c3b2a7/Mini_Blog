package me.lolico.blog.lang.aspect;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import me.lolico.blog.lang.annotation.Limit;
import me.lolico.blog.util.RequestUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author Lolico Li
 */
@Aspect
@Component
public class LimitAspect {
    private final static String SEPARATOR = "::";
    private final static Logger logger = LoggerFactory.getLogger(LimitAspect.class);
    private final StringRedisTemplate redisTemplate;

    public LimitAspect(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Pointcut("@annotation(me.lolico.blog.lang.annotation.Limit)")
    public void pointcut() {
    }

    @Before("pointcut()&&@annotation(limit)")
    public void doBefore(JoinPoint joinPoint, Limit limit) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String key = generateKey(limit, request, method);
        long timeout = limit.timeout();
        int maxLimit = limit.maxLimit();
        TimeUnit timeUnit = limit.timeUnit();
        // redisTemplate.opsForValue()
        RedisScript<Number> redisScript = new DefaultRedisScript<>(buildLuaScript(), Number.class);
        Number number = redisTemplate.execute(redisScript, ImmutableList.of(key), maxLimit, timeUnit.toSeconds(timeout));
        if (number == null || number.intValue() > maxLimit) {
            logger.info("The {}th attempt to access failed for [key={}, maxLimit={}, timeout={}ms]", number, key, maxLimit, timeUnit.toMillis(timeout));
            throw new RuntimeException("访问频繁");
        }
    }

    private String generateKey(Limit limit, HttpServletRequest request, Method method) {
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        String key = limit.key();
        if (StringUtils.isEmpty(key)) {
            switch (limit.limitLevel()) {
                case Method:
                    key = method.getName();
                    break;
                case User:
                    key = RequestUtils.getIp(request);
                    break;
            }
        }
        return Joiner.on(SEPARATOR).join(requestMethod, requestURI, key);
    }

    public String buildLuaScript() {
        String lua = "local c\n" +
                "c = redis.call('get',KEYS[1])\n" +
                "if c and tonumber(c) > tonumber(ARGV[1]) then\n" +
                "return tonumber(c);\n" +
                "end\n" +
                "c = redis.call('incr',KEYS[1])\n" +
                "if tonumber(c) == 1 then\n" +
                "redis.call('expire',KEYS[1],ARGV[2])\n" +
                "end\n" +
                "return c;";
        return lua;
    }

}
