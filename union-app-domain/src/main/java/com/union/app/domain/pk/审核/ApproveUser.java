package com.union.app.domain.pk.审核;

import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveUser {


    private User user;
    private int role;

    private UserIntegral userIntegral;

    private ApproveMessage approveMessage;

    private ApproveDynamic approveDynamic;

    private ApproveComment approveComment;

    private ApproveComplain approveComplain;

}
