package spring.ioc.Test_BeanPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;


public class ChangeTeacherSmokingBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (bean instanceof Teacher){
                Teacher teacher = (Teacher)bean;
                if (teacher.isSmoking()){
                    ((Teacher)bean).setTempSmoking(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
