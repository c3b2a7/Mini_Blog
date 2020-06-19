package me.lolico.blog.lang;

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
public class SystemBootPluginManager implements ApplicationListener<ContextRefreshedEvent>, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(SystemBootPluginManager.class);
    private List<SystemBootPlugin> systemBootPlugins = Collections.emptyList();
    private boolean hasRunOnce = false;

    @Autowired(required = false)
    public void setSystemBootPlugins(List<SystemBootPlugin> systemBootPlugins) {
        Assert.notEmpty(systemBootPlugins, "SystemBootAddons must not be empty");
        OrderComparator.sort(systemBootPlugins);
        this.systemBootPlugins = systemBootPlugins;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!hasRunOnce) {
            ApplicationContext applicationContext = event.getApplicationContext();
            systemBootPlugins.forEach(systemBootPlugin -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("Execute plugin:{}", systemBootPlugin.getName());
                }
                systemBootPlugin.onReady(applicationContext);
            });
            hasRunOnce = true;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("The system plugins have been loaded");
            }
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
