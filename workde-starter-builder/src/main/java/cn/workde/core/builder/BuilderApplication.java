package cn.workde.core.builder;

import cn.workde.core.boot.launch.WorkdeApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhujingang
 * @date 2019/9/17 2:48 PM
 */
@SpringBootApplication
public class BuilderApplication {

	public static void main(String[] args) {
		WorkdeApplication.run("builder application", BuilderApplication.class, args);
	}
}
