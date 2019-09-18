package cn.workde.core.builder.config;

import cn.workde.core.builder.engine.ControlBuffer;
import cn.workde.core.builder.engine.ModuleBuffer;
import cn.workde.core.builder.engine.ScriptBuffer;
import cn.workde.core.builder.engine.service.BuilderService;
import cn.workde.core.builder.engine.service.ControlService;
import cn.workde.core.builder.engine.service.IdeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author zhujingang
 * @date 2019/9/17 9:56 PM
 */
@Configuration
public class WorkdeBuilderAutoConfiguration {

	@Bean
	@Order(1)
	public ControlBuffer controlBuffer() {
		return new ControlBuffer();
	}

	@Bean
	@Order(2)
	public ModuleBuffer moduleBuffer() {
		return new ModuleBuffer();
	}

	@Bean
	public BuilderService builderService() {
		return new BuilderService();
	}

	@Bean
	public ControlService controlService() {
		return new ControlService();
	}

	@Bean
	public ScriptBuffer scriptBuffer() {
		return new ScriptBuffer();
	}

	@Bean
	public IdeService ideService() { return new IdeService(); }
}
