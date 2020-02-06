package me.lolicom.blog.config;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author lolicom
 */
@Getter
@Setter
@ConfigurationProperties("jwt")
public class JwtProperties {
    /**
     * Base64-encoded secret key
     */
    private String secret;
    
    /**
     * Time to Live of token
     */
    private Duration timeToLive;
    
    /**
     * Issuer of token
     */
    private String iss;
    
    /**
     * Algorithm used for signature
     */
    private SignatureAlgorithm alg = SignatureAlgorithm.HS256;
    
    private final Token token = new Token();
    
    @Getter
    @Setter
    public static class Token {
        /**
         * Property name of request header used for token
         */
        private String header = "Authorization";
        
        /**
         * Prefix of token
         */
        private String prefix = "Bearer ";
    }
}
