package me.lolicom.blog.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import me.lolicom.blog.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.Soundbank;
import java.time.Duration;

class JwtUtilsTest {
    
    JwtUtils jwtUtils;
    JwtProperties properties;
    
    @BeforeEach
    void setUp() {
        properties = new JwtProperties();
        properties.setIss("lolicom");
        properties.setSecret(TextCodec.BASE64.encode("lolicom"));
        properties.setTimeToLive(Duration.ofSeconds(3));
        properties.setAlg(SignatureAlgorithm.HS256);
        jwtUtils = new JwtUtils(properties);
    }
    
    
    @Test
    void test() {
        String token = jwtUtils.createToken("admin");
        jwtUtils.getClaimFromToken(token, Claims::getSubject).ifPresent(System.out::println);
        System.out.println(jwtUtils.isTokenExpired(token));
        System.out.println(jwtUtils.validateToken(token, "admin"));
    }
    
    @Test
    void createBase64EncodedKey() {
        System.out.println(TextCodec.BASE64.encode("me.lolicom.blog"));
    }
    
}