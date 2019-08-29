package cn.workde.core.base.utils;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author zhujingang
 * @date 2019/8/29 9:28 AM
 */
public class MapUtils extends MapUtil {

    /**
     * 不可变的空JSON常量
     */
    public final static JSONObject FINAL_EMPTY_JSON = new JSONObject();

    /**
     * 判断Map数据结构key的一致性
     * @param paramMap			参数
     * @param mustContainKeys	必须包含的key（必传）
     * @param canContainKeys	可包含的key（非必传）
     * @return 是否满足条件
     */
    public static boolean isKeys(Map<String, Object> paramMap, String[] mustContainKeys, String... canContainKeys) {
        // 1. 必传参数校验
        for (String key : mustContainKeys) {
            if (!paramMap.containsKey(key)) {
                return false;
            }
        }

        // 2. 无可选参数
        if (StringUtils.isEmptys(canContainKeys)) {
            return true;
        }

        // 3. 可选参数校验-确认paramMap大小
        int keySize = mustContainKeys.length + canContainKeys.length;
        if (paramMap.size() > keySize) {
            return false;
        }

        // 4. 获得paramMap中包含可包含key的大小
        int paramMapCanContainKeysLength = 0;
        for (String key : canContainKeys) {
            if (paramMap.containsKey(key)) {
                paramMapCanContainKeysLength++;
            }
        }

        // 5. 确认paramMap中包含的可包含key大小 + 必须包含key大小 是否等于 paramMap大小
        if (paramMapCanContainKeysLength + mustContainKeys.length != paramMap.size()) {
            return false;
        }

        // 6. 通过所有校验，返回最终结果
        return true;
    }

    /**
     * 判断Map数组是否为空<br>
     * <p>弱判断，只确定数组中第一个元素是否为空</p>
     * @param paramMaps 要判断的Map[]数组
     * @return Map数组==null或长度==0或第一个元素为空（true）
     */
    public static boolean isEmptys(Map<String, Object>[] paramMaps) {
        return (null == paramMaps || paramMaps.length == 0 || paramMaps[0].isEmpty()) ? true : false;
    }

    /**
     * 获取所有的key
     * @param paramMap 需要获取keys的map
     * @return keyList
     */
    public static List<String> keyList(Map<String, Object> paramMap) {
        List<String> list = new ArrayList<>();
        paramMap.keySet().forEach(action -> {
            list.add(action);
        });
        return list;
    }

    /**
     * 批量移除
     * @param paramMap 要操作的Map
     * @param keys 被移除的key数组
     */
    public static void remove(Map<String, Object> paramMap, String[] keys) {
        for (String key : keys) {
            paramMap.remove(key);
        }
    }

    /**
     * 移除空对象
     * @param paramMap 要操作的Map
     */
    public static void removeEmpty(Map<String, Object> paramMap) {
        Iterator<Map.Entry<String, Object>> iter = paramMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            Object value = entry.getValue();
            if (ObjectUtils.isNull(value)) {
                iter.remove();
            }
        }
    }
}
