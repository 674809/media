package com.egar.mediaui.util.date;

import java.util.Calendar;

/**
 * 日期信息
 * <p>
 * 该类获取指定时间所包含的详细信息，如年月日等
 * 
 * @author Jun.Wang
 */
public final class DateInfo {
	/**
	 * 一天的毫秒数
	 */
	private final static long M_ONE_DAY_Millis = 1000 * 60 * 60 * 24;

	/**
	 * 1970以来的毫秒数
	 */
	public long timeMillis;

	/**
	 * 年/月/日
	 */
	public int year, month, day;

	/**
	 * 日期格式化字符串
	 * <p>
	 * 格式如 {@link DateTemplates#FORMAT_11}
	 */
	public String fomat11Str = "";

	/**
	 * Constructor
	 * 
	 * @param millis
	 *            : 1970以来的毫秒数
	 */
	public DateInfo(long millis) {
		timeMillis = millis;
		parseTimeInfo(timeMillis);
	}

	/**
	 * Constructor
	 * 
	 * @param pastDayNum
	 *            : 如 (pastDayNum==1),表示要创建昨天的时间对象
	 */
	public DateInfo(int pastDayNum) {
		timeMillis = System.currentTimeMillis() - pastDayNum * M_ONE_DAY_Millis;
		parseTimeInfo(timeMillis);
	}

	/**
	 * Parse Date Information
	 * 
	 * @param millis
	 *            :1970以来的毫秒数
	 */
	private void parseTimeInfo(long millis) {
		// format
		fomat11Str = DateFormatUtil.format(timeMillis, DateTemplates.FORMAT_11);
		// Parse
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
	}
}
