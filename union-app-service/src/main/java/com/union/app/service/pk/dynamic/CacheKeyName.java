package com.union.app.service.pk.dynamic;

import com.union.app.util.time.TimeUtils;

import java.util.Date;

public class CacheKeyName {



//    public static String 榜主已审核列表(String pkId){return "Approved-Creator-Set" + pkId;}
//    public static String 榜主审核中列表(String pkId){return "Approving-Creator-Set" + pkId;}




//    public static String 审核员审核中列表(String pkId, Date date, String approverId){return "ApprovingSet-"+ pkId +"-"+ TimeUtils.dateStr(date) + "-" + approverId ;}
//    public static String 审核员已审核列表(String pkId, Date date, String approverId){return "ApprovedSet-"+ pkId +"-"+ TimeUtils.dateStr(date) + "-" + approverId ;}
//    public static String 打榜排名列表(String pkId){return "Sort-"+ pkId ;}
//    public static String 预设审核员列表(String pkId){return "PreApprover-"+ pkId ;}



//    Map   -    String

//    public static String 所有审核人(String pkId){return "DailyApprover-"+ pkId ;}

//    public static String 榜帖评论消息(String pkId, Date date){return "Comment-"+ pkId +"-"+ TimeUtils.dateStr(date) ;}
//    public static String 打榜群组(Date date){return "GroupCode-"+ TimeUtils.dateStr(date) ;}

//    Map   -    long
////    public static String 审核人已审核数量(String pkId, Date date){return "ApprovedNum-"+ pkId +"-"+ TimeUtils.dateStr(date) ;}
////    public static String 审核人审核中数量(String pkId, Date date){return "ApprovingNum-"+ pkId +"-"+ TimeUtils.dateStr(date) ;}
////    public static String 榜主审核中数量(){return "Approving-Creator";}
////    public static String 榜主已审核数量(){return "Approved-Creator";}
////    public static String 打榜Follow(String pkId){return "Follow-"+ pkId ;}
////    public static String 打榜Share(String pkId){return "Share-"+ pkId  ;}
    public static String 群组二维码(){return "Group-Code-MediaId"  ;}
//    public static String 审核消息ID(){return "Approve-MessageId-MediaId" ;}
    public static String 群组分配人数(){return "GROUP_MEMBERS" ;}
    public static String 收款码分配人数(){return "FEECODE_MEMBERS" ;}
//    public static String 收款码确认次数(){return "FEECODE_CONFIRM_TIMES" ;}

    public static String 群组URL(){return "Group-Code-Url" ;}
//    public static String 内置公开PK群组URL(){return "Inner-Public-Group-Code-Url" ;}


    public static String 拉取资源图片() { return "USER-MEDIA-CURRENT" ;}

    public static String PK排名() { return "PK-POST-MEMBER-SORT" ;}

    public static String 内置相册已审核() { return "PK-PRE-ALBUM-APPROVED-NUMS" ;}
    public static String 内置相册审核中() { return "PK-PRE-ALBUM-APPROVING-NUMS" ;}

    public static String 内置相册群组状态() { return "PK-PRE-ALBUM-GROUP-STATU" ;}

    public static String PK扫码次数(String pkId) {return "PK-SCAN-" + pkId ;}

//    public static String 内置公开PK群组二维码() {return "Inner-Public-Group-Code-MediaId" ;}

//    public static String 更新公告的PKId列表() {return "PK-NEED-UPDATE" ; }
}
