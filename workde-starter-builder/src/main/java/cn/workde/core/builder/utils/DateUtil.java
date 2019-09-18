package cn.workde.core.builder.utils;

import cn.workde.core.base.utils.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhujingang
 * @date 2019/9/18 10:25 AM
 */
public class DateUtil {

	public static String format(final Date date, final String format) {
		if (date == null) {
			return "";
		}
		final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	public static String format(final Date date) {
		return format(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String formatDate(final Date date) {
		return format(date, "yyyy-MM-dd");
	}

	public static Timestamp getTimestamp(final long time) {
		return new Timestamp(time);
	}

	public static Timestamp now() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static int daysInMonth(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getActualMaximum(5);
	}

	public static int dayOfMonth(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(5);
	}

	public static int yearOf(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(1);
	}

	public static int dayOfYear(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(6);
	}

	public static int dayOfWeek(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(7);
	}

	public static String dateToStr(final Date value) {
		if (value == null) {
			return null;
		}
		final Timestamp t = new Timestamp(value.getTime());
		return t.toString();
	}

	public static Timestamp strToDate(final String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return Timestamp.valueOf(value);
	}

	public static boolean isDate(final String dateStr) {
		final int len = dateStr.length();
		if (len < 19) {
			return false;
		}
		for (int i = 0; i < len; ++i) {
			final char ch = dateStr.charAt(i);
			switch (i) {
				case 4:
				case 7: {
					if (ch != '-') {
						return false;
					}
					break;
				}
				case 10: {
					if (ch != ' ') {
						return false;
					}
					break;
				}
				case 13:
				case 16: {
					if (ch != ':') {
						return false;
					}
					break;
				}
				case 19: {
					if (ch != '.') {
						return false;
					}
					break;
				}
				default: {
					if (ch < '0' || ch > '9') {
						return false;
					}
					break;
				}
			}
		}
		return true;
	}

	public static Date incYear(final Date date, final int years) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(1, years);
		return cal.getTime();
	}

	public static Date incMonth(final Date date, final int months) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(2, months);
		return cal.getTime();
	}

	public static int hourOfDay(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(11);
	}

	public static String format(final long milliSecs) {
		final long h = milliSecs / 3600000L;
		final long hm = milliSecs % 3600000L;
		final long m = hm / 60000L;
		final long mm = hm % 60000L;
		final long s = mm / 1000L;
		final long sm = mm % 1000L;
		return StringUtil.concat(Long.toString(h), ":", Long.toString(m), ":", Long.toString(s), Float.toString(sm / 1000.0f).substring(1));
	}

	public static Date incDay(final Date date, final long days) {
		return new Date(date.getTime() + 86400000L * days);
	}

	public static Date incSecond(final Date date, final long seconds) {
		return new Date(date.getTime() + 1000L * seconds);
	}

	public static int getElapsedDays(final Date beginDate, final Date endDate) {
		return (int)((endDate.getTime() - beginDate.getTime()) / 86400000L);
	}

	public static String fixTime(final String str) {
		if (str.indexOf(58) == -1) {
			return "00:00:00";
		}
		int b = str.indexOf(32);
		int e = str.indexOf(46);
		if (b == -1) {
			b = 0;
		}
		else {
			++b;
		}
		if (e == -1) {
			e = str.length();
		}
		return str.substring(b, e);
	}

	public static String fixTimestamp(final String str, final boolean dateOnly) {
		final int pos = str.indexOf(32);
		String timePart = null;
		String datePart;
		if (pos == -1) {
			datePart = str;
			if (!dateOnly) {
				timePart = "00:00:00";
			}
		}
		else {
			datePart = str.substring(0, pos);
			if (!dateOnly) {
				timePart = str.substring(pos + 1);
			}
		}
		final String[] sec = StringUtils.split(datePart, "-");
		if (sec.length == 3) {
			final StringBuilder buf = new StringBuilder(dateOnly ? 10 : 30);
			buf.append(sec[0]);
			buf.append('-');
			if (sec[1].length() == 1) {
				buf.append('0');
			}
			buf.append(sec[1]);
			buf.append('-');
			if (sec[2].length() == 1) {
				buf.append('0');
			}
			buf.append(sec[2]);
			if (!dateOnly) {
				buf.append(' ');
				buf.append(timePart);
			}
			return buf.toString();
		}
		return str;
	}
}
