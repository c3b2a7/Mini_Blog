package me.lolicom.blog.security;

import me.lolicom.blog.security.filter.FormLoginAuthenticationFilter;
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
public class LoginAuthzConfigure<B extends HttpSecurityBuilder<B>>
        extends AbstractHttpConfigurer<LoginAuthzConfigure<B>, B> {

    private FormLoginAuthenticationFilter filter;

    public LoginAuthzConfigure() {
        this.filter = new FormLoginAuthenticationFilter();
    }

    @Override
    public void configure(B http) {
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        filter = postProcess(this.filter);
        http.addFilterAfter(filter, LogoutFilter.class);
    }

    public LoginAuthzConfigure<B> processingUrl(String url) {
        filter.setProcessingUrl(url);
        return this;
    }

    public LoginAuthzConfigure<B> allowSessionCreation(boolean allowSessionCreation) {
        filter.setAllowSessionCreation(allowSessionCreation);
        return this;
    }

    public LoginAuthzConfigure<B> successHandler(AuthenticationSuccessHandler successHandler) {
        filter.setAuthenticationSuccessHandler(successHandler);
        return this;
    }

    public LoginAuthzConfigure<B> failureHandler(AuthenticationFailureHandler failureHandler) {
        filter.setAuthenticationFailureHandler(failureHandler);
        return this;
    }

    public LoginAuthzConfigure<B> postOnly(boolean postOnly) {
        filter.setPostOnly(postOnly);
        return this;
    }

    public LoginAuthzConfigure<B> usernameParameter(String usernameParameter) {
        filter.setUsernameParameter(usernameParameter);
        return this;
    }

    public LoginAuthzConfigure<B> passwordParameter(String passwordParameter) {
        filter.setPasswordParameter(passwordParameter);
        return this;
    }

}