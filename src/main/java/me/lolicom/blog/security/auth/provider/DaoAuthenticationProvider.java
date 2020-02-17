package me.lolicom.blog.security.auth.provider;

import me.lolicom.blog.lang.Constants;
import me.lolicom.blog.security.auth.BearerAuthenticationToken;
import me.lolicom.blog.util.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author lolicom
 */
public class DaoAuthenticationProvider extends org.springframework.security.authentication.dao.DaoAuthenticationProvider {

    public DaoAuthenticationProvider(UserDetailsService userDetailsService) {
        setUserDetailsService(userDetailsService);
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {

        String token = JwtUtils.createToken(user.getUsername(),
                map -> map.put(Constants.AUTHORITIES_CLAIMS_NAME, user.getAuthorities().stream()
                        .distinct().map(GrantedAuthority::getAuthority).toArray(String[]::new)));

        BearerAuthenticationToken authResult = new BearerAuthenticationToken(token, user.getAuthorities());
        authResult.setDetails(authentication.getDetails());

        return authResult;
    }
}
