package com.union.app.domain.pk.审核;

import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveComment {

    private User user;
    private String pkId;
    private String postId;
    private String approverId;
    private String text;
    private String imgUrl;
    private String voiceUrl;
    private int speckTime;
    private KeyNameValue statu;
    private String time;
    private String date;
    private String word1 = "编辑内容";
    private String word2 = "审核留言";











}
