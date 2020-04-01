package com.union.app.plateform.common;

public class CommonUtil {
    public static String memberTip(int boys, int grils) {

        if(boys + grils < 10){
            return "人数不足";
        }
        else if(boys == 0){
            return "全是女生";
        }
        else if(grils == 0){
            return "全是男生";
        }
        else if((boys > grils)&& ((boys - grils)*1.0/(boys+grils) > 0.3)){
            return "男生多";
        }
        else if((boys < grils)&& ((grils - boys)*1.0/(boys+grils) > 0.3)){
            return "女生多";
        }
        else
        {
            return "男女平衡";
        }



    }


}
