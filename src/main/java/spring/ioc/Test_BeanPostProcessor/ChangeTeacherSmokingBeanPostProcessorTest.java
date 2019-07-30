package spring.ioc.Test_BeanPostProcessor;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ChangeTeacherSmokingBeanPostProcessorTest {

    @Test
    public void test2() throws Exception{
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean-post-processor-teacher.xml");
        Teacher teacher = applicationContext.getBean("teacher",Teacher.class);
        teacher.teach();

    }
}
