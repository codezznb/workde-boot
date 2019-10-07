package cn.workde.core.alisms.config;


import cn.workde.core.alisms.properties.AlismsProperties;
import cn.workde.core.alisms.service.IAliyunService;
import cn.workde.core.alisms.service.impl.AliyunServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhujingang
 * @date 2019/9/23 9:01 AM
 */
@Configuration
@EnableConfigurationProperties(AlismsProperties.class)
public class WorkdeSmsAutoConfiguration {

	@Bean
	public IAliyunService aliyunService() {
		return new AliyunServiceImpl();
	}
}
