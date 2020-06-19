package me.lolico.blog.util;

import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class RedisLockUtils {

    private static final String LOCK_VALUE = "1";
    private static final String LOCK_HEARD = "lock:";
    private static final DefaultRedisScript<String> UNLOCK_LUA;

    static {
        //构造脚本
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        String sb =
                "if redis.call(\"get\",KEYS[1]) == ARGV[1] then " +
                        "    return redis.call(\"del\",KEYS[1]) " +
                        "else " +
                        "    return 0 " +
                        "end ";
        script.setScriptText(sb);
        UNLOCK_LUA = script;
    }

    private final StringRedisTemplate redisTemplate;

    public RedisLockUtils(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean setLock(String key, String value, long expire) {
        if (StringUtils.isBlank(value)) {
            value = LOCK_VALUE;
        }
        return setNx(buildKey(key), value, expire);
    }

    public boolean setLock(String key, long expire) {
        return setNx(key, null, expire);
    }

    public boolean setLock(String key, long expire, long waitTime) {
        return setLock(key, null, expire, waitTime);
    }

    public boolean setLock(String key, String value, long expire, long waitTime) {
        if (waitTime == 0L) {
            return setLock(key, value, expire);
        }
        long start = System.currentTimeMillis();
        while (true) {
            //检测是否超时
            if (System.currentTimeMillis() - start > waitTime) {
                return false;
            }
            if (setLock(key, value, expire)) {
                return Boolean.TRUE;
            }
        }
    }

    public Optional<String> getLockValue(String key) {
        String o = redisTemplate.opsForValue().get(buildKey(key));
        return Optional.ofNullable(o);
    }

    public boolean releaseLock(String key) {
        return releaseLock(key, LOCK_VALUE);
    }

    public boolean releaseLock(String key, String value) {
        try {
            Object execute = redisTemplate.execute(
                    (RedisConnection connection) -> connection.eval(
                            UNLOCK_LUA.getScriptAsString().getBytes(),
                            ReturnType.INTEGER,
                            1,
                            buildKey(key).getBytes(),
                            value.getBytes())
            );
            return execute != null && execute.equals(1L);
        } catch (Exception e) {
            log.error("release lock occurred an exception", e);
        }
        return false;
    }

    /**
     * @param key         key值
     * @param value       value值
     * @param expiredTime 毫秒
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean setNx(String key, String value, long expiredTime) {
        Boolean resultBoolean = null;
        try {
            resultBoolean = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                String redisResult = "";
                @SuppressWarnings("unchecked")
                RedisSerializer<String> stringRedisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
                //lettuce连接包下序列化键值，否知无法用默认的ByteArrayCodec解析
                byte[] keyByte = stringRedisSerializer.serialize(key);
                byte[] valueByte = stringRedisSerializer.serialize(value);
                // lettuce连接包下 redis 单机模式setnx
                if (nativeConnection instanceof RedisAsyncCommands) {
                    RedisAsyncCommands commands = (RedisAsyncCommands) nativeConnection;
                    //同步方法执行、setnx禁止异步
                    redisResult = commands.getStatefulConnection().sync()
                            .set(keyByte, valueByte, SetArgs.Builder.nx().px(expiredTime));
                }
                // lettuce连接包下 redis 集群模式setnx
                if (nativeConnection instanceof RedisAdvancedClusterAsyncCommands) {
                    RedisAdvancedClusterAsyncCommands clusterAsyncCommands = (RedisAdvancedClusterAsyncCommands) nativeConnection;
                    redisResult = clusterAsyncCommands.getStatefulConnection().sync()
                            .set(keyByte, keyByte, SetArgs.Builder.nx().px(expiredTime));
                }
                //返回加锁结果
                return "OK".equalsIgnoreCase(redisResult);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBoolean != null && resultBoolean;
    }

    private String buildKey(String key) {
        return LOCK_HEARD + key;
    }
}