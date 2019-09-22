package cn.workde.core.builder.utils;

import cn.hutool.core.codec.Base64;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;

import java.io.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author zhujingang
 * @date 2019/9/18 9:49 AM
 */
public class StringUtil {

	public static String[] split(final String string, final String separator) {
		return split(string, separator, false);
	}

	public static String[] split(String string, final String separator, final boolean trim) {
		int pos = 0;
		int oldPos = 0;
		int index = 0;
		final int separatorLen = separator.length();
		final ArrayList<Integer> posData = new ArrayList<Integer>();
		if (string == null) {
			string = "";
		}
		while ((pos = string.indexOf(separator, pos)) != -1) {
			posData.add(pos);
			pos += separatorLen;
		}
		posData.add(string.length());
		final String[] result = new String[posData.size()];
		for (final int p : posData) {
			if (trim) {
				result[index] = string.substring(oldPos, p).trim();
			}
			else {
				result[index] = string.substring(oldPos, p);
			}
			oldPos = p + separatorLen;
			++index;
		}
		return result;
	}

	public static String[] split(final String string, final char separator) {
		return split(string, separator, false);
	}

	public static String[] split(String string, final char separator, final boolean trim) {
		int pos = 0;
		int oldPos = 0;
		int index = 0;
		final ArrayList<Integer> posData = new ArrayList<Integer>();
		if (string == null) {
			string = "";
		}
		while ((pos = string.indexOf(separator, pos)) != -1) {
			posData.add(pos);
			++pos;
		}
		posData.add(string.length());
		final String[] result = new String[posData.size()];
		for (final int p : posData) {
			if (trim) {
				result[index] = string.substring(oldPos, p).trim();
			}
			else {
				result[index] = string.substring(oldPos, p);
			}
			oldPos = p + 1;
			++index;
		}
		return result;
	}

	public static boolean isEmpty(final String string) {
		return string == null || string.length() == 0;
	}

	public static int indexOf(final String[] list, final String string) {
		if (list == null) {
			return -1;
		}
		for (int j = list.length, i = 0; i < j; ++i) {
			if (list[i].equals(string)) {
				return i;
			}
		}
		return -1;
	}

