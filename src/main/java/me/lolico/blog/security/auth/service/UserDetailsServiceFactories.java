package me.lolico.blog.security.auth.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

/**
 * @author lolico
 */
public class UserDetailsServiceFactories {
    private final Map<String, UserDetailsService> serviceMap;

    public UserDetailsServiceFactories(Map<String, UserDetailsService> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public UserDetailsService createDelegatingUserDetailsService() {
        return new DelegatingUserDetailsService(serviceMap);
    }
}
