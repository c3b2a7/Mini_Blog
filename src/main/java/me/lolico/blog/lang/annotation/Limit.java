package me.lolico.blog.lang.annotation;

import me.lolico.blog.lang.LimitLevel;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Lolico Li
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Limit {

    int maxLimit();

    long timeout();

    TimeUnit timeUnit();

    LimitLevel limitLevel() default LimitLevel.Method;

}
