package me.lolicom.blog.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import me.lolicom.blog.config.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author lolicom
 */
@Slf4j
@Component
public class JwtUtils {

    private static String issuer;
    private static String secret;
    private static Duration lifeTime;
    private static SignatureAlgorithm algorithm;

    public static String createToken(String subject) {
        Map<String, Object> claims = Collections.emptyMap();
        return doCreateToken(subject, claims, lifeTime);
    }

    public static String createToken(String subject, Consumer<Map<String, Object>> consumer) {
        Map<String, Object> claims = new HashMap<>();
        consumer.accept(claims);
        return doCreateToken(subject, claims, lifeTime);
    }

    public static String createToken(String subject, Map<String, Object> claims) {
        return createToken(subject, map -> map.putAll(claims));
    }

    public static String createToken(String subject, Map<String, Object> claims, long expiration, ChronoUnit unit) {
        return doCreateToken(subject, claims, Duration.of(expiration, unit));
    }

    private static String doCreateToken(String subject, Map<String, Object> claims, Duration expiration) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //添加令牌类型
                .addClaims(claims) //添加自定义Claims
                .setSubject(subject) //接受人
                .setIssuedAt(Date.from(now)) //签发时间
                .signWith(algorithm, secret);
        if (StringUtils.hasText(issuer)) {
            builder.setIssuer(issuer); //签发人
        }
        if (expiration != null && expiration.isNegative()) {
            builder.setExpiration(Date.from(now.plus(expiration))); //过期时间
        }
        String token = builder.compact();
        if (log.isTraceEnabled()) {
            log.trace("Create token[{}] in {}", token, Date.from(now));
        }
        return token;
    }

    public static Claims getClaims(String token) {
        Claims body;
        try {
            body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // ignoring expired token exception
            body = e.getClaims();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("An exception[{}] occurred during get claims", e.getMessage());
            }
            body = null;
        }
        return body;
    }

    public static boolean verifyToken(String token) {
        //noinspection rawtypes
        Jwt jwt;
        try {
            jwt = parseToken(token);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Token[{}] verify failed, message: {}", token, e.getMessage());
            }
            return false;
        }
        Claims claims = (Claims) jwt.getBody();
        String issuer = claims.getIssuer();
        if (issuer != null && !issuer.equals(JwtUtils.issuer)) {
            if (log.isDebugEnabled()) {
                String msg = "Incorrect issuer: " + issuer;
                log.debug("Token[{}] verify failed, message: {}", token, msg);
            }
            return false;
        }
        String subject = claims.getSubject();
        if (!StringUtils.hasText(subject)) {
            if (log.isDebugEnabled()) {
                String msg = "subject cannot be null or empty";
                log.debug("Token[{}] verify failed, message: {}", token, msg);
            }
            return false;
        }

        return true;
    }

    /**
     * @see JwtParser#parse(String)
     */
    @SuppressWarnings("rawtypes")
    private static Jwt parseToken(String token) throws ExpiredJwtException, MalformedJwtException, SignatureException {
        return Jwts.parser()
                .setSigningKey(secret)
                .parse(token);
    }

    public static boolean isValidToken(String token, String subject) {
        Optional<String> username = getClaimFromToken(token, Claims::getSubject);
        return username.filter(s -> (s.equals(subject) && !isTokenExpired(token))).isPresent();
    }

    public static boolean isTokenExpired(String token) {
        Optional<Date> expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.map(date -> date.before(new Date())).orElse(false);
    }

    public static <T> Optional<T> getClaimFromToken(String token, Function<Claims, T> resolver) {
        Claims claims = getClaims(token);
        if (claims == null) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot operate on a null Claims");
            }
            return Optional.empty();
        }
        return Optional.ofNullable(resolver.apply(claims));
    }

    @Autowired
    public void init(SecurityProperties securityProperties) {
        SecurityProperties.JWT jwt = securityProperties.getJwt();
        JwtUtils.issuer = jwt.getIss();
        JwtUtils.secret = jwt.getSecret();
        JwtUtils.lifeTime = jwt.getLifeTime();
        JwtUtils.algorithm = jwt.getAlg();
    }
}
