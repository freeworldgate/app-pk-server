package com.union.app.util.idGenerator;

import java.util.Random;
import java.util.UUID;

public class IdGenerator {

    public static String getInviteTimeId() {
        return UUID.randomUUID().toString();
    }

    public static String getInvitePayId() {
        return UUID.randomUUID().toString();
    }

    public static String getInviteId() {return UUID.randomUUID().toString(); }

    public static String getLocationId() {return UUID.randomUUID().toString(); }

    public static String getOrgId() {return UUID.randomUUID().toString(); }

    public static String 生成订单ID() {
        return UUID.randomUUID().toString();
    }

    public static String getPostId() {return UUID.randomUUID().toString(); }

    public static String getImageId() {return UUID.randomUUID().toString(); }

    public static String getOrderId() {return UUID.randomUUID().toString(); }

    public static String 生成收款码ID() {return UUID.randomUUID().toString(); }

    public static String 收款用户ID() {return UUID.randomUUID().toString(); }

    public static String 收款群组ID() {return UUID.randomUUID().toString(); }

    public static String 收款码ID()  {return UUID.randomUUID().toString(); }

    public static String 生成用户ID() {return UUID.randomUUID().toString(); }

    public static String getActiveTipId() {return UUID.randomUUID().toString(); }



    public static String getActiveCode()
    {
        String val = "E-";
        Random random = new Random();
        for(int i = 0; i < 12; i++)
        {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if("char".equalsIgnoreCase(charOrNum))
            {
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            }
            else if("num".equalsIgnoreCase(charOrNum))
            {
                val += String.valueOf(random.nextInt(10));
            }
        }

        return val.toUpperCase();
    }



}

