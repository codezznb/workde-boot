package cn.workde.core.builder;

import cn.workde.core.boot.launch.WorkdeApplication;
import cn.workde.core.builder.engine.Builder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * @author zhujingang
 * @date 2019/9/17 2:48 PM
 */
@SpringBootApplication
public class BuilderApplication {

	public static void main(String[] args) throws IOException {
		WorkdeApplication.run("builder application", BuilderApplication.class, args);
	}
}
