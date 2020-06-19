package me.lolico.blog.lang.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Lolico Li
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLock {
    String key() default "";

    long timeout() default 5;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
