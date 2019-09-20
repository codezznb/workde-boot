package cn.workde.core.builder.db;

import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.boot.launch.WorkdeApplication;
import cn.workde.core.builder.utils.*;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author zhujingang
 * @date 2019/9/19 10:16 AM
 */
@Data
public class Query {

	private HttpServletRequest request;
	private String sql;
	private String arrayName;
	private JSONArray arrayData;
	private boolean batchUpdate;
	private String type;
	private String loadParams;
	private boolean returnStatement;
	private String transaction;
	private String isolation;
	private boolean uniqueUpdate;
	private String errorText;
	private String debugSql;
	private String formattedSql;
	private ArrayList<Object[]> paramList;
	private ArrayList<String> paramValList;
	private int paramCount;
	private PreparedStatement statement;

	public Object run() throws Exception {
		this.checkProperties();
		final boolean hasArray = this.arrayData != null || !StringUtil.isEmpty(this.arrayName);
		Object result = null;
		this.sql = this.sql.trim();
		this.replaceMacros();

		final Connection connection = DbUtil.getConnection(request);

		final boolean isCommit = "commit".equals(this.transaction);
		if(isCommit) {
			if(connection.getAutoCommit()) {
				this.transaction = "start";
			}
		}else if(StringUtil.isEmpty(this.transaction) && (this.uniqueUpdate || hasArray) && connection.getAutoCommit()) {
			this.transaction = "start";
		}
		if("start".equals(this.transaction)) DbUtil.startTransaction(connection, this.isolation);
		if(StringUtil.isEmpty(this.type) && this.sql.startsWith("{")) this.type = "call";
		else this.type = "execute";

		final boolean isCall = "call".equals(this.type);
		if (isCall) this.statement = connection.prepareCall(this.formattedSql);
		else this.statement = connection.prepareStatement(this.formattedSql, Statement.RETURN_GENERATED_KEYS);

		WebUtil.setObject(request, SysUtil.getId(), this.statement);
		regParameters();

		if (hasArray) {
			this.executeBatch();
		} else {

			if (WorkdeApplication.isLocalDev()) {
				this.printSql();
			}

			if ("query".equals(this.type)) {
				result = this.statement.executeQuery();
				WebUtil.setObject(request, SysUtil.getId(), result);
			}else if("update".equals(this.type)) {
				final int affectedRows = this.statement.executeUpdate();
				result = affectedRows;
				if(this.uniqueUpdate && affectedRows != 1) this.notUnique();
			}else {
				final boolean autoLoadParams = StringUtil.isEmpty(this.loadParams) || "auto".equals(this.loadParams);
				if (this.statement.execute()) {
					if(autoLoadParams) {
						this.loadParams = "none";
					}
					result = this.statement.getResultSet();
					WebUtil.setObject(request, SysUtil.getId(), result);
				}else {
					if (autoLoadParams) {
						this.loadParams = "load";
					}
					final int affectedRows = this.statement.getUpdateCount();
					if(this.formattedSql.startsWith("insert") && affectedRows > 0) {
						ResultSet kResultSet = this.statement.getGeneratedKeys();
						if(kResultSet != null && kResultSet.next()) {
							Long id = kResultSet.getLong(1);
							request.setAttribute("NEW_PK_ID", id);
						}
					}

					result = affectedRows;
					if (this.uniqueUpdate && affectedRows != 1) {
						this.notUnique();
					}
				}

				if (isCall && (this.paramCount > 0 || this.returnStatement)) {
					final HashMap<String, Object> map = this.getOutParameter("load".equals(this.loadParams));
					if (map.size() > 0) {
						if (map.containsKey("return")) {
							throw new IllegalArgumentException("Invalid output parameter name \"return\"");
						}
						map.put("return", result);
						result = map;
					}
				}
			}
		}

		if (isCommit) {
			connection.commit();
			connection.setAutoCommit(true);
		}
		checkError(result);
		return result;
	}

	private void checkProperties() {
		if (!StringUtil.isEmpty(this.transaction)) {
			final String[] trans = { "start", "commit", "none" };
			if (StringUtil.indexOf(trans, this.transaction) == -1) {
				throw new IllegalArgumentException("Invalid transaction \"" + this.transaction + "\".");
			}
		}
		if (!StringUtil.isEmpty(this.loadParams)) {
			final String[] params = { "auto", "load", "none" };
			if (StringUtil.indexOf(params, this.loadParams) == -1) {
				throw new IllegalArgumentException("Invalid loadParams \"" + this.loadParams + "\".");
			}
		}
		if (!StringUtil.isEmpty(this.type)) {
			final String[] types = { "query", "update", "execute", "call" };
			if (StringUtil.indexOf(types, this.type) == -1) {
				throw new IllegalArgumentException("Invalid type \"" + this.type + "\".");
			}
		}
		if (!StringUtil.isEmpty(this.isolation)) {
			final String[] isolations = { "readCommitted", "readUncommitted", "repeatableRead", "serializable" };
			if (StringUtil.indexOf(isolations, this.isolation) == -1) {
				throw new IllegalArgumentException("Invalid isolation \"" + this.isolation + "\".");
			}
		}
	}

