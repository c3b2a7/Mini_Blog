package me.lolico.blog.lang;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * @author lolico
 */
@Slf4j
public class DynamicDataSourceContextHolder {
    private final static ThreadLocal<String> KEY = new ThreadLocal<>();
    private static Map<Object, Object> targetDataSourceMap;
    private static String defaultKey;

    public static void setDataSourceMap(Map<Object, Object> targetDataSourceMap) {
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
            log.debug("设置defaultKey:[{}]", defaultKey);
        }
        DynamicDataSourceContextHolder.defaultKey = defaultKey;
    }
}