	public static boolean checkName(final String name) {
		for (int j = name.length(), i = 0; i < j; ++i) {
			final char c = name.charAt(i);
			if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && c != '_' && (c < '0' || c > '9')) {
				return false;
			}
		}
		return true;
	}

	public static boolean isInteger(final String string) {
		if (string == null) {
			return false;
		}
		final int j = string.length();
		if (j == 0) {
			return false;
		}
		for (int i = 0; i < j; ++i) {
			final char ch = string.charAt(i);
			if ((ch < '0' || ch > '9') && (i != 0 || ch != '-')) {
				return false;
			}
		}
		return true;
	}

	public static boolean isSame(final String string1, final String string2) {
		if (string1 != null) {
			return string1.equalsIgnoreCase(string2);
		}
		return string2 == null || string2.equalsIgnoreCase(string1);
	}

	public static boolean isEqual(final String string1, final String string2) {
		if (string1 != null) {
			return string1.equals(string2);
		}
		return string2 == null || string2.equals(string1);
	}

	public static String concat(final String... string) {
		int length = 0;
		for (final String str : string) {
			length += str.length();
		}
		final StringBuilder buf = new StringBuilder(length);
		for (final String str2 : string) {
			buf.append(str2);
		}
		return buf.toString();
	}

	public static String quoteIf(final String name) {
		if (checkName(name)) {
			return name;
		}
		return quote(name);
	}

	public static String quote(final String string) {
		return quote(string, true);
	}

	public static String text(final Object value) {
		return quote((value == null) ? null : value.toString(), false);
	}

	public static String quote(final String string, final boolean addQuotes) {
		final int len;
		if (string != null && (len = string.length()) != 0) {
			char curChar = '\0';
			final StringBuilder sb = new StringBuilder(len + 10);
			if (addQuotes) {
				sb.append('\"');
			}
			for (int i = 0; i < len; ++i) {
				final char lastChar = curChar;
				curChar = string.charAt(i);
				switch (curChar) {
					case '\"':
					case '\\': {
						sb.append('\\');
						sb.append(curChar);
						break;
					}
					case '/': {
						if (lastChar == '<') {
							sb.append('\\');
						}
						sb.append(curChar);
						break;
					}
					case '\b': {
						sb.append("\\b");
						break;
					}
					case '\t': {
						sb.append("\\t");
						break;
					}
					case '\n': {
						sb.append("\\n");
						break;
					}
					case '\f': {
						sb.append("\\f");
						break;
					}
					case '\r': {
						sb.append("\\r");
						break;
					}
					default: {
						if (curChar < ' ' || (curChar >= '\u0080' && curChar < ' ') || (curChar >= '\u2000' && curChar < '\u2100')) {
							sb.append("\\u");
							final String str = Integer.toHexString(curChar);
							sb.append("0000", 0, 4 - str.length());
							sb.append(str);
							break;
						}
						sb.append(curChar);
						break;
					}
				}
			}
			if (addQuotes) {
				sb.append('\"');
			}
			return sb.toString();
		}
		if (addQuotes) {
			return "\"\"";
		}
		return "";
	}

	public static String replaceAll(final String string, final String oldString, final String newString) {
		return innerReplace(string, oldString, newString, true);
	}

	public static String replaceFirst(final String string, final String oldString, final String newString) {
		return innerReplace(string, oldString, newString, false);
	}

	private static String innerReplace(final String string, final String oldString, final String newString, final boolean isAll) {
		int index = string.indexOf(oldString);
		if (index == -1) {
			return string;
		}
		int start = 0;
		final int len = oldString.length();
		if (len == 0) {
			return string;
		}
		final StringBuilder buffer = new StringBuilder(string.length());
		do {
			buffer.append(string.substring(start, index));
			buffer.append(newString);
			start = index + len;
			if (!isAll) {
				break;
			}
			index = string.indexOf(oldString, start);
		} while (index != -1);
		buffer.append(string.substring(start));
		return buffer.toString();
	}

	public static String replaceParams(final JSONObject jo, final String text) {
		System.out.println("text:" + text);
		if (jo == null || isEmpty(text)) {
			return text;
		}
		int start = 0;
		int startPos = text.indexOf("${", start);
		int endPos = text.indexOf("}", startPos + 2);
		if (startPos != -1 && endPos != -1) {
			final StringBuilder buf = new StringBuilder(text.length());
			while (startPos != -1 && endPos != -1) {
				final String paramName = text.substring(startPos + 2, endPos);
				String paramValue = jo.optString(paramName);

				buf.append(text.substring(start, startPos));
				if (paramValue != null) {
					buf.append(paramValue);
				}
				start = endPos + 1;
				startPos = text.indexOf("${", start);
				endPos = text.indexOf("}", startPos + 2);
			}
			buf.append(text.substring(start));
			return buf.toString();
		}
		System.out.println("return text:" + text);
		return text;
	}

	public static String opt(final String string) {
		if (string == null) {
			return "";
		}
		return string;
	}

	public static String encodeBase64(final byte[] bytes) throws Exception {
		return Base64.encode(bytes);
	}

	public static String encodeBase64(final InputStream is) throws Exception {
		return Base64.encode(is);
	}

	public static byte[] decodeBase64(final String data) throws Exception {
		return Base64.decode(data);
	}

	public static String encode(final Object object) throws Exception {
		if (object == null) {
			return "null";
		}
		if (object instanceof InputStream) {
			return quote(encodeBase64((InputStream)object));
		}
		if (object instanceof Number || object instanceof Boolean) {
			return object.toString();
		}
		if (object instanceof Timestamp || object instanceof java.sql.Date || object instanceof Time) {
			return quote(object.toString());
		}
		if (object instanceof Date) {
			return quote(DateUtil.dateToStr((Date)object));
		}
		return quote(object.toString());
	}


	public static String getString(final InputStream stream) throws IOException {
		return getString(stream, "utf-8");
	}

	public static String getStringA(final InputStream stream) throws IOException {
		return getString(stream, "utf-8", false);
	}

	public static String getString(final InputStream stream, final String charset) throws IOException {
		return getString(stream, charset, true);
	}

	public static String getString(final InputStream stream, final String charset, final boolean closeStream) throws IOException {
		if (stream == null) {
			return null;
		}
		try {
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			IOUtils.copy(stream, os);
			if (isEmpty(charset)) {
				return new String(os.toByteArray());
			}
			return new String(os.toByteArray(), charset);
		}
		finally {
			if (closeStream) {
				stream.close();
			}
		}
	}

	public static String select(final String... string) {
		for (final String s : string) {
			if (!isEmpty(s)) {
				return s;
			}
		}
		return "";
	}

	public static String toString(final Object object) {
		if (object == null) {
			return "";
		}
		if (object instanceof Date) {
			return DateUtil.dateToStr((Date)object);
		}
		return object.toString();
	}

	public static String getNamePart(final String string) {
		return getNamePart(string, '=');
	}

	public static String getValuePart(final String string) {
		return getValuePart(string, '=');
	}

	public static String getNamePart(final String string, final char separator) {
		if (string == null) {
			return "";
		}
		final int index = string.indexOf(separator);
		if (index == -1) {
			return string;
		}
		return string.substring(0, index);
	}

	public static String getValuePart(final String string, final char separator) {
		if (string == null) {
			return "";
		}
		final int index = string.indexOf(separator);
		if (index == -1) {
			return "";
		}
		return string.substring(index + 1);
	}

	public static String convertBool(final String value) {
		if ("true".equalsIgnoreCase(value)) {
			return "1";
		}
		if ("false".equalsIgnoreCase(value)) {
			return "0";
		}
		return value;
	}

	public static boolean getBool(final String value) {
		return value != null && !value.equalsIgnoreCase("false") && !value.equals("0") && !value.isEmpty();
	}
}
