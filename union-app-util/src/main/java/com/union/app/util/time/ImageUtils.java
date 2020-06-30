package com.union.app.util.time;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {

    public static String 缩放处理(String imgUrl,int size) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(imgUrl);
        stringBuffer.append("?x-oss-process=image/resize,w_");
        stringBuffer.append(size);
        return stringBuffer.toString();
    }

    public static String 添加水印(String imgUrl) {

        return imgUrl;

    }
}
