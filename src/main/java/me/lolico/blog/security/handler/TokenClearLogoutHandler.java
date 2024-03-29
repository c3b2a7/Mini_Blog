package me.lolico.blog.security.handler;

import me.lolico.blog.util.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenClearLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        clearToken(authentication);
    }

    protected void clearToken(Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            JwtUtils.deleteUserLoginInfo(authentication.getName());
        }
    }

}