package me.lolicom.blog.web.filter;

import me.lolicom.blog.web.JWTToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author lolicom
 */
public class JwtHttpAuthenticationFilter extends AuthenticatingFilter {
    
    /**
     * This class's private logger.
     */
    private static final Logger log = LoggerFactory.getLogger(JwtHttpAuthenticationFilter.class);
    
    /**
     * HTTP Authorization header, equal to <code>Authorization</code>
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    /**
     * HTTP Authentication header, equal to <code>WWW-Authenticate</code>
     */
    private static final String AUTHENTICATE_HEADER = "WWW-Authenticate";
    
    /**
     * The authzScheme default value to look for in the <code>Authorization</code> header
     */
    private static final String DEFAULT_AUTHORIZATION_SCHEME = "Bearer";
    
    /**
     * The name that is displayed during the challenge process of authentication, defauls to <code>application</code>
     * and can be overridden by the {@link #setApplicationName(String) setApplicationName} method.
     */
    private String applicationName = "application";
    
    /**
     * The authzScheme value to look for in the <code>Authorization</code> header, defaults to <code>Bearer</code>
     * Can override by {@link #setAuthzScheme(String)}
     */
    private String authzScheme = DEFAULT_AUTHORIZATION_SCHEME;
    
    /**
     * <code>true</code> will enable "OPTION" request method, <code>false</code> otherwise
     */
    private boolean isCorsEnable = true;
    
    /**
     * the callback handler for successful authentication
     */
    private SuccessfulHandler successfulHandler;
    
    /**
     * the callback handler for unsuccessful authentication
     */
    private UnsuccessfulHandler unsuccessfulHandler;
    
    
    public JwtHttpAuthenticationFilter() {
        unsuccessfulHandler = (token, e, request, response) -> {
            //defaults to set 401-unauthorized http status
            HttpServletResponse httpResponse = ((HttpServletResponse) response);
            String authcHeader = getAuthzScheme() + " realm=\"" + getApplicationName() + "\"";
            httpResponse.setHeader(AUTHENTICATE_HEADER, authcHeader);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        };
    }
    
    public JwtHttpAuthenticationFilter(String authzScheme, boolean isCorsEnable, SuccessfulHandler successfulHandler, UnsuccessfulHandler unsuccessfulHandler) {
        this.authzScheme = authzScheme;
        this.isCorsEnable = isCorsEnable;
        this.successfulHandler = successfulHandler;
        this.unsuccessfulHandler = unsuccessfulHandler;
    }
    
    /**
     * Returns the name that is displayed during the challenge process of authentication
     * Default value is <code>application</code>
     *
     * @return the name that is displayed during the challenge process of authentication
     */
    public String getApplicationName() {
        return applicationName;
    }
    
    /**
     * Sets the name that is displayed during the challenge process of authentication
     * Default value is <code>application</code>
     *
     * @param applicationName the name that is displayed during the challenge process of authentication
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    /**
     * Returns the HTTP <b><code>Authorization</code></b> header value that this filter will respond to as indicating
     * a login request.
     * <p/>
     * Unless overridden by the {@link #setAuthzScheme(String)} method, the
     * default value is <code>Bearer</code>.
     *
     * @return the Http 'Authorization' header value that this filter will respond to as indicating a login request
     */
    public String getAuthzScheme() {
        return authzScheme;
    }
    
    /**
     * Sets the HTTP <b><code>Authorization</code></b> header value that this filter will respond to as indicating a
     * login request.
     * <p/>
     * Unless overridden by this method, the default value is <code>Bearer</code>
     *
     * @param authzScheme the HTTP <code>Authorization</code> header value that this filter will respond to as
     *                    indicating a login request.
     */
    public void setAuthzScheme(String authzScheme) {
        this.authzScheme = authzScheme;
    }
    
    /**
     * Default value is <code>true</code>
     *
     * @param corsEnable <code>true</code> will enable "OPTION" request method, <code>false</code> otherwise
     */
    public void setCorsEnable(boolean corsEnable) {
        isCorsEnable = corsEnable;
    }
    
    /**
     * Default value is <code>true</code>
     *
     * @return is cors enable
     */
    public boolean isCorsEnable() {
        return isCorsEnable;
    }
    
    /**
     * Returns the callback handler for successful authentication
     *
     * @return the callback handler for successful authentication
     */
    public SuccessfulHandler getSuccessfulHandler() {
        return successfulHandler;
    }
    
    /**
     * @param successfulHandler the callback handler for successful authentication
     */
    public void setSuccessfulHandler(SuccessfulHandler successfulHandler) {
        this.successfulHandler = successfulHandler;
    }
    
    /**
     * Returns the callback handler for unsuccessful authentication
     *
     * @return the callback handler for unsuccessful authentication
     */
    public UnsuccessfulHandler getUnsuccessfulHandler() {
        return unsuccessfulHandler;
    }
    
    /**
     * @param unsuccessfulHandler the callback handler for successful authentication
     */
    public void setUnsuccessfulHandler(UnsuccessfulHandler unsuccessfulHandler) {
        this.unsuccessfulHandler = unsuccessfulHandler;
    }
    
    /**
     * The Basic authentication filter can be configured with a list of HTTP methods to which it should apply. This
     * method ensures that authentication is <em>only</em> required for those HTTP methods specified. For example,
     * if you had the configuration:
     * <pre>
     *      [urls]
     *      /basic/** = authcJwt[POST,PUT,DELETE]
     * </pre>
     * then a GET request would not required authentication but a POST would.
     *
     * @param request     The current HTTP servlet request.
     * @param response    The current HTTP servlet response.
     * @param mappedValue The array of configured HTTP methods as strings. This is empty if no methods are configured.
     */
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        String httpMethod = httpRequest.getMethod();
        
