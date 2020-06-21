package me.lolico.blog.lang.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.lolico.blog.util.RequestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Lolico Li
 */
@Aspect
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class WebLogAspect {

    public static final String EMPTY_BODY = "EMPTY";
    public static final String BINARY_DATA_BODY = "BINARY DATA";

    public final ObjectMapper objectMapper;

    public WebLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("execution(*  *..*.*.controller..*Controller.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object webLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Get request attribute
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();

        printRequestLog(request, className, methodName, args);
        long start = System.currentTimeMillis();
        Object returnObj;
        try {
            returnObj = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("{}#{}({}) {}: [{}], Usage: [{}]ms",
                    className,
                    methodName,
                    Arrays.toString(args),
                    throwable.getClass().getSimpleName(),
                    throwable.getMessage(),
                    System.currentTimeMillis() - start);
            throw throwable; // let controller advice do its work.
        }
        printResponseLog(request, className, methodName, returnObj, System.currentTimeMillis() - start);
        return returnObj;
    }

    private void printRequestLog(HttpServletRequest request, String clazzName, String methodName, Object[] args) throws JsonProcessingException {
        log.debug("Request URL: [{}], URI: [{}], Request Method: [{}], IP: [{}]",
                request.getRequestURL(),
                request.getRequestURI(),
                request.getMethod(),
                RequestUtils.getIp(request));

        if (args == null || !log.isDebugEnabled()) {
            return;
        }

        boolean shouldNotLog = false;
        for (Object arg : args) {
            if (arg == null ||
                    arg instanceof HttpServletRequest ||
                    arg instanceof HttpServletResponse ||
                    arg instanceof MultipartFile ||
                    arg.getClass().isAssignableFrom(MultipartFile[].class)) {
                shouldNotLog = true;
                break;
            }
        }

        if (!shouldNotLog) {
            String requestBody = objectMapper.writeValueAsString(args);
            log.debug("{}#{} Parameters: [{}]", clazzName, methodName, requestBody);
        }
    }

    @SuppressWarnings("rawtypes")
    private void printResponseLog(HttpServletRequest request, String className, String methodName, Object returnObj, long usage) throws JsonProcessingException {
        if (log.isDebugEnabled()) {
            String returnData = "";
            if (returnObj != null) {
                if (returnObj instanceof ResponseEntity) {
                    ResponseEntity responseEntity = (ResponseEntity) returnObj;
                    Object body = responseEntity.getBody();
                    if (body instanceof Resource ||
                            body instanceof BufferedImage) {
                        returnData = BINARY_DATA_BODY;
                    } else {
                        returnData = body == null ? EMPTY_BODY : toString(body);
                    }
                } else {
                    returnData = toString(returnObj);
                }

            }
            log.debug("{}#{} Response: [{}], Usage: [{}]ms", className, methodName, returnData, usage);
        }
    }

    @NonNull
    private String toString(@NonNull Object obj) throws JsonProcessingException {
        Assert.notNull(obj, "Return object must not be null");

        String toString;
        if (obj.getClass().isAssignableFrom(byte[].class) &&
                obj instanceof Resource ||
                obj instanceof BufferedImage) {
            toString = BINARY_DATA_BODY;
        } else {
            toString = objectMapper.writeValueAsString(obj);
        }
        return toString;
    }
}

