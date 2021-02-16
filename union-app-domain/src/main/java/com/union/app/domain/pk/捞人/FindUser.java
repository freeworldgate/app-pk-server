package com.union.app.domain.pk.捞人;

import com.union.app.domain.pk.*;
import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class FindUser implements Serializable {

    private int findId;

    private String pkId;

    private PkDetail pk;

    private String pkName;

    private User user;

    private String leftTime;

    private String text = "";

    private String img1;

    private String img2;

    private String img3;

    private KeyValuePair statu;

    private int findLength;

    private String timeExpire;

    private boolean exist;

    private String startTime;
    private String endTime;

    private String reason;
}
