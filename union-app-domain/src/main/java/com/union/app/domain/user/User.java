package com.union.app.domain.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class User implements Serializable{

    private String userId;

    private int userType;

    private String userName;

    private String imgUrl;

    private int userSex;

    private int age;

    private String fromUser;

    private int pkTimes;
    private int postTimes;
    private int inviteTimes;



    private String time;

    private String followTime;
    private int followStatu;
}
