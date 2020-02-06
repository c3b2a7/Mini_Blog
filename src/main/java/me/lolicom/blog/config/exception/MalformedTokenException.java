package me.lolicom.blog.config.exception;

/**
 * @author lolicom
 */
public class MalformedTokenException extends RuntimeException {
    
    public MalformedTokenException(String message) {
        super(message);
    }
}
