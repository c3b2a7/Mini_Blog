package me.lolico.blog.security.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lolico
 */
public class BearerAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private String realmName = "Realm";

    public BearerAuthenticationEntryPoint() {
    }

    public BearerAuthenticationEntryPoint(String realmName) {
        if (realmName != null)
            this.realmName = realmName;
    }

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.addHeader("WWW-Authenticate", "Bearer realm=\"" + realmName + "\"");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }
}
