package me.lolico.blog.security.converter;

import me.lolico.blog.security.auth.BearerAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;


/**
 * @author lolico
 */
public class BearerAuthenticationConverter implements AuthenticationConverter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String DEFAULT_AUTHORIZATION_SCHEME = "Bearer";
    private String authorizationScheme;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    public BearerAuthenticationConverter() {
        this(DEFAULT_AUTHORIZATION_SCHEME, new WebAuthenticationDetailsSource());
    }

    public BearerAuthenticationConverter(String authorizationScheme, AuthenticationDetailsSource<HttpServletRequest,
            ?> authenticationDetailsSource) {
        this.authorizationScheme = authorizationScheme;
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    public String getAuthorizationScheme() {
        return authorizationScheme;
    }

    public void setAuthorizationScheme(String authorizationScheme) {
        this.authorizationScheme = authorizationScheme;
    }

    public AuthenticationDetailsSource<HttpServletRequest, ?> getAuthenticationDetailsSource() {
        return authenticationDetailsSource;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "authenticationDetailsSource cannot be null");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header == null) {
            return null;
        }

        header = header.trim();

        if (!StringUtils.startsWithIgnoreCase(header, authorizationScheme)) {
            return null;
        }

        header = StringUtils.trimLeadingWhitespace(header.substring(getAuthorizationScheme().length()));
        BearerAuthenticationToken token = new BearerAuthenticationToken(header);
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return token;
    }
}
