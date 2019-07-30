package spring.ioc.Test_FactoryBean;

import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProxyDBObjectTest {

    @Test
    public void test2() throws Exception{
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("factory_bean.xml");
        DBOperation dBoperation =  applicationContext.getBean("proxyDB",DBOperation.class);

        FactoryBean factoryBean =  applicationContext.getBean("&proxyDB",FactoryBean.class);
        DBOperation db = (DBOperation)factoryBean.getObject();
        MysqlDBEntity dbEntity = new MysqlDBEntity();
        dBoperation.save(dbEntity);
    }

}
