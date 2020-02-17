package me.lolicom.blog.security.auth.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

/**
 * @author lolicom
 */
public class UserDetailsServiceFactories {
    private Map<String, UserDetailsService> serviceMap;

    public UserDetailsServiceFactories(Map<String, UserDetailsService> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public UserDetailsService createDelegatingUserDetailsService() {
        return new DelegatingUserDetailsService(serviceMap);
    }
}
