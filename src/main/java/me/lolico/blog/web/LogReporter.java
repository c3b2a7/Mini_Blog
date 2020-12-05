package me.lolico.blog.web;

import me.lolico.blog.lang.annotation.CheckParam;
import me.lolico.blog.lang.plugin.BeanSelfProxyAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Lolico li
 */
@Component
public class LogReporter implements BeanSelfProxyAware {

    private final Logger logger = LoggerFactory.getLogger(LogReporter.class);
    private LogReporter proxy;

    public static LogEvent logEvent(Object source) {
        return new LogEvent(source);
    }

    @Async
    @EventListener(LogEvent.class)
    public void log(LogEvent event) {
        logger.info("getInfo()");
        proxy.getInfo(event);
    }

    @Async
    @CheckParam(index = 0)
    public void getInfo(LogEvent event) {
        logger.info(event.getSource().toString());
    }

    @Override
    public void setSelfProxy(Object proxy) {
        this.proxy = (LogReporter) proxy;
    }

    static class LogEvent extends ApplicationEvent {

        public LogEvent(Object source) {
            super(source);
        }
    }

}
