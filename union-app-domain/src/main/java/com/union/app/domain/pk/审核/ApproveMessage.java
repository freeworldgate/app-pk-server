package com.union.app.domain.pk.审核;

import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveMessage {


    private String date;
    private User user;

    private String text;

    private String imgeUrl;

    private String mediaId;

    private String time;

    private int speckTime;

    private String voiceUrl;

    private String moreStr = "查看详情";










}
