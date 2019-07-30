package spring.ioc.Test_FactoryBean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MysqlDB implements DBOperation<MysqlDBEntity>{

    public int save(MysqlDBEntity t) {
        log.info("save object to mysql");
        return 1;
    }

    public int update(MysqlDBEntity t) {
        log.info("update object to mysql");
        return 0;
    }

    public int delete(MysqlDBEntity t) {
        log.info("delete object from mysql");
        return 0;
    }

    public MysqlDBEntity select(Integer id) {
        return new MysqlDBEntity();
    }

   
}
