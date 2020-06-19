package me.lolico.blog.lang.aspect;

import me.lolico.blog.lang.DynamicDataSourceContextHolder;
import me.lolico.blog.lang.SpelAnnotationResolver;
import me.lolico.blog.lang.annotation.DataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public final class DataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);

    @Pointcut(value = "@annotation(dataSource)", argNames = "dataSource")
    public void pointcut(DataSource dataSource) {
    }

    @Around(value = "pointcut(dataSource)", argNames = "pjp,dataSource")
    public Object around(ProceedingJoinPoint pjp, DataSource dataSource) throws Throwable {
        String value = dataSource.value();
        String parseValue = SpelAnnotationResolver.getValue(value, pjp, String.class);
        DynamicDataSourceContextHolder.setKey(parseValue);
        logger.debug("使用数据库{}", value);
        Object proceed = pjp.proceed();
        DynamicDataSourceContextHolder.remove();
        return proceed;
    }

}
