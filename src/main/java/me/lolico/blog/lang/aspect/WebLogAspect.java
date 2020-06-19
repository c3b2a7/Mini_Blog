package me.lolico.blog.lang.aspect;

import me.lolico.blog.lang.annotation.WebLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * @author Lolico Li
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);
    private static final String START_TIME = "START_TIME";
    private static final String REQUEST_ARGS_INFO = "REQUEST_PARAMS";
    private final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    @Pointcut("execution(* me.lolico.blog.web..*Controller.*(..))")
    public void pointcut() {
    }

    @Before(value = "pointcut() && @annotation(webLog)")
    public void doBefore(JoinPoint jp, WebLog webLog) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> threadInfo = new HashMap<>();
        threadInfo.put(START_TIME, startTime);
        List<String> requestArgsInfo = new ArrayList<>();
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        String[] paraNames = methodSignature.getParameterNames();
        Object[] args = jp.getArgs();
        int argCount = args.length;
        int paramCount = paraNames != null ? paraNames.length : methodSignature.getParameterTypes().length;
        for (int i = 0; i < paramCount; i++) {
            Object value = null;
            if (argCount > paramCount && i == paramCount - 1) {
                value = Arrays.copyOfRange(args, i, argCount);
            } else if (argCount > i) {
                value = args[i];
            }
            if (paraNames != null) {
                requestArgsInfo.add(paraNames[i] + ":" + value);
            }
        }
        threadInfo.put(REQUEST_ARGS_INFO, requestArgsInfo.toString());
        threadLocal.set(threadInfo);
        logger.info("{}接口开始调用:requestData={}", webLog.name(), threadInfo.get(REQUEST_ARGS_INFO));
    }

    @AfterReturning(value = "pointcut() && @annotation(webLog)", returning = "result")
    public void doAfterReturning(WebLog webLog, Object result) {
        Map<String, Object> threadInfo = threadLocal.get();
        long takeTime = System.currentTimeMillis() - (long) threadInfo.getOrDefault(START_TIME, System.currentTimeMillis());
        if (webLog.intoDb()) {
            //TODO insert log to database;
            // log name, request params, result, take time
        }
        threadLocal.remove();
        logger.info("{}接口结束调用:耗时={}ms,result={}", webLog.name(), takeTime, result);
    }

    @AfterThrowing(value = "pointcut() && @annotation(webLog)", throwing = "throwable")
    public void doAfterThrowing(WebLog webLog, Throwable throwable) {
        Map<String, Object> threadInfo = threadLocal.get();
        if (webLog.intoDb()) {
            //TODO insert log to database;
            // log name, request params
        }
        threadLocal.remove();
        logger.error("{}接口调用异常，异常信息{}", webLog.name(), throwable);
    }
}
