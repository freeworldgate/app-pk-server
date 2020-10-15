package com.union.app.util.time;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static String getTime() {
        return "20190505344534";
    }



    public static String currentTime(){
        return "201909080708899";
    }



//
//    public static String currentDate() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String currentDay = simpleDateFormat.format(new Date());
//        return currentDay;
//    }
//    public static String dateStr(Date date){
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String currentDay = simpleDateFormat.format(date);
//        return currentDay;
//    }


    public static String convertTime(long time) {
//        if(time == 0){return StringUtils.EMPTY;}
        return "今天  12:20";
    }

    public static String currentDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDay = simpleDateFormat.format(new Date());
        return currentDay;
    }

//    public static String convertTime(String createTime) {
//        return "刚刚";
//    }


//    public static String 距离上传的小时数(long createTime) {
//
//        long day = (System.currentTimeMillis() - createTime)/(3600 * 1000) ;
//                return String.valueOf(day);
//
//
//    }
}
