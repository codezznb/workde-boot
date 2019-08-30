package cn.workde.core.tk.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.BaseMapper;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.util.Date;

/**
 * @author zhujingang
 * @date 2019/8/29 4:36 PM
 */
public class BaseService<T extends BaseEntity, M extends BaseMapper<T>> {

    @Autowired
    protected M mapper;

    private Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public boolean unique(String property, String value) {
        Example example = new Example(entityClass);
        example.createCriteria().andEqualTo(property, value);
        int result = mapper.selectCountByExample(example);
        if (result > 0) {
            return false;
        }
        return true;
    }

    public T one(T entity){
        return mapper.selectOne(entity);
    }

    public T byId(Long id) { return mapper.selectByPrimaryKey(id); }

    public T save(T entity) {
        int result = 0;
        Date currentDate = new Date();
        entity.setUpdated(currentDate);

        // 创建
        if (entity.getId() == null) {

            entity.setCreated(currentDate);

            /**
             * 用于自动回显 ID，领域模型中需要 @ID 注解的支持
             * {@link AbstractBaseDomain}
             */
            result = mapper.insertUseGeneratedKeys(entity);
        }

        // 更新
        else {
            result = mapper.updateByPrimaryKey(entity);
        }

        // 保存数据成功
        if (result > 0) {
            return entity;
        }

        // 保存数据失败
        return null;
    }

    public PageInfo<T> page(T entity, int pageNum, int pageSize) {
        Example example = new Example(entityClass);
        example.createCriteria().andEqualTo(entity);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<T> pageInfo = new PageInfo<>(mapper.selectByExample(example));
        return pageInfo;
    }
}
