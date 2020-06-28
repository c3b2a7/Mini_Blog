package me.lolico.blog.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * @author Lolico Li
 */
@Component
public class StreamConsumerRunner implements ApplicationRunner, DisposableBean {

    public static final String MAIL_CHANNEL = "channel:stream:mail";
    public static final String MAIL_GROUP = "group:mail";

    private final ThreadPoolTaskExecutor executor;
    private final RedisConnectionFactory redisConnectionFactory;
    private final StringRedisTemplate stringRedisTemplate;

    private StreamMessageListenerContainer<String, ObjectRecord<String, MailInfo>> container;

    public StreamConsumerRunner(ThreadPoolTaskExecutor executor, RedisConnectionFactory redisConnectionFactory, StringRedisTemplate stringRedisTemplate) {
        this.executor = executor;
        this.redisConnectionFactory = redisConnectionFactory;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public void run(ApplicationArguments args) {
        StreamMessageListenerContainerOptions<String, ObjectRecord<String, MailInfo>> options =
                StreamMessageListenerContainerOptions.builder()
                        .batchSize(10)
                        .executor(executor)
                        .pollTimeout(Duration.ZERO)
                        .targetType(MailInfo.class)
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, MailInfo>> container =
                StreamMessageListenerContainer.create(redisConnectionFactory, options);

        prepareChannelAndGroup(stringRedisTemplate.opsForStream(), MAIL_CHANNEL, MAIL_GROUP);

        container.receive(Consumer.from(MAIL_GROUP, "consumer-1"),
                StreamOffset.create(MAIL_CHANNEL, ReadOffset.lastConsumed()),
                new StreamMessageListener(stringRedisTemplate));

        this.container = container;
        this.container.start();
    }

    private void prepareChannelAndGroup(StreamOperations<String, ?, ?> ops, String channel, String group) {
        String status = "OK";
        try {
            StreamInfo.XInfoGroups groups = ops.groups(channel);
            if (groups.stream().noneMatch(xInfoGroup -> group.equals(xInfoGroup.groupName()))) {
                status = ops.createGroup(channel, group);
            }
        } catch (Exception exception) {
            RecordId initialRecord = ops.add(ObjectRecord.create(channel, "Initial Record"));
            Assert.notNull(initialRecord, "Cannot initial stream with key '" + channel + "'");
            status = ops.createGroup(channel, ReadOffset.from(initialRecord), group);
        } finally {
            Assert.isTrue("OK".equals(status), "Cannot create group with name '" + group + "'");
        }
    }

    @Override
    public void destroy() {
        this.container.stop();
    }

    public static class StreamMessageListener implements StreamListener<String, ObjectRecord<String, MailInfo>> {

        private final Logger logger = LoggerFactory.getLogger(StreamMessageListener.class);
        private final StringRedisTemplate stringRedisTemplate;

        public StreamMessageListener(StringRedisTemplate stringRedisTemplate) {
            this.stringRedisTemplate = stringRedisTemplate;
        }

        @Override
        public void onMessage(ObjectRecord<String, MailInfo> message) {
            RecordId id = message.getId();
            MailInfo messageValue = message.getValue();

            logger.info("消费stream:{}中的信息:{}, 消息id:{}", message.getStream(), messageValue, id);

            stringRedisTemplate.opsForStream().acknowledge(MAIL_GROUP, message);
        }
    }
}
