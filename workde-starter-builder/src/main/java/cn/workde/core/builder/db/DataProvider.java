package cn.workde.core.builder.db;

import cn.workde.core.builder.utils.DbUtil;
import cn.workde.core.builder.utils.StringUtil;
import cn.workde.core.builder.utils.WebUtil;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhujingang
 * @date 2019/9/19 1:51 PM
 */
@Data
public class DataProvider {

	private HttpServletRequest request;
	private HttpServletResponse response;

	private SqlRowSet resultSet;
	public Long totalCount;
	public long startTime;
	public long beginIndex;
	public long endIndex;
	private String type;
	private String fields;
	private String fieldsTag;

	private Integer limitRecords;
	private Integer limitExportRecords;

	private static JSONArray treeMeta;

	static {
		DataProvider.treeMeta = new JSONArray("[{name:'parentId',type:'auto',defaultValue:null,useNull:true},{name:'index',type:'int',defaultValue:-1,persist:false,convert:null},{name:'depth',type:'int',defaultValue:0,persist:false,convert:null},{name:'expanded',type:'bool',defaultValue:false,persist:false},{name:'expandable',type:'bool',defaultValue:true,persist:false},{name:'checked',type:'bool',defaultValue:null,persist:false},{name:'leaf',type:'bool',defaultValue:false},{name:'cls',type:'string',defaultValue:'',persist:false,convert:null},{name:'iconCls',type:'string',defaultValue:'',persist:false,convert:null},{name:'icon',type:'string',defaultValue:'',persist:false,convert:null},{name:'root',type:'bool',defaultValue:false,persist:false},{name:'isLast',type:'bool',defaultValue:false,persist:false},{name:'isFirst',type:'bool',defaultValue:false,persist:false},{name:'allowDrop',type:'bool',defaultValue:true,persist:false},{name:'allowDrag',type:'bool',defaultValue:true,persist:false},{name:'loaded',type:'bool',defaultValue:false,persist:false},{name:'loading',type:'bool',defaultValue:false,persist:false},{name:'href',type:'string',defaultValue:'',persist:false,convert:null},{name:'hrefTarget',type:'string',defaultValue:'',persist:false,convert:null},{name:'qtip',type:'string',defaultValue:'',persist:false,convert:null},{name:'qtitle',type:'string',defaultValue:'',persist:false,convert:null},{name:'qshowDelay',type:'int',defaultValue:0,persist:false,convert:null},{name:'children',type:'auto',defaultValue:null,persist:false,convert:null},{name:'visible',type:'bool',defaultValue:true,persist:false}]");
	}

	public String getScript() throws Exception {
		if ("array".equals(this.type) || StringUtil.isEmpty(this.type)) {
			return this.getArray(false);
		}else {
			return this.getArray(true);
		}
	}

	public void output() throws Exception{
		String script;
		if ("array".equals(this.type) || StringUtil.isEmpty(this.type)) {
			script = this.getArray(false);
		}else {
			script = this.getArray(true);
		}
		WebUtil.send(this.response, script);
	}

	public String getArray(Boolean isTree) throws Exception {
		long count = 0;
		boolean first = true;
		final boolean hasTotal = this.totalCount != null;
		final StringBuilder buf = new StringBuilder();
		final SqlRowSetMetaData meta = this.resultSet.getMetaData();
		final int j = meta.getColumnCount();
		final String[] names = new String[j];
		final String[] keyNames = new String[j];
		final int[] types = new int[j];
		int maxRecs;

		if(getLimitRecords() == null) maxRecs = 20;
		else if(getLimitRecords() == -1) maxRecs = Integer.MAX_VALUE;
		else maxRecs = getLimitRecords();

		for (int i = 0; i < j; ++i) {
			names[i] = meta.getColumnLabel(i + 1);
			names[i] = DbUtil.getFieldName(names[i]);
			if (StringUtil.isEmpty(names[i])) {
				names[i] = "FIELD" + Integer.toString(i + 1);
			}

			keyNames[i] = StringUtil.quote(String.valueOf(names[i]) + "__V");
			names[i] = StringUtil.quote(names[i]);
			types[i] = meta.getColumnType(i + 1);
		}

		buf.append("{\"success\":true");

		if (!"-".equals(this.fields)) {
			final JSONArray sysMeta = DbUtil.getFields(meta);
			if (!StringUtil.isEmpty(this.fields)) {
				this.mergeFields(sysMeta, new JSONArray(this.fields));
			}
			buf.append(",\"metaData\":{\"fields\":");
			if (isTree) {
				buf.append(this.mergeFields(sysMeta, DataProvider.treeMeta).toString());
			}
			else {
				buf.append(sysMeta.toString());
			}
			if (!StringUtil.isEmpty(this.fieldsTag)) {
				buf.insert(buf.length() - 1, String.valueOf(',') + this.fieldsTag.substring(1, this.fieldsTag.length() - 1));
			}
			buf.append('}');
		}

		if (isTree) {
			buf.append(",\"children\":[");
		}else {
			buf.append(",\"rows\":[");
		}

		while (this.resultSet.next()) {
			++count;

			if (count > maxRecs) {
				--count;
				break;
			}

			if (count < this.beginIndex) {
				continue;
			}
			if (count > this.endIndex) {
				if (hasTotal) {
					break;
				}
				continue;
			} else {
				if (first) {
					first = false;
				}
				else {
					buf.append(',');
				}

				buf.append('{');
				for (int i = 0; i < j; ++i) {
					if (i > 0) {
						buf.append(',');
					}
					Object object = DbUtil.getObject(this.resultSet, i + 1, types[i]);
					buf.append(names[i]);
					buf.append(':');
					if (isTree) {
						String val;
						if (object == null) {
							val = "null";
						}
						else {
							val = object.toString();
							if (val.equals("[]") && "\"children\"".equals(names[i])) {
								val = "[]";
							}
							else {
								val = StringUtil.encode(val);
							}
						}
						buf.append(val);
					}
					else {
						buf.append(StringUtil.encode(object));
					}
				}
				buf.append('}');
			}
		}
		if (!hasTotal) {
			this.totalCount = count;
		}
		buf.append("],\"total\":");
		buf.append(this.totalCount);
		if (this.startTime > 0L) {
			buf.append(",\"elapsed\":");
			buf.append(Long.toString(System.currentTimeMillis() - this.startTime));
		}
		buf.append("}");
		return buf.toString();
	}

	private JSONArray mergeFields(final JSONArray source, final JSONArray dest) {
		int j = source.length() - 1;
		final int l = dest.length();
		for (int k = 0; k < l; ++k) {
			final JSONObject destObj = dest.getJSONObject(k);
			final String destName = destObj.getString("name");
			for (int i = j; i >= 0; --i) {
				final JSONObject sourceObj = source.getJSONObject(i);
				if (destName.equals(sourceObj.getString("name"))) {
					source.remove(i);
					--j;
					break;
				}
			}
		}
		for (int k = 0; k < l; ++k) {
			source.put((Object)dest.getJSONObject(k));
		}
		return source;
	}
}
