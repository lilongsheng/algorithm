package spring.ioc.Test_BeanPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ChangeConfigSupportMultiEnvironment implements BeanPostProcessor {

    /**
     * 目前所属的环境是dev还是prd
     */
    private String devlocation;

    private String configName;


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            if(bean instanceof DataSourceConnection){
                if(configName.contains("${")&&configName.contains("}")){
                    configName = configName.substring(0,configName.indexOf("${")).concat(devlocation);
                    Properties prop = new Properties();
                    InputStream in = Object.class.getResourceAsStream("/"+configName+".properties");
                    try {
                        prop.load(in);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    DataSourceConnection dsc = (DataSourceConnection)bean;
                    String driver = dsc.getDriver();
                    String jdbcName = dsc.getJdbcName();
                    String password = dsc.getPassword();
                    String url = dsc.getUrl();
                    if(driver.contains("${")&&driver.contains("}")){
                        driver = driver.replace("${", "").replace("}", "");
                    }
                    if(jdbcName.contains("${")&&jdbcName.contains("}")){
                        jdbcName = jdbcName.replace("${", "").replace("}", "");
                    }
                    if(password.contains("${")&&password.contains("}")){
                        password = password.replace("${", "").replace("}", "");
                    }
                    if(url.contains("${")&&url.contains("}")){
                        url = url.replace("${", "").replace("}", "");
                    }
                    driver = prop.getProperty(driver);
                    jdbcName = prop.getProperty(jdbcName);
                    password = prop.getProperty(password);
                    url = prop.getProperty(url);
                    return new DataSourceConnection(driver,jdbcName,password,url);
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

    public void setDevlocation(String devlocation) {
        this.devlocation = devlocation;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }
}
