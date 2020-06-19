package me.lolico.blog.util;

import me.lolico.blog.config.prop.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lolico
 */
public class SystemSetting implements InitializingBean {

    private static Map<String, String> map;

    static {
        map = new HashMap<>();
    }

    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(SystemSetting.class);

    private final SystemProperties properties;

    public SystemSetting(JdbcTemplate jdbcTemplate, SystemProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    public static String getValue(String key) {
        return map.get(key);
    }

    public static String getValue(String key, String defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    /**
     * filling map
     */
    @Override
    public void afterPropertiesSet() {
        String tableName = properties.getTableName();
        Assert.notNull(tableName, "table name must be specified");
        Integer keyColumnIndex = properties.getKeyColumnIndex();
        Assert.notNull(keyColumnIndex, "key column index must be specified");
        Integer valueColumnIndex = properties.getValueColumnIndex();
        Assert.notNull(valueColumnIndex, "value column index must be specified");
        try {
            jdbcTemplate.query(" select * from " + tableName, rs -> {
                while (rs.next()) {
                    map.put(rs.getString(keyColumnIndex), rs.getString(valueColumnIndex));
                }
            });
            map = Collections.unmodifiableMap(map);
        } catch (Throwable t) {
            logger.warn(t.getMessage());
        }
    }

}
