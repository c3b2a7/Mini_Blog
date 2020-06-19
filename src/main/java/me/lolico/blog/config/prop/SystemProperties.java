package me.lolico.blog.config.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Lolico li
 */
@Getter
@Setter
@ConfigurationProperties("me.lolico.blog.sys")
public class SystemProperties {
    private boolean enabled;
    private String tableName;
    private Integer keyColumnIndex;
    private Integer valueColumnIndex;
}
