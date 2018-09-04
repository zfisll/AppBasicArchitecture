package basic.app.com.basiclib.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import basic.app.com.basiclib.utils.logger.LogUtil;

/**
 * author : user_zf
 * date : 2018/9/3
 * desc : 时间工具
 */
public class DateUtil {

    /**
     * 日期格式 yyyyMMdd
     */
    public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
    /**
     * 日期格式 yyyy-MM-dd
     */
    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    /**
     * 日期格式 yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    /**
     * 日期格式 yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期格式 HH:mm:ss
     */
    public static final String DATE_FORMAT_HH_MM = "HH:mm";
    /**
     * 日期格式 yyyyMMddHHmmss
     */
    public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    /**
     * 日期格式yyyy.MM.dd
     */
    public static final String DATE_FORMAT_YYYY_MM_DD_POINT = "yyyy.MM.dd";
    /**
     * 日期格式yyyy/MM/dd
     */
    public static final String DATE_FORMAT_YYYY_MM_DD_SLASH = "yyyy/MM/dd";
    /**
     * 日期格式MM-dd
     */
    public static final String DATE_FORMAT_MM_DD = "MM-dd";

    /**
     * 日期格式yyyy.MM.dd hh:mm
     */
    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_POINT = "yyyy.MM.dd HH:mm";

    /**
     * 日期格式MM-dd hh:mm
     */
    public static final String DATE_FORMAT_MM_DD_HH_MM = "MM-dd HH:mm";
    /**
     * 日期个是yyyy-MM
     */
    public static final String DATE_FORMAT_YYYY_MM = "yyyy-MM";
    /**
     * 美东时区 id
     */
    public static final String TIMEZONE_NEW_YORK = "America/New_York";
    /**
     * 中国时区 id
     */
    public static final String TIMEZONE_SHANG_HAI = "Asia/Shanghai";

    /**
     * 系统当前时区的日期转换为 Date 对象
     */
    public static Date str2Date(String dateStr, String pattern) {
        return str2Date(TimeZone.getDefault(), dateStr, pattern);
    }

