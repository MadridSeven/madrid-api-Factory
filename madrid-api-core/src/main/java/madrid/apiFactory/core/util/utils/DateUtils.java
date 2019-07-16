package madrid.apiFactory.core.util.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
	private static final Logger log = LoggerFactory.getLogger(DateUtils.class);
	public static final String LONG_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String LONG_DATE_FORMAT = "yyyy-MM-dd";
	public static final String SHORT_DATE_FORMAT = "MM-dd";
	public static final String LONG_TIME_FORMAT = "HH:mm:ss";
	/**yyyyMM ：示例：201710*/
	public static final String LONG_MONTH_FORMAT = "yyyyMM";

	public static Date toDate(String date, String format) {
		ParsePosition pos = new ParsePosition(0);
		Date d = toDate(date, format, pos);
		if ((d != null) && (pos.getIndex() != date.length())) {
			d = null;
		}
		return d;
	}

	public static Date toDate(String date, String format, ParsePosition pos) {
		if (date == null) {
			return null;
		}
		Date d = null;
		SimpleDateFormat formater = new SimpleDateFormat(format);
		try {
			formater.setLenient(false);
			d = formater.parse(date, pos);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			d = null;
		}
		return d;
	}

	public static Date toDate(String date) {
		if ((date == null) || (date.trim().equals(""))) {
			return null;
		}
		Date d = null;
		try {
			if (date.contains(" CST")) {
				try {
					d = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(date);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			} else if (date.contains(" GMT+08:00")) {
				date = date.replace(" GMT+08:00", "");
				d = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH).parse(date);
			} else if (date.length() > 10) {
				d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
			} else {
				d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			}
		} catch (Exception ex) {
			throw new RuntimeException("无法解析的日期字符串 : " + date);
		}
		return d;
	}

	public static boolean isDate(String date) {
		return isDate(date, "yyyy-MM-dd");
	}

	public static boolean isDate(String date, String format) {
		if (date == null) {
			return false;
		}

		Date d = null;
		ParsePosition pos = new ParsePosition(0);
		d = toDate(date, format, pos);
		return (d != null) && (pos.getIndex() == date.length());
	}

	public static String toString(Date date, String format) {
		if (date == null) {
			return "";
		}
		String result = null;
		SimpleDateFormat formater = new SimpleDateFormat(format);
		try {
			result = formater.format(date);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result == null ? "" : result;
	}

	public static Date currentDate() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(11, 0);
		calendar.set(12, 0);
		calendar.set(13, 0);
		calendar.set(14, 0);
		return calendar.getTime();
	}

	public static String formatDate(Date date) {
		if (date == null)
			return null;
		String d = null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		try {
			if ((c.get(11) > 0) || (c.get(12) > 0) || (c.get(13) > 0))
				d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			else
				d = new SimpleDateFormat("yyyy-MM-dd").format(date);
		} catch (Exception ex) {
			throw new RuntimeException("无法格式化的日期 : " + date);
		}
		return d;
	}

	public static String formatDate(Date date, String format) {
		if (date == null)
			return null;
		String d = null;
		try {
			d = new SimpleDateFormat(format).format(date);
		} catch (Exception ex) {
			throw new RuntimeException("无法格式化的日期 : " + date);
		}
		return d;
	}

	public static Date getEarliestTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(11, 0);
		calendar.set(12, 0);
		calendar.set(13, 0);
		return calendar.getTime();
	}

	public static Date getLatestTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(11, 23);
		calendar.set(12, 59);
		calendar.set(13, 59);
		return calendar.getTime();
	}

	public static Date currentTime() {
		return new Date();
	}

	public static int getDaysOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1);
		return calendar.getActualMaximum(5);
	}

	public static Date getFirstDateOfWeek(Date weekDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(weekDate);
		calendar.add(7, calendar.getFirstDayOfWeek() - calendar.get(7));
		Date date = calendar.getTime();
		zeroTimeOfDate(date);
		return date;
	}

	public static Date getFirstDateOfNextWeek(Date weekDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(weekDate);
		calendar.add(6, 8 - calendar.get(7));
		Date date = calendar.getTime();
		zeroTimeOfDate(date);
		return date;
	}

	public static Date getFirstDateOfMonth(Date monthDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(monthDate);
		calendar.set(5, 1);
		Date date = calendar.getTime();
		zeroTimeOfDate(date);
		return date;
	}

	public static Date getFirstDateOfNextMonth(Date monthDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(monthDate);
		calendar.add(2, 1);
		Date date = calendar.getTime();
		zeroTimeOfDate(date);
		return date;
	}

	public static Date getDateOfPrevDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate());
		calendar.add(5, -1);
		Date date = calendar.getTime();
		return date;
	}

	public static Date getDateOfNexts(Date d, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(5, day);
		Date date = calendar.getTime();
		return date;
	}

	public static Date getDateOfNexts(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate());
		calendar.add(5, day);
		Date date = calendar.getTime();
		return date;
	}

	private static void zeroTimeOfDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(11, 0);
		calendar.set(12, 0);
		calendar.set(13, 0);
	}

	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(1);
	}

	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(2) + 1;
	}

	public static int getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(5);
	}

	public static String getTodayDateString() {
		return toString(new Date(), "yyyy-MM-dd");
	}

	public static String getNowTimeString() {
		return toString(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	public static String getNowHourAndMiTime() {
		return toString(new Date(), "HH:mm");
	}

	public static Date getDateAfterAddMonth(Date date, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(2, month);
		Date date_new = calendar.getTime();
		return date_new;
	}

	public static int getYearAfterAddMonth(int year, int month, int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1);
		calendar.add(2, n);
		return calendar.get(1);
	}

	public static int getMonthAfterAddMonth(int year, int month, int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1);
		calendar.add(2, n);
		return calendar.get(2) + 1;
	}

	public static void main(String[] args) {
		System.out.println(getYearAfterAddMonth(2007, 8, 4) + ":" + getMonthAfterAddMonth(2007, 8, 4));
		System.out.println(getYearAfterAddMonth(2007, 8, 5) + ":" + getMonthAfterAddMonth(2007, 8, 5));
		System.out.println(getYearAfterAddMonth(2007, 10, 1));
	}
}
