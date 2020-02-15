package me.lolicom.blog.web.realm;

import lombok.extern.slf4j.Slf4j;
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
public class JwtRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String jwt = ((JWTToken) token).getToken();
        boolean authResult = JwtUtils.verifyToken(jwt);
        if (!authResult) {
            throw new IncorrectCredentialsException();
        }
        return new SimpleAuthenticationInfo(jwt, jwt, getName());
    }
    
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }
}
