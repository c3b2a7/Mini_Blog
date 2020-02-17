package me.lolicom.blog.lang;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author lolicom
 */
@Slf4j
@Aspect
@Component
public class FunctionalAspect {

    private boolean contextHasRefreshed;

    @EventListener(ContextRefreshedEvent.class)
    public void setContextHasRefreshed(ContextRefreshedEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("ApplicationContext '{}' has refreshed", event.getApplicationContext());
        }
        this.contextHasRefreshed = true;
    }

    @Pointcut("@annotation(Functional)")
    public void cut() {
    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        if (!contextHasRefreshed) {
            throw new IllegalStateException("Application context is not refreshed");
        }
        return pjp.proceed();
    }
}
