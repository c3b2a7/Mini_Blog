package me.lolico.blog.lang.aspect;

import me.lolico.blog.lang.annotation.Functional;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author lolico
 */
@Aspect
@Component
public final class FunctionalAspect implements ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(FunctionalAspect.class);
    private boolean contextHasRefreshed;

    /**
     * @param event ContextRefreshedEvent object
     * @see org.springframework.boot.context.event
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("ApplicationContext '{}' has refreshed", event.getApplicationContext());
        }
        this.contextHasRefreshed = true;
    }

    @Pointcut("@annotation(functional)")
    public void cut(Functional functional) {
    }

    @Before(value = "cut(functional)", argNames = "jp,functional")
    public void around(JoinPoint jp, Functional functional) {
        if (!contextHasRefreshed) {
            throw new IllegalStateException("Application context is not refresh");
        }
    }

}
