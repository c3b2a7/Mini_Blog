package me.lolico.blog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.Arrays;

/**
 * @author Lolico Li
 */
public class CglibTest {

    @BeforeEach
    void setUp() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./tools");
    }

    @Test
    void name() {
        Demo target1 = new Demo();
        ProxyFactory proxyFactory = new ProxyFactory(target1);
        proxyFactory.addAdvice((MethodBeforeAdvice) (method, args1, target) ->
                System.out.println("你被拦截了：方法名为：" + method.getName() + " 参数为--" + Arrays.asList(args1))
        );

        Demo demo = (Demo) proxyFactory.getProxy();
        //你被拦截了：方法名为：setAge 参数为--[10]
        demo.setAge(10);

        //你被拦截了：方法名为：getAge 参数为--[]
        System.out.println(demo.getAge()); //10
        System.out.println(demo.age); //null 对你没看错，这里是null
        System.out.println(demo.findAge()); //null 对你没看错，这里是null
    }

    @Test
    void name1() {
        Demo target = new Demo();
        Demo demo = (Demo) Enhancer.create(Demo.class, (MethodInterceptor) (o, method, objects, methodProxy) -> {
            System.out.println("qian");
            Object o1 = method.invoke(target, objects);
            System.out.println("hou");
            return o1;
        });
        demo.setAge(10);

        System.out.println(demo.getAge());
        System.out.println(demo.age);
        System.out.println(demo.findAge());
    }

    @Test
    void name2() {
        Dem dem = new Dem();
        System.out.println(dem.findAge());
    }
}

class Demo {
    public Integer age;

    // 此处用final修饰了  CGLIB也不会代理此方法了
    public final Integer findAge() {
        return age;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

class Dem extends Demo {

}