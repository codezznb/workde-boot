package cn.workde.core.boot.config;

import cn.workde.core.base.properties.WorkdeProperties;
import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.base.validation.Validator;
import cn.workde.core.boot.json.JacksonObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * @author zhujingang
 * @date 2019/8/28 6:13 PM
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(WorkdeProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Import({ExceptionHandlerConfig.class})
@AllArgsConstructor
public class WorkdeAutoConfiguration {

    private WorkdeProperties workdeProperties;

	@Bean
	@ConditionalOnMissingBean
	public Validator validator(){
		log.info("【初始化配置-校验器】Bean：Validator ... 已初始化完毕。");
		return new Validator();
	}

	@Bean
	public SpringUtils springUtils() {
		return new SpringUtils();
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper jacksonObjectMapper() {
		return new JacksonObjectMapper();
	}


}
