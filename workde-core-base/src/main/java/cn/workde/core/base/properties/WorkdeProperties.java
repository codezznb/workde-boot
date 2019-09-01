package cn.workde.core.base.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhujingang
 * @date 2019/8/28 6:14 PM
 */
@Data
@ConfigurationProperties("workde")
public class WorkdeProperties {

    private WorkdeAsyncProperties workdeAsyncProperties;

    /**
     * 装载自定义配置workde.prop.xxx
     */
    @Getter
    private final Map<String, String> prop = new HashMap<>();

    /**
     * 获取配置
     *
     * @param key key
     * @return value
     */
    @Nullable
    public String get(String key) {
        return get(key, null);
    }

    /**
     * 获取配置
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return value
     */
    @Nullable
    public String get(String key, @Nullable String defaultValue) {
        String value = prop.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 获取配置
     *
     * @param key key
     * @return int value
     */
    @Nullable
    public Integer getInt(String key) {
        return getInt(key, null);
    }

    /**
     * 获取配置
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return int value
     */
    @Nullable
    public Integer getInt(String key, @Nullable Integer defaultValue) {
        String value = prop.get(key);
        if (value != null) {
            return Integer.valueOf(value.trim());
        }
        return defaultValue;
    }

    /**
     * 获取配置
     *
     * @param key key
     * @return long value
     */
    @Nullable
    public Long getLong(String key) {
        return getLong(key, null);
    }

    /**
     * 获取配置
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return long value
     */
    @Nullable
    public Long getLong(String key, @Nullable Long defaultValue) {
        String value = prop.get(key);
        if (value != null) {
            return Long.valueOf(value.trim());
        }
        return defaultValue;
    }

    /**
     * 获取配置
     *
     * @param key key
     * @return Boolean value
     */
    @Nullable
    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    /**
     * 获取配置
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return Boolean value
     */
    @Nullable
    public Boolean getBoolean(String key, @Nullable Boolean defaultValue) {
        String value = prop.get(key);
        if (value != null) {
            value = value.toLowerCase().trim();
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    /**
     * 获取配置
     *
     * @param key key
     * @return double value
     */
    @Nullable
    public Double getDouble(String key) {
        return getDouble(key, null);
    }

    /**
     * 获取配置
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return double value
     */
    @Nullable
    public Double getDouble(String key, @Nullable Double defaultValue) {
        String value = prop.get(key);
        if (value != null) {
            return Double.parseDouble(value.trim());
        }
        return defaultValue;
    }

    /**
     * 判断是否存在key
     *
     * @param key prop key
     * @return boolean
     */
    public boolean containsKey(String key) {
        return prop.containsKey(key);
    }
}
