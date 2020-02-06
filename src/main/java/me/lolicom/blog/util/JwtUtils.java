package me.lolicom.blog.util;

import com.google.common.collect.Maps;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import me.lolicom.blog.config.JwtProperties;
import me.lolicom.blog.config.exception.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author lolicom
 */
@Component
public class JwtUtils {
    
    private final JwtProperties properties;
    
    public JwtUtils(JwtProperties properties) {
        this.properties = properties;
    }
    
    public String createToken(String subject) {
        return doCreateToken(subject, Collections.emptyMap(), properties.getTimeToLive());
    }
    
    // public String createToken(String subject, Map<String, Object> claims) {
    //     return doCreateToken(subject, claims, properties.getTimeToLive());
    // }
    
    public String createToken(String subject, Function<Map<String,Object>, Map<String,Object>> claimsResolver) {
        return doCreateToken(subject, claimsResolver.apply(Maps.newHashMap()), properties.getTimeToLive());
    }
    
    private String doCreateToken(String subject, Map<String, Object> claims, Duration expiration) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                //添加令牌类型
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                //添加自定义Claims
                .addClaims(claims)
                //接受人
                .setSubject(subject)
                //签发时间
                .setIssuedAt(Date.from(now))
                //签名
                .signWith(properties.getAlg(), properties.getSecret());
        if (StringUtils.hasText(properties.getIss())) {
            //签发人
            builder.setIssuer(properties.getIss());
        }
        if (expiration != null && !expiration.isNegative()) {
            //过期时间
            builder.setExpiration(Date.from(now.plus(expiration)));
        }
        return builder.compact();
    }
    
    
    public Claims getClaims(String token) {
        Claims body;
        try {
            body = Jwts.parser()
                    .setSigningKey(properties.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
            
        } catch (ExpiredJwtException e) {
            // ignoring expired token exception
            body = e.getClaims();
        }
        return body;
    }
    
    public boolean verifyToken(String token) throws ExpiredTokenException, MalformedTokenException, IncorrectSignatureException, IncorrectIssuerException {
        if (!StringUtils.hasText(token)) {
            throw new MalformedTokenException("JWT token cannot be null or empty");
        }
        //noinspection rawtypes
        Jwt jwt;
        try {
            jwt = parseToken(token);
        } catch (ExpiredJwtException e) {
            // first, check is expired token?
            throw new ExpiredTokenException(e.getMessage());
        } catch (MalformedJwtException e) {
            // second, check is malformed token?
            throw new MalformedTokenException(e.getMessage());
        } catch (SignatureException e) {
            // next, check is incorrect signature?
            throw new IncorrectSignatureException(e.getMessage());
        }
        Claims claims = (Claims) jwt.getBody();
        String issuer = claims.getIssuer();
        if (issuer != null && !issuer.equals(properties.getIss())) {
            // now, check is incorrect issuer?
            throw new IncorrectIssuerException("Incorrect issuer " + issuer);
        }
        // finally, if token is valid, check subject
        String subject = claims.getSubject();
        if (!StringUtils.hasText(subject)) {
            throw new IncorrectSubjectException("Incorrect subject " + subject);
        }
        
        return true;
    }
    
    /**
     * @see JwtParser#parse(String)
     */
    @SuppressWarnings("rawtypes")
    private Jwt parseToken(String token) throws ExpiredJwtException, MalformedJwtException, SignatureException {
        return Jwts.parser()
                .setSigningKey(properties.getSecret())
                .parse(token);
    }
    
    public boolean validateToken(String token, String subject) {
        Optional<String> username = getClaimFromToken(token, Claims::getSubject);
        return username.filter(s -> (s.equals(subject) && !isTokenExpired(token))).isPresent();
    }
    
    public boolean isTokenExpired(String token) {
        Optional<Date> expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.map(date -> date.before(new Date())).orElse(false);
    }
    
    public <T> Optional<T> getClaimFromToken(String token, Function<Claims, T> resolver) {
        Claims claims = getClaims(token);
        return Optional.ofNullable(resolver.apply(claims));
    }
    
}
