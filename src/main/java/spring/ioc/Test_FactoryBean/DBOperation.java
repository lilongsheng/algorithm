package spring.ioc.Test_FactoryBean;

public interface DBOperation<T extends DBEntity>{

    int save(T t);

    int update(T t);

    int delete(T t);

    T select(Integer id);
}
