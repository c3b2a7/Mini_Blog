package me.lolico.blog.lang.annotation;

import me.lolico.blog.lang.LimitLevel;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Lolico Li
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Limit {

    /**
     * @return the key used for generate redis key.
     */
    String key() default "";

    int maxLimit() default Integer.MAX_VALUE;

    long timeout() default 1;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Only used when {@link #key()} is null or empty. In most cases, you only need to
     * set this limit level to automatically generate the key, you can also customize
     * the value of {@link #key()} if necessary.
     *
     * @return limit level
     */
    LimitLevel limitLevel() default LimitLevel.Method;

}
