package cn.workde.core.builder.utils;

import cn.workde.core.builder.db.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * @author zhujingang
 * @date 2019/9/19 10:53 AM
 */
public class DbUtil {

	public static final Object[][] sqlTypes;

	static {
		sqlTypes = new Object[][] { { "BIT", -7 }, { "TINYINT", -6 }, { "SMALLINT", 5 }, { "INTEGER", 4 }, { "BIGINT", -5 }, { "FLOAT", 6 }, { "REAL", 7 }, { "DOUBLE", 8 }, { "NUMERIC", 2 }, { "DECIMAL", 3 }, { "CHAR", 1 }, { "VARCHAR", 12 }, { "LONGVARCHAR", -1 }, { "DATE", 91 }, { "TIME", 92 }, { "TIMESTAMP", 93 }, { "BINARY", -2 }, { "VARBINARY", -3 }, { "LONGVARBINARY", -4 }, { "NULL", 0 }, { "OTHER", 1111 }, { "JAVA_OBJECT", 2000 }, { "DISTINCT", 2001 }, { "STRUCT", 2002 }, { "ARRAY", 2003 }, { "BLOB", 2004 }, { "CLOB", 2005 }, { "REF", 2006 }, { "DATALINK", 70 }, { "BOOLEAN", 16 }, { "ROWID", -8 }, { "NCHAR", -15 }, { "NVARCHAR", -9 }, { "LONGNVARCHAR", -16 }, { "NCLOB", 2011 }, { "SQLXML", 2009 } };
	}

	public static Integer getFieldType(final String name) {
		if (StringUtil.isEmpty(name)) {
			return 12;
		}
		for (int j = DbUtil.sqlTypes.length, i = 0; i < j; ++i) {
			if (name.equalsIgnoreCase((String)DbUtil.sqlTypes[i][0])) {
				return (Integer)DbUtil.sqlTypes[i][1];
			}
		}
		if (StringUtil.isInteger(name)) {
			return Integer.parseInt(name);
		}
		return null;
	}

	public static String getTypeName(final int type) {
		final int j = DbUtil.sqlTypes.length;
		switch (type) {
			case -15:
			case -9:
			case 1:
			case 12: {
				return null;
			}
			default: {
				for (int i = 0; i < j; ++i) {
					if (type == (int)DbUtil.sqlTypes[i][1]) {
						return ((String)DbUtil.sqlTypes[i][0]).toLowerCase();
					}
				}
				return Integer.toString(type);
			}
		}
	}

	public static void startTransaction(final Connection connection, final String isolation) throws Exception {
		if (!connection.getAutoCommit()) {
			connection.commit();
		}
		connection.setAutoCommit(false);
		if (!StringUtil.isEmpty(isolation)) {
			if (isolation.equals("readUncommitted")) {
				connection.setTransactionIsolation(1);
			} else if (isolation.equals("readCommitted")) {
				connection.setTransactionIsolation(2);
			} else if (isolation.equals("repeatableRead")) {
				connection.setTransactionIsolation(4);
			} else if (isolation.equals("serializable")) {
				connection.setTransactionIsolation(8);
			}
		}
	}

	public static String getOrderSql(final String sort, final String orderFields) {
		final JSONArray ja = new JSONArray(sort);
		final int j = ja.length();
		if (j > 0) {
			final StringBuilder exp = new StringBuilder();
			JSONObject orderJo;
			String defaultPrefix;
			if (StringUtil.isEmpty(orderFields)) {
				orderJo = null;
				defaultPrefix = null;
			} else {
				orderJo = new JSONObject(orderFields);
				defaultPrefix = orderJo.optString("default", (String) null);
			}
			for (int i = 0; i < j; ++i) {
				final JSONObject jo = ja.getJSONObject(i);
				if (i > 0) {
					exp.append(',');
				}
				final String property = jo.getString("property");
				if (!StringUtil.checkName(property)) {
					throw new IllegalArgumentException("Invalid name \"" + property + "\".");
				}
				if (orderJo != null) {
					if (orderJo.has(property)) {
						final String prefix = orderJo.optString(property);
						if (!prefix.isEmpty()) {
							exp.append(prefix);
							exp.append('.');
						}
					} else if (defaultPrefix != null) {
						exp.append(defaultPrefix);
						exp.append('.');
					}
				}
				exp.append(property);
				if (StringUtil.isSame(jo.optString("direction"), "desc")) {
					exp.append(" desc");
				}
			}
			return exp.toString();
		}
		return null;
	}

	public static SqlRowSet run(final HttpServletRequest request, final String sql) throws Exception {
		final Query query = new Query();
		query.setRequest(request);
		query.setSql(sql);
		return query.run();
	}

	public static String getFieldName(final String fieldName) {
		return fieldName;
//		if (fieldName.startsWith("#")) {
//			return fieldName.substring(1);
//		}
//		return fieldName.toUpperCase();
	}

