package me.lolico.blog.lang.annotation;

import me.lolico.blog.lang.aspect.WebLogAspect;

import java.lang.annotation.*;

/**
 * @deprecated {@link WebLogAspect#pointcut()}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Deprecated
public @interface WebLog {
    String name();

    boolean intoDb() default false;

}