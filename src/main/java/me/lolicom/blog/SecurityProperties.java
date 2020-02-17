package me.lolicom.blog;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * @author lolicom
 */
@Getter
@Setter
@ConfigurationProperties("me.lolicom.blog.security")
public class SecurityProperties implements InitializingBean {

    private boolean enable = true;
    private final Token token = new Token();
    private final JWT jwt = new JWT();

    @Override
    public void afterPropertiesSet() {
        if (token.isAutoRefresh()) {
            Duration timeToLive = jwt.getLifeTime();
            Duration timeToRefresh = token.getTimeToRefresh();
            Assert.isTrue(timeToLive.compareTo(timeToRefresh) >= 0,
                    "The lifetime of the token must be longer than the time it can be refreshed");
        }
    }

    @Getter
    @Setter
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

    }

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
        private String prefix = "Bearer";

        /**
         * The time to refresh the token
         */
        private Duration timeToRefresh;

        private boolean autoRefresh = false;
    }

}
