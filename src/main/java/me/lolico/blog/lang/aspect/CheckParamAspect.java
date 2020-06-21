package me.lolico.blog.lang.aspect;

import me.lolico.blog.lang.annotation.CheckParam;
import me.lolico.blog.lang.exception.NullParamException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @author lolico
 */
@Aspect
@Component
public final class CheckParamAspect {

    @Pointcut(value = "@annotation(checkParam)")
    public void pointcut(CheckParam checkParam) {
    }

    @Before(value = "pointcut(checkParam)", argNames = "joinPoint,checkParam")
    public void doBefore(JoinPoint joinPoint, CheckParam checkParam) {
        int[] index = checkParam.index();
        Object[] args = joinPoint.getArgs();

        for (int i : index) {
            if (args[i] == null && i <= args.length - 1) {
                String parameterName = ((MethodSignature) joinPoint.getSignature()).getParameterNames()[i];
                Class<?> parameterType = ((MethodSignature) joinPoint.getSignature()).getParameterTypes()[i];

                throw new NullParamException(parameterName, parameterType);
            }
        }
    }
}
