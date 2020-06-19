package me.lolico.blog.lang.exception;

/**
 * @author lolico
 */
public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException(String message) {
        super(message);
    }
}
