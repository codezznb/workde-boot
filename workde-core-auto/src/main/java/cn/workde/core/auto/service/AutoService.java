package cn.workde.core.auto.service;

import java.lang.annotation.*;

/**
 * @author zhujingang
 * @date 2019/9/6 12:20 AM
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoService {
	/**
	 * Returns the interfaces implemented by this service provider.
	 *
	 * @return interface array
	 */
	Class<?>[] value();
}
