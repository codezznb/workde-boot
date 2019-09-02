package cn.workde.core.admin.logic;

import cn.workde.core.tk.base.BaseEntity;

/**
 * @author zhujingang
 * @date 2019/9/2 12:59 PM
 */
public interface IModuleLogic<T extends BaseEntity> {

	default void beforeCreate(T inserted) {}

	default boolean beforeInsert(T updated, String errorMessage) { return true; }
	default boolean afterInsert(T inserted) { return true; }

	default void beforeEdit(T updated) {}

	default boolean beforeUpdate(T updated, String errorMessage) { return true; }
	default boolean afterUpdate(T updated) { return true; }

	default boolean beforeDelete(T deleted, String errorMessage) { return true; }
	default boolean afterDelete(T deleted) { return true; }

}
