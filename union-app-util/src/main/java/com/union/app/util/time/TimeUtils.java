package com.union.app.util.time;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {




    public static String convertTime(long time) {
//        if(time == 0){return StringUtils.EMPTY;}
        return "今天  12:20";
    }


    public static boolean 是否顶置已经过期(long topPostSetTime) {



        //顶置时间最少一小时

        return true;

    }

    public static String 当前日期() {

        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
