package cn.workde.core.builder.controls;

import cn.workde.core.builder.db.DataProvider;
import cn.workde.core.builder.utils.DbUtil;
import cn.workde.core.builder.utils.StringUtil;
import org.springframework.jdbc.support.rowset.SqlRowSet;


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

		final String limitRecords = this.gs("limitRecords");
		final String limitExportRecords = this.gs("limitExportRecords");
		final String loadParams = this.gs("loadParams");
		final String resultName = this.gs("resultName");
		final String totalCountName = this.gs("totalCountName");
		final String totalLoadParams = this.gs("totalLoadParams");
		final String startParam = this.request.getParameter("start");
		final String limitParam = this.request.getParameter("limit");

		String type = this.gs("type");
		final boolean autoPage = this.gb("autoPage", true);

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
		long endIndex;
		if (StringUtil.isEmpty(limitParam)) {
			endIndex = Long.MAX_VALUE;
			this.request.setAttribute("limit", endIndex);
		} else {
			endIndex = beginIndex + Long.parseLong(limitParam) - 1L;
		}

		this.request.setAttribute("beginIndex", beginIndex);
		this.request.setAttribute("endIndex", endIndex);
		SqlRowSet result = DbUtil.run(this.request, sql);


		if(autoPage) {

			final String totalSql = this.gs("totalSql");
			if(!StringUtil.isEmpty(totalSql)) {
				final SqlRowSet totalResult = DbUtil.run(request, totalSql);
				if(!totalResult.next()) {
					throw new NullPointerException("Empty total ResultSet.");
				}
				totalCount = totalResult.getLong(1);
			}
		}

		DataProvider dp = new DataProvider();
		dp.setRequest(request);
		dp.setResponse(response);
		dp.setTotalCount(totalCount);
		if(autoPage) {
			dp.setBeginIndex(beginIndex);
			dp.setEndIndex(endIndex);
		}

		if(!limitRecords.isEmpty()) {
			dp.setLimitRecords(Integer.parseInt(limitRecords));
		}

		if(!limitExportRecords.isEmpty()) {
			dp.setLimitExportRecords(Integer.parseInt(limitExportRecords));
		}
		dp.setFields(this.gs("fields"));
		dp.setFieldsTag(this.gs("fieldsTag"));
		dp.setResultSet(result);
		dp.setType(type);
		if (directOutput) {
			dp.output();
			return null;
		}
		return dp.getScript();


//		final String text = StringUtil.concat("{\"total\":1,\"metaData\":{\"fields\":[{\"name\":\"result\",\"type\":\"string\"}]},\"columns\":[{\"xtype\":\"rownumberer\",\"width\":40},{\"dataIndex\":\"result\",flex:1,\"text\":\"result\"}],\"rows\":[{\"result\":", (result == null) ? "null" : StringUtil.quote(result.toString()), "}],\"elapsed\":", Long.toString(System.currentTimeMillis() - startTime), "}");
//		if (directOutput) {
//			WebUtil.send(this.response, text);
//			return null;
//		}
//		return text;
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
