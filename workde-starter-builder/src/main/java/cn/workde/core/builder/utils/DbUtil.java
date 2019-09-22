package cn.workde.core.builder.utils;

import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.builder.db.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.*;

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

	public static String[] buildSQLs(final String tableName, final boolean ignoreBlob, final int scriptType, final HttpServletRequest request, final JSONObject fields, final JSONObject whereFields, final JSONObject fieldsMap, final String pk) throws Exception {
		final String[] sqls = new String[4];
		final StringBuilder selectFields = new StringBuilder();
		final StringBuilder insertFields = new StringBuilder();
		final StringBuilder insertParams = new StringBuilder();
		final StringBuilder condition = new StringBuilder();
		final StringBuilder updateParams = new StringBuilder();
		boolean isFirstSelect = true;
		boolean isFirstUpdate = true;
		boolean isFirstCondi = true;
		final boolean hasRequest = request != null;
		final boolean useDouble = true;
		final boolean whereUseDate = false;
		final boolean whereUseFloat = false;

		JdbcTemplate jdbcTemplate = SpringUtils.getBean(JdbcTemplate.class);
		ResultSetWrappingSqlRowSet rs = (ResultSetWrappingSqlRowSet) jdbcTemplate.queryForRowSet("select * from " + tableName + " where 1 = 0");
		ResultSetMetaData meta = rs.getResultSet().getMetaData();

		for (int j = meta.getColumnCount() + 1, i = 1; i < j; ++i) {
			int type = meta.getColumnType(i);
			String typeName = getTypeName(type);
			int precision = meta.getPrecision(i);
			if (precision <= 0) {
				precision = 100;
			}
			final boolean isText = isTextField(type) || precision > 10000;
			final boolean isBlob = isBlobField(type);
			final boolean isDateTime = type == 92 || type == 91 || type == 93;
			int scale = meta.getScale(i);
			if (scale < 0) {
				scale = 100;
			}
			final boolean isFloat = maybeFloatField(type) && scale > 0;
			if (isFloat && useDouble) {
				type = 8;
				typeName = "double";
			}

			final boolean required = meta.isNullable(i) == 0;
			final boolean readOnly = meta.isReadOnly(i);
			String fieldName = meta.getColumnLabel(i);
			String fieldValueName;
			fieldName = (fieldValueName = getFieldName(fieldName));
			fieldName = StringUtil.quoteIf(fieldName);
			if (fieldsMap != null) {
				final String mapName = fieldsMap.optString(fieldValueName, (String)null);
				if (mapName != null) {
					fieldValueName = mapName;
				}
			}
			if (!isBlob || !hasRequest || !StringUtil.isEmpty(WebUtil.fetch(request, fieldValueName)) || "1".equals(WebUtil.fetch(request, "$" + fieldValueName))) {
				if ((!ignoreBlob || !isBlob) && (fields == null || fields.has(fieldValueName) || fields.has("$" + fieldValueName))) {
					if (isFirstSelect) {
						isFirstSelect = false;
					}
					else {
						selectFields.append(',');
					}
					selectFields.append(fieldName);
					if (!readOnly) {
						if (isFirstUpdate) {
							isFirstUpdate = false;
						}
						else {
							insertFields.append(',');
							insertParams.append(',');
							updateParams.append(',');
						}
						String param = null;
						switch (scriptType) {
							case 1: {
								if (typeName == null) {
									param = StringUtil.concat("{?", fieldValueName, "?}");
									break;
								}
								param = StringUtil.concat("{?", typeName, ".", fieldValueName, "?}");
								break;
							}
							case 2: {
								param = StringUtil.concat("{#", fieldValueName, "#}");
								break;
							}
							default: {
								param = fieldValueName;
								break;
							}
						}
						insertFields.append(fieldName);
						insertParams.append(param);
						updateParams.append(fieldName);
						updateParams.append('=');
						updateParams.append(param);
					}
				}
				if (!isText && !isBlob && (!isFloat || whereUseFloat) && (!isDateTime || whereUseDate) && (whereFields == null || whereFields.has(fieldValueName) || whereFields.has("#" + fieldValueName))) {
					if (isFirstCondi) isFirstCondi = false;
					else condition.append(" and ");
					condition.append(getCondition(fieldName, fieldValueName, isStringField(type), typeName, required, scriptType));
				}
			}
		}

//		if (!StringUtils.isEmpty(pk)) {
//			condition.append( pk + " = {?"+pk+"?}");
//		}


		//preparePagingData();
		sqls[0] = StringUtil.concat("insert into ", tableName, " (", insertFields.toString(), ") values (", insertParams.toString(), ")");
		sqls[1] = StringUtil.concat("update ", tableName, " set ", updateParams.toString(), " where ", condition.toString());
		sqls[2] = StringUtil.concat("delete from ", tableName, " where ", condition.toString());
		sqls[3] = StringUtil.concat("select ", selectFields.toString(), " from ", tableName, " where ", condition.toString());

		return sqls;
	}

	private static String getCondition(final String fieldName, final String fieldValueName, final boolean isStringField, final String typeName, final boolean required, final int scriptType) {
		final StringBuilder buf = new StringBuilder();
		switch (scriptType) {
			case 1: {
				if (isStringField) {
					buf.append("({?#");
					buf.append(fieldValueName);
					buf.append("?} is null and (");
					buf.append(fieldName);
					buf.append(" is null or ");
					buf.append(fieldName);
					buf.append("='') or ");
					buf.append(fieldName);
					buf.append("={?");
					if (typeName == null) {
						buf.append('#');
					}
					else {
						buf.append(typeName);
						buf.append(".#");
					}
					buf.append(fieldValueName);
					buf.append("?})");
					break;
				}
				if (!required) {
					buf.append("({?#");
					buf.append(fieldValueName);
					buf.append("?} is null and ");
					buf.append(fieldName);
					buf.append(" is null or ");
				}
				buf.append(fieldName);
				buf.append("={?");
				if (typeName == null) {
					buf.append('#');
				}
				else {
					buf.append(typeName);
					buf.append(".#");
				}
				buf.append(fieldValueName);
				if (required) {
					buf.append("?}");
					break;
				}
				buf.append("?})");
				break;
			}
			case 2: {
				if (!required) {
					buf.append("({##");
					buf.append(fieldValueName);
					buf.append("#} is null and ");
					buf.append(fieldName);
					buf.append(" is null or ");
				}
				buf.append(fieldName);
				buf.append("={##");
				buf.append(fieldValueName);
				if (required) {
					buf.append("#}");
					break;
				}
				buf.append("#})");
				break;
			}
			default: {
				if (!required) {
					buf.append("(#");
					buf.append(fieldValueName);
					buf.append(" is null and ");
					buf.append(fieldName);
					buf.append(" is null or ");
				}
				buf.append(fieldName);
				buf.append("=#");
				buf.append(fieldValueName);
				if (!required) {
					buf.append(')');
					break;
				}
				break;
			}
		}
		return buf.toString();
	}

	public static String getOrderBySql(final HttpServletRequest request, final String orderFields) {
		final String sort = request.getParameter("sort");
		if (StringUtil.isEmpty(sort)) {
			return "";
		}
		final String orderExp = getOrderSql(sort, orderFields);
		return " order by " + orderExp;
	}

	public static String getOrderSql(final HttpServletRequest request, final String orderFields) {
		final String sort = request.getParameter("sort");
		if (StringUtil.isEmpty(sort)) {
			return "";
		}
		return "," + getOrderSql(sort, orderFields);
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

	public static Object run(final HttpServletRequest request, final String sql) throws Exception {
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

	public static JSONArray getFields(final ResultSetMetaData meta) throws Exception {
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

	public static void setObject(final PreparedStatement statement, final int index, final int type, Object object) throws Exception {
		if (object == null || object instanceof String) {
			String value;
			if (object == null) {
				value = null;
			}
			else {
				value = (String)object;
			}
			if (StringUtil.isEmpty(value)) {
				statement.setNull(index, type);
			}
			else {
				switch (type) {
					case -15:
					case -9:
					case 1:
					case 12: {
						if ("__@".equals(value)) {
							statement.setString(index, "");
							break;
						}
						statement.setString(index, value);
						break;
					}
					case 4: {
						statement.setInt(index, Integer.parseInt(StringUtil.convertBool(value)));
						break;
					}
					case -6: {
						statement.setByte(index, Byte.parseByte(StringUtil.convertBool(value)));
						break;
					}
					case 5: {
						statement.setShort(index, Short.parseShort(StringUtil.convertBool(value)));
						break;
					}
					case -5: {
						statement.setLong(index, Long.parseLong(StringUtil.convertBool(value)));
						break;
					}
					case 6:
					case 7: {
						statement.setFloat(index, Float.parseFloat(StringUtil.convertBool(value)));
						break;
					}
					case 8: {
						statement.setDouble(index, Double.parseDouble(StringUtil.convertBool(value)));
						break;
					}
					case 2:
					case 3: {
						statement.setBigDecimal(index, new BigDecimal(StringUtil.convertBool(value)));
						break;
					}
					case 93: {
						statement.setTimestamp(index, Timestamp.valueOf(DateUtil.fixTimestamp(value, false)));
						break;
					}
					case 91: {
						if (value.indexOf(32) != -1) {
							statement.setTimestamp(index, Timestamp.valueOf(DateUtil.fixTimestamp(value, false)));
							break;
						}
						statement.setDate(index, java.sql.Date.valueOf(DateUtil.fixTimestamp(value, true)));
						break;
					}
					case 92: {
						if (value.indexOf(32) != -1) {
							statement.setTimestamp(index, Timestamp.valueOf(DateUtil.fixTimestamp(value, false)));
							break;
						}
						statement.setTime(index, Time.valueOf(DateUtil.fixTime(value)));
						break;
					}
					case -7:
					case 16: {
						statement.setBoolean(index, StringUtil.getBool(value));
						break;
					}
					case -16:
					case -1:
					case 2005:
					case 2011: {
						statement.setCharacterStream(index, new StringReader(value), value.length());
						break;
					}
					case -4:
					case -3:
					case -2:
					case 2004: {
						final InputStream is = new ByteArrayInputStream(StringUtil.decodeBase64(value));
						statement.setBinaryStream(index, is, is.available());
						break;
					}
					default: {
						statement.setObject(index, value, type);
						break;
					}
				}
			}
		}
		else if (object instanceof InputStream) {
			statement.setBinaryStream(index, (InputStream)object, ((InputStream)object).available());
		}
		else if (object instanceof Date) {
			statement.setTimestamp(index, new Timestamp(((Date)object).getTime()));
		}
		else if (object instanceof Double && !maybeFloatField(type)) {
			object = ((Double)object).intValue();
			statement.setObject(index, object, type);
		}
		else {
			statement.setObject(index, object, type);
		}
	}

	public static Object getObject(final CallableStatement statement, final int index, final int type) throws Exception {
		Object obj = null;
		switch (type) {
			case -15:
			case -9:
			case 1:
			case 12: {
				obj = statement.getString(index);
				break;
			}
			case 4: {
				obj = statement.getInt(index);
				break;
			}
			case -6: {
				obj = statement.getByte(index);
				break;
			}
			case 5: {
				obj = statement.getShort(index);
				break;
			}
			case -5: {
				obj = statement.getLong(index);
				break;
			}
			case 6:
			case 7: {
				obj = statement.getFloat(index);
				break;
			}
			case 8: {
				obj = statement.getDouble(index);
				break;
			}
			case 2:
			case 3: {
				obj = statement.getBigDecimal(index);
				break;
			}
			case 93: {
				obj = statement.getTimestamp(index);
				break;
			}
			case 91: {
				obj = statement.getDate(index);
				break;
			}
			case 92: {
				obj = statement.getTime(index);
				break;
			}
			case -7:
			case 16: {
				obj = (statement.getBoolean(index) ? 1 : 0);
				break;
			}
			case -16:
			case -1:
			case 2005:
			case 2011: {
				final Reader rd = statement.getCharacterStream(index);
				if (rd == null) {
					obj = null;
					break;
				}
				obj = SysUtil.readString(rd);
				break;
			}
			default: {
				obj = statement.getObject(index);
				break;
			}
		}
		if (statement.wasNull()) {
			return null;
		}
		return obj;
	}

	public static Object getObject(final ResultSet rs, final int index, final int type) throws Exception {
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
			case 2011: {
				final Reader rd = rs.getCharacterStream(index);
				if (rd == null) {
					obj = null;
					break;
				}
				obj = SysUtil.readString(rd);
				break;
			}
			case -4:
			case -3:
			case -2:
			case 2004: {
				final InputStream is = rs.getBinaryStream(index);
				if (is != null) {
					is.close();
				}
				obj = "(blob)";
				break;
			}
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

	public static Object getObject(final ResultSet rs, final String fieldName, final int type) throws Exception {
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
			case 2011: {
				final Reader rd = rs.getCharacterStream(fieldName);
				if (rd == null) {
					obj = null;
					break;
				}
				obj = SysUtil.readString(rd);
				break;
			}
			case -4:
			case -3:
			case -2:
			case 2004: {
				final InputStream is = rs.getBinaryStream(fieldName);
				if (is != null) {
					is.close();
				}
				obj = "(blob)";
				break;
			}
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

	public static boolean isBlobField(final int type) {
		switch (type) {
			case -4:
			case -3:
			case -2:
			case 2004: {
				return true;
			}
			default: {
				return false;
			}
		}
	}

	public static boolean isTextField(final int type) {
		switch (type) {
			case -16:
			case -1:
			case 2005:
			case 2011: {
				return true;
			}
			default: {
				return false;
			}
		}
	}

	public static boolean isStringField(final int type) {
		switch (type) {
			case -15:
			case -9:
			case 1:
			case 12: {
				return true;
			}
			default: {
				return false;
			}
		}
	}

	public static boolean maybeFloatField(final int type) {
		switch (type) {
			case 2:
			case 3:
			case 6:
			case 7:
			case 8: {
				return true;
			}
			default: {
				return false;
			}
		}
	}

	public static Connection getConnection(HttpServletRequest request) throws SQLException {
		String storeName = "conn@@";
		final Object obj = WebUtil.getObject(request, storeName);
		Connection conn;
		if (obj == null) {
			JdbcTemplate jdbcTemplate = SpringUtils.getBean(JdbcTemplate.class);
			conn = jdbcTemplate.getDataSource().getConnection();
			WebUtil.setObject(request, storeName, conn);
		}
		else {
			conn = (Connection)obj;
		}
		return conn;
	}

	public static void closeCommit(final Connection connection, DataSource dataSource) {
		if (connection != null) {
			closeConnection(connection, false, dataSource);
		}
	}

	private static void closeConnection(Connection connection, final boolean rollback, DataSource dataSource) {
		try {
			if (connection.isClosed()) {
				return;
			}
			try {
				if (!connection.getAutoCommit()) {
					if (rollback) connection.rollback();
					else connection.commit();
				}
			}
			catch (Throwable e) {
				if (!rollback) {
					connection.rollback();
				}
				return;
			}
			finally {
				DataSourceUtils.releaseConnection(connection, dataSource);
			}

		}
		catch (Throwable t) {}
	}
}
