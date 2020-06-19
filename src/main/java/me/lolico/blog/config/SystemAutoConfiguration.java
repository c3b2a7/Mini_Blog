package me.lolico.blog.config;

import me.lolico.blog.config.prop.SystemProperties;
import me.lolico.blog.util.SystemSetting;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lolico li
 */
@Configuration
@AutoConfigureAfter({
        DataSourceAutoConfiguration.class,
        JdbcTemplateAutoConfiguration.class
})
@ConditionalOnProperty(prefix = "me.lolico.blog.sys", name = "enabled")
@EnableConfigurationProperties(SystemProperties.class)
public class SystemAutoConfiguration {

    @Bean
    public SystemSetting systemSetting(JdbcTemplate jdbcTemplate, SystemProperties properties) {
        return new SystemSetting(jdbcTemplate, properties);
    }
}
