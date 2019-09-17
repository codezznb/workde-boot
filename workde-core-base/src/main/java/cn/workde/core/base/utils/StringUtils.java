package cn.workde.core.base.utils;

import cn.hutool.core.util.StrUtil;

/**
 * @author zhujingang
 * @date 2019/8/29 9:00 AM
 */
public class StringUtils extends StrUtil {

    /**
     * 判断String数组是否为空<br>
     * <p>弱判断，只确定数组中第一个元素是否为空</p>
     * @param array 要判断的String[]数组
     * @return String数组长度==0或者第一个元素为空（true）
     */
    public static boolean isEmptys(String[] array) {
        return (null == array || array.length == 0 || isEmpty(array[0])) ? true : false;
    }

    /**
     * 删除尾部相等的字符串
     * @param sb 		需要处理的字符串
     * @param condition 条件
     * @return 删除后的StringBuffer
     */
    public static StringBuffer deleteLastEqualString(StringBuffer sb, String condition) {
        int end = sb.length();
        int start = end - condition.length();
        String str = sb.substring(start, end);
        if (condition.equals(str)) {
            return sb.delete(start, end);
        }
        return sb;
    }

    /**
     * 删除前后字符串
     * @param str		需要处理的字符串
     * @param length	删除长度
     * @return 删除后的字符串
     */
    public static String deleteFirstLastString(String str, int length) {
        return str.substring(length, str.length() - length);
    }

	private static boolean containsText(CharSequence str) {
		int strLen = str.length();

		for(int i = 0; i < strLen; ++i) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 如果字符串里面的值为null， ""， "   "，那么返回值为false；否则为true
	 * @param str
	 * @return
	 */
	public static Boolean hasText(String str){
		return str != null && !str.isEmpty() && containsText(str);
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
}
