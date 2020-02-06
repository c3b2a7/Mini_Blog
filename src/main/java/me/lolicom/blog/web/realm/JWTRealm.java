package me.lolicom.blog.web.realm;

import lombok.extern.slf4j.Slf4j;
import me.lolicom.blog.config.exception.ExpiredTokenException;
import me.lolicom.blog.config.exception.IncorrectIssuerException;
import me.lolicom.blog.config.exception.IncorrectSignatureException;
import me.lolicom.blog.config.exception.MalformedTokenException;
import me.lolicom.blog.util.JwtUtils;
import me.lolicom.blog.web.JWTToken;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

/**
 * @author lolicom
 */
@Slf4j
@Component
public class JWTRealm extends AuthorizingRealm {
    
    final JwtUtils jwtUtils;
    
    public JWTRealm(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String jwt = ((JWTToken) token).getToken();
        try {
            jwtUtils.verifyToken(jwt);
        } catch (ExpiredTokenException e) {
            // transform this exception to shiro's AuthenticationException
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            throw new ExpiredCredentialsException(e.getMessage());
        } catch (MalformedTokenException | IncorrectSignatureException | IncorrectIssuerException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            throw new IncorrectCredentialsException(e.getMessage());
        }
        
        return new SimpleAuthenticationInfo(jwt, jwt, getName());
    }
    
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }
}
