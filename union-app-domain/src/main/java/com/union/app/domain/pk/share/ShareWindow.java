package com.union.app.domain.pk.share;

import com.union.app.domain.pk.ShareDynamicMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ShareWindow {

    private String userId;
    private String pkId;

    private boolean shareSwitch;

    private PosterInfo posterInfo;
    private CreatorInfo creatorInfo;
    private ShareInfo shareInfo;


    private String userFeeCodeUrl;



    private List<ShareDynamicMessage> message;







}




