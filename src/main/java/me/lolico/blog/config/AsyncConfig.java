package me.lolico.blog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.ErrorHandler;

/**
 * @author Lolico li
 */
@Configuration
@EnableAsync(mode = AdviceMode.ASPECTJ)
public class AsyncConfig {

    @Bean(AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME)
    public ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        // multicaster.setTaskExecutor(..); //async multicaster
        // multicaster.setErrorHandler(new LoggingErrorHandler());
        return multicaster;
    }

    private static class LoggingErrorHandler implements ErrorHandler {

        private static final Logger logger = LoggerFactory.getLogger(LoggingErrorHandler.class);

        @Override
        public void handleError(Throwable throwable) {
            logger.warn("Error calling ApplicationEventListener", throwable);
        }
    }

}

