package me.lolico.blog.lang.annotation;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Lolico Li
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLock {
    /**
     * Support spel, just like {@link Cacheable#key()}.
     *
     * @return the spel expression for computing the key dynamically
     */
    String key() default "";

    long timeout() default 5;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
