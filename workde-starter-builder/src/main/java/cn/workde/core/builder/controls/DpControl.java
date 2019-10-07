package cn.workde.core.builder.controls;

import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.builder.db.DataProvider;
import cn.workde.core.builder.utils.DbUtil;
import cn.workde.core.builder.utils.StringUtil;
import cn.workde.core.builder.utils.WebUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author zhujingang
 * @date 2019/9/19 12:21 PM
 */
public class DpControl extends Control{

	@Override
	public void create() throws Exception {
		getContent(true);
	}

	public String getContent(final boolean directOutput) throws Exception{
		if (this.gb("disabled", false)) {
			return null;
		}
		final long startTime = System.currentTimeMillis();
		Long totalCount = null;
		ResultSet resultSet = null;
		ResultSet totalResultSet = null;

		final String limitRecords = this.gs("limitRecords");
		final String limitExportRecords = this.gs("limitExportRecords");
		final String loadParams = this.gs("loadParams");
		final String resultName = this.gs("resultName");
		final String totalCountName = this.gs("totalCountName");
		final String totalLoadParams = this.gs("totalLoadParams");
		final String startParam = this.request.getParameter("start");
		final String limitParam = this.request.getParameter("limit");
		final String totalSql = this.gs("totalSql");

		String type = this.gs("type");
		final boolean autoPage = this.gb("autoPage", true);
		try {
			if (type.isEmpty() && "1".equals(this.request.getParameter("_istree"))) {
				type = "tree";
			}

			if (StringUtil.isEmpty(type) || "array".equals(type)) {
				this.setOrderSql();
			}

			final String sql = this.gs("sql");
			long beginIndex;

			if (StringUtil.isEmpty(startParam)) {
				beginIndex = 1L;
				this.request.setAttribute("start", 0L);
			} else {
				beginIndex = Long.parseLong(startParam) + 1L;
			}

			if(!StringUtil.isEmpty(totalSql)) {
				beginIndex = 0L;
			}
			long endIndex;
			if (StringUtil.isEmpty(limitParam)) {
				endIndex = Long.MAX_VALUE;
				this.request.setAttribute("limit", endIndex);
			} else {
				endIndex = beginIndex + Long.parseLong(limitParam) - 1L;
			}

			this.request.setAttribute("beginIndex", beginIndex);
			this.request.setAttribute("endIndex", endIndex);
			final Object result = this.getResult(DbUtil.run(this.request, sql), StringUtil.select(resultName, "result"));
			if (result instanceof ResultSet) {
				resultSet = (ResultSet) result;
				if (autoPage) {
					if (!StringUtil.isEmpty(totalSql)) {
						final Object totalResult = this.getResult(DbUtil.run(request, totalSql), StringUtil.select(totalCountName, "totalCount"));
						if (totalResult == null) {
							throw new NullPointerException("No value in the totalSql.");
						}
						if (totalResult instanceof ResultSet) {
							totalResultSet = (ResultSet) totalResult;

							if (!totalResultSet.next()) {
								throw new NullPointerException("Empty total ResultSet.");
							}
							totalCount = Long.parseLong(totalResultSet.getString(1));
						} else {
							totalCount = Long.parseLong(totalResult.toString());
						}
					}
				}

				DataProvider dp = new DataProvider();
				dp.setRequest(request);
				dp.setResponse(response);
				dp.setResultSet(resultSet);
				dp.setTotalCount(totalCount);
				if (autoPage) {
					dp.setBeginIndex(beginIndex);
					dp.setEndIndex(endIndex);
				}
				if (!limitRecords.isEmpty()) {
					dp.setLimitRecords(Integer.parseInt(limitRecords));
				}
				if (!limitExportRecords.isEmpty()) {
					dp.setLimitExportRecords(Integer.parseInt(limitExportRecords));
				}
				dp.setFields(this.gs("fields"));
				dp.setFieldsTag(this.gs("fieldsTag"));
				dp.setResultSet(resultSet);
				dp.setType(type);
				if (directOutput) {
					dp.output();
					return null;
				}
				return dp.getScript();
			} else {
				final String text = StringUtil.concat("{\"total\":1,\"metaData\":{\"fields\":[{\"name\":\"result\",\"type\":\"string\"}]},\"columns\":[{\"xtype\":\"rownumberer\",\"width\":40},{\"dataIndex\":\"result\",flex:1,\"text\":\"result\"}],\"rows\":[{\"result\":", (result == null) ? "null" : StringUtil.quote(result.toString()), "}],\"elapsed\":", Long.toString(System.currentTimeMillis() - startTime), "}");
				if (directOutput) {
					WebUtil.send(this.response, text);
					return null;
				}
				return text;
			}
		}finally {
			JdbcUtils.closeResultSet(resultSet);
			JdbcUtils.closeResultSet(totalResultSet);
		}
	}

	private Object getResult(final Object result, final String resultName) throws Exception {
		if (!(result instanceof HashMap)) {
			return result;
		}
		final HashMap<?, ?> map = (HashMap<?, ?>)result;
		final Set<?> es = map.entrySet();
		final String itemId = StringUtil.select(this.gs("itemId"));
		for (final Object e : es) {
			final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)e;
			String name = (String)entry.getKey();
			if (name.equals("return")) {
				if (!itemId.isEmpty()) {
					name = itemId;
				}
			}
			else if (!itemId.isEmpty()) {
				name = StringUtil.concat(itemId, ".", name);
			}
			this.request.setAttribute(name, (Object)entry.getValue());
		}
		final String rsIndex;
		if (resultName.startsWith("@") && StringUtil.isInteger(rsIndex = resultName.substring(1))) {
			return this.getMoreResult(map, Integer.parseInt(rsIndex));
		}
		final Object val = map.get(resultName);
		if (val == null) {
			return map.get("return");
		}
		return val;
	}

	private Object getMoreResult(final HashMap<?, ?> map, final int index) throws Exception {
		final CallableStatement st = (CallableStatement)map.get("sys.statement");
		for (int i = 1; i < index; ++i) {
			st.getMoreResults();
		}
		Object result = st.getResultSet();
		if (result == null) {
			result = st.getUpdateCount();
		}
		return result;
	}


	private void setOrderSql() {
		final String sort = this.request.getParameter("sort");
		if (StringUtil.isEmpty(sort) || this.request.getAttribute("sql.orderBy") != null) {
			return;
		}
		final String orderExp = DbUtil.getOrderSql(sort, this.gs("orderFields"));
		if (orderExp != null) {
			this.request.setAttribute("sql.orderBy", (Object)(" order by " + orderExp));
			this.request.setAttribute("sql.orderFields", (Object)("," + orderExp));
		}
	}
}
