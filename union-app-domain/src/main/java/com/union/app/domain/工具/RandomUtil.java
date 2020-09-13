package com.union.app.domain.工具;

import com.union.app.domain.user.City;
import com.union.app.domain.user.Job;
import com.union.app.domain.user.User;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtil {

    public static String[] names = new String[]{"小猫咪","无敌Max无敌Max无敌Max无敌Max无敌Max无敌Max无敌Max","大风车小朋友","我勒个去我勒个去我勒个去我勒个去我勒个去我勒个去我勒个去","香蕉个巴拉","就喜欢吹就喜欢吹就喜欢吹","江山如此多娇江山如此多娇江山如此多娇江山如此多娇","我爱北京天安门我爱北京天安门","你奶奶个腿"};
    public static String[] dirName = new String[]{"JHDKJ","adasdas","sadasd","sadasda","sadasd","sadasd","df","sadasd","asdsadsads"};
    public static String[] text = new String[]{"总结：以上就是本篇文章所","介绍的在js数组中添加","元素的2种方法，分别为","splice()方法和delete方法。工作中","splice()方法和delete方法。","如何删除指定位置的元素？删除指定位置元素的2种方法的详细内容，更多请关注php中文网其它相关文章！","如何删除指定位置的元素？删除指定位置元素的2种方法的详细内容，更多请关注php中文网其它相关文章！","以上就是js数组","splice"};
    public static int[] numbers = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43};
    public static String[] date = new String[]{"3分钟前","2小时以前","2018-9-8","9-08","昨天 21:00","今天"};
    public static String[] users = new String[]{"U1","U2","U3","U4","U5","U6","U7","U8","U9","U10","U11","U12","U13","U14","U15"};

    public static String[] imageUrl = new String[]{






            "https://oss.211shopper.com/logo/FB_IMG_1543803767584.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543805058846.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543812164803.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543812798352.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543812813662.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543836383730.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543852637379.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543853901113.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988783334.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988792981.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988809377.jpg",
            "https://oss.211shopper.com/049b154f-db17-4634-b1cc-965ca2d2f468/wx-1551595917678.jpg",
            "https://oss.211shopper.com/04a4af08-951d-439c-9838-e2907f017fdb/wx-1551546619541.jpg",
            "https://oss.211shopper.com/wx1548240368990_9999.jpg",
            "https://oss.211shopper.com/wx1548222393473_9999.jpg",
            "https://oss.211shopper.com/wx1548219690269_9999.jpg",
            "https://oss.211shopper.com/wx1548167981662_9999.jpg",
            "https://oss.211shopper.com/wx1548167909042_9999.jpg",
            "https://oss.211shopper.com/wx1548156415538_9999.jpg",
            "https://oss.211shopper.com/wx1548129324302_9999.jpg",
            "https://oss.211shopper.com/wx1548129133577_9999.jpg",
            "https://oss.211shopper.com/wx1548129129822_9999.jpg",
            "https://oss.211shopper.com/wx1548129126065_9999.jpg",
            "https://oss.211shopper.com/wx1548129121750_9999.jpg",
            "https://oss.211shopper.com/wx1548129117738_9999.jpg",
            "https://oss.211shopper.com/wx1548129113641_9999.jpg",
            "https://oss.211shopper.com/wx1548129109948_9999.jpg",
            "https://oss.211shopper.com/wx1548129106013_9999.jpg",
            "https://oss.211shopper.com/wx1548129081102_9999.jpg",
            "https://oss.211shopper.com/wx/fuck/1548222429542_9999.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543989054483.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988979276.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988977026.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988972879.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988970393.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988965827.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988961169.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988959036.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988956130.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988952415.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988952415.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988946266.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988940456.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988933875.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988979276.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988972879.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988931582.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988929038.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988925610.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988923401.jpg",
            "https://oss.211shopper.com/logo/FB_IMG_1543988918746.jpg",


    };

    public static String[] imageBack = new String[]{

//            "https://fenghao211.oss-cn-beijing.aliyuncs.com/imgback/1.png",
//            "https://fenghao211.oss-cn-beijing.aliyuncs.com/imgback/2.png",
//            "https://fenghao211.oss-cn-beijing.aliyuncs.com/imgback/3.png",
            "https://fenghao211.oss-cn-beijing.aliyuncs.com/imgback/4.png",
//            "https://fenghao211.oss-cn-beijing.aliyuncs.com/imgback/5.png",
//
//            "https://fenghao211.oss-cn-beijing.aliyuncs.com/imgback/9.png",
//            "https://fenghao211.oss-cn-beijing.aliyuncs.com/imgback/14.png",
//            "https://fenghao211.oss-cn-beijing.aliyuncs.com/imgback/15.png",
//            "https://fenghao211.oss-cn-beijing.aliyuncs.com/imgback/16.png",


    };


    public static String getRandomImage(){
        Random random = new SecureRandom();
        int url = Math.abs(random.nextInt())%imageUrl.length;
        return imageUrl[url];
    }

//    public static String getRandomImage(){
//        Random random = new SecureRandom();
//        int url = Math.abs(random.nextInt())%imageUrl.length;
//        return imageUrl[url] + "?x-oss-process=image/resize,h_200";
//    }
    public static String getRandomName(){
        Random random = new SecureRandom();
        int url = Math.abs(random.nextInt())%names.length;
        return names[url];
    }

    public static int getRandomNumber(){
        Random random = new SecureRandom();
        int url = Math.abs(random.nextInt())%numbers.length;
        return numbers[url];
    }

    public static String getRandomDate(){
        Random random = new SecureRandom();
        int i = random.nextInt();
        int mai = Math.abs(i);
        int url = mai%date.length;
        return date[url];
    }

    public static List<String> getRandomImageList() {
        List<String> urls = new ArrayList<>();
        for(int i =0 ;i <  Math.abs((new SecureRandom()).nextInt())%100;i++) {
            urls.add(getRandomImage());
        }
        return urls;
    }


    public static String getRandomText(){
        Random random = new SecureRandom();
        int url = Math.abs(random.nextInt())%text.length;
        return text[url];
    }

    public static User getRandomUser() {
        Random random = new SecureRandom();
        int i = random.nextInt();
        int mai = Math.abs(i);
        int userIndex = mai%users.length;

        User user = new User();
        user.setUserId(users[userIndex]);
        user.setImgUrl(RandomUtil.getRandomImage());
        user.setUserName(RandomUtil.getRandomName());


        return user;
    }

    public static Job getRandomJob() {
        Job job = new Job();
        job.setJobId("Job001");
        job.setJobName("程序員");
        return job;
    }
    public static City getRandomCity() {
        City city = new City();
        city.setCityCode("nj");
        city.setCityName("南京");
        return city;
    }

    public static String getRandomBackImg() {


        Random random = new SecureRandom();
        int url = Math.abs(random.nextInt())%imageBack.length;
        return imageBack[url];






    }

    public static String getRandomDirName() {

        Random random = new SecureRandom();
        int url = Math.abs(random.nextInt())%dirName.length;
        return dirName[url];

    }
}
