package me.lolicom.blog.config.exception;

/**
 * @author lolicom
 */
public class ExpiredTokenException extends RuntimeException {
    
    public ExpiredTokenException(String message) {
        super(message);
    }
}
