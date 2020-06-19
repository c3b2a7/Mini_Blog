package me.lolico.blog.lang;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Lolico Li
 */
public class SpelAnnotationResolver implements BeanFactoryAware {
    private static final SpelExpressionParser parser = new SpelExpressionParser();
    private static final ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
    private static BeanFactory beanFactory;


    public static Object getValue(String spel, JoinPoint jp) {
        return parser.parseExpression(spel).getValue(prepareEvaluationContext(((MethodSignature) jp.getSignature()).getMethod(), jp.getArgs()));
    }

    public static <T> T getValue(String spel, JoinPoint jp, Class<T> desiredResultType) {
        return parser.parseExpression(spel).getValue(prepareEvaluationContext(((MethodSignature) jp.getSignature()).getMethod(), jp.getArgs()), desiredResultType);
    }

    public static EvaluationContext prepareEvaluationContext(Method method, Object[] arguments) {
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(TypedValue.NULL, method, arguments, discoverer);
        if (beanFactory != null && evaluationContext.getBeanResolver() == null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    public static EvaluationContext prepareEvaluationContext(JoinPoint jp) {
        return prepareEvaluationContext(new StandardEvaluationContext(), jp);
    }

    public static EvaluationContext prepareEvaluationContext(EvaluationContext evaluationContext, JoinPoint jp) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Object[] args = jp.getArgs();
        String[] paraNames = methodSignature.getParameterNames();
        int argCount = args.length;
        int paramCount = paraNames != null ? paraNames.length : methodSignature.getParameterTypes().length;
        for (int i = 0; i < paramCount; i++) {
            Object value = null;
            if (argCount > paramCount && i == paramCount - 1) {
                value = Arrays.copyOfRange(args, i, argCount);
            } else if (argCount > i) {
                value = args[i];
            }
            evaluationContext.setVariable("a" + i, value);
            evaluationContext.setVariable("p" + i, value);
            evaluationContext.setVariable("?" + i, value);
            if (paraNames != null) {
                evaluationContext.setVariable(paraNames[i], value);
            }
        }
        if (beanFactory != null && evaluationContext.getBeanResolver() == null && evaluationContext instanceof StandardEvaluationContext) {
            ((StandardEvaluationContext) evaluationContext).setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    @Autowired
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SpelAnnotationResolver.beanFactory = beanFactory;
    }
}
