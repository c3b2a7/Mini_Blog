package me.lolico.blog.security.auth.provider;

import io.jsonwebtoken.*;
import me.lolico.blog.security.Constants;
import me.lolico.blog.security.auth.BearerAuthenticationToken;
import me.lolico.blog.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lolico
 */
public class BearerAuthenticationProvider implements AuthenticationProvider, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(BearerAuthenticationProvider.class);
    private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private boolean autoRefreshEnable;
    private Duration duration;

    public BearerAuthenticationProvider() {
    }

    public BearerAuthenticationProvider(boolean autoRefreshEnable, Duration duration) {
        this.autoRefreshEnable = autoRefreshEnable;
        this.duration = duration;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(BearerAuthenticationToken.class, authentication,
                () -> messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only UsernamePasswordAuthenticationToken is supported"));

        String credentials = ((BearerAuthenticationToken) authentication).getCredentials();
        Claims claims;
        try {
            claims = JwtUtils.verifyAndParseToken(credentials).getBody();
            if (!StringUtils.hasText(claims.getSubject())) {
                throw new BadCredentialsException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.badCredentials"));
            }
            // transform to AuthenticationException
        } catch (ExpiredJwtException ex) {
            throw new CredentialsExpiredException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.credentialsExpired", ex.getMessage()));
        } catch (ClaimJwtException | IllegalArgumentException | MalformedJwtException | SignatureException ex) {
            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", ex.getMessage()));
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }

        return createSuccessAuthentication(authentication, claims);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(BearerAuthenticationToken.class);
    }

    protected Authentication createSuccessAuthentication(Authentication authentication, Claims claims) {
        String token = (String) authentication.getCredentials();

        BearerAuthenticationToken authResult = generateBearerAuthenticationToken(token, claims);
        authResult.setDetails(authentication.getDetails());

        return authResult;
    }

    private BearerAuthenticationToken generateBearerAuthenticationToken(String token, Claims claims) {
        String subject = claims.getSubject();
        Object authorities = claims.get(Constants.AUTHORITIES_CLAIMS_NAME);
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        if (issuedAt != null && expiration != null && isAutoRefreshEnable()) {
            Instant now = Instant.now();
            if (now.minus(duration).isAfter(issuedAt.toInstant()) && now.isAfter(expiration.toInstant())) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Refresh token '{}'", token);
                }
                token = JwtUtils.createToken(subject,
                        map -> map.put(Constants.AUTHORITIES_CLAIMS_NAME, authorities));
            }
        }
        return new BearerAuthenticationToken(token, resolveGrantedAuthorities(authorities));
    }

    private Collection<GrantedAuthority> resolveGrantedAuthorities(Object authorities) {
        if (authorities instanceof Collection) {
            if (CollectionUtils.isEmpty((Collection<?>) authorities)) {
                return AuthorityUtils.NO_AUTHORITIES;
            } else {
                return ((Collection<?>) authorities).stream()
                        .filter(Objects::nonNull)
                        .map(o -> new SimpleGrantedAuthority(String.valueOf(o)))
                        .collect(Collectors.toList());
            }
        } else if (authorities instanceof String) {
            String[] array = StringUtils.tokenizeToStringArray((String) authorities,
                    Constants.AUTHORITIES_CLAIMS_VALUE_SEPARATOR);
            return AuthorityUtils.createAuthorityList(array);
        }
        if (authorities != null) {
            logger.warn("Cannot parse type of obj '{}', " +
                    "Check if wrong type was selected when generating authorization", authorities);
        }
        return AuthorityUtils.NO_AUTHORITIES;
    }

    @Override
    public void afterPropertiesSet() {
        if (autoRefreshEnable) {
            Assert.notNull(duration, "Duration is required");
        }
    }

    public boolean isAutoRefreshEnable() {
        return autoRefreshEnable;
    }

    public void setAutoRefreshEnable(boolean autoRefreshEnable) {
        this.autoRefreshEnable = autoRefreshEnable;
    }
}
