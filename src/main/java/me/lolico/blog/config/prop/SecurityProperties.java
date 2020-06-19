package me.lolico.blog.config.prop;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * @author lolico
 */
@ConfigurationProperties("me.lolico.blog.security")
public class SecurityProperties implements InitializingBean {
    private final Token token = new Token();
    private final JWT jwt = new JWT();
    private boolean enabled = true;
    private String[] ignorePath;

    @Override
    public void afterPropertiesSet() {
        if (token.isAutoRefresh()) {
            Assert.isTrue(jwt.getLifeTime().compareTo(token.getTimeToRefresh()) >= 0,
                    "The lifetime of the token must be longer than the time it can be refreshed");
        }
    }

    public Token getToken() {
        return token;
    }

    public JWT getJwt() {
        return jwt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String[] getIgnorePath() {
        return ignorePath;
    }

    public void setIgnorePath(String[] ignorePath) {
        this.ignorePath = ignorePath;
    }


    public static class JWT {
        /**
         * Issuer of token
         */
        private String iss;
        /**
         * Base64-encoded secret key
         */
        private String secret;
        /**
         * The lifetime of token
         */
        private Duration lifeTime;
        /**
         * Algorithm used for signature
         */
        private SignatureAlgorithm alg = SignatureAlgorithm.HS512;

        public String getIss() {
            return iss;
        }

        public void setIss(String iss) {
            this.iss = iss;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Duration getLifeTime() {
            return lifeTime;
        }

        public void setLifeTime(Duration lifeTime) {
            this.lifeTime = lifeTime;
        }

        public SignatureAlgorithm getAlg() {
            return alg;
        }

        public void setAlg(SignatureAlgorithm alg) {
            this.alg = alg;
        }
    }


    public static class Token {
        /**
         * Property name of request header used for token
         */
        private String header = "Authorization";
        /**
         * Prefix of token
         */
        private String prefix = "Bearer";
        /**
         * The time to refresh the token
         */
        private Duration timeToRefresh;
        /**
         * Whether to enable auto refresh token
         */
        private boolean autoRefresh = false;

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public Duration getTimeToRefresh() {
            return timeToRefresh;
        }

        public void setTimeToRefresh(Duration timeToRefresh) {
            this.timeToRefresh = timeToRefresh;
        }

        public boolean isAutoRefresh() {
            return autoRefresh;
        }

        public void setAutoRefresh(boolean autoRefresh) {
            this.autoRefresh = autoRefresh;
        }

    }

}
