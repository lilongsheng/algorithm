package spring.ioc.Test_BeanPostProcessor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Teacher {

    /**
     * 老师的姓名
     */
    private String name;

    /**
     * 年龄
     */
    private int age;

    /**
     * 是否抽烟
     */
    private boolean smoking;

    /**
     * 老师教授的课程
     */
    private String language;

    /**
     * 临时变量，默认抽烟
     */
    private boolean tempSmoking = true;

    public void teach(){
        log.info("I am :"+name+" and I will teach you :"+language + " and I "+(tempSmoking?"will":"will not")+" smoking");
    }


}
