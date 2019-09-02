package cn.workde.core.admin.logic;


import java.util.Map;

/**
 * @author zhujingang
 * @date 2019/9/2 6:47 PM
 */
public class BaseLogic<T> implements IModuleLogic<T> {


	@Override
	public boolean beforeInsert(T updated) {
		return true;
	}

	@Override
	public boolean afterInsert(T inserted) {
		return true;
	}

	@Override
	public boolean beforeUpdate(T updated) {
		return false;
	}

	@Override
	public boolean afterUpdate(T updated) {
		return false;
	}

	@Override
	public boolean beforeDelete(T deleted) {
		return false;
	}

	@Override
	public boolean afterDelete(T deleted) {
		return false;
	}

	@Override
	public Map<String, Object> getNewDefultValue() {
		return null;
	}
}
