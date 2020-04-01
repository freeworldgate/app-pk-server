package com.union.app.common.id;

import com.union.app.domain.pk.PostPart;

public class KeyGetter {


    /**
     * 用户是否在PKID下发帖
     * @param pkId
     * @param userId
     * @return
     */
    public static String 用户发布帖子记录(String pkId, String userId) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer = stringBuffer.append("PK/USER").append("/").append(userId).append("-").append(pkId);
        return stringBuffer.toString();
    }


    public static String getPkDynamicKey(String pkId) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer = stringBuffer.append("Pk-Dynamic-").append(pkId);
        return stringBuffer.toString();

    }

    public static String 用户收藏Key(String pkId, String userId, String postId) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer = stringBuffer.append("PK/Like/User/").append(userId).append("/").append(pkId).append("/").append(postId);
        return stringBuffer.toString();

    }

    public static String PK分页POST缓存Key(String pkId, int page) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer = stringBuffer.append("PK/Cache/").append(pkId).append("/Pk-Page-").append(page);
        return stringBuffer.toString();

    }

    public static String Post的Key(PostPart postPart, String postId) {
        String pkId = postId.split("-")[0];
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer = stringBuffer.append("PK/PK-POST/").append(pkId).append("/").append("POST/").append(postId).append("/").append(postPart.getPart());
        return stringBuffer.toString();
    }


    public static String 异常的POSTID列表(String pkId) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer = stringBuffer.append("PK-USED-POSTID-").append(pkId);
        return stringBuffer.toString();
    }

    public static String 拼接POSTID(String pkId, int seq) {
        StringBuffer stringBuffer = new StringBuffer();
        String postId = stringBuffer.append(pkId).append("-POST-SEQ-").append(seq).toString();
        return postId;
    }

    public static String 记录PK最新POST序列MAP() {
        return "POST-SEQ";
    }

    public static String 分页刷新标识Key(String pkId) {
        StringBuffer stringBuffer = new StringBuffer();
        String refreshMap = stringBuffer.append(pkId).append("-POST-PAGE-REFRESH").toString();
        return refreshMap;

    }

    public static int 当前序列值(String postSeq)
    {
            String[] seqArray = postSeq.split("-");
            return Integer.valueOf(seqArray[seqArray.length-1]);
    }

    public static String 获取PKID(String postId) {
        String[] seqArray = postId.split("-");

        return seqArray[0];
    }
}
