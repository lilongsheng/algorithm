package spring;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class SpringOtherBean {

    public void say(){
        log.info("{}","l am SpringOtherBean");
    }

}
