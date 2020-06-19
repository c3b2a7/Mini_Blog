package me.lolico.blog.util;

import io.jsonwebtoken.*;
import me.lolico.blog.config.prop.SecurityProperties;
import me.lolico.blog.config.prop.SecurityProperties.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author lolico
 */
public class JwtUtils implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private static String DEFAULT_ISSUER;
    private static String DEFAULT_SECRET;
    private static Duration DEFAULT_LIFETIME;
    private static SignatureAlgorithm DEFAULT_ALGORITHM;

    private static Token token;

    public static String createToken(String subject) {
        Map<String, Object> claims = Collections.emptyMap();
        return createToken(JwtUtils.DEFAULT_ISSUER, subject, claims, DEFAULT_LIFETIME,
                JwtUtils.DEFAULT_ALGORITHM,
                JwtUtils.DEFAULT_SECRET);
    }

    public static String createToken(String subject, Consumer<Map<String, Object>> consumer) {
        Map<String, Object> claims = new HashMap<>();
        consumer.accept(claims);
        return createToken(JwtUtils.DEFAULT_ISSUER, subject, claims, DEFAULT_LIFETIME,
                JwtUtils.DEFAULT_ALGORITHM,
                JwtUtils.DEFAULT_SECRET);
    }

    public static String createToken(String subject, Map<String, Object> claims, long expiration, TimeUnit unit) {
        return createToken(JwtUtils.DEFAULT_ISSUER, subject, claims,
                Duration.ofMillis(unit.toMillis(expiration)),
                JwtUtils.DEFAULT_ALGORITHM,
                JwtUtils.DEFAULT_SECRET);
    }

    public static String createToken(@NonNull String issuer,
                                     @NonNull String subject,
                                     @NonNull Map<String, Object> claims,
                                     Duration lifetime,
                                     @NonNull SignatureAlgorithm algorithm,
                                     @NonNull String secret) {

        Assert.hasText(issuer, "Issuer must be specified");
        Assert.hasText(secret, "Secret must be specified");

        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //添加令牌类型
                .setIssuer(issuer) //签发人
                .addClaims(claims) //添加自定义Claims
                .setSubject(subject) //接受人
                .setIssuedAt(Date.from(now)); //签发时间
        if (lifetime != null && !lifetime.isNegative()) {
            builder.setExpiration(Date.from(now.plus(lifetime))); //过期时间
        }
        String token = builder.signWith(algorithm, secret).compact();
        if (logger.isTraceEnabled()) {
            logger.trace("Create token[{}] in {}", token, Date.from(now));
        }
        return token;
    }

    public static Jws<Claims> verifyAndParseToken(String token) {
        try {
            Jws<Claims> parse = parse(token, DEFAULT_ISSUER, getSecret(token));
            if (logger.isTraceEnabled()) {
                logger.trace("token[{}] verify success", token);
            }
            return parse;
        } catch (Exception e) {
            if (logger.isTraceEnabled()) {
                logger.trace("Token[{}] verify failed, message: {}", token, e.getMessage());
            }
            throw e;
        }
    }

    private static String getSecret(String token) {
        return DEFAULT_SECRET;
    }

    /**
     * Maybe throw exception
     *
     * @see JwtParser#parseClaimsJws(String)
     */
    private static Jws<Claims> parse(String token, String issuer, String secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .requireIssuer(issuer)
                .parseClaimsJws(token);
    }

    public static boolean isTokenExpired(String token) {
        Optional<Date> expiration = Optional.ofNullable(getProperty(token, Claims::getExpiration));
        return expiration.map(date -> date.before(new Date())).orElse(false);
    }

    @Nullable
    public static <T> T getProperty(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getClaims(token);
        if (claims == null) {
            return null;
        }
        return claimsResolver.apply(claims);
    }

    public static <T> T getProperty(String token, Function<Claims, T> claimsResolver, T defaultValue) {
        T value;
        try {
            Claims claims = getClaims(token);
            value = claimsResolver.apply(claims);
        } catch (Exception ex) {
            if (logger.isTraceEnabled()) {
                logger.trace("An exception '{}' occurred during getProperty", ex.getMessage());
            }
            return defaultValue;
        }
        return value == null ? defaultValue : value;
    }

    @Nullable
    public static <T> T getProperty(Function<Token, T> propertyResolver) {
        return propertyResolver.apply(JwtUtils.token);
    }

    public static <T> T getProperty(Function<Token, T> propertyResolver, T defaultValue) {
        T value;
        try {
            value = propertyResolver.apply(JwtUtils.token);
        } catch (Exception ex) {
            if (logger.isTraceEnabled()) {
                logger.trace("An exception '{}' occurred during getProperty", ex.getMessage());
            }
            return defaultValue;
        }
        return value == null ? defaultValue : value;
    }

    @Nullable
    private static Claims getClaims(String token) {
        Claims body;
        try {
            body = parse(token, DEFAULT_ISSUER, getSecret(token)).getBody();
        } catch (ClaimJwtException e) {
            // ignoring expired token exception
            body = e.getClaims();
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("An exception '{}' occurred during getClaims", e.getMessage());
            }
            body = null;
        }
        return body;
    }

    /**
     * 清除数据库或者缓存中的salt
     */
    public static void deleteUserLoginInfo(String username) {

    }

    @Autowired
    public void init(SecurityProperties securityProperties) {
        SecurityProperties.JWT jwt = securityProperties.getJwt();
        JwtUtils.DEFAULT_ISSUER = jwt.getIss();
        JwtUtils.DEFAULT_SECRET = jwt.getSecret();
        JwtUtils.DEFAULT_LIFETIME = jwt.getLifeTime();
        JwtUtils.DEFAULT_ALGORITHM = jwt.getAlg();
        JwtUtils.token = securityProperties.getToken();
    }

    @Override
    public void afterPropertiesSet() {
        Assert.hasText(JwtUtils.DEFAULT_ISSUER, "Issuer must be specified");
        Assert.hasText(JwtUtils.DEFAULT_SECRET, "Secret must be specified");
        Assert.notNull(JwtUtils.DEFAULT_ALGORITHM, "Signature algorithm cannot be null");
    }

}
