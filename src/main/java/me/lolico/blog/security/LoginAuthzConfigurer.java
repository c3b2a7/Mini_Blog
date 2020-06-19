package me.lolico.blog.security;

import me.lolico.blog.security.filter.FormLoginAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * This configure will add <code>FormLoginAuthenticationFilter</code> after <code>LogoutFilter</code>.
 * <p>
 * <strong>The filter-specific default configuration</strong>
 * <ul>
 *     <li>processing_url : "/login"</li>
 *     <li>post_only: true</li>
 *     <li>form_username_key : "username"</li>
 *     <li>form_password_key : "password"</li>
 * </ul>
 * <p>
 * Other configuration options are inherited from {@link AbstractAuthenticationProcessingFilter}
 *
 * @see FormLoginAuthenticationFilter
 * @see LogoutFilter
 */
public class LoginAuthzConfigurer<B extends HttpSecurityBuilder<B>>
        extends AbstractHttpConfigurer<LoginAuthzConfigurer<B>, B> {

    private FormLoginAuthenticationFilter filter;

    public LoginAuthzConfigurer() {
        this.filter = new FormLoginAuthenticationFilter();
    }

    @Override
    public void configure(B http) {
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        filter = postProcess(this.filter);
        http.addFilterAfter(filter, LogoutFilter.class);
    }

    public LoginAuthzConfigurer<B> processingUrl(String url) {
        filter.setProcessingUrl(url);
        return this;
    }

    public LoginAuthzConfigurer<B> allowSessionCreation(boolean allowSessionCreation) {
        filter.setAllowSessionCreation(allowSessionCreation);
        return this;
    }

    public LoginAuthzConfigurer<B> successHandler(AuthenticationSuccessHandler successHandler) {
        filter.setAuthenticationSuccessHandler(successHandler);
        return this;
    }

    public LoginAuthzConfigurer<B> failureHandler(AuthenticationFailureHandler failureHandler) {
        filter.setAuthenticationFailureHandler(failureHandler);
        return this;
    }

    public LoginAuthzConfigurer<B> usernameParameter(String usernameParameter) {
        filter.setUsernameParameter(usernameParameter);
        return this;
    }

    public LoginAuthzConfigurer<B> passwordParameter(String passwordParameter) {
        filter.setPasswordParameter(passwordParameter);
        return this;
    }

}