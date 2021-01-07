package com.union.app.util.time;

import com.mchange.lang.LongUtils;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {




    public static String convertTime(long time) {
//        if(time == 0){return StringUtils.EMPTY;}
        return "今天  12:20";
    }


    public static boolean 是否顶置已经过期(long topPostSetTime,long hour) {

        long time = System.currentTimeMillis() - topPostSetTime;
        long minTime = hour*3600*1000;
        //顶置时间最少一小时
        return minTime < time;

    }

    public static String 当前日期() {

        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String 已打捞时间(long startTime) {
        if(startTime>0 && startTime < System.currentTimeMillis())
        {
            long findTimeLength = System.currentTimeMillis() - startTime;
            long day = findTimeLength/(3600*24*1000);
            long hour = findTimeLength%(3600*24*1000) / (3600*1000);
            long min = findTimeLength%(3600*24*1000)%(3600*1000)/(60*1000);
            if(day == 0 && hour == 0)
            {
                return (min+1) + "分钟";
            }
            else if(day == 0){return hour+"小时" + (min+1) + "分钟";}
            else{return day+"天"+hour+"小时";}
        }
        return "";


    }

    public static String 剩余可打捞时间(long findTimeLength) {
        long day = findTimeLength/(3600*24*1000);
        long hour = findTimeLength%(3600*24*1000) / (3600*1000);
//        long min = findTimeLength%(3600*24*1000)%(3600*1000)/(60*1000);
        if(day == 0){return hour+"小时";}
        else{return day+"天"+hour+"小时";}

    }

    public static String 全局时间(long startTime) {
        if(startTime == 0){
            return null;
        }
        else
        {
            return "9月3日23:00";
        }
    }
}
