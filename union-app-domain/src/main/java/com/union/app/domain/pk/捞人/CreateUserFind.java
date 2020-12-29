package com.union.app.domain.pk.捞人;

import com.union.app.domain.pk.apply.KeyValuePair;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CreateUserFind implements Serializable{

    private String pkId;

    private String userId;

    private String text;

    private String img1;

    private String img2;

    private String img3;

    private int findLength;



}
