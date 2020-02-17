package me.lolicom.blog;

import me.lolicom.blog.security.BearerAuthzConfigure;
import me.lolicom.blog.security.LoginAuthzConfigure;
import me.lolicom.blog.security.auth.provider.BearerAuthenticationProvider;
import me.lolicom.blog.security.auth.provider.DaoAuthenticationProvider;
import me.lolicom.blog.security.auth.service.DaoUserDetailsService;
import me.lolicom.blog.security.handler.TokenClearLogoutHandler;
import me.lolicom.blog.security.handler.TokenReturningAuthenticationSuccessHandler;
import me.lolicom.blog.service.UserService;
import me.lolicom.blog.service.entity.User;
import me.lolicom.blog.service.impl.UserServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author lolicom
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(value = "me.lolicom.blog.security.enable", matchIfMissing = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private String headerName;
    private String prefix;
    private boolean autoRefreshEnable;
    private Duration duration;

    public SecurityConfig(UserService userService, SecurityProperties securityProperties) {
        this.userService = userService;
        this.headerName = securityProperties.getToken().getHeader();
        this.prefix = securityProperties.getToken().getPrefix();
        this.autoRefreshEnable = securityProperties.getToken().isAutoRefresh();
        this.duration = securityProperties.getToken().getTimeToRefresh();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        UserDetailsService userDetailsService = new DaoUserDetailsService(s -> {
            User user = userService.findUserForLogin(s);
            if (user == null) {
                throw new UsernameNotFoundException("Could not find user with username '" + s + "'");
            }
            return new UserAdapter(user);
        });

        BearerAuthenticationProvider bearerAuthenticationProvider =
                new BearerAuthenticationProvider(autoRefreshEnable, duration);
        bearerAuthenticationProvider.afterPropertiesSet();

        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(((UserServiceImpl) userService).getPasswordEncoder());
        daoAuthenticationProvider.afterPropertiesSet();

        auth.authenticationProvider(bearerAuthenticationProvider)
                .authenticationProvider(daoAuthenticationProvider);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().disable() // 禁用session
                .formLogin().disable() // 禁用form登录
                .csrf().disable() // 禁用csrf
                .cors().configurationSource(corsConfigurationSource()) // 跨域
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated() //任何请求都需要认证
                .and()
                .apply(new LoginAuthzConfigure<>()) // 应用表单登录配置器
                .successHandler(new TokenReturningAuthenticationSuccessHandler(headerName, prefix, true))
                .and()
                .apply(new BearerAuthzConfigure<>(headerName, prefix)) // 应用token认证配置器
                .and()
                .logout()
                .addLogoutHandler(new TokenClearLogoutHandler()) // 登出后清除token
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.addExposedHeader(headerName);
        configuration.applyPermitDefaultValues();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static class UserAdapter implements UserDetails {
        private User user;

        public UserAdapter(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            if (user.getIsAdmin().equals(Boolean.TRUE)) {
                return Collections.singleton(new SimpleGrantedAuthority("ADMIN"));
            }
            return AuthorityUtils.NO_AUTHORITIES;
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
