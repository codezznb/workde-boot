package cn.workde.core.base.utils;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author zhujingang
 * @date 2019/8/29 9:12 PM
 */
public class DateUtils {

	/** 年-月-日T时:分:秒 */
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	/** 年-月-日 时:分:秒（标准北京时间） */
	public static final DateTimeFormatter y_M_d_H_m_s = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	/** 年-月-日 时:分:秒:毫秒 */
	public static final DateTimeFormatter y_M_d_H_m_s_S = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
	/** 年-月-日 */
	public static final DateTimeFormatter y_M_d = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	/** 年-月-日 00:00:00（当天起始时间） */
	public static final DateTimeFormatter TODAY_START = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
	/** 年-月-日 23:59:59（当天结束时间） */
	public static final DateTimeFormatter TODAY_END = DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59");
	/** 年-月-01 00:00:00（当月起始时间） */
	public static final DateTimeFormatter MONTH_START = DateTimeFormatter.ofPattern("yyyy-MM-01 00:00:00");

}
