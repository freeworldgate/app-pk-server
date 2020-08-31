package com.union.app.util.time;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static String getTime() {
        return "20190505344534";
    }

    public static String getCreateTime(String createTime) {
        return "刚刚";
    }




    public static long translateTime(int dateKey, int hour, int minute) {
        //dataKey是 System.currentTimeMillis()之精确到天的int型数据。代表的是日期


        return System.currentTimeMillis();
    }

    public static boolean isInviteTimeOut(long time) {

        return true;
    }

    public static String currentTime(){
        return "201909080708899";
    }


    public static String translateTime(String 创建时间) {
        return "三天前";
    }

    public static String leftTime(String 联谊时间) {
        return null;
    }

    public static String getOrderCreateTime(String createTime) {
        return "2019.04.23 13:34";
    }



    public static String currentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDay = simpleDateFormat.format(new Date());
        return currentDay;
    }
    public static String dateStr(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDay = simpleDateFormat.format(date);
        return currentDay;
    }
    public static Date 前一天(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long preDay = date.getTime() - 24 * 60 * 60 *1000;
        return new Date(preDay);
    }


    public static String convertTime(long time) {
//        if(time == 0){return StringUtils.EMPTY;}
        return "今天  12:20";
    }

    public static String currentDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDay = simpleDateFormat.format(new Date());
        return currentDay;
    }

    public static String convertTime(String createTime) {
        return "刚刚";
    }

    public static boolean 图片是否在微信中过期(long time) {

        return (System.currentTimeMillis() - time) > 2 * 24 * 3600 * 1000; //两天过期
    }

    public static String 距离上传的小时数(long createTime) {

        long day = (System.currentTimeMillis() - createTime)/(3600 * 1000) ;
                return String.valueOf(day);


    }
}
