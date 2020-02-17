package me.lolicom.blog.security.handler;

import me.lolicom.blog.lang.Constants;
import me.lolicom.blog.security.auth.TokenAware;
import me.lolicom.blog.util.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenReturningAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private String tokenHeaderName;
    private String tokenPrefix;
    private boolean required;

    public TokenReturningAuthenticationSuccessHandler(String tokenHeaderName, String tokenPrefix, boolean required) {
        this.tokenHeaderName = tokenHeaderName;
        this.tokenPrefix = tokenPrefix;
        this.required = required;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        String token = prepareToken(authentication);
        if (StringUtils.hasText(token)) {
            response.setHeader(tokenHeaderName, tokenPrefix + " " + token);
        }
    }

    private String prepareToken(Authentication authentication) {
        if (authentication instanceof TokenAware) {
            return ((TokenAware) authentication).getToken();
        }
        if (required) {
            return JwtUtils.createToken(authentication.getName(),
                    map -> map.put(Constants.AUTHORITIES_CLAIMS_NAME, authentication.getAuthorities().stream()
                            .distinct().map(GrantedAuthority::getAuthority).toArray(String[]::new)));
        }
        return null;
    }
}