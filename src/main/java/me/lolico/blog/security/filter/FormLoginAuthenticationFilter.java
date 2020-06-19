package me.lolico.blog.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * Based on {@link UsernamePasswordAuthenticationFilter}, support for getting parameter values from json
 *
 * @author lolico
 */
public class FormLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean postOnly = true;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (postOnly && !request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        if (isAuthenticationWithJson(request, response)) {
            // from json
            Map<String, Object> jsonContext = parseJsonContext(request);

            String username = (String) this.resolverSpecifiedValue(jsonContext, getUsernameParameter());
            String password = (String) this.resolverSpecifiedValue(jsonContext, getPasswordParameter());

            username = username.trim();

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);

            return this.getAuthenticationManager().authenticate(authRequest);
        } else {
            // form urlencoded
            return super.attemptAuthentication(request, response);
        }
    }

    /**
     * Ensure fields value sync with parent class
     */
    @Override
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
        super.setPostOnly(postOnly);
    }

    protected boolean isAuthenticationWithJson(HttpServletRequest request, HttpServletResponse response) {
        return request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE);
    }

    private Object resolverSpecifiedValue(Map<String, Object> jsonContext, String parameterName) {
        return jsonContext.getOrDefault(parameterName, "");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonContext(HttpServletRequest request) {
        try {
            String json = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            return objectMapper.readValue(json, Map.class);
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Occur exception during parse json context", e);
            }
            return Collections.EMPTY_MAP;
        }
    }

    public void setProcessingUrl(String url) {
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(url, "POST"));
    }
}
