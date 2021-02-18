package com.union.app.util.time;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NumberUtils {


    public static String convert(long num)
    {
        if(num > 99999999)
        {
            double rangeLength = num/100000000.0D;
            BigDecimal bg = new BigDecimal(rangeLength);
            double d3 = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            String valueStr = String.valueOf(d3);
            if(valueStr.endsWith("0")){
                valueStr = valueStr.substring(0,valueStr.length()-2);
            }
            return valueStr +"亿";
        }
        else if(num > 9999)
        {
            double rangeLength = num/10000.0D;
            BigDecimal bg = new BigDecimal(rangeLength);
            double d3 = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            String valueStr = String.valueOf(d3);
            if(valueStr.endsWith("0")){
                valueStr = valueStr.substring(0,valueStr.length()-2);
            }
            return  valueStr+"万";
        }
        else
        {
            return String.valueOf(num);
        }



    }
}
