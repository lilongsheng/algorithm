package spring.ioc.Test_BeanPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class ChangeTeacherBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("teacher");
        MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
        if (mutablePropertyValues.contains("smoking")){
            mutablePropertyValues.add("smoking",false);
        }
    }
}
