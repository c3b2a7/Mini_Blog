package me.lolico.blog.security.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * @author lolico
 */
public class BearerAuthenticationToken extends AbstractAuthenticationToken implements TokenAware {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private final Logger logger = LoggerFactory.getLogger(BearerAuthenticationToken.class);
    private final String token;

    public BearerAuthenticationToken(String token) {
        super(null);
        this.token = token;
    }

    public BearerAuthenticationToken(String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        super.setAuthenticated(true);
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public String getPrincipal() {
        return token;
    }

    /**
     * Note: do not set authenticated here
     */
    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            logger.warn("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }
}
