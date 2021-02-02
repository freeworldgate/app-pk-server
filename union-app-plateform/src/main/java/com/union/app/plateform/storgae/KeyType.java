package com.union.app.plateform.storgae;

public enum  KeyType {

    群组成员("GROUP_MEMBERS"),

    用户粉丝("USER_FANS"),

    想认识我的人("LIKE_ME"),

    卡点人数("PK_TOTAL_USERS"),

    卡点POST("PK_TOTAL_POST"),

    要同步的PK列表("SYNC_PK_LIST"),
    PK同步时间Map("SYNC_PK_LIST_TIME"),

    打卡图片("PK_POST_IMG_CACHE"),
    顶置图片("PK_TOP_POST_IMG_CACHE"),
    用户缓存("USER_ENTITY_CACHE"),

    PK图片总量("PK_TOTAL_IMAGES_CACHE"),


    配置缓存("CONFIG_CACHE_MAP"),
    卡点偏移缩放("PK_RANGE_SCALE_OFFSET"),

    配置图片类型缓存("PK_TYPE_IMAGE_CACHE");




















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
