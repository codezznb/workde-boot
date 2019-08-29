package cn.workde.core.sample.boot;

import cn.workde.core.boot.launch.WorkdeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhujingang
 * @date 2019/8/28 10:19 PM
 */
@SpringBootApplication
public class BootApplication {

    public static void main(String[] args) {
        WorkdeApplication.run("workde application", BootApplication.class, args);
    }
}
