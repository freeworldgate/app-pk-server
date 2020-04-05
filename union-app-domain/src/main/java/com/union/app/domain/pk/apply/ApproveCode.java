package com.union.app.domain.pk.apply;

import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApproveCode {

    private String dynamicId;

    private String pkId;

    private User user;

    private int feeNum;

    private String url;

    private KeyNameValue statu;

    private String phone;


    private int approving;
    private int approved;
    private int unapproved;

}
