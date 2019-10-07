package cn.workde.core.builder.controls;

import cn.workde.core.builder.db.Query;

/**
 * @author zhujingang
 * @date 2019/9/19 10:11 AM
 */
public class QueryControl extends Control{

	@Override
	public void create() throws Exception {
		if (this.gb("disabled", false)) {
			return;
		}

		final String itemId = this.gs("itemId");
		final Query query = new Query();
		query.setSql(this.gs("sql"));
		query.setRequest(this.request);
		query.setArrayName(this.gs("arrayName"));
		query.setBatchUpdate(this.gb("batchUpdate"));
		query.setType(this.gs("type"));
		query.setErrorText(this.gs("errorText"));
		query.setTransaction(this.gs("transaction"));
		query.setLoadParams(this.gs("loadParams"));
		query.setIsolation(this.gs("isolation"));
		query.setUniqueUpdate(this.gb("uniqueUpdate", false));
		query.setReturnStatement(this.gb("returnStatement", false));
		Object result = query.run();
		this.request.setAttribute(itemId, result);
	}
}
