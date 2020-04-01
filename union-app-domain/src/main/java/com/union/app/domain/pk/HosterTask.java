package com.union.app.domain.pk;

import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HosterTask {

    String taskId;

    String pkId;

    User creator;

    int feeNum;

    User feeUser;

    String feeUrl;

    KeyNameValue statu;

    int userIntegral;




}
