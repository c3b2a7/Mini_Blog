package me.lolico.blog.security.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import java.util.function.Function;

public class DaoUserDetailsService implements UserDetailsService {

    private final Function<String, UserDetails> userLoader;

    public DaoUserDetailsService(Function<String, UserDetails> userLoader) {
        this.userLoader = userLoader;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("Could not find user with null username");
        }
        UserDetails user = userLoader.apply(username);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user with the username '" + username + "'");
        }
        return user;
    }
}