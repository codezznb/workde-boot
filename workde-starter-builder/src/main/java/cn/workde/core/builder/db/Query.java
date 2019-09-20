package cn.workde.core.builder.db;

import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.builder.utils.DbUtil;
import cn.workde.core.builder.utils.StringUtil;
import cn.workde.core.builder.utils.SysUtil;
import cn.workde.core.builder.utils.WebUtil;
import lombok.Data;
import org.json.JSONArray;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public SqlRowSet run() throws Exception {
		this.checkProperties();
		final boolean hasArray = this.arrayData != null || !StringUtil.isEmpty(this.arrayName);
		this.sql = this.sql.trim();
		this.replaceMacros();
		JdbcTemplate jdbcTemplate = SpringUtils.getBean(JdbcTemplate.class);
		Object[] args = getParameters();
		System.out.println(this.formattedSql);
		System.out.println(args);
		SqlRowSet result = jdbcTemplate.queryForRowSet(this.formattedSql, args);
		return result;
//		final Connection connection = jdbcTemplate.getDataSource().getConnection();
//		final boolean isCommit = "commit".equals(this.transaction);
//
//		if (isCommit) {
//			if (connection.getAutoCommit()) {
//				this.transaction = "start";
//			}
//		} else if(StringUtil.isEmpty(this.transaction) && (this.uniqueUpdate || hasArray) && connection.getAutoCommit()) {
//			this.transaction = "start";
//		}
//
//		if ("start".equals(this.transaction)) {
//			DbUtil.startTransaction(connection, this.getIsolation());
//		}
//
//		if (StringUtil.isEmpty(this.type)) {
//			if (this.sql.startsWith("{")) {
//				this.type = "call";
//			}
//			else {
//				this.type = "execute";
//			}
//		}
//
//		final boolean isCall = "call".equals(this.type);
//		if (isCall) {
//			this.statement = connection.prepareCall(this.formattedSql);
//		}
//		else {
//			this.statement = connection.prepareStatement(this.formattedSql);
//		}

		// WebUtil.setObject(this.request, SysUtil.getId(), this.statement);

		// this.regParameters();
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


	private Object[] getParameters() {
		List<Object> parameters = new ArrayList<>();
		int startPos = 0;
		int endPos = 0;

		while ((startPos = this.sql.indexOf("{?", startPos)) > -1 && (endPos = this.sql.indexOf("?}", endPos)) > -1) {
			startPos += 2;
			String param = this.sql.substring(startPos, endPos);
			endPos += 2;
			final String orgParam = param;
			String paraName;
			int type;

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
			parameters.add(obj);
		}

		return parameters.toArray();
	}

	public static void main(String[] args) {
		Query query = new Query();
		query.setSql("select * from yd_user where 1=1 and phone like ${phone}");
		query.replaceMacros();
		System.out.println(query.debugSql.toString());
		System.out.println(query.formattedSql.toString());
	}
}
