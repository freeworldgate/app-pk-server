package com.union.app.domain.pk;

import com.union.app.domain.pk.PkDynamic.PkDynamic;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.PkType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PkDetail implements Serializable{

    private String publishName = "审核公告";

    private String pkId;

    private User user;

    private String topic;

    private String watchWord;

    private KeyNameValue invite;

    private int shareMode;

    private KeyNameValue totalSort;

    private KeyNameValue totalApprover;

    private KeyNameValue pkStatu;


    private ApproveMessage approveMessage;

    private PkType pkType;

    private String time;



    private String approved;
    private String approving;
    private GroupInfo groupInfo;
}
