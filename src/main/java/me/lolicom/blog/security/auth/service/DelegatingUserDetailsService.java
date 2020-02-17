package me.lolicom.blog.security.auth.service;

import io.jsonwebtoken.lang.Assert;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author lolicom
 */
public class DelegatingUserDetailsService implements UserDetailsService {
    private static final String PREFIX = "{";
    private static final String SUFFIX = "}";
    private final Map<String, UserDetailsService> serviceMap;
    private UserDetailsService defaultService = new UnmappedIdUserDetailsService();

    public DelegatingUserDetailsService(Map<String, UserDetailsService> serviceMap) {
        for (String id : serviceMap.keySet()) {
            if (id == null) {
                continue;
            }
            if (id.contains(PREFIX)) {
                throw new IllegalArgumentException("id " + id + " cannot contain " + PREFIX);
            }
            if (id.contains(SUFFIX)) {
                throw new IllegalArgumentException("id " + id + " cannot contain " + SUFFIX);
            }
        }
        this.serviceMap = serviceMap;
    }

    public static String extractId(String prefixUserDetailsService) {
        if (prefixUserDetailsService == null) {
            return null;
        }
        int start = prefixUserDetailsService.indexOf(PREFIX);
        if (start != 0) {
            return null;
        }
        int end = prefixUserDetailsService.indexOf(SUFFIX, start);
        if (end < 0) {
            return null;
        }
        return prefixUserDetailsService.substring(start + 1, end);
    }

    public static String extractUsername(String prefixUsername) {
        int start = prefixUsername.indexOf(SUFFIX);
        return prefixUsername.substring(start + 1);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("Could not find user with null username");
        }
        String id = extractId(username);
        UserDetailsService delegate = serviceMap.get(id);
        if (delegate == null) {
            return this.defaultService.loadUserByUsername(username);
        }
        String extractUsername = extractUsername(username);
        return delegate.loadUserByUsername(extractUsername);
    }

    public UserDetailsService getDefaultService() {
        return defaultService;
    }

    public void setDefaultService(UserDetailsService defaultService) {
        Assert.notNull(defaultService, "defaultService cannot be null");
        this.defaultService = defaultService;
    }

    private static class UnmappedIdUserDetailsService implements UserDetailsService {
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            String id = extractId(username);
            throw new IllegalArgumentException("There is no UserDetailService mapped for id '" + id + "'");
        }
    }
}
