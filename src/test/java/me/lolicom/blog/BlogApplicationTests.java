package me.lolicom.blog;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

class BlogApplicationTests {

    @Test
    void contextLoads() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Instant instant = Instant.now();
        Duration duration = Duration.ofMinutes(30);
        System.out.println(Date.from(instant));
        System.out.println(Date.from(instant.plus(duration)));


        // System.out.println(localDateTime.toEpochSecond(ZoneOffset.UTC));
        // System.out.println(instant.getEpochSecond());
        //
        // System.out.println(instant.toEpochMilli());
        // Timestamp timestamp = Timestamp.from(instant);
        // System.out.println(timestamp.getTime()+"  "+instant.toEpochMilli());
        // System.out.println(timestamp + "  " + instant.atZone(ZoneOffset.systemDefault()));
        // System.out.println(System.currentTimeMillis());


    }

    @Test
    void test() {
        // HashCode admin = Hashing.sha256().hashString("admin", StandardCharsets.UTF_8);
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        long start = System.currentTimeMillis();
        String encode = passwordEncoder.encode("admin");
        long end = System.currentTimeMillis();
        System.out.println(encode + "[" + (end - start) + "]");
        start = System.currentTimeMillis();
        boolean matches = passwordEncoder.matches("admin", encode);
        end = System.currentTimeMillis();
        System.out.println(matches + "[" + (end - start) + "]");
    }

    @Test
    void timeUnit() {
        System.out.println(TimeUnit.MILLISECONDS.toNanos(1));
    }
}
