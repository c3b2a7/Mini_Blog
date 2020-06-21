package me.lolico.blog.lang.aspect;

import me.lolico.blog.lang.DynamicDataSourceContextHolder;
import me.lolico.blog.lang.annotation.DataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * @author lolico
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public final class DataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);

    @Pointcut("@annotation(me.lolico.blog.lang.annotation.DataSource)")
    public void pointcut() {
    }

    @Before(value = "pointcut() && @annotation(dataSource)")
    public void doBefore(JoinPoint jp, DataSource dataSource) {
        String value = dataSource.value();
        DynamicDataSourceContextHolder.setKey(value);
        logger.debug("使用数据库{}", value);
    }

    @AfterReturning("pointcut()")
    public void doAfterReturning() {
        DynamicDataSourceContextHolder.remove();
    }
}
