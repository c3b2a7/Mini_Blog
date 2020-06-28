package me.lolico.blog.util;

import me.lolico.blog.mq.MailInfo;
import me.lolico.blog.mq.StreamConsumerRunner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lolico Li
 */
@SpringBootTest
public class RedisTemplateTest {

    public static final String channel = StreamConsumerRunner.MAIL_CHANNEL;
    public static final String group = StreamConsumerRunner.MAIL_GROUP;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    void listmq() {
        stringRedisTemplate.opsForList().leftPush(channel, "534619360@qq.com");
        System.out.println(stringRedisTemplate.opsForList().rightPop(channel));
        System.out.println(stringRedisTemplate.opsForList().rightPop(channel, 0, TimeUnit.SECONDS));
    }

    @Test
    void addStream() {
        MailInfo mailInfo = new MailInfo("554205726@qq.com", "sendmail");
        ObjectRecord<String, MailInfo> record = StreamRecords.objectBacked(mailInfo).withStreamKey(channel);

        RecordId recordId = stringRedisTemplate.opsForStream().add(record);
    }


    @Test
    void redisTemplate() {
        MailInfo mailInfo = new MailInfo("sendmail", "554205726@qq.com");
        MailInfo message = new MailInfo("发送注册验证邮件", "534619360@qq.com");

        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        opsForValue.set("test:value", "534619360@qq.com");

        ListOperations<String, String> opsForList = stringRedisTemplate.opsForList();
        opsForList.rightPush("test:list", "534619360@qq.com");
        opsForList.rightPush("test:list", "805453921@qq.com");

        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();

        opsForHash.put("test:hash", String.valueOf(mailInfo.hashCode()), mailInfo);
        opsForHash.put("test:hash", String.valueOf(message.hashCode()), message);

        SetOperations<String, String> opsForSet = stringRedisTemplate.opsForSet();
        opsForHash.put("test:set", String.valueOf(mailInfo.hashCode()), mailInfo);
        opsForHash.put("test:set", String.valueOf(message.hashCode()), message);

    }

    @Test
    void test5() {
        MailInfo mailInfo = new MailInfo("554205726@qq.com", "sendmail");
        ObjectRecord<String, MailInfo> record = StreamRecords.objectBacked(mailInfo).withStreamKey("test:stream");
        stringRedisTemplate.opsForStream().add(record);

        mailInfo.setReceiver("534619360@qq.com");
        stringRedisTemplate.opsForStream().add(record);

    }

    @Test
    void test6() {
        MailInfo mailInfo = new MailInfo("554205726@qq.com", "sendmail");
        stringRedisTemplate.setHashValueSerializer(RedisSerializer.string());

        StreamOperations<String, Object, Object> ops = stringRedisTemplate.opsForStream();

        RecordId recordId = ops.add(Record.of(mailInfo).withStreamKey(channel));

        String group = ops.createGroup(channel, ReadOffset.lastConsumed(), RedisTemplateTest.group);

        // mailInfo.setReceiver("534619360");
        // RecordId id = ops.add(Record.of(mailInfo).withStreamKey(channel));


        List<ObjectRecord<String, MailInfo>> list = ops.read(MailInfo.class,
                Consumer.from(RedisTemplateTest.group, "consumer1"),
                StreamReadOptions.empty().block(Duration.ZERO),
                StreamOffset.create(channel, ReadOffset.lastConsumed()));

        System.out.println(list);
    }

    @Test
    void test7() {
        MailInfo mailInfo = new MailInfo("534619360@qq.com", "send mail");

        StreamOperations<String, Object, Object> ops = stringRedisTemplate.opsForStream();
        RecordId recordId = ops.add(Record.of(mailInfo).withStreamKey(channel));

    }

    @Test
    void test8() {
        MailInfo mailInfo = new MailInfo("534619360@qq.com", "send mail");
        stringRedisTemplate.opsForStream().add(Record.of(mailInfo).withStreamKey("test"));
        stringRedisTemplate.opsForStream(new Jackson2HashMapper(true)).add(Record.of(mailInfo).withStreamKey("test"));
        stringRedisTemplate.opsForStream(new Jackson2HashMapper(false)).add(Record.of(mailInfo).withStreamKey("test"));
    }

    @Test
    void info() {
        StreamOperations<String, Object, Object> ops = stringRedisTemplate.opsForStream();
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

    @Test
    void test() {
        MailInfo mailInfo = new MailInfo("534619360@qq.com", "send mail");
        stringRedisTemplate.opsForStream().add(Record.of(mailInfo).withStreamKey(channel));
    }
}
