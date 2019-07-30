package spring;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import spring.ioc.SpringSimpleMultiBean;

public class SpringSimpleMultiBeanTest {

    @Test
    public void test2() throws Exception{

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-init.xml");
        SpringSimpleMultiBean bean = applicationContext.getBean("springMultiBean", SpringSimpleMultiBean.class);
        bean.say();

        		 SpringOtherBean springOtherBean = applicationContext.getBean("springOtherBean",SpringOtherBean.class);
        		 springOtherBean.say();
    }
}
