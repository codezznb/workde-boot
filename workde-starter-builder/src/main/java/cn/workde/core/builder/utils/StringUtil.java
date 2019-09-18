package cn.workde.core.builder.utils;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;

/**
 * @author zhujingang
 * @date 2019/9/18 9:49 AM
 */
public class StringUtil {

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

	public static String opt(final String string) {
		if (string == null) {
			return "";
		}
		return string;
	}

	public static String encodeBase64(final byte[] bytes) throws Exception {
		OutputStream os = null;
		ByteArrayOutputStream data;
		try {
			data = new ByteArrayOutputStream();
			os = MimeUtility.encode((OutputStream)data, "base64");
			os.write(bytes);
		}
		finally {
			os.close();
		}
		os.close();
		return new String(data.toByteArray());
	}

	public static String encodeBase64(final InputStream is) throws Exception {
		OutputStream os = null;
		ByteArrayOutputStream inData = null;
		ByteArrayOutputStream outData = null;
		try {
			inData = new ByteArrayOutputStream();
			outData = new ByteArrayOutputStream();
			IOUtils.copy(is, (OutputStream)inData);
			os = MimeUtility.encode((OutputStream)outData, "base64");
			os.write(inData.toByteArray());
		}
		finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}
		IOUtils.closeQuietly(is);
		IOUtils.closeQuietly(os);
		return new String(outData.toByteArray());
	}

	public static byte[] decodeBase64(final String data) throws Exception {
		final ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes());
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final InputStream base64is = MimeUtility.decode((InputStream)is, "base64");
		try {
			IOUtils.copy(base64is, (OutputStream)os);
		}
		finally {
			base64is.close();
		}
		base64is.close();
		return os.toByteArray();
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
			IOUtils.copy(stream, (OutputStream)os);
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
}
