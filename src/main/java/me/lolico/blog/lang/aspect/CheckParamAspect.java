package me.lolico.blog.lang.aspect;

import me.lolico.blog.lang.annotation.CheckParam;
import me.lolico.blog.lang.exception.NullParamException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @author lolico
 */
@Aspect
@Component
public final class CheckParamAspect {

    @Pointcut(value = "@annotation(checkParam)", argNames = "checkParam")
    public void pointcut(CheckParam checkParam) {
    }

    @Around(value = "pointcut(checkParam)", argNames = "pjp,checkParam")
    public Object checkParam(ProceedingJoinPoint pjp, CheckParam checkParam) throws Throwable {
        int[] index = checkParam.index();
        Object[] args = pjp.getArgs();

        for (int i : index) {
            if (args[i] == null && i <= args.length - 1) {
                String parameterName = ((MethodSignature) pjp.getSignature()).getParameterNames()[i];
                Class<?> parameterType = ((MethodSignature) pjp.getSignature()).getParameterTypes()[i];
                throw new NullParamException(parameterName, parameterType);
            }
        }
        return pjp.proceed();
    }
}
