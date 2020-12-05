package me.lolico.blog.lang.plugin;

import me.lolico.blog.lang.SpelAnnotationResolver;
import me.lolico.blog.util.JwtUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Lolico Li
 */
@Component
public class AnnotationAutowiredStaticFieldInjector implements SystemBootPlugin {

    @Override
    public void onReady(ApplicationContext applicationContext) {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBean(new JwtUtils());
        beanFactory.autowireBean(new SpelAnnotationResolver());
    }
}
