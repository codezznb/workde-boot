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
public class BaseService<T extends BaseEntity, M extends BaseMapper<T>> implements IBaseService<T> {

    @Autowired
    protected M mapper;

    private Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	public List<T> all() {
		return mapper.selectAll();
	}

	@Override
    public boolean unique(String property, String value) {
        Example example = new Example(entityClass);
        example.createCriteria().andEqualTo(property, value);
        int result = mapper.selectCountByExample(example);
        if (result > 0) {
            return false;
        }
        return true;
    }

	@Override
    public T one(T entity){
        return mapper.selectOne(entity);
    }

	@Override
    public T one(Map<String, Object> params) {
		Example example = new Example(entityClass);
		if(ObjectUtils.isNotEmpty(params)) {
			example.createCriteria().andEqualTo(params);
		}
		return mapper.selectOneByExample(example);
	}

	@Override
    public T byId(String id) { return mapper.selectByPrimaryKey(id); }

	@Override
    public T save(T entity) {
        int result = 0;
        Date currentDate = new Date();
        entity.setUpdated(currentDate);

        // 创建
        if (entity.getId() == null) {
			entity.setId(IdUtils.getId().toString());
            entity.setCreated(currentDate);
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

	@Override
	public int count(T entity) {
		return mapper.selectCount(entity);
	}


	@Override
	public int count(Map<String, Object> params) {
		Example example = new Example(entityClass);
		if(ObjectUtils.isNotEmpty(params)) {
			example.createCriteria().andEqualTo(params);
		}
		return count(example);
	}

	@Override
	public int count(Example example) {
		return mapper.selectCountByExample(example);
	}

	@Override
	public List<T> list(Map<String, Object> params) {
		return list(params, null);
	}

	@Override
	public List<T> list(String orderBy) {
		return list(null, orderBy);
	}

	@Override
	public List<T> list(Map<String, Object> params, String orderBy) {
		Example example = new Example(entityClass);
		if(ObjectUtils.isNotEmpty(params)) {
			example.createCriteria().andEqualTo(params);
		}
		if(StringUtils.isNotEmpty(orderBy)) {
			example.setOrderByClause(orderBy);
		}
		return mapper.selectByExample(example);
	}

	@Override
	public List<T> list(Example example) {
		return mapper.selectByExample(example);
	}

	@Override
	public PageInfo<T> page(int pageNum, int pageSize) {
		return page(null, null, pageNum, pageSize);
	}

	@Override
	public PageInfo<T> page(Map<String, Object> params, int pageNum, int pageSize) {
		return page(params, null, pageNum, pageSize);
	}

	@Override
	public PageInfo<T> page(String orderBy, int pageNum, int pageSize) {
		return page(null, orderBy, pageNum, pageSize);
	}

	public PageInfo<T> page(Map<String, Object> params, String orderBy, int pageNum, int pageSize) {
		Example example = new Example(entityClass);
		if(ObjectUtils.isNotEmpty(params)) {
			example.createCriteria().andEqualTo(params);
		}
		if(StringUtils.isNotEmpty(orderBy)) {
			example.setOrderByClause(orderBy);
		}
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<T> pageInfo = new PageInfo<>(mapper.selectByExample(example));
		return pageInfo;
	}

	@Override
	public PageInfo<T> page(Example example, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<T> pageInfo = new PageInfo<>(mapper.selectByExample(example));
		return pageInfo;
	}

	@Override
	public void delete(T entity) {
		mapper.delete(entity);
	}

	@Override
	public void deleteById(String id) {
		mapper.deleteByPrimaryKey(id);
	}

	@Override
	public void deleteByExample(T entity){
		mapper.deleteByExample(entity);
	}

	@Override
	public void update(T entity, Map<String, Object> params) {
		Example example = new Example(entityClass);
		if(ObjectUtils.isNotEmpty(params)) {
			example.createCriteria().andEqualTo(params);
		}
		updateByExample(entity, example);
	}

	@Override
	public void updateById(T entity) {
		mapper.updateByPrimaryKey(entity);
	}

	@Override
	public void updateByExample(T entity, Example example) {
		mapper.updateByExample(entity, example);
	}

	@Override
	public void updateByIdSelective(T entity) {
		mapper.updateByPrimaryKeySelective(entity);
	}

	@Override
	public void updateByExampleSelective(T entity, Example example) {
		mapper.updateByExampleSelective(entity, example);
	}

}