        // Check whether the current request's method requires authentication.
        // If no methods have been configured, then all of them require auth,
        // otherwise only the declared ones need authentication.
        
        Set<String> methods = httpMethodsFromOptions((String[]) mappedValue);
        boolean authcRequired = methods.size() == 0;
        
        // If enable cors and this request_method is equal to "OPTION", do not authentication.
        // Can override by configuration:
        // /** = authcJwt[POST,DELETE,OPTION]
        // then a OPTION request would required authentication.
        // if (isCorsEnable && httpMethod.equalsIgnoreCase("OPTION")) {
        //     authcRequired = false;
        // }
        for (String m : methods) {
            if (httpMethod.toUpperCase(Locale.ENGLISH).equals(m)) { // list of methods is in upper case
                authcRequired = true;
                break;
            }
        }
        // if (isCorsEnable && httpMethod.equalsIgnoreCase("OPTION")) {
        //     responseForCors(request, response);
        // }
        
        if (authcRequired) {
            return super.isAccessAllowed(request, response, mappedValue);
        } else {
            return true;
        }
    }
    
    /**
     * cors support
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        if (isCorsEnable() && ((HttpServletRequest) request).getMethod().equals("OPTIONS")) {
            responseForCors(request, response);
            return false;
        }
        return super.preHandle(request, response);
    }
    
    private void responseForCors(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
        httpResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Allow-Headers"));
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        
        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }
    
    private Set<String> httpMethodsFromOptions(String[] options) {
        Set<String> methods = new HashSet<>();
        
        if (options != null) {
            for (String option : options) {
                // to be backwards compatible with 1.3, we can ONLY check for known args
                // ideally we would just validate HTTP methods, but someone could already be using this for webdav
                if (!option.equalsIgnoreCase(PERMISSIVE)) {
                    methods.add(option.toUpperCase(Locale.ENGLISH));
                }
            }
        }
        return methods;
    }
    
    /**
     * Processes unauthenticated requests. It handles the two-stage request/challenge authentication protocol.
     *
     * @param request  incoming ServletRequest
     * @param response outgoing ServletResponse
     * @return true if the request should be processed; false if the request should not continue to be processed
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false; //false by default or we wouldn't be in this method
        if (isLoginRequest(request, response)) {
            if (log.isDebugEnabled()) {
                log.debug("Attempting to execute login with auth header");
            }
            loggedIn = executeLogin(request, response);
        }
        return loggedIn;
    }
    
    /**
     * Returns <code>true</code> if the incoming request have {@link #getAuthzHeader(ServletRequest)}
     * and the header's value is start with {@link #getAuthzScheme()}, <code>false</code> otherwise.
     *
     * @param request  the incoming <code>ServletRequest</code>
     * @param response the outgoing <code>ServletResponse</code>
     * @return <code>true</code> if the incoming request is required auth, <code>false</code> otherwise.
     */
    @Override
    protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
        String authzHeader = getAuthzHeader(request);
        String scheme = getAuthzScheme().toLowerCase(Locale.ENGLISH);
        return authzHeader != null && authzHeader.toLowerCase(Locale.ENGLISH).startsWith(scheme);
    }
    
    /**
     * @param request the incoming <code>ServletRequest</code>
     * @return the <code>Authorization</code> header's value
     */
    private String getAuthzHeader(ServletRequest request) {
        return ((HttpServletRequest) request).getHeader(AUTHORIZATION_HEADER);
    }
    
    /**
     * Returns the authentication token encapsulated by the value of the Authorization header
     *
     * @param request  the incoming <code>ServletRequest</code>
     * @param response the outgoing <code>ServletResponse</code>
     * @return the authentication token encapsulated by the value of the Authorization header
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        String authzHeader = getAuthzHeader(request);
        if (authzHeader == null || authzHeader.length() == 0) {
            return JWTToken.NONE;
        }
        String scheme = getAuthzScheme();
        if (scheme != null && scheme.length() != 0) {
            authzHeader = authzHeader.substring(scheme.length());
        }
        String token = authzHeader.trim();
        String host = request.getRemoteHost();
        return new JWTToken(token, host);
    }
    
    /**
     * Callback processing after authentication successful.
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        if (successfulHandler != null) {
            if (log.isDebugEnabled()) {
                log.debug("{} can pass auth, the auth subject is {}", token, subject);
            }
            successfulHandler.onSuccessful(token, subject, request, response);
        }
        return true;
    }
    
    /**
     * Callback processing after authentication failure.
     */
    @Override
    protected final boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        if (unsuccessfulHandler != null) {
            if (log.isDebugEnabled()) {
                log.debug("{} can not pass auth, the auth exception message is {}", token, e.getMessage());
            }
            unsuccessfulHandler.onUnsuccessful(token, e, request, response);
        }
        return false;
    }
    
}

interface UnsuccessfulHandler {
    /**
     * Callback processing when auth successful
     *
     * @param token    the token can not pass authentication
     * @param e        the exception thrown during authentication
     * @param request  the incoming <code>ServletRequest</code>
     * @param response the outgoing <code>ServletResponse</code>
     */
    void onUnsuccessful(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response);
}

interface SuccessfulHandler {
    /**
     * Callback processing when auth unsuccessful
     *
     * @param token    the token can pass authentication
     * @param subject  the incoming auth <code>Subject</code>
     * @param request  the incoming <code>ServletRequest</code>
     * @param response the outgoing <code>ServletResponse</code>
     */
    void onSuccessful(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response);
}