package spring.ioc.Test_FactoryBean;

import org.springframework.beans.factory.FactoryBean;

public class ProxyDBObject implements FactoryBean<Object> {

    private String currentDB;


    public String getCurrentDB() {
        return currentDB;
    }

    public void setCurrentDB(String currentDB) {
        this.currentDB = currentDB;
    }

    public Object getObject() throws Exception {
        if("mysql".equals(currentDB)){
            return new MysqlDB();
        }
        return new RedisDB();
    }

    public Class<?> getObjectType() {
        if("mysql".equals(currentDB)){
            return MysqlDB.class;
        }
        return RedisDB.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
