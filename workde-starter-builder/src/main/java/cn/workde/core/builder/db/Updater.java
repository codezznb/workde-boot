package cn.workde.core.builder.db;

import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.builder.utils.DbUtil;
import cn.workde.core.builder.utils.JsonUtil;
import cn.workde.core.builder.utils.StringUtil;
import cn.workde.core.builder.utils.WebUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;

/**
 * @author zhujingang
 * @date 2019/9/20 2:36 PM
 */
public class Updater {

	public HttpServletRequest request;
	public String type;
	public String transaction;
	public boolean batchUpdate;
	public boolean uniqueUpdate;
	public String isolation;
	public String tableName;
	public String sqlInsert;
	public String sqlUpdate;
	public String sqlDelete;
	public String paramInsert;
	public String paramUpdate;
	public String paramDelete;
	public String mode;
	public boolean ignoreBlob;
	public boolean useExistFields;
	public String useFields;
	public String whereFields;
	public JSONObject fieldsMap;
	public String pk;

	public Updater() {
		this.uniqueUpdate = false;
		this.ignoreBlob = false;
		this.useExistFields = true;
	}

	public void run() throws Exception {
		JSONArray destroyData = null;
		JSONArray createData = null;
		JSONArray updateData = null;
		JSONObject useFieldsObj = null;
		JSONObject whereFieldsObj = null;

		final boolean specifyTableName = !StringUtil.isEmpty(this.tableName);

		if (specifyTableName) {

			final boolean notHasUseFields = StringUtil.isEmpty(this.useFields);
			if (notHasUseFields) useFieldsObj = null;
			else useFieldsObj = JsonUtil.fromCSV(this.useFields);
			final boolean notHasWhereFields = StringUtil.isEmpty(this.whereFields);
			if (notHasWhereFields) whereFieldsObj = null;
			else whereFieldsObj = JsonUtil.fromCSV(this.whereFields);
			if (StringUtil.isEmpty(this.mode)) {
				Object params = WebUtil.fetchObject(this.request, StringUtil.select(this.paramDelete, "destroy"));
				destroyData = JsonUtil.getArray(params);
				if (this.useExistFields && destroyData != null && destroyData.length() > 0) {
					final JSONObject fieldsObj = destroyData.getJSONObject(0);
					if (notHasUseFields) {
						useFieldsObj = JsonUtil.clone(fieldsObj);
					}
					if (notHasWhereFields) {
						whereFieldsObj = JsonUtil.clone(fieldsObj);
					}
				}

				params = WebUtil.fetchObject(this.request, StringUtil.select(this.paramInsert, "create"));
				createData = JsonUtil.getArray(params);
				if (this.useExistFields && createData != null && createData.length() > 0) {
					final JSONObject fieldsObj = createData.getJSONObject(0);
					if (notHasUseFields) {
						if (useFieldsObj == null) useFieldsObj = JsonUtil.clone(fieldsObj);
						else JsonUtil.apply(useFieldsObj, fieldsObj);
					}
					if (notHasWhereFields) {
						if (whereFieldsObj == null) whereFieldsObj = JsonUtil.clone(fieldsObj);
						else JsonUtil.apply(whereFieldsObj, fieldsObj);
					}
				}
				params = WebUtil.fetchObject(this.request, StringUtil.select(this.paramUpdate, "update"));
				updateData = JsonUtil.getArray(params);
				if (this.useExistFields && updateData != null && updateData.length() > 0) {
					final JSONObject fieldsObj = updateData.getJSONObject(0);
					if (notHasUseFields) {
						if (useFieldsObj == null) useFieldsObj = JsonUtil.clone(fieldsObj);
						else JsonUtil.apply(useFieldsObj, fieldsObj);
					}
					if (notHasWhereFields) {
						if (whereFieldsObj == null) whereFieldsObj = JsonUtil.clone(fieldsObj);
						else JsonUtil.apply(whereFieldsObj, fieldsObj);
					}
				}
				if (((destroyData == null) ? 0 : destroyData.length()) + ((createData == null) ? 0 : createData.length()) + ((updateData == null) ? 0 : updateData.length()) == 0) {
					return;
				}
			}else if (this.useExistFields) {
				final JSONObject fieldsObj = WebUtil.fetch(this.request);
				if (notHasUseFields) useFieldsObj = fieldsObj;
				if (notHasWhereFields) whereFieldsObj = fieldsObj;
			}
		}
		final Connection connection = DbUtil.getConnection(request);
		final Query query = new Query();

		final boolean isCommit = "commit".equals(this.transaction);
		if ((isCommit || StringUtil.isEmpty(this.transaction)) && connection.getAutoCommit()) {
			this.transaction = "start";
		}
		if ("start".equals(this.transaction)) {
			DbUtil.startTransaction(connection, this.isolation);
		}
		query.setRequest(this.request);
		query.setType(this.type);
		query.setBatchUpdate(this.batchUpdate);
		query.setTransaction("none");
		query.setUniqueUpdate(this.uniqueUpdate);

		if (specifyTableName) {

			final String[] sqls = DbUtil.buildSQLs(this.tableName, this.ignoreBlob, 1, this.request, useFieldsObj, whereFieldsObj, this.fieldsMap, this.pk);
			if (StringUtil.isEmpty(this.sqlInsert)) {
				this.sqlInsert = sqls[0];
			}
			if (StringUtil.isEmpty(this.sqlUpdate)) {
				this.sqlUpdate = sqls[1];
			}
			if (StringUtil.isEmpty(this.sqlDelete)) {
				this.sqlDelete = sqls[2];
			}
		}

		if (StringUtil.isEmpty(this.mode)) {
			if (!StringUtil.isEmpty(this.sqlDelete) && !"-".equals(this.sqlDelete) && destroyData != null && destroyData.length() > 0 && !this.sqlDelete.isEmpty()) {
				query.setSql(this.sqlDelete);
				query.setArrayData(destroyData);
				query.run();
			}
			if (!StringUtil.isEmpty(this.sqlUpdate) && !"-".equals(this.sqlUpdate) && updateData != null && updateData.length() > 0 && !this.sqlUpdate.isEmpty()) {
				query.setSql(this.sqlUpdate);
				query.setArrayData(updateData);
				query.run();
			}
			if (!StringUtil.isEmpty(this.sqlInsert) && !"-".equals(this.sqlInsert) && createData != null && createData.length() > 0 && !this.sqlInsert.isEmpty()) {
				query.setSql(this.sqlInsert);
				query.setArrayData(createData);
				query.run();
			}
		} else {
			if (this.mode.equals("delete")) {
				query.setSql(this.sqlDelete);
			} else if (this.mode.equals("update")) {
				query.setSql(this.sqlUpdate);
			} else {
				query.setSql(this.sqlInsert);
			}
			query.run();
		}

		if (isCommit) {
			connection.commit();
			connection.setAutoCommit(true);
		}


	}
}
