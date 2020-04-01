package com.union.app.plateform.meet;

import org.apache.commons.lang.math.RandomUtils;

public class MeetTimeCalculator {
    public static String getMeetTime(String[][] time, int[] timeIndex) {
        return "2019-05-09 12:00:00";
    }

    public static String[][] getMeetTimePicker(String meetTime) {
        return new String[][]{{"今天","明天","后天","2019-05-08","2019-05-09","2019-05-10","2019-05-11"},{"00","01","02","03","04","05","06"},{"00","01","02","03","04","05","06"}};


    }

    public static int[] getMeetTimePickerIndex(String meetTime) {
        return new int[]{1,1,1};
    }

    public static String getMeetTimeDesc(String meetTime) {
        return "刚刚";
    }

    public static boolean isExpired(String meetTime) {
        //时间是否过期
        return false;
    }
}
