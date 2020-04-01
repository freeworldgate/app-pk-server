package com.union.app.service.pk.dynamic;

import com.union.app.plateform.storgae.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisService {


    @Autowired
    RedisUtil redisUtil;

    public int getAssistanceValue(String s) {
        return 0;
    }

    public void setAssistanceValue(String s, int assistanceValue) {

    }

    public void setShareValue(String s, int shareValue) {

    }

    public int getShareValue(String s) {
        return 0;
    }

    public void setMemberValue(String s, int memberValue) {
    }

    public int getMemberValue(String s) {
        return 0;
    }

    public void test() {



//
//        for(int i=0;i<10000;i++) {
//            redisUtil.set("AAA"+ i, "Value" + i);
//        }
//        for(int i=0;i<10000;i++) {
//            System.out.println(redisUtil.get("AAA" + i));
//        }
//
//        for(int i=0;i<10000;i++) {
//            redisUtil.del("AAA"+ i);
//        }


    }
}
