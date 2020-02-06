package me.lolicom.blog.config.exception;

/**
 * @author lolicom
 */
public class IncorrectIssuerException extends RuntimeException {
    
    public IncorrectIssuerException(String message) {
        super(message);
    }
}
