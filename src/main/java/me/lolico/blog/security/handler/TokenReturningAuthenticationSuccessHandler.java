package me.lolico.blog.security.handler;

import me.lolico.blog.security.Constants;
import me.lolico.blog.security.auth.TokenAware;
import me.lolico.blog.util.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The successful authentication handler will check whether there is a token in
 * the <code>Authentication</code> after the authentication is successful, and
 * if it exists, the token will be written in the response header.If need to force
 * written a token, <code>required</code> attribute is helpful. when the attribute
 * is <code>true</code> and cannot find token in <code>Authentication</code>, this
 * handler will create a token to written in the response header.
 *
 * @author lolico
 */
public class TokenReturningAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final String tokenHeaderName;
    private final String tokenPrefix;
    private final boolean required;

    public TokenReturningAuthenticationSuccessHandler(String tokenHeaderName,
                                                      String tokenPrefix,
                                                      boolean required) {
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
                    map -> map.put(Constants.AUTHORITIES_CLAIMS_NAME, authentication.getAuthorities()
                            .stream()
                            .distinct().filter(Objects::nonNull)
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(Constants.AUTHORITIES_CLAIMS_VALUE_SEPARATOR))));
        }
        return null;
    }
}