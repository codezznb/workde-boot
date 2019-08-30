package cn.workde.core.redis.config;

import cn.workde.core.redis.Redis;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author zhujingang
 * @date 2019/8/30 2:31 PM
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class WorkdeRedisAutoConfiguration {

	@Bean
	@Primary
	@ConditionalOnBean(RedisTemplate.class)
	public Redis redis(RedisTemplate redisTemplate) {
		return new Redis(redisTemplate);
	}
}
