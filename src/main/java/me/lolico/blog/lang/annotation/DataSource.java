package me.lolico.blog.lang.annotation;

import java.lang.annotation.*;

/**
 * @author lolico
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DataSource {
    String value() default "";
}
