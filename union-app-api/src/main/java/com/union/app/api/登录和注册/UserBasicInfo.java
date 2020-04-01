package com.union.app.api.登录和注册;


import com.union.app.domain.user.City;
import com.union.app.domain.user.Job;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBasicInfo {

        private int userType;
        private String appName;
        private String userId;
        private String imgUrl;
        private String userName;
        private String fromUser;



}
