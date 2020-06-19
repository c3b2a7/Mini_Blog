package me.lolico.blog.web;

import me.lolico.blog.lang.annotation.CheckParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author Lolico li
 */
@Component
// @Async
public class LogReporter {

    private final Logger logger = LoggerFactory.getLogger(LogReporter.class);

    public static LogEvent logEvent(Object source) {
        return new LogEvent(source);
    }

    // @Async
    @EventListener(LogEvent.class)
    public void log(LogEvent event) {
        logger.info("getInfo()");
        getInfo(event);
    }

    @CheckParam(index = 0)
    private void getInfo(LogEvent event) {
        logger.info(event.getSource().toString());
    }

    static class LogEvent extends ApplicationEvent {

        public LogEvent(Object source) {
            super(source);
        }
    }

}
