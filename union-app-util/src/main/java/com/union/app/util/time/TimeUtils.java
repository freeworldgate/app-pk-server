package com.union.app.util.time;

import com.union.app.domain.pk.PostTime;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {


    private static final long DAY_TIME = 24 * 3600 * 1000L;
    private static final long HOUR_TIME = 3600 * 1000L;
    private static final long MINUTE_TIME = 60 * 1000L;
    private static final long JUST_TIME = 5*1000L;
    public static String convertTime(long time) {

        long time_expire = System.currentTimeMillis() - time;
        if(time_expire > DAY_TIME)
        {
            long days = time_expire/DAY_TIME;
            if(days == 1){return "昨天";}
            else{ return days+"天前";}

        }
        else if(time_expire > HOUR_TIME)
        {
            long hours = time_expire/HOUR_TIME;
            return hours+"小时前";
        }
        else if(time_expire > MINUTE_TIME)
        {
            long hours = time_expire/MINUTE_TIME;
            return hours+"分钟前";
        }
        else if(time_expire > JUST_TIME)
        {
            long hours = time_expire/JUST_TIME;
            return hours+"秒钟前";
        }
        else if(time_expire > 0)
        {
            return "刚刚";
        }
        else
        {
            return "~";
        }

    }


    public static PostTime convertPostTime(long time) {
        PostTime postTime = new PostTime();
        String timeStr = getTime(time);
        String date = getDate(time);
        String year = getYear(time);
        postTime.setDate(date);
        postTime.setTimeStr(timeStr);
        postTime.setYear(year);
        return postTime;
    }

    private static String getTime(long time) {
        if(time == 0){return null;}
        long time_expire = System.currentTimeMillis() - time;
        if(time_expire < 24 * 3600*1000L)
        {
            if(time_expire > HOUR_TIME)
            {
                long hours = time_expire/HOUR_TIME;
                return hours+"小时前";
            }
            else if(time_expire > MINUTE_TIME)
            {
                long hours = time_expire/MINUTE_TIME;
                return hours+"分钟前";
            }
            else if(time_expire > 0)
            {
                return "刚刚";
            }
            else
            {
                return "~";
            }



        }


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        String timeStr = simpleDateFormat.format(new Date(time));
        return timeStr;
    }

    private static String getYear(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String year = simpleDateFormat.format(new Date(time));
        String currentYear = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        if(StringUtils.equalsIgnoreCase(year,currentYear)){return "";}
        else{return year+"年";}

    }

    private static String getDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
        String date = simpleDateFormat.format(new Date(time));
        String currentDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        String lastDate = simpleDateFormat.format(new Date(System.currentTimeMillis()-24*3600*1000L));
        if(StringUtils.equalsIgnoreCase(date,currentDate)){return "今天";}
        else if(StringUtils.equalsIgnoreCase(date,lastDate)){return "昨天";}
        else{return date;}
    }





    public static String 已打捞时间(long startTime) {
        if(startTime>0 && startTime < System.currentTimeMillis())
        {
            long findTimeLength = System.currentTimeMillis() - startTime;
            long day = findTimeLength/(3600*24*1000L);
            long hour = findTimeLength%(3600*24*1000L) / (3600*1000L);
            long min = findTimeLength%(3600*24*1000L)%(3600*1000L)/(60*1000L);
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
        long day = findTimeLength/(3600*24*1000L);
        long hour = findTimeLength%(3600*24*1000L) / (3600*1000L);
//        long min = findTimeLength%(3600*24*1000)%(3600*1000)/(60*1000);
        if(day == 0){return hour+"小时";}
        else{
            if(hour==0){return day+"天";}else{return day+"天"+hour+"小时";}

        }

    }
    public static String 已打捞总时间(long findTimeLength) {
        long day = findTimeLength/(3600*24*1000L);
        long hour = findTimeLength%(3600*24*1000L) / (3600*1000L);
//        long min = findTimeLength%(3600*24*1000)%(3600*1000)/(60*1000);
        if(day == 0){return hour+"小时";}
        else{return day+"天"+hour+"小时";}
    }
    public static String 已顶置时间(long topTimeLength) {

        long timeLength = topTimeLength/(1000);

        if(timeLength < 60){
            return timeLength+"秒";
        }
        else if(timeLength < 3600)
        {
            return timeLength/60+"分钟";
        }
        else
        {
            return timeLength/3600+"小时"+((timeLength%3600/60==0L)?"":timeLength%3600/60+"分钟");
        }




    }
    public static String 全局时间(long startTime) {
        if(startTime == 0){return null;}

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日hh:mm");
        return simpleDateFormat.format(new Date(startTime));

    }


    public static String 顶置剩余时间(long topPostSetTime, long topPostTimeLength)
    {
        long left = topPostTimeLength*60*1000L - (System.currentTimeMillis() - topPostSetTime);
        if(left > HOUR_TIME)
        {
            long hours = left/HOUR_TIME;
            long minute = left%HOUR_TIME/MINUTE_TIME;
            return hours+"小时"+minute+"分钟";
        }
        else
        {
            long minute = left/MINUTE_TIME;
            if(minute == 0){minute = 1;}
            return minute+"分钟";
        }


    }

    public static String 计算时间(int time) {
        if(time < 60){
            return time+"秒";
        }else if(time < 3600){return time/60+"分钟";}
        else
        {
            return time/3600+"小时";
        }


    }

    public static String 顶置周期(long topPostTimeLength) {

        if(topPostTimeLength < 60)
        {
            return topPostTimeLength+"分钟";
        }
        else
        {
            double rangeLength = topPostTimeLength/60D;
            BigDecimal bg = new BigDecimal(rangeLength);
            double d3 = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            String valueStr = String.valueOf(d3);
            if(valueStr.endsWith("0")){
                valueStr = valueStr.substring(0,valueStr.length()-2);
            }
            return valueStr +"小时";
        }


    }

    public static String 详细时间(long time) {
        if(time == 0){return null;}
        if(System.currentTimeMillis() - time < 60 * 1000){
            return "刚刚";
        }
        SimpleDateFormat year = new SimpleDateFormat("yyyy年");
        String yearStr = year.format(new Date(System.currentTimeMillis()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        return simpleDateFormat.format(new Date(time)).replace(yearStr,"");
    }
}