	private void executeBatch() throws Exception {
		JSONArray ja;
		if (this.arrayData == null) {
			final Object obj = WebUtil.fetchObject(this.request, this.arrayName);
			if (obj instanceof JSONArray) {
				ja = (JSONArray)obj;
			}
			else {
				if (obj == null) {
					return;
				}
				final String val = obj.toString();
				if (val.isEmpty()) {
					return;
				}
				ja = new JSONArray(val);
			}
		}
		else {
			ja = this.arrayData;
		}
		final int j = ja.length();
		if (j == 0) {
			return;
		}
		for (int i = 0; i < j; ++i) {
			final JSONObject jo = ja.getJSONObject(i);
			for (int k = 0; k < this.paramCount; ++k) {
				final Object[] param = this.paramList.get(k);
				final String name = (String)param[0];
				if (!(boolean)param[2] && jo.has(name)) {
					final Object valObj = JsonUtil.opt(jo, name);
					DbUtil.setObject(this.statement, k + 1, (int)param[1], valObj);
					if (WorkdeApplication.isLocalDev()) {
						this.paramValList.set(k, StringUtil.toString(valObj));
					}
				}
			}
			if (WorkdeApplication.isLocalDev()) {
				this.printSql();
			}
			if (this.batchUpdate) {
				this.statement.addBatch();
			}
			else {
				final int affectedRows = this.statement.executeUpdate();
				if (this.uniqueUpdate && affectedRows != 1) {
					this.notUnique();
				}
			}
		}
		if (this.batchUpdate) {
			this.statement.executeBatch();
		}
	}

	private void notUnique() {
		throw new RuntimeException("记录更新不唯一.");
	}

	private void replaceMacros() {
		final StringBuilder buf = new StringBuilder();
		int startPos = 0;
		int endPos = 0;
		int lastPos = 0;
		while ((startPos = this.sql.indexOf("{?", startPos)) > -1 && (endPos = this.sql.indexOf("?}", endPos)) > -1) {
			buf.append(this.sql.substring(lastPos, startPos));
			startPos += 2;
			endPos += 2;
			buf.append("'{?}");
			buf.append('\'');
			lastPos = endPos;
		}
		buf.append(this.sql.substring(lastPos));
		this.debugSql = buf.toString();
		this.formattedSql = StringUtil.replaceAll(this.debugSql, "'{?}'", "?");
	}


	private void regParameters() throws Exception {
		int index = 1;
		int startPos = 0;
		int endPos = 0;
		this.paramList = new ArrayList<>();
		if (WorkdeApplication.isLocalDev()) {
			this.paramValList = new ArrayList<String>();
		}

		CallableStatement callStatement;
		if (this.statement instanceof CallableStatement) callStatement = (CallableStatement)this.statement;
		else callStatement = null;
		final boolean isCall = callStatement != null;

		while ((startPos = this.sql.indexOf("{?", startPos)) > -1 && (endPos = this.sql.indexOf("?}", endPos)) > -1) {
			startPos += 2;
			String param = this.sql.substring(startPos, endPos);
			endPos += 2;
			final String orgParam = param;
			final boolean isOutParam = isCall && param.startsWith("@");
			String paraName;
			int type;
			if (isOutParam) {
				param = param.substring(1);
				final int dotPos = param.indexOf(".");
				String typeText;
				if (dotPos == -1) {
					typeText = "varchar";
					paraName = param;
				}
				else {
					typeText = param.substring(0, dotPos);
					paraName = param.substring(dotPos + 1);
				}
				final boolean hasSub = typeText.indexOf(61) != -1;
				if (hasSub) {
					final Integer typeObj = DbUtil.getFieldType(StringUtil.getNamePart(typeText));
					if (typeObj == null) {
						throw new Exception("Invalid type " + typeText);
					}
					type = typeObj;
					final int subType = Integer.parseInt(StringUtil.getValuePart(typeText));
					callStatement.registerOutParameter(index, type, subType);
				}
				else {
					final Integer typeObj = DbUtil.getFieldType(typeText);
					if (typeObj == null) {
						throw new Exception("Invalid type " + typeText);
					}
					type = typeObj;
					callStatement.registerOutParameter(index, type);
				}
				if (WorkdeApplication.isLocalDev()) {
					this.paramValList.add(orgParam);
				}
			}else {
				final int dotPos = param.indexOf(".");
				if (dotPos == -1) {
					type = 12;
					paraName = param;
				}else {
					final Integer typeObj = DbUtil.getFieldType(param.substring(0, dotPos));

					if (typeObj == null) {
						type = 12;
						paraName = param;
					} else {
						type = typeObj;
						paraName = param.substring(dotPos + 1);
					}
				}
				final Object obj = WebUtil.fetchObject(this.request, paraName);
				DbUtil.setObject(this.statement, index, type, obj);
				if (WorkdeApplication.isLocalDev()) {
					this.paramValList.add(StringUtil.toString(obj));
				}
			}

			final Object[] paramObjects = { paraName, type, isOutParam };
			this.paramList.add(paramObjects);
			++index;
		}
		this.paramCount = this.paramList.size();
	}

	private HashMap<String, Object> getOutParameter(final boolean loadOutParams) throws Exception {
		final CallableStatement st = (CallableStatement)this.statement;
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("sys.statement", st);
		if (!loadOutParams) {
			return map;
		}
		for (int i = 0; i < this.paramCount; ++i) {
			final Object[] param = this.paramList.get(i);
			if (param[2] != null) {
				final Object object = DbUtil.getObject(st, i + 1, (int)param[1]);
				if (object instanceof ResultSet) {
					WebUtil.setObject(this.request, SysUtil.getId(), object);
				}
				map.put((String)param[0], object);
			}
		}
		return map;
	}

	private void checkError(final Object object) throws Exception {
		if (!StringUtil.isEmpty(this.errorText) && object instanceof ResultSet) {
			ResultSet rs = (ResultSet) object;
			if (rs.next()) {
				throw new RuntimeException(this.errorText);
			}
		}
	}

	private void printSql() {
		String sql = this.debugSql;
		for (final String s : this.paramValList) {
			sql = StringUtil.replaceFirst(sql, "{?}", s);
		}
		System.out.println(sql);
	}
}
