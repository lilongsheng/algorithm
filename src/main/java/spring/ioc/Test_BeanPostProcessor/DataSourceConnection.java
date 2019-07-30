package spring.ioc.Test_BeanPostProcessor;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
@Data
public class DataSourceConnection {

    private String driver;

    private String jdbcName;

    private String password;

    private String url;

    /**
     * 这里得出数据库链接的各个属性
     */
    public void initConnection() {
        log.info("I am database connection ,and I get connection by dirver :" + driver + " and jdbcName :" + jdbcName + " and password:" + password);
    }


    public DataSourceConnection(String driver, String jdbcName,
                                String password, String url) {
        this.driver = driver;
        this.jdbcName = jdbcName;
        this.password = password;
        this.url = url;
    }

}
