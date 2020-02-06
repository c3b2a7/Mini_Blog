package me.lolicom.blog.web.realm;

import me.lolicom.blog.entity.User;
import me.lolicom.blog.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lolicom
 */
@Component("authorizer")
public class UsernamePasswordRealm extends AuthorizingRealm {
    
    private final UserService userService;
    
    public UsernamePasswordRealm(UserService userService) {
        this.userService = userService;
    }
    
    @Autowired
    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        super.setCredentialsMatcher(credentialsMatcher);
    }
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = ((UsernamePasswordToken) token).getUsername();
        User user = userService.findUserByName(username);
        if (user == null) {
            throw new UnknownAccountException("account[username:'" + username + "']");
        }
        if (userService.isLocked(user)) {
            throw new LockedAccountException("account[username:'" + username + "']");
        }
        if (userService.isDisable(user)) {
            throw new DisabledAccountException("account[username:'" + username + "']");
        }
        return new SimpleAuthenticationInfo(user,
                user.getPassword(),
                ByteSource.Util.bytes(userService.getSalt(user)),
                getName());
    }
    
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }
}
