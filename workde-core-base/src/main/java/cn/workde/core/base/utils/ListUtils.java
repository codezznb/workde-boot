package cn.workde.core.base.utils;

import cn.workde.core.base.cover.Convert;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhujingang
 * @date 2019/8/29 9:08 AM
 */
public class ListUtils {

    /**
     * {@linkplain List} - {@linkplain Map} 转 {@linkplain List} - {@linkplain JSONObject}
     * <p>
     * 	<b><i>性能测试说明：</i></b><br>
     * 	<i>测试CPU：</i>i7-4710MQ<br>
     * 	<i>测试结果：</i>百万级数据平均200ms（毫秒）<br>
     * </p>
     *
     * @param list 		需要转换的List
     * @return			转换后的List
     */
    public static List<JSONObject> toJsonList(List<Map<String, Object>> list) {
        List<JSONObject> jsonList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            jsonList.add(new JSONObject(list.get(i)));
        }

        return jsonList;
    }

    /**
     * {@linkplain JSONArray} 转 {@linkplain List} - {@linkplain JSONObject}
     * <p>
     * 	<b><i>性能测试报告：</i></b><br>
     *  <i>无类型转换（类型推断）：</i>见 {@linkplain #toJsonList(List)}<br>
     * 	<i>安全模式强制类型转换：</i>暂未测试<br>
     * </p>
     *
     * @param jsonArray 需要转换的JSONArray
     * @return 转换后的jsonList
     */
    public static List<JSONObject> toJsonList(JSONArray jsonArray) {
        List<JSONObject> jsonList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonList.add(jsonArray.getJSONObject(i));
        }

        return jsonList;
    }

    /**
     * 数组转List
     * <p>此方法为 {@linkplain Arrays#asList(Object...)} 的安全实现</p>
     *
     * @param <T>	数组中的对象类
     * @param array	将被转换的数组
     * @return		被转换数组的列表视图
     */
    public static <T> ArrayList<T> toList(T[] array) {
        ArrayList<T> toList = new ArrayList<>(Arrays.asList(array));
        return toList;
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain List}-{@linkplain Class}
     *
     * @param <T> 		泛型
     * @param list 		需要转换的List
     * @param clazz		json转换的POJO类型
     * @return			转换后的List
     */
    public static <T> List<T> toList(List<JSONObject> list, Class<T> clazz) {
        List<T> toList = new ArrayList<> ();
        for(JSONObject json : list) {
            toList.add(Convert.toJavaBean(json, clazz));
        }

        return toList;
    }

    /**
     * {@linkplain List}-{@linkplain JSONObject} 转 {@linkplain List}-{@linkplain String}
     *
     * @param list 		需要转换的List
     * @param keepKey	保留值的key
     * @return			转换后的List
     */
    public static List<String> toList(List<JSONObject> list, String keepKey) {
        List<String> toList = new ArrayList<> ();
        for(JSONObject json : list) {
            String value = json.getString(keepKey);
            toList.add(value);
        }

        return toList;
    }

    /**
     * 保留相同值
     * @param list	循环第一层
     * @param list2	循环第二层
     * @return 处理后的list
     */
    public static List<String> keepSameValue(List<String> list, List<String> list2) {
        List<String> result = new ArrayList<>();
        list.forEach(str -> {
            list2.forEach(str2 -> {
                if (str.equals(str2)) {
                    result.add(str);
                }
            });
        });
        return result;
    }
}
