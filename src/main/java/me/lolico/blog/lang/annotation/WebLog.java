package me.lolico.blog.lang.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebLog {
    String name();

    boolean intoDb() default false;

}