	public static JSONArray getFields(final SqlRowSetMetaData meta) throws Exception {
		final int j = meta.getColumnCount();
		final String[] mapTable = { null };
		final JSONArray ja = new JSONArray();
		for (int i = 0; i < j; ++i) {
			final int k = i + 1;
			JSONObject jo = new JSONObject();
			String name = meta.getColumnLabel(k);
			name = getFieldName(name);
			if (StringUtil.isEmpty(name)) {
				name = "FIELD" + Integer.toString(k);
			}
			final int type = meta.getColumnType(k);
			String category= getTypeCategory(type);
			String format = null;
			switch (type) {
				case 93: {
					format = "Y-m-d H:i:s.u";
					break;
				}
				case 91: {
					format = "Y-m-d";
					break;
				}
				case 92: {
					format = "H:i:s";
					break;
				}
				default: {
					format = null;
					break;
				}
			}
			jo.put("name", (Object)name);
			jo.put("type", (Object)category);
			if (format != null) {
				jo.put("dateFormat", (Object)format);
			}
			if (category.equals("string")) {
				jo.put("useNull", false);
			}
			ja.put((Object)jo);
		}
		return ja;
	}

	public static String getTypeCategory(final int type) {
		switch (type) {
			case -7:
			case -6:
			case -5:
			case 4:
			case 5:
			case 16: {
				return "int";
			}
			case 2:
			case 3:
			case 6:
			case 7:
			case 8: {
				return "float";
			}
			case 91:
			case 92:
			case 93: {
				return "date";
			}
			default: {
				return "string";
			}
		}
	}

	public static Object getObject(final SqlRowSet rs, final int index, final int type) throws Exception {
		Object obj = null;
		switch (type) {
			case -15:
			case -9:
			case 1:
			case 12: {
				obj = rs.getString(index);
				break;
			}
			case 4: {
				obj = rs.getInt(index);
				break;
			}
			case -6: {
				obj = rs.getByte(index);
				break;
			}
			case 5: {
				obj = rs.getShort(index);
				break;
			}
			case -5: {
				obj = rs.getLong(index);
				break;
			}
			case 6:
			case 7: {
				obj = rs.getFloat(index);
				break;
			}
			case 8: {
				obj = rs.getDouble(index);
				break;
			}
			case 2:
			case 3: {
				obj = rs.getBigDecimal(index);
				break;
			}
			case 93: {
				obj = rs.getTimestamp(index);
				break;
			}
			case 91: {
				obj = rs.getDate(index);
				break;
			}
			case 92: {
				obj = rs.getTime(index);
				break;
			}
			case -7:
			case 16: {
				obj = (rs.getBoolean(index) ? 1 : 0);
				break;
			}
			case -16:
			case -1:
			case 2005:
//			case 2011: {
//				final Reader rd = rs.getCharacterStream(index);
//				if (rd == null) {
//					obj = null;
//					break;
//				}
//				obj = SysUtil.readString(rd);
//				break;
//			}
			case -4:
			case -3:
			case -2:
//			case 2004: {
//				final InputStream is = rs.getBinaryStream(index);
//				if (is != null) {
//					is.close();
//				}
//				obj = "(blob)";
//				break;
//			}
			default: {
				obj = rs.getObject(index);
				break;
			}
		}
		if (rs.wasNull()) {
			return null;
		}
		return obj;
	}

	public static Object getObject(final SqlRowSet rs, final String fieldName, final int type) throws Exception {
		Object obj = null;
		switch (type) {
			case -15:
			case -9:
			case 1:
			case 12: {
				obj = rs.getString(fieldName);
				break;
			}
			case 4: {
				obj = rs.getInt(fieldName);
				break;
			}
			case -6: {
				obj = rs.getByte(fieldName);
				break;
			}
			case 5: {
				obj = rs.getShort(fieldName);
				break;
			}
			case -5: {
				obj = rs.getLong(fieldName);
				break;
			}
			case 6:
			case 7: {
				obj = rs.getFloat(fieldName);
				break;
			}
			case 8: {
				obj = rs.getDouble(fieldName);
				break;
			}
			case 2:
			case 3: {
				obj = rs.getBigDecimal(fieldName);
				break;
			}
			case 93: {
				obj = rs.getTimestamp(fieldName);
				break;
			}
			case 91: {
				obj = rs.getDate(fieldName);
				break;
			}
			case 92: {
				obj = rs.getTime(fieldName);
				break;
			}
			case -7:
			case 16: {
				obj = (rs.getBoolean(fieldName) ? 1 : 0);
				break;
			}
			case -16:
			case -1:
			case 2005:
//			case 2011: {
//				final Reader rd = rs.getCharacterStream(fieldName);
//				if (rd == null) {
//					obj = null;
//					break;
//				}
//				obj = SysUtil.readString(rd);
//				break;
//			}
			case -4:
			case -3:
			case -2:
//			case 2004: {
//				final InputStream is = rs.getBinaryStream(fieldName);
//				if (is != null) {
//					is.close();
//				}
//				obj = "(blob)";
//				break;
//			}
			default: {
				obj = rs.getObject(fieldName);
				break;
			}
		}
		if (rs.wasNull()) {
			return null;
		}
		return obj;
	}
}