    /**
     * 指定时区的日期转换为 Date 对象
     */
    public static Date str2Date(TimeZone timeZone, String dateStr, String pattern) {
        if (TextUtils.isEmpty(dateStr)) {
            return new Date(0);
        }
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
            dateFormat.setTimeZone(timeZone);
            return dateFormat.parse(dateStr);
        } catch (NullPointerException | IllegalArgumentException | ParseException e) {
            LogUtil.e(e, e.getMessage());
            e.printStackTrace();
            return new Date(0);
        }
    }

    /**
     * 系统当前时区的日期字符串转换时间戳
     */
    public static long str2TimeMillisInCurTimeZone(String dateStr, String pattern) {
        return str2TimeMillis(TimeZone.getDefault(), dateStr, pattern);
    }

    /**
     * 指定时区的日期字符串转换时间戳
     */
    public static long str2TimeMillis(TimeZone timeZone, String dateStr, String pattern) {
        Date date = str2Date(timeZone, dateStr, pattern);
        return date.getTime();
    }

    /**
     * Date 对象转换为系统当前时区的日期字符串
     */
    public static String date2StrInCurTimeZone(Date date, String pattern) {
        return date2Str(TimeZone.getDefault(), date, pattern);
    }

    /**
     * Date 转换为指定时区日期字符串
     */
    public static String date2Str(TimeZone timeZone, Date date, String pattern) {
        if (date == null) {
            return "";
        }
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
            dateFormat.setTimeZone(timeZone);
            return dateFormat.format(date);
        } catch (NullPointerException | IllegalArgumentException ex) {
            LogUtil.e(ex, ex.getMessage());
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 时间戳转换为系统当前时区的日期字符串
     */
    public static String timeMillis2StrInCurTimeZone(long milliSeconds, String pattern) {
        return timeMillis2Str(TimeZone.getDefault(), milliSeconds, pattern);
    }

    /**
     * 时间戳转换为指定时区日期字符串
     */
    public static String timeMillis2Str(TimeZone timeZone, long milliSeconds, String pattern) {
        if (milliSeconds == 0) {
            return "";
        }
        return date2Str(timeZone, new Date(milliSeconds), pattern);
    }

    /**
     * 获取两个时间戳间的月差
     * milliSeconds1 > milliSeconds，获取1大于2的月差
     */
    public static int getMonthDiff(long milliSeconds1, long milliSeconds2) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_YYYY_MM);

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        try {
            calendar1.setTime(format.parse(timeMillis2StrInCurTimeZone(milliSeconds1, DATE_FORMAT_YYYY_MM)));
            calendar2.setTime(format.parse(timeMillis2StrInCurTimeZone(milliSeconds2, DATE_FORMAT_YYYY_MM)));
        } catch (ParseException e) {
            LogUtil.e(e.getMessage());
        }
        int n = 0;
        while (calendar2.before(calendar1)) {
            calendar2.add(Calendar.MONTH, 1);
            n++;
        }
        return n;
    }

    /**
     * 获取当前日期几个月之前的日期
     *
     * @param monthAgo 几个月以前
     */
    public static Date getMonthAgoDate(int monthAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, monthAgo * -1);
        calendar.add(Calendar.DAY_OF_YEAR, +1);
        return calendar.getTime();
    }

    /*
     * 获取当前时间
     */
    public static String getCurrentDateInCurTimeZone(String pattern) {
        return getCurrentDate(TimeZone.getDefault(), pattern);
    }

    /*
     * 获取当前时间
     */
    public static String getCurrentDate(TimeZone timeZone, String pattern) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
            dateFormat.setTimeZone(timeZone);
            return dateFormat.format(new Date());
        } catch (NullPointerException | IllegalArgumentException ex) {
            LogUtil.e(ex, ex.getMessage());
            ex.printStackTrace();
            return "";
        }
    }

    public static Date getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 判断两个日期是否在同一天
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @return 相差天数
     */
    public static int daysBetween(Date date1, Date date2) {
        Long beginTime = str2TimeMillis(getChinaTimeZone(), timeMillis2Str(getChinaTimeZone(),
                date1.getTime(), DATE_FORMAT_YYYY_MM_DD), DATE_FORMAT_YYYY_MM_DD);
        Long endTiem = str2TimeMillis(getChinaTimeZone(), timeMillis2Str(getChinaTimeZone(),
                date2.getTime(), DATE_FORMAT_YYYY_MM_DD), DATE_FORMAT_YYYY_MM_DD);
        return (int) (Math.abs(beginTime - endTiem) / (1000 * 3600 * 24));
    }

    /**
     * 判断输入时间是否大于今天
     */
    public static boolean isAfterToday(Date date) {

        //时间均设为当天0点后,比较大小
        Calendar current = Calendar.getInstance();
        current.setTime(date);
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        Calendar today = Calendar.getInstance();    //今天
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return current.compareTo(today) >= 0;
    }

    /**
     * 获取时间戳当天指定时间的时间戳
     */
    public static long getCurDayTimeInMills(TimeZone timeZone, long timeMillis, int hour, int minute) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(timeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTimeInMillis();
    }

    /**
     * 判断是港股时间还是美股时间，用在自选股智能排序上
     * 港股时段 08:00-20:00
     * 美股时段 00:00-08:00 20:00-24:00
     *
     * @return true=港股时间 false=美股时间
     */
    public static boolean isHKTime() {
        long nowTimeMillis = new Date().getTime();
        long timeMillis0800 = getCurDayTimeInMills(getChinaTimeZone(), nowTimeMillis, 8, 0);
        long timeMillis2000 = getCurDayTimeInMills(getChinaTimeZone(), nowTimeMillis, 20, 0);
        return nowTimeMillis >= timeMillis0800 && nowTimeMillis < timeMillis2000;
    }

    /**
     * 获取美国东部时区
     */
    public static TimeZone getEastUsTimeZone() {
        return TimeZone.getTimeZone(TIMEZONE_NEW_YORK);
    }

    /**
     * 获取中国时区
     */
    public static TimeZone getChinaTimeZone() {
        return TimeZone.getTimeZone(TIMEZONE_SHANG_HAI);
    }
}
