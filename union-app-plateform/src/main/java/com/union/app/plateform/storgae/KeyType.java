package com.union.app.plateform.storgae;

public enum  KeyType {

    群组成员("GROUP_MEMBERS"),

    用户粉丝("USER_FANS"),

    想认识我的人("LIKE_ME"),


    卡点人数("PK_TOTAL_USERS"),


    卡点POST("PK_TOTAL_POST")



    ;




















    private String name;

    KeyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
