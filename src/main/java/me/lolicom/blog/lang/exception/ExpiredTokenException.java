package me.lolicom.blog.lang.exception;

/**
 * @author lolicom
 */
public class ExpiredTokenException extends RuntimeException {
    
    public ExpiredTokenException(String message) {
        super(message);
    }
}
