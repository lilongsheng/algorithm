package spring.ioc.Test_FactoryBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisDB implements DBOperation<RedisDBEntity>{

    public int save(RedisDBEntity t) {
        log.info("save this object"+t.getJsonStr());
        return 1;
    }

    public int update(RedisDBEntity t) {
        log.info("update this object"+t.getJsonStr());
        return 0;
    }

    public int delete(RedisDBEntity t) {
        log.info("delete this object"+t.getJsonStr());
        return 1;
    }

    public RedisDBEntity select(Integer id) {
        log.info("select this object by id "+id);
        return new RedisDBEntity();
    }
}
