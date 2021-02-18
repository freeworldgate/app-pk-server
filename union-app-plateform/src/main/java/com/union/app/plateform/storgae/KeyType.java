package com.union.app.plateform.storgae;

public enum  KeyType {

    群组成员("GROUP_MEMBERS"),

    用户粉丝("USER_FANS"),

    想认识我的人("LIKE_ME"),

    卡点人数("PK_TOTAL_USERS"),

    卡点POST("PK_TOTAL_POST"),

    打卡图片("PK_POST_IMG_CACHE"),

    顶置图片("PK_TOP_POST_IMG_CACHE"),

    用户缓存("USER_ENTITY_CACHE"),

    PK图片总量("PK_TOTAL_IMAGES_CACHE"),

    配置缓存("CONFIG_CACHE_MAP"),

    卡点偏移缩放("PK_RANGE_SCALE_OFFSET"),

    配置图片类型缓存("PK_TYPE_IMAGE_CACHE"),

    温馨提示("TYPE_TEXT_TIPS"),

    城市码("CITY_CODE_NAME"),

    文字背景更新时间("TEXT_BACK_UPDATE_TIME"),

    卡点待同步队列("PK_DATA_SYNC_SET"),

    PK热度排行("PK_HOT_SORT_ALL_LOCATE"),
    卡点已隐藏打卡数量("PK_HIDDEN_POST_NUM"),
    城市卡点数量("CITY_PK_NUM"),
    城市卡点数量同步队列("CITY_PK_NUM_SYNC");




















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
