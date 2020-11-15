package com.union.app.domain.pk;

import com.union.app.domain.pk.PkDynamic.PkDynamic;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.InviteType;
import com.union.app.entity.pk.PkLocationEntity;
import com.union.app.entity.pk.PkType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class PkDetail implements Serializable{

    private String publishName = "封面";

    private String pkId;

    private PkLocationEntity location;

    private User user;

    private String userBack;

    private String topic;

    private String watchWord;

    private List<ActiveTip> tips;

    private int shareMode;

    private KeyNameValue totalSort;

    private KeyNameValue totalApprover;

    private KeyNameValue pkStatu;
    private KeyNameValue charge;

    private List<String> imgs;



    private ApproveMessage approveMessage;

    private String pkType;
    private int pkTypeValue;

    private String time;

    //点赞
    private int greate;
    //邀请
    private int invite;
    //踩一脚
    private int dislike;
    //评论
    private int comment;



    private int approved;
    private int approving;
    private PkButton groupInfo;

    private long nonGeneticPriority;
    private long geneticPriority;

    private int complainTimes;

    private String backUrl;


}
