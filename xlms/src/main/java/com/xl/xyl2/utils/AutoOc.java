package com.xl.xyl2.utils;

import android.content.Context;
import android.content.Intent;

import com.gdswlw.library.toolkit.DateUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.play.AutoOcSetting;
import com.xl.xyl2.play.Setting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Afun on 2019/10/15.
 */

public class AutoOc {
    public static Context context;

    public static void init(Context context) {
        AutoOc.context = context;
    }

    /**
     * 同步开关机设置
     *
     * @param setting
     */
    public static void syncOcSettings(Setting setting) {
        if(setting ==null){
            return;
        }
        if(!setting.getAutoOc()){
            enableAutoOc(null, null, false);
            return;
        }
        AutoOcSetting autoOcSetting = setting.getAutoOcSetting();
        enableAutoOc(null, null, false);
        if (autoOcSetting != null) {
            Date now =  XLContext.getLocalServerDate();
            //不在有效期限内
            if ((autoOcSetting.getSdate() != null && now.getTime() < autoOcSetting.getSdate().getTime())
                    || (autoOcSetting.getEdate() != null && now.getTime() > autoOcSetting.getEdate().getTime())) {
                enableAutoOc(null, null, false);
                return;
            }
            //检查时间设置的合法性
            if (!checkAutoOcSetting(autoOcSetting)) {
                enableAutoOc(null, null, false);
                return;
            }

            //获取当前的时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            XLContext.showLogMessage("当前时间："+DateUtil.getDateStrYmdHmsByDate(now));
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 2; //0-6

            String dateStr = null;//获取当前的
            int incre = -1;//增量
            do {
                incre++;
                week++;
                if (week > 6) {
                    week = 0;
                }
                dateStr = getNextWeekTime(autoOcSetting, week);
                if(dateStr == null || dateStr.trim().length() == 0){
                    continue;
                }
                String nhms = new SimpleDateFormat("HH:mm").format(now);
                String startTimeAndEndTime = null;
                //获取下一的开机时间
                if (dateStr != null) {
                    //分割时间段
                    String[] times = dateStr.split(",");
                    if (incre == 0) {//当前
                        for (int i = 0; i < times.length; i++) {
                            String endTime = times[i].split("-")[1];
                            if (endTime.compareTo(nhms) > 0) {//当天的结束时间大于当前的时间
                                startTimeAndEndTime = times[i];
                                break;
                            }
                        }
                    } else if (incre > 0) {
                        startTimeAndEndTime = times[0];//默认取第一个时间
                    }

                    if (startTimeAndEndTime == null) {
                        dateStr = null;
                    } else {
                        String startTime = startTimeAndEndTime.split("-")[0];//获取开始时间
                        String endTime = startTimeAndEndTime.split("-")[1];//获取开始时间
                        //设置开关机时间
                        Calendar calendarOn = Calendar.getInstance();
                        calendarOn.add(Calendar.DATE, incre);//加上增量
                        String[] ms = startTime.split(":");//获取时分
                        calendarOn.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ms[0]));
                        calendarOn.set(Calendar.MINUTE, Integer.parseInt(ms[1]));

                        Calendar calendarOff = Calendar.getInstance();
                        calendarOff.add(Calendar.DATE, incre);
                        ms = endTime.split(":");
                        calendarOff.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ms[0]));
                        calendarOff.set(Calendar.MINUTE, Integer.parseInt(ms[1]));
                        enableAutoOc(calendarOn, calendarOff, true);
                        return;
                    }
                }
            } while (dateStr == null);

        }
    }

    /**
     * 检查设置的合法性
     *
     * @param autoOcSetting
     * @return
     */
    private static boolean checkAutoOcSetting(AutoOcSetting autoOcSetting) {
        if (autoOcSetting.getWeek0() && autoOcSetting.getTimeInterval0() != null) {
            return true;
        }
        if (autoOcSetting.getWeek1() && autoOcSetting.getTimeInterval1() != null) {
            return true;
        }
        if (autoOcSetting.getWeek2() && autoOcSetting.getTimeInterval2() != null) {
            return true;
        }
        if (autoOcSetting.getWeek3() && autoOcSetting.getTimeInterval3() != null) {
            return true;
        }
        if (autoOcSetting.getWeek4() && autoOcSetting.getTimeInterval4() != null) {
            return true;
        }
        if (autoOcSetting.getWeek5() && autoOcSetting.getTimeInterval5() != null) {
            return true;
        }
        if (autoOcSetting.getWeek6() && autoOcSetting.getTimeInterval6() != null) {
            return true;
        }
        return false;
    }

    /**
     * 获取下一日期的开关机时间
     *
     * @param week
     * @return
     */
    private static String getNextWeekTime(AutoOcSetting autoOcSetting, int week) {
        if (week > 6) {
            week = 0;
        }
        String dateStr = null;
        switch (week) {
            case 0:
                if (autoOcSetting.week0) {
                    dateStr = autoOcSetting.getTimeInterval0();
                }
                break;
            case 1:
                if (autoOcSetting.week1) {
                    dateStr = autoOcSetting.getTimeInterval1();
                }
                break;
            case 2:
                if (autoOcSetting.week2) {
                    dateStr = autoOcSetting.getTimeInterval2();
                }
                break;
            case 3:
                if (autoOcSetting.week3) {
                    dateStr = autoOcSetting.getTimeInterval3();
                }
                break;
            case 4:
                if (autoOcSetting.week4) {
                    dateStr = autoOcSetting.getTimeInterval4();
                }
                break;
            case 5:
                if (autoOcSetting.week5) {
                    dateStr = autoOcSetting.getTimeInterval5();
                }
                break;
            case 6:
                if (autoOcSetting.week6) {
                    dateStr = autoOcSetting.getTimeInterval6();
                }
                break;

        }
        return dateStr;
    }

    /**
     * 设置开关机
     *
     * @param timeon
     * @param timeoff
     * @param enable  启用开关机设置
     */
    private static void enableAutoOc(Calendar timeon, Calendar timeoff, boolean enable) {
        if (context == null) {
            throw new IllegalStateException("context is null");
        }
        Intent intent = new Intent("android.56iq.intent.action.setpoweronoff");
        if (enable) {
            //下次开机具体日期时间
            int[] timeonArray = {timeon.get(Calendar.YEAR), timeon.get(Calendar.MONTH) + 1, timeon.get(Calendar.DATE),
                    timeon.get(Calendar.HOUR_OF_DAY), timeon.get(Calendar.MINUTE)};
            //下次关机具体日期时间
            int[] timeoffArray = {timeoff.get(Calendar.YEAR), timeoff.get(Calendar.MONTH) + 1, timeoff.get(Calendar.DATE),
                    timeoff.get(Calendar.HOUR_OF_DAY), timeoff.get(Calendar.MINUTE)};
            //会关机
            intent.putExtra("timeon", timeonArray);
            intent.putExtra("timeoff", timeoffArray);
            UIKit.dLog("timeon:" + DateUtil.getDateStrYmdHmsByDate(timeon.getTime()) + ",timeoff：" + DateUtil.getDateStrYmdHmsByDate(timeoff.getTime()));
            XLContext.showLogMessage("开关机设置 timeon:" + DateUtil.getDateStrYmdHmByDate(timeon.getTime()) + ",timeoff：" + DateUtil.getDateStrYmdHmByDate(timeoff.getTime()));
            XLContext.config.save("timeon", timeon.getTimeInMillis());//关机时间
            XLContext.config.save("timeoff", timeoff.getTimeInMillis());//关机时间
            intent.putExtra("enable", enable); //使能开关机功能，设为false,则为关闭
            context.sendBroadcast(intent);
        }
    }

}
