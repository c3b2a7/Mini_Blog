package me.lolicom.blog;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

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
        HashCode admin = Hashing.sha256().hashString("admin", StandardCharsets.UTF_8);
    }
}
