package me.lolicom.blog.web;

import org.apache.shiro.authc.HostAuthenticationToken;

/**
 * @author lolicom
 */
public class JWTToken implements HostAuthenticationToken {
    
    public static final JWTToken NONE = new JWTToken(null, null);
    
    private String token;
    private String host;
    
    public JWTToken(String token) {
        this(token, null);
    }
    
    public JWTToken(String token, String host) {
        this.token = token;
        this.host = host;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public String getHost() {
        return host;
    }
    
    @Override
    public Object getPrincipal() {
        return token;
    }
    
    @Override
    public Object getCredentials() {
        return token;
    }
    
    @Override
    public String toString() {
        return "JWTToken{" +
                "token='" + token + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
