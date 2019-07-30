package spring.ioc.Test_BeanFactoryAware;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MyBeanFactoryAwareTest {

    @Test
    public void test2() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean-factory-aware.xml");
        MyBeanFactoryAware mybeanFactoryAware = applicationContext.getBean("mybeanFactoryAware", MyBeanFactoryAware.class);

    }
}
