package me.lolico.blog.config;

import me.lolico.blog.config.prop.SecurityProperties;
import me.lolico.blog.security.BearerAuthzConfigurer;
import me.lolico.blog.security.LoginAuthzConfigurer;
import me.lolico.blog.security.auth.provider.BearerAuthenticationProvider;
import me.lolico.blog.security.auth.provider.DaoAuthenticationProvider;
import me.lolico.blog.security.auth.service.DaoUserDetailsService;
import me.lolico.blog.security.handler.TokenClearLogoutHandler;
import me.lolico.blog.security.handler.TokenReturningAuthenticationSuccessHandler;
import me.lolico.blog.service.UserService;
import me.lolico.blog.service.entity.User;
import me.lolico.blog.service.impl.UserServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author lolico
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(prefix = "me.lolico.blog.security", name = "enabled", matchIfMissing = true)
public class SecurityAutoConfiguration extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final String headerName;
    private final String prefix;
    private final boolean autoRefreshEnable;
    private final Duration duration;
    private final String[] ignorePath;

    public SecurityAutoConfiguration(UserService userService, SecurityProperties securityProperties) {
        this.userService = userService;
        this.headerName = securityProperties.getToken().getHeader();
        this.prefix = securityProperties.getToken().getPrefix();
        this.autoRefreshEnable = securityProperties.getToken().isAutoRefresh();
        this.duration = securityProperties.getToken().getTimeToRefresh();
        this.ignorePath = securityProperties.getIgnorePath();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //BearerAuthenticationProvider
        BearerAuthenticationProvider bearerAuthenticationProvider =
                new BearerAuthenticationProvider(autoRefreshEnable, duration);
        bearerAuthenticationProvider.afterPropertiesSet();
        //DaoAuthenticationProvider
        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider(getUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(
                ((UserServiceImpl) userService).getPasswordEncoder());
        daoAuthenticationProvider.afterPropertiesSet();
        // apply
        auth.authenticationProvider(bearerAuthenticationProvider)
                .authenticationProvider(daoAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        if (ignorePath != null && ignorePath.length != 0) {
            web.ignoring().antMatchers(ignorePath);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable() // 禁用csrf
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers("/public/**", "/account/**").permitAll()
                .anyRequest().authenticated() //任何请求都需要认证
                .and()
                .apply(new BearerAuthzConfigurer<>(headerName, prefix)) // 应用token认证配置器
                .and()
                .apply(new LoginAuthzConfigurer<>()) // 应用表单登录配置器
                .processingUrl("/account/login")
                .successHandler(new TokenReturningAuthenticationSuccessHandler(headerName, prefix, true))
                .and()
                .logout()
                .logoutUrl("/account/logout")
                .addLogoutHandler(new TokenClearLogoutHandler()) // 登出后清除token
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
    }

    @Bean
    public DaoUserDetailsService getUserDetailsService() {
        return new DaoUserDetailsService(s -> new UserDelegator(userService.findUserForLogin(s)));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addExposedHeader(headerName);
        configuration.setAllowCredentials(true);
        configuration.applyPermitDefaultValues();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static class UserDelegator implements UserDetails {
        private final User user;

        public UserDelegator(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            if (Boolean.TRUE.equals(user.getIsAdmin())) {
                return Arrays.asList(new SimpleGrantedAuthority("ADMIN"),
                        new SimpleGrantedAuthority(user.getRole().getName()));
            }
            return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()));
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getName();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return user.getStatus() != User.Status.LOCKING;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return user.getStatus() == User.Status.VALID;
        }
    }
}
