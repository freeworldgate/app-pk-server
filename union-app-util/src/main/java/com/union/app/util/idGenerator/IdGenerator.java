package com.union.app.util.idGenerator;

import com.union.app.util.time.TimeUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.RandomAccess;
import java.util.UUID;

public class IdGenerator {




    public static String getImageId() {return UUID.randomUUID().toString(); }

    public static String 生成用户ID() {return UUID.randomUUID().toString(); }

    public static String getActiveTipId() {return UUID.randomUUID().toString(); }




    public static String getPkId() {return RandomStringUtils.randomAlphanumeric(30); }

    //二维码场景值:保证唯一性?,。
    public static String getScene() {return System.nanoTime()+RandomStringUtils.randomAlphanumeric(10); }

    public static String getPostId() {return RandomStringUtils.randomAlphanumeric(30); }


    public static String getBackId() {return UUID.randomUUID().toString(); }

    public static String getGroupId() {return UUID.randomUUID().toString(); }

    public static int getCityCode() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmm");
        String value = simpleDateFormat.format(new Date(System.currentTimeMillis()));

        return Integer.valueOf(value.substring(2,value.length()));


    }

    public static String 生成PayId() {return UUID.randomUUID().toString();}

    public static String getComplainId() {return UUID.randomUUID().toString();}
    public static String getCommentId() {return UUID.randomUUID().toString();}
    public static String getGreateId() {return UUID.randomUUID().toString();}

    public static String getRestoreId() {return UUID.randomUUID().toString();}

}

