package com.union.app.domain.wechat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class UserInfo
{

    private String openId;

    private String nickName;

    private int gender;

    private String language;

    private String city;

    private String nce;

    private String country;

    private String avatarUrl;

    private Watermark watermark;


}
