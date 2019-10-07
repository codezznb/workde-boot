package cn.workde.core.tk.base;

import cn.workde.core.base.utils.ObjectUtils;
import cn.workde.core.base.utils.StringUtils;
import cn.workde.core.base.utils.IdUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.BaseMapper;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhujingang
 * @date 2019/8/29 4:36 PM
 */
public class BaseService<T extends BaseEntity, M extends BaseMapper<T>> {

    @Autowired
    protected M mapper;

    private Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	public List<T> all() {
		return mapper.selectAll();
	}

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

    public T byId(String id) { return mapper.selectByPrimaryKey(id); }

    public T save(T entity) {
        int result = 0;
        Date currentDate = new Date();
        entity.setUpdated(currentDate);

        // 创建
        if (entity.getId() == null) {
			entity.setId(IdUtils.getId().toString());
            entity.setCreated(currentDate);

            /**
             * 用于自动回显 ID，领域模型中需要 @ID 注解的支持
             * {@link AbstractBaseDomain}
             */
            //result = mapper.insertUseGeneratedKeys(entity);
			result = mapper.insert(entity);
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

    public List<T> list(Map<String, Object> params) {
		return list(params, null);
	}

	public List<T> list(String orderBy) {
		return list(null, orderBy);
	}

	public List<T> list(Map<String, Object> params, String orderBy) {
		Example example = new Example(entityClass);
		if(ObjectUtils.isNotEmpty(params)) example.createCriteria().andEqualTo(params);
		if(StringUtils.isNotEmpty(orderBy)) example.setOrderByClause(orderBy);
		return mapper.selectByExample(example);
	}

	public PageInfo<T> page(int pageNum, int pageSize) {
		return page(null, null, pageNum, pageSize);
	}

	public PageInfo<T> page(Map<String, Object> params, int pageNum, int pageSize) {
		return page(params, null, pageNum, pageSize);
	}

	public PageInfo<T> page(String orderBy, int pageNum, int pageSize) {
		return page(null, orderBy, pageNum, pageSize);
	}


	public PageInfo<T> page(Map<String, Object> params, String orderBy, int pageNum, int pageSize) {
		Example example = new Example(entityClass);
		if(ObjectUtils.isNotEmpty(params)) example.createCriteria().andEqualTo(params);
		if(StringUtils.isNotEmpty(orderBy)) example.setOrderByClause(orderBy);
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<T> pageInfo = new PageInfo<>(mapper.selectByExample(example));
		return pageInfo;
	}

	public void delete(T entity) {
		mapper.delete(entity);
	}

	public void deleteById(String id) {
		mapper.deleteByPrimaryKey(id);
	}

	public void deleteByExample(T entity){
		mapper.deleteByExample(entity);
	}

}
