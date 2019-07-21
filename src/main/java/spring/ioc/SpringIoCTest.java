package spring.ioc;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringIoCTest {


    @Test
    public void test1() {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean.xml");
        spring.ioc.SayService sayService = (spring.ioc.SayService) applicationContext.getBean("test");
        sayService.say();
    }
}
