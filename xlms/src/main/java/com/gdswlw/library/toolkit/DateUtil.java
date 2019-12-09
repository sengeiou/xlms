package com.gdswlw.library.toolkit;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    private static Calendar calendar;
    private static final Locale locale = Locale.getDefault();
    private static final String[] weekends = new String[]{"星期日", "星期一",
            "星期二", "星期三", "星期四", "星期五", "星期六",};

    public static Calendar getCalendarInstance() {
        if (calendar == null) {
            calendar = Calendar.getInstance(locale);
        }
        return calendar;
    }

    /**
     * 返回日期字符串
     *
     * @param date   日期
     * @param format 要显示的日期格式
     * @return 解析后的字符串格式
     */
    public static String getStringWithDate(Date date, String format) {
        if (date == null) {
            date = getCurrentDate();
        }
        if (StrUtil.nullToStr(format).equals("")) {
            format = "yyyy-MM-dd";
        }
        return new SimpleDateFormat(format, locale).format(date);
    }

    public static String getWeekendName() {
        return weekends[getWeekend()];
    }

    /**
     * 返回星期几
     *
     * @return
     */
    public static int getWeekend() {
        return getCalendarInstance().get(Calendar.DAY_OF_WEEK);
    }

    public static String getStringWithDate(Date date) {
        if (date == null) {
            date = getCurrentDate();
        }
        return new SimpleDateFormat("yyyy-MM-dd", locale).format(date);
    }

    public static String getStringWithDateAndTime(Date date) {
        if (date == null) {
            date = getCurrentDate();
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale).format(date);
    }

    /**
     * 将日期转化成毫秒
     * 时间格式: yyyy-MM-dd
     *
     * @param time
     * @return
     */
    public static Long date4Long(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Long second = format.parse(time).getTime();
            return second;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1l;
    }

    /**
     * 将时间转化成毫秒
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static Long time4Long(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long second = format.parse(time).getTime();
            return second;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1l;
    }

    /**
     * 通过字符串解析还原日期
     *
     * @param dateStr
     * @return
     */
    public static Date parseDateWithString(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", locale).parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getCurrentDate();
    }

    /**
     * 通过字符串解析还原日期
     *
     * @param dateStr 日期字符串
     * @param format  格式
     * @return
     */
    public static Date parseDateWithString(String dateStr, String format) {
        format = StrUtil.nullToStr(format).equals("") ? "yyyy-MM-dd" : format;
        try {
            return new SimpleDateFormat(format, locale).parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getCurrentDate();
    }

    /**
     * 计算两个日期之间的天数
     *
     * @param early
     * @param late
     * @return
     */
    public static final int daysBetween(String early, String late) {
        Date earlydate = new Date();
        Date latedate = new Date();
        DateFormat df = DateFormat.getDateInstance();
        try {
            earlydate = df.parse(early);
            latedate = df.parse(late);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        Calendar calst = Calendar.getInstance();
        Calendar caled = Calendar.getInstance();
        calst.setTime(earlydate);
        caled.setTime(latedate);
        // 设置时间为0时
        calst.set(Calendar.HOUR_OF_DAY, 0);
        calst.set(Calendar.MINUTE, 0);
        calst.set(Calendar.SECOND, 0);
        caled.set(Calendar.HOUR_OF_DAY, 0);
        caled.set(Calendar.MINUTE, 0);
        caled.set(Calendar.SECOND, 0);
        // 得到两个日期相差的天数
        int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst
                .getTime().getTime() / 1000)) / 3600 / 24;

        return days;
    }

    /**
     * 获取日历对象
     *
     * @return
     */
    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取当前字符串格式
     *
     * @param format
     * @return
     */
    public static String getCurrentDateString(String format) {
        return getStringWithDate(getCurrentDate(), format);
    }

    /**
     * 获取当前字符串格式
     *
     * @return
     */
    public static String getCurrentDateString() {
        return getStringWithDate(getCurrentDate());
    }

    /***
     * 获取日期时间字符串
     *
     * @param date
     * @return
     */
    public static String getDateStrYmdHmsByDate(Date date) {
        date = (date == null ? getCurrentDate() : date);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale).format(date);
    }

    public static String getDateStrYmdHmByDate(Date date) {
        date = (date == null ? getCurrentDate() : date);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", locale).format(date);
    }

    /**
     * 获取日期时间字符串
     *
     * @return
     */
    public static String getDateStrYmdHms() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale).format(getCurrentDate());
    }

    public static String getDateStrHm() {
        return new SimpleDateFormat("HH:mm", locale).format(getCurrentDate());
    }

    public static String getDateStringYmdHms() {
        return new SimpleDateFormat("yyyyMMddHHmmss", locale).format(getCurrentDate());
    }

    /**
     * 获取日期时间字符串
     *
     * @return
     */
    public static String getDateStrYmdHm() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", locale)
                .format(getCurrentDate());
    }

    public static String getDateStrYmd() {
        return new SimpleDateFormat("yyyy-MM-dd", locale)
                .format(getCurrentDate());
    }
    public static String getDateStrYmd(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", locale)
                .format(date);
    }

    /**
     * 将毫秒转换为日期格式
     *
     * @param mill 毫秒
     * @return
     */
    public static String long4Date(long mill) {
        if (mill <= 0) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", locale)
                .format(new Date(mill));
    }

    /**
     * @param lo 毫秒数
     * @return String yyyy-MM-dd HH:mm:ss
     * @Description: long类型转换成日期
     */
    public static String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }

    /**
     * 将"2015-08-31 21:08:06"型字符串转化为Date
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date StringToDate(String str) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
    }

    /**
     * 将CST时间类型字符串进行格式化输出
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static String CSTFormat(String str) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        return new SimpleDateFormat("yyyy-MM-dd", locale)
                .format(formatter.parse(str));
    }


    /**
     * 获取明天
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getStrTomorrow() {
        Date date = new Date(getCurrentDate().getTime() + (24 * 60 * 60 * 1000));
        return getStringWithDate(
                new Date(date.getYear(), date.getMonth(), date.getDate()),
                "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取昨天
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getStrYesterday() {
        Date date = new Date(getCurrentDate().getTime() - (24 * 60 * 60 * 1000));
        return getStringWithDate(
                new Date(date.getYear(), date.getMonth(), date.getDate()),
                "yyyy-MM-dd HH:mm:ss");
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateByString(String time) {
        Date date = null;
        if (time == null)
            return date;
        String date_format = "yyyy-MM-dd HH:mm";
        SimpleDateFormat format = new SimpleDateFormat(date_format);
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getShortTime(String time) {
        return getShortTime_message(time);
    }

    public static String getShortTime_message(String time) {
        String shortstring = null;
        long now = Calendar.getInstance().getTimeInMillis();
        Date date = getDateByString(time);
        if (date == null)
            return shortstring;
        long deltime = (now - date.getTime()) / 1000;
        if (deltime > 30 * 24 * 60 * 60) {
            shortstring = time;
        } else if (deltime > 7 * 24 * 60 * 60 && deltime < 30 * 24 * 60 * 60) {
            shortstring = (int) (deltime / (24 * 60 * 60)) + "天前";
        } else if (deltime > 1 * 24 * 60 * 60 && deltime < 7 * 24 * 60 * 60) {
            shortstring = "周" + getWeekDay(date) + " " + dateToStrLong(date);
        } else if (deltime > 60 * 60 && deltime < 1 * 24 * 60 * 60) {
            shortstring = (int) (deltime / (60 * 60)) + "小时前" + " "
                    + dateToStrLong(date);
        } else if (deltime > 60) {
            shortstring = (int) (deltime / (60)) + "分前";
        } else if (deltime > 1) {
            shortstring = "刚刚";
        } else {
            shortstring = "刚刚";
        }
        return shortstring;
    }

    public static int getWeekDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int week_of_year = c.get(Calendar.DAY_OF_WEEK);
        return week_of_year - 1;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateToStrLong(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    @SuppressLint("SimpleDateFormat")
    public static String twoDateDistance(Date startDate, Date endDate) {

        if (startDate == null || endDate == null) {
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 60 * 1000)
            return timeLong / 1000 + "秒前";
        else if (timeLong < 60 * 60 * 1000) {
            timeLong = timeLong / 1000 / 60;
            return timeLong + "分钟前";
        } else if (timeLong < 60 * 60 * 24 * 1000) {
            timeLong = timeLong / 60 / 60 / 1000;
            return timeLong + "小时前";
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            return timeLong + "天前";
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4) {
            timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
            return timeLong + "周前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            return sdf.format(startDate);
        }
    }


    public static String distanceTodayOrYesterDay(Date d1, Date d2) {
        Calendar c1 = getCalendar();
        Calendar c2 = getCalendar();
        c1.setTime(d1);
        c2.setTime(d2);
        int startYear = c1.get(Calendar.YEAR);
        int startMonth = c1.get(Calendar.MONTH) + 1;
        int startDate = c1.get(Calendar.DATE);
        int endYear = c2.get(Calendar.YEAR);
        int endMonth = c2.get(Calendar.MONTH) + 1;
        int endDate = c2.get(Calendar.DATE);
        if (startYear < endYear) {
            return "昨天";
        } else {
            if (startMonth < endMonth) {
                return "昨天";
            } else {
                if (startDate < endDate) {
                    return "昨天";
                }
            }
        }
        return "今天";
    }

    @SuppressLint("SimpleDateFormat")
    public static String twoDateDistance2(Date startDate, Date endDate) {

        if (startDate == null || endDate == null) {
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 60 * 1000) {
            return StrUtil.appendString(distanceTodayOrYesterDay(startDate, endDate), getPeriodOfTime(startDate), " ", "刚刚");
        } else if (timeLong < 60 * 60 * 1000) {
            timeLong = timeLong / 1000 / 60;
            return StrUtil.appendString(distanceTodayOrYesterDay(startDate, endDate), getPeriodOfTime(startDate), " ", getHourMinuteWithDate(startDate));
        } else if (timeLong < 60 * 60 * 24 * 1000) {
            timeLong = timeLong / 60 / 60 / 1000;
            return StrUtil.appendString(distanceTodayOrYesterDay(startDate, endDate), getPeriodOfTime(startDate), " ", getHourMinuteWithDate(startDate));
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            switch ((int) timeLong) {
                case 1:
                    return StrUtil.appendString("昨天", getPeriodOfTime(startDate), " ", getHourMinuteWithDate(startDate));
                case 2:
                    return StrUtil.appendString("前天", getPeriodOfTime(startDate), " ", getHourMinuteWithDate(startDate));
                default:
                    return timeLong + "天前";
            }
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4) {
            timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
            return timeLong + "周前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            return sdf.format(startDate);
        }
    }

    public static String getPeriodOfTime(Date date) {
        if (date != null) {
            Calendar c = getCalendar();
            c.setTime(date);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour < 6) {
                return "凌晨";
            } else if (hour >= 6 && hour < 8) {
                return "早上";
            } else if (hour >= 8 && hour < 11) {
                return "上午";
            } else if (hour >= 11 && hour < 13) {
                return "中午";
            } else if (hour >= 13 && hour < 18) {
                return "下午";
            } else {
                return "晚上";
            }
        }
        return "";
    }

    public static String getHourMinuteWithDate(Date date) {
        if (date != null) {
            Calendar c = getCalendar();
            c.setTime(date);
            return StrUtil.appendString(
                    c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + c.get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR_OF_DAY),
                    ":", c.get(Calendar.MINUTE) < 10 ? "0" + c.get(Calendar.MINUTE) : c.get(Calendar.MINUTE));
        }
        return "";
    }

    /**
     * 算出两个点的差数 差数由minutes决定
     *
     * @param startDate
     * @param endDate
     * @param minute
     * @return
     */
    public static boolean isTwoDateDistanceForTime(Date startDate, Date endDate, int minute) {

        if (startDate == null || endDate == null) {
            return false;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong >= (minute * 60 * 1000)) {
            return true;
        }
        return false;
    }


    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致，如00:00:00
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断日期是否在指定的日期范围内
     *
     * @param dateBengin 开始日期
     * @param dateEnd    结束日期
     * @param date       指定日期
     * @return
     */
    public static boolean isEffectiveDate(String dateBengin, String dateEnd, String date) {
        //判断某个日期是否在两个日期范围之内
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date1 = simpleDateFormat.parse(dateBengin);
            Date date2 = simpleDateFormat.parse(dateEnd);
            Date date3 = simpleDateFormat.parse(date);
            return date1.getTime() <= date3.getTime() && date2.getTime() >= date3.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 判断日期date1是否小于date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isBeforeDate(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date bt = sdf.parse(date1);
            Date et = sdf.parse(date2);
            return bt.before(et);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 判断某一时间是否在一个区间内
     *
     * @param sourceTime
     *            时间区间,半闭合,如[10:00-20:00)
     * @param curTime
     *            需要判断的时间 如10:00
     * @return
     * @throws IllegalArgumentException
     */
    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
        if (curTime == null || !curTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
            if (end < start) {
                if (now >= end && now < start) {
                    return false;
                } else {
                    return true;
                }
            }
            else {
                if (now >= start && now < end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }

    }
    /**
     * 判断某一时间是否在一个区间内
     *
     *  @param timeMillis 时间毫秒数
     *  @param sourceTime 时间区间,半闭合,如[10:00-20:00)
     */
    public static boolean isInTime(long timeMillis,String sourceTime) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String currentTime =  df.format(timeMillis);
        return isInTime(sourceTime,currentTime);
    }

    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        //设置当前时间
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);//设置开始时间
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        //设置结束时间
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        //处于开始时间之后，和结束时间之前的判断
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
}
