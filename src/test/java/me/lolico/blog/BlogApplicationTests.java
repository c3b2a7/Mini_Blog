package me.lolico.blog;

import me.lolico.blog.security.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        String encode = passwordEncoder.encode("lolico");
        long end = System.currentTimeMillis();
        System.out.println(encode + "[" + (end - start) + "]");
        start = System.currentTimeMillis();
        boolean matches = passwordEncoder.matches("lolico", encode);
        end = System.currentTimeMillis();
        System.out.println(matches + "[" + (end - start) + "]");
    }

    @Test
    void timeUnit() {
        System.out.println(TimeUnit.MILLISECONDS.toNanos(1));
    }

    @Test
    void authority() {
        List<GrantedAuthority> list = AuthorityUtils.commaSeparatedStringToAuthorityList("USER,ADMIN");
        System.out.println(list);
        list.add(null);
        list.add(new SimpleGrantedAuthority("ADMIN"));
        System.out.println(list);

        String collect = list.stream()
                .distinct().filter(Objects::nonNull)
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(Constants.AUTHORITIES_CLAIMS_VALUE_SEPARATOR));

        System.out.println(collect);

    }

    @Test
    void name() {
        String email = "534619360@qq.com";
        String code = generateMailConfirmationCode(email);
        System.out.println(confirm(code));
        System.out.println(email.substring(0, email.indexOf("@")));

    }

    public String generateMailConfirmationCode(String email) {
        String digest = DigestUtils.md5DigestAsHex(email.getBytes());
        return Base64Utils.encodeToUrlSafeString((email.replace(".", "+") + "." + digest).getBytes());
    }

    public boolean confirm(String code) {
        byte[] bytes = Base64Utils.decodeFromUrlSafeString(code);
        String[] src = new String(bytes).split("\\.");
        src[0] = src[0].replace("+", ".");
        return DigestUtils.md5DigestAsHex(src[0].getBytes()).equals(src[1]);
    }
}
