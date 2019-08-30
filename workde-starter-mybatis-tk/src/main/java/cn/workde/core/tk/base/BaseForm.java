package cn.workde.core.tk.base;

import cn.hutool.core.util.ClassUtil;
import cn.workde.core.base.exception.BusinessException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * @author zhujingang
 * @date 2019/8/29 4:40 PM
 */
@Slf4j
@Data
public class BaseForm<T> {

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected Class<T> mappedClass = (Class<T>) ClassUtil.getTypeArgument(getClass());

	public T toModel() {
		try {
			T model = mappedClass.newInstance();
			BeanUtils.copyProperties(this, model);
			return model;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new BusinessException("数据转换失败");
	}

	public T toUpdateModel(Object model) {
		try {
			BeanUtils.copyProperties(this, model);
			return (T) model;
		}catch (Exception ex){
			ex.printStackTrace();
		}
		throw new BusinessException("数据转换失败");
	}
}
