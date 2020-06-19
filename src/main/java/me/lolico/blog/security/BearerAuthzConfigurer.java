package me.lolico.blog.security;

import me.lolico.blog.security.auth.BearerAuthenticationEntryPoint;
import me.lolico.blog.security.auth.BearerAuthenticationToken;
import me.lolico.blog.security.converter.BearerAuthenticationConverter;
import me.lolico.blog.security.filter.BearerAuthenticationFilter;
import me.lolico.blog.security.handler.TokenReturningAuthenticationSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

/**
 * This configure will add <code>BearerAuthenticationFilter</code> after <code>LogoutFilter</code>.
 * <p>
 * <strong>The filter-specific default configuration</strong>
 * <ul>
 *     <li>request_matcher : {@link RequestHeaderRequestMatcher}</li>
 *     <li>remember_me_services : {@link NullRememberMeServices}</li>
 *     <li>authentication_converter : {@link BearerAuthenticationConverter}</li>
 *     <li>authentication_success_handler : {@link TokenReturningAuthenticationSuccessHandler}</li>
 *     <li>authentication_failure_handler : {@link AuthenticationEntryPointFailureHandler}</li>
 * </ul>
 *
 * @see BearerAuthenticationToken
 * @see BearerAuthenticationFilter
 * @see BearerAuthenticationConverter
 * @see BearerAuthenticationEntryPoint
 * @see TokenReturningAuthenticationSuccessHandler
 */
public class BearerAuthzConfigurer<B extends HttpSecurityBuilder<B>>
        extends AbstractHttpConfigurer<BearerAuthzConfigurer<B>, B> {

    private BearerAuthenticationFilter filter;

    public BearerAuthzConfigurer(String headerName, String prefix) {
        this.filter = new BearerAuthenticationFilter(headerName, prefix);
    }

    @Override
    public void configure(B http) {
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        filter = postProcess(this.filter);
        http.addFilterAfter(filter, LogoutFilter.class);
    }

    public BearerAuthzConfigurer<B> failureHandler(AuthenticationFailureHandler failureHandler) {
        filter.setFailureHandler(failureHandler);
        return this;
    }

    public BearerAuthzConfigurer<B> successHandler(AuthenticationSuccessHandler successHandler) {
        filter.setSuccessHandler(successHandler);
        return this;
    }


}