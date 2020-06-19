package me.lolico.blog.lang;

import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author Lolico Li
 */
@Component
public interface SystemBootPlugin extends Ordered {
    void onReady(ApplicationContext applicationContext);

    default String getName() {
        return this.getClass().getCanonicalName();
    }

    default int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}
