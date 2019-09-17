package cn.workde.core.cache.utils;

import cn.workde.core.base.utils.ObjectUtils;
import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.base.utils.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

/**
 * 缓存工具类
 * @author zhujingang
 * @date 2019/9/17 4:07 PM
 */
public class CacheUtils {

	private static CacheManager cacheManager = SpringUtils.getBean(CacheManager.class);

	/**
	 * 获取缓存对象
	 *
	 * @param cacheName 缓存名
	 * @return Cache
	 */
	public static Cache getCache(String cacheName) {
		return cacheManager.getCache(cacheName);
	}

	/**
	 * 获取缓存
	 *
	 * @param cacheName 缓存名
	 * @param keyPrefix 缓存键前缀
	 * @param key       缓存键值
	 * @return
	 */
	@Nullable
	public static Object get(String cacheName, String keyPrefix, Object key) {
		if (ObjectUtils.hasEmpty(cacheName, keyPrefix, key)) {
			return null;
		}
		return getCache(cacheName).get(keyPrefix.concat(String.valueOf(key))).get();
	}

	/**
	 * 获取缓存
	 *
	 * @param cacheName 缓存名
	 * @param keyPrefix 缓存键前缀
	 * @param key       缓存键值
	 * @param type
	 * @param <T>
	 * @return
	 */
	@Nullable
	public static <T> T get(String cacheName, String keyPrefix, Object key, @Nullable Class<T> type) {
		if (ObjectUtils.hasEmpty(cacheName, keyPrefix, key)) {
			return null;
		}
		return getCache(cacheName).get(keyPrefix.concat(String.valueOf(key)), type);
	}

	/**
	 * 获取缓存
	 *
	 * @param cacheName   缓存名
	 * @param keyPrefix   缓存键前缀
	 * @param key         缓存键值
	 * @param valueLoader 重载对象
	 * @param <T>
	 * @return
	 */
	@Nullable
	public static <T> T get(String cacheName, String keyPrefix, Object key, Callable<T> valueLoader) {
		if (ObjectUtils.hasEmpty(cacheName, keyPrefix, key)) {
			return null;
		}
		try {
			return getCache(cacheName).get(keyPrefix.concat(String.valueOf(key)), valueLoader);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 设置缓存
	 *
	 * @param cacheName 缓存名
	 * @param keyPrefix 缓存键前缀
	 * @param key       缓存键值
	 * @param value     缓存值
	 */
	public static void put(String cacheName, String keyPrefix, Object key, @Nullable Object value) {
		getCache(cacheName).put(keyPrefix.concat(String.valueOf(key)), value);
	}

	/**
	 * 清除缓存
	 *
	 * @param cacheName 缓存名
	 * @param keyPrefix 缓存键前缀
	 * @param key       缓存键值
	 */
	public static void evict(String cacheName, String keyPrefix, Object key) {
		if (ObjectUtils.hasEmpty(cacheName, keyPrefix, key)) {
			return;
		}
		getCache(cacheName).evict(keyPrefix.concat(String.valueOf(key)));
	}

	/**
	 * 清空缓存
	 *
	 * @param cacheName 缓存名
	 */
	public static void clear(String cacheName) {
		if (ObjectUtils.isEmpty(cacheName)) {
			return;
		}
		getCache(cacheName).clear();
	}

}
