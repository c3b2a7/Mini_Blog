package me.lolico.blog.lang;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author lolico
 */
@Slf4j
public class DynamicDataSourceContextHolder {
    private final static ThreadLocal<String> KEY = new ThreadLocal<>();
    private static Map<Object, Object> targetDataSourceMap = Collections.emptyMap();
    private static String defaultKey;

    public static void setDataSourceMap(Map<Object, Object> targetDataSourceMap) {
        Assert.notNull(targetDataSourceMap, "targetDataSourceMap must not be null!");
        DynamicDataSourceContextHolder.targetDataSourceMap = targetDataSourceMap;
    }

    public static String getKey() {
        return Optional.ofNullable(DynamicDataSourceContextHolder.KEY.get())
                .orElseGet(() -> DynamicDataSourceContextHolder.defaultKey);
    }

    public static void setKey(String key) {
        DynamicDataSourceContextHolder.KEY.set(targetDataSourceMap.containsKey(key) ?
                key : DynamicDataSourceContextHolder.defaultKey);
    }

    public static void remove() {
        DynamicDataSourceContextHolder.KEY.remove();
    }

    public static void setDefaultKey(String defaultKey) {
        if (log.isDebugEnabled()) {
            log.debug("Set default Key: '{}'", defaultKey);
        }
        DynamicDataSourceContextHolder.defaultKey = defaultKey;
    }
}
