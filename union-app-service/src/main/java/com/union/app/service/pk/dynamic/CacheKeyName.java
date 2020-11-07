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
    public static final String 点赞 =  "PK-Greate";
    public static final String 邀请 =  "PK-Invite";
    public static final String 踩一脚 =  "PK-Dislike";
    public static final String 评论 =  "PK-COMMENT";
    public static final String 用户评论 =  "PK-USER-COMMENT";

    public static final String 审核中数量 =  "PK-Approving";
    public static final String 已审核数量 =  "PK-Approved";
    public static final String 群组二维码 =  "Group-Code-MediaId";
    public static final String 群组URL = "Group-Code-Url" ;
    public static final String 拉取资源图片 =  "USER-MEDIA-CURRENT" ;
    public static final String PK排名 = "PK-POST-MEMBER-SORT" ;



//    public static String 内置相册已审核() { return "PK-PRE-ALBUM-APPROVED-NUMS" ;}
//    public static String 内置相册审核中() { return "PK-PRE-ALBUM-APPROVING-NUMS" ;}

//    public static String 内置相册群组状态() { return "PK-PRE-ALBUM-GROUP-STATU" ;}



//    public static String 内置公开PK群组二维码() {return "Inner-Public-Group-Code-MediaId" ;}

//    public static String 更新公告的PKId列表() {return "PK-NEED-UPDATE" ; }
}
