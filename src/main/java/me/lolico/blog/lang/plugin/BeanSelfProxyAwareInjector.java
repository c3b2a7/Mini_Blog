package me.lolico.blog.lang.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author Lolico Li
 */
@Component
public class BeanSelfProxyAwareInjector implements SystemBootPlugin {
    private static final Logger logger = LoggerFactory.getLogger(BeanSelfProxyAwareInjector.class);

    @Override
    public void onReady(ApplicationContext applicationContext) {
        Map<String, BeanSelfProxyAware> proxyAwareMap = applicationContext.getBeansOfType(BeanSelfProxyAware.class);
        proxyAwareMap.forEach((beanName, beanSelfProxyAware) -> {
            beanSelfProxyAware.setSelfProxy(beanSelfProxyAware);
            if (logger.isDebugEnabled()) {
                logger.debug("Inject self-proxy '{}' for {}", beanSelfProxyAware, beanName);
            }
        });
    }

}
