package me.lolico.blog.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.concurrent.CountDownLatch;

/**
 * @author Lolico Li
 */
@Configuration
public class RedisMessageListenerAutoConfiguration {

    public static final String MAIL_CHANNEL = "Channel:mail";

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListener messageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener, PatternTopic.of(MAIL_CHANNEL));
        return container;
    }

    @Bean
    MessageListener listenerAdapter(RedisReceiver redisReceiver) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(redisReceiver, "onMessage");
        adapter.setSerializer(RedisSerializer.json());
        return adapter;
    }

    @Bean
    CountDownLatch latch() {
        return new CountDownLatch(1); //指定了计数的次数 1
    }

    @Bean
    public RedisReceiver redisReceiver(CountDownLatch countDownLatch) {
        return new RedisReceiver(countDownLatch);
    }

    @Bean
    public RedisService redisService(StringRedisTemplate redisTemplate, CountDownLatch countDownLatch) {
        return new RedisService(redisTemplate, countDownLatch);
    }

    @Slf4j
    public static class RedisReceiver {

        private final CountDownLatch latch;

        @Autowired
        public RedisReceiver(CountDownLatch latch) {
            this.latch = latch;
        }

        public void onMessage(Object message) {
            log.info("我收到通道里你发的的消息了: {}", message);
            latch.countDown();
        }
    }

    @Slf4j
    public static class RedisService {

        private final StringRedisTemplate stringRedisTemplate;

        private final CountDownLatch latch;

        public RedisService(StringRedisTemplate stringRedisTemplate, CountDownLatch latch) {
            this.stringRedisTemplate = stringRedisTemplate;
            this.latch = latch;
        }

        public void publish(String channel, Object message) {
            try {
                log.info("开始发送信息：{}", message);
                stringRedisTemplate.convertAndSend(channel, message);
                latch.await();
            } catch (InterruptedException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailInfo {

        private String description;

        private String receiver;

    }

}
