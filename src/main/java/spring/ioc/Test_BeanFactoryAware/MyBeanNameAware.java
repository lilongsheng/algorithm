package spring.ioc.Test_BeanFactoryAware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;

@Slf4j
public class MyBeanNameAware implements BeanNameAware {
    @Override
    public void setBeanName(String name) {
        log.info("my name is :{}",name);
    }
}
