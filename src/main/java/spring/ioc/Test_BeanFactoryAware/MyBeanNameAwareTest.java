package spring.ioc.Test_BeanFactoryAware;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MyBeanNameAwareTest {

    @Test
    public void test2() throws Exception{
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean-name-aware.xml");
        MyBeanNameAware myBeanNameAware = applicationContext.getBean("mybeannameaware",MyBeanNameAware.class);
    }
}
