package cn.workde.core.admin.logic;


import java.util.Map;

/**
 * @author zhujingang
 * @date 2019/9/2 6:47 PM
 */
public class BaseLogic<T> implements IModuleLogic<T> {


	/**
	 * 插入前
	 * @param updated
	 * @return
	 */
	@Override
	public boolean beforeInsert(T updated) {
		return true;
	}

	/**
	 * 插入后
	 * @param inserted
	 * @return
	 */
	@Override
	public boolean afterInsert(T inserted) {
		return true;
	}

	/**
	 * 修改前
	 * @param updated
	 * @return
	 */
	@Override
	public boolean beforeUpdate(T updated) {
		return false;
	}

	/**
	 * 修改后
	 * @param updated
	 * @return
	 */
	@Override
	public boolean afterUpdate(T updated) {
		return false;
	}

	/**
	 * 删除前
	 * @param deleted
	 * @return
	 */
	@Override
	public boolean beforeDelete(T deleted) {
		return false;
	}

	/**
	 * 删除后
	 * @param deleted
	 * @return
	 */
	@Override
	public boolean afterDelete(T deleted) {
		return false;
	}

	/**
	 * 新增默认值
	 * @return
	 */
	@Override
	public Map<String, Object> getNewDefultValue() {
		return null;
	}
}
