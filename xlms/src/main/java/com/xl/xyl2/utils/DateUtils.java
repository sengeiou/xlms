package com.xl.xyl2.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat TIME_MM_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat _TIME_FORMAT = new SimpleDateFormat("HH:mm");

    /**
     * 判断一个时间是否在另一个时间之前
     *
     * @param time1 第一个时间
     * @param time2 第二个时间
     * @return 判断结果
     */
    public static boolean before(String time1, String time2) {
        try {
            Date dateTime1 = TIME_FORMAT.parse(time1);
            Date dateTime2 = TIME_FORMAT.parse(time2);
            if (dateTime1.before(dateTime2)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断一个时间是否在另一个时间之后
     *
     * @param time1 第一个时间
     * @param time2 第二个时间
     * @return 判断结果
     */
    public static boolean after(String time1, String time2) {
        try {
            Date dateTime1 = TIME_FORMAT.parse(time1);
            Date dateTime2 = TIME_FORMAT.parse(time2);
            if (dateTime1.after(dateTime2)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 计算时间差值（单位为秒）
     *
     * @param time1 时间1
     * @param time2 时间2
     * @return 差值
     */
    public static int minus(String time1, String time2) {
        try {
            Date datetime1 = TIME_FORMAT.parse(time1);
            Date datetime2 = TIME_FORMAT.parse(time2);
            long millisecond = datetime1.getTime() - datetime2.getTime();
            return Integer.valueOf(String.valueOf(millisecond / 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取年月日和小时
     *
     * @param datetime 时间（yyyy-MM-dd HH:mm:ss）
     * @return 结果
     */
    public static String getDateHour(String datetime) {
        String date = datetime.split(" ")[0];
        String hourMinuteSecond = datetime.split(" ")[1];
        String hour = hourMinuteSecond.split(":")[0];
        return date + "_" + hour;
    }

    /**
     * 获取当天日期（yyyy-MM-dd）
     *
     * @return 当天日期
     */
    public static String getTodayDate() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * 获取昨天的日期（yyyy-MM-dd）
     *
     * @return 昨天的日期
     */
    public static String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date date = cal.getTime();
        return DATE_FORMAT.format(date);
    }

    /**
     * 格式化日期（yyyy-MM-dd）
     *
     * @param date Date对象
     * @return 格式化后的日期
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatDateMM(Date date) {
        return TIME_MM_FORMAT.format(date);
    }

    /**
     * 格式化时间（yyyy-MM-dd HH:mm:ss）
     *
     * @param date Date对象
     * @return 格式化后的时间
     */
    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    /**
     * long to date
     *
     * @param l
     * @return
     */
    public static Date long2date(long l) {
        return new Date(l);
    }

    /**
     * long to date string
     *
     * @param l
     * @return
     */
    public static String long2timeStr(long l) {
        return formatTime(new Date(l));
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(String startTime, String endTime) {
        try {
            if (startTime == null) return false;
            if (endTime == null) return false;
            Date nowDate = _TIME_FORMAT.parse(_TIME_FORMAT.format(new Date()));
            Date startDate = _TIME_FORMAT.parse(startTime);
            Date endDate = _TIME_FORMAT.parse(endTime);
            //
            Log.e(TAG, "nowDate:" + formatDate(nowDate) + "," + nowDate.getHours());
            Log.e(TAG, "startDate:" + formatDate(startDate) + "," + startDate.getHours());
            Log.e(TAG, "endDate:" + formatDate(endDate) + "," + endDate.getHours());
            //
            Calendar date = Calendar.getInstance();
            date.setTime(nowDate);
            Calendar begin = Calendar.getInstance();
            begin.setTime(startDate);
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);
            if (date.after(begin) && date.before(end)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean intervalTime(String startTime, String endTime) {
        try {
            Date nowDate = new Date();
            Date startDate = _TIME_FORMAT.parse(startTime);
            Date endDate = _TIME_FORMAT.parse(endTime);
            //
            if (nowDate.getHours() < startDate.getHours() && nowDate.getHours() > endDate.getHours()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static Date dateStrToDate(String time) {
        try {
            return DATE_FORMAT.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date timeStrToDate(String time) {
        try {
            return _TIME_FORMAT.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String long2DateStr(long inTiming) {
        return formatDate(new Date(inTiming));
    }


    //根据日期取得星期几
    public static String getWeek(Date date) {
        String[] weeks = {"w7", "w1", "w2", "w3", "w4", "w5", "w6"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    //获取当前时间
    public static int getCalendarTime() {
        Calendar now = Calendar.getInstance();
        if (now != null) {
            try {
                String time = "";
                int minute = now.get(Calendar.MINUTE);
                if (minute > 9) {
                    time = now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE);
                } else {
                    time = now.get(Calendar.HOUR_OF_DAY) + "0" + now.get(Calendar.MINUTE);
                }
                return Integer.parseInt(time);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }
}
