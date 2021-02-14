package me.lolico.blog.lang.plugin;

import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

/**
 * @author Lolico Li
 */
public interface SystemBootPlugin extends Ordered {

    void onReady(ApplicationContext applicationContext);

    default String getName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    default int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
