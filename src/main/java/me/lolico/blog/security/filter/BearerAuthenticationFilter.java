package me.lolico.blog.security.filter;

import me.lolico.blog.security.auth.BearerAuthenticationEntryPoint;
import me.lolico.blog.security.converter.BearerAuthenticationConverter;
import me.lolico.blog.security.handler.TokenReturningAuthenticationSuccessHandler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lolico
 */
public class BearerAuthenticationFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher;
    private final BearerAuthenticationConverter authenticationConverter;
    private final String headerName;
    private final String tokenPrefix;
    private AuthenticationManager authenticationManager;
    private ApplicationEventPublisher eventPublisher;
    private RememberMeServices rememberMeServices;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;

    public BearerAuthenticationFilter(String headerName, String tokenPrefix) {
        Assert.hasText(headerName, "headerName must be specified");
        Assert.hasText(tokenPrefix, "headerName must be specified");
        this.headerName = headerName;
        this.tokenPrefix = tokenPrefix;
        this.requestMatcher = new RequestHeaderRequestMatcher(headerName);
        this.rememberMeServices = new NullRememberMeServices();
        this.authenticationConverter = new BearerAuthenticationConverter(
                tokenPrefix, new WebAuthenticationDetailsSource());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication authRequest = authenticationConverter.convert(request);
        try {
            if (authRequest == null) {
                filterChain.doFilter(request, response);
                return;
            }
            if (requiredAuthentication(request)) {
                if (this.logger.isDebugEnabled()) {
                    logger.debug("Bearer Authentication Authorization header found for token[" +
                            authRequest.getName() + "]");
                }
                Authentication authResult = this.authenticationManager.authenticate(authRequest);
                successfulAuthentication(request, response, filterChain, authResult);
            }
        } catch (AuthenticationException failed) {
            unsuccessfulAuthentication(request, response, authRequest, failed);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiredAuthentication(HttpServletRequest request) {
        return this.requestMatcher.matches(request);
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authResult)
            throws IOException, ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
            if (successHandler != null) {
                logger.debug("Delegating to authentication success handler " + successHandler);
            }
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);
        rememberMeServices.loginSuccess(request, response, authResult);

        // publish event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, getClass()));
        }

        // invoke handler
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              Authentication authRequest, AuthenticationException failed)
            throws IOException, ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication request failed: " + failed.toString(), failed);
            logger.debug("Updated SecurityContextHolder to contain null Authentication");
            if (failureHandler != null) {
                logger.debug("Delegating to authentication failure handler " + failureHandler);
            }
        }
        SecurityContextHolder.clearContext();
        rememberMeServices.loginFail(request, response);

        // publish event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new AuthenticationFailureBadCredentialsEvent(authRequest, failed));
        }

        // invoke handler
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(authenticationManager, "An AuthenticationManager is required");
        if (this.successHandler == null) {
            this.successHandler = new TokenReturningAuthenticationSuccessHandler(
                    headerName, tokenPrefix, true);
        }
        if (this.failureHandler == null) {
            this.failureHandler = new AuthenticationEntryPointFailureHandler(
                    new BearerAuthenticationEntryPoint(getFilterName())
            );
        }
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        this.authenticationManager = authenticationManager;
    }

    public void setAuthenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        authenticationConverter.setAuthenticationDetailsSource(authenticationDetailsSource);
    }

    public void setRememberMeServices(RememberMeServices rememberMeServices) {
        Assert.notNull(rememberMeServices, "rememberMeServices cannot be null");
        this.rememberMeServices = rememberMeServices;
    }

    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "eventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

}