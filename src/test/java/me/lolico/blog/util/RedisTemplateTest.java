package me.lolico.blog.util;

import me.lolico.blog.config.RedisMessageListenerAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;

/**
 * @author Lolico Li
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RedisTemplateTest {

    public static final String channel = "Channel:mail";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RedisMessageListenerAutoConfiguration.RedisService redisService;

    @Test
    void test1() {
        redisTemplate.opsForList().leftPush(channel, "534619360@qq.com");
        System.out.println(redisTemplate.opsForList().rightPop(channel));
        System.out.println(redisTemplate.opsForList().rightPop(channel, 0, TimeUnit.SECONDS));
    }

    @Test
    void test2() {
        redisService.publish(RedisMessageListenerAutoConfiguration.MAIL_CHANNEL, new RedisMessageListenerAutoConfiguration.EmailInfo("发送注册验证邮件", "534619360@qq.com"));
        redisService.publish(RedisMessageListenerAutoConfiguration.MAIL_CHANNEL, new RedisMessageListenerAutoConfiguration.EmailInfo("发送注册验证邮件", "805453921@qq.com"));
        redisService.publish(RedisMessageListenerAutoConfiguration.MAIL_CHANNEL, new RedisMessageListenerAutoConfiguration.EmailInfo("发送注册验证邮件", "554205726@qq.com"));
    }
}
