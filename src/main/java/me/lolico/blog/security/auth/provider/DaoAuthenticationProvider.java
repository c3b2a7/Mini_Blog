package me.lolico.blog.security.auth.provider;

import me.lolico.blog.security.Constants;
import me.lolico.blog.security.auth.BearerAuthenticationToken;
import me.lolico.blog.util.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author lolico
 */
public class DaoAuthenticationProvider extends
        org.springframework.security.authentication.dao.DaoAuthenticationProvider {

    public DaoAuthenticationProvider(UserDetailsService userDetailsService) {
        setUserDetailsService(userDetailsService);
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String token = JwtUtils.createToken(user.getUsername(),
                map -> map.put(Constants.AUTHORITIES_CLAIMS_NAME, authorities.stream()
                        .distinct().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(Constants.AUTHORITIES_CLAIMS_VALUE_SEPARATOR))));

        BearerAuthenticationToken authResult = new BearerAuthenticationToken(token, authorities);
        authResult.setDetails(authentication.getDetails());

        return authResult;
    }
}
