package tk.mybatis.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 自动的 Mapper
 * 特别注意，该接口不能被扫描到，否则会出错
 * @author zhujingang
 * @date 2019/8/29 4:21 PM
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
