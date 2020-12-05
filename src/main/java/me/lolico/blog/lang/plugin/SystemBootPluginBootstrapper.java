package me.lolico.blog.lang.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * @author Lolico Li
 */
@Component
public class SystemBootPluginBootstrapper implements ApplicationListener<ContextRefreshedEvent>, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(SystemBootPluginBootstrapper.class);
    private List<SystemBootPlugin> systemBootPlugins = Collections.emptyList();
    private final Object monitor = new Object();
    private boolean hasRunOnce = false;

    @Autowired(required = false)
    public void setSystemBootPlugins(List<SystemBootPlugin> systemBootPlugins) {
        Assert.notEmpty(systemBootPlugins, "systemBootPlugins must not be empty");
        OrderComparator.sort(systemBootPlugins);
        this.systemBootPlugins = systemBootPlugins;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!hasRunOnce) {
            synchronized (monitor) {
                if (!hasRunOnce) {
                    logger.info("Found {} system boot plugin(s)", systemBootPlugins.size());
                    bootSystemPlugins(event.getApplicationContext());
                    hasRunOnce = true;
                }
            }
        }
    }

    private void bootSystemPlugins(ApplicationContext applicationContext) {
        int i = 0;
        for (SystemBootPlugin plugin : systemBootPlugins) {
            if (logger.isDebugEnabled()) {
                logger.info("Booting {}th plugin: {}", ++i, plugin.getName());
            }
            plugin.onReady(applicationContext);
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
