package com.union.app.plateform.constant;

import org.apache.commons.lang.StringUtils;

public enum ConfigItem {

    单个群最大人数("groupMaxMembers","SINGLE_GROUP_MAX_MEMBERS","单个群最大人数","200"),
    媒体图片最大过期时间("mediaMaxOutTime","MEDIA_MAX_OUT_TIME","微信图片最大过期时间,默认是3天",String.valueOf(3 * 24)),
    单个PK页面的帖子数("singlePkPagePosts", "SINGLE_PK_PAGE_POSTS", "单个PK页面的帖子数","50" ),

    RAM秘钥ID("accessKeyID", "accessKeyID", "RAM秘钥ID", "accessKeyID"),
    RAM秘钥("accessKeySecret", "accessKeySecret", "RAM秘钥", "accessKeySecret"),
    OSS基础地址("ossBaseUrl", "ossBaseUrl", "OSS基础地址", "ossBaseUrl"),
    Bucket名字("bucketName", "bucketName", "Bucket名字", "211shop"),

    RedisMapKey缓存时间("redisMapKeyCacheTime", "REDIS_MAP_KEY_CACHE_TIME", "缓存时间(时间秒)", "60"),
    热度排行榜缓存时间("hotSortCacheTime", "HOT_SORT_CACHE_TIME", "热度排行榜缓存时间(时间秒)", "60"),
    PK缓存数量("pkCacheNumbers", "PK_CACHE_NUMBERS", "PK缓存数量", "100"),
    发帖的时间间隔("time_period", "POST_TIME_PERIED", "发帖的时间间隔", "3600"),
    顶置最少时间("topPostMinLength", "MIN_LENGTH_TOP_POST", "顶置最少时间", "1"),
    创建卡点范围("maxPkLength", "MAX_PK_LENGTH_CREATE", "创建卡点范围(公里)","20" ),
    打捞开关("findSwitch","FIND_SWITCH", "打捞开关","false" ),
    卡点创建收费("pkNeedPay", "PK_NEED_PAY", "卡点收费开关", "false"),
    卡点打捞卡数量("pkFindNum", "PK_FIND_NUM", "卡点打捞卡数量","5" ),
    修改Pk打卡范围时间间隔("updatePkRangeTimeLength", "UPDATE_PK_RANGE_TIMELENGTH", "修改Pk打卡范围时间间隔", "24"),
    顶置打卡周期开关("topPostSwitch", "TOP_POST_SWITCH", "顶置打卡周期开关", "false"),
    隐藏打卡最大数量("maxHiddenPostNum", "MAX_HIDDEN_POST_NUM", "隐藏打卡最大数量","10" ),
    捞人有效操作范围("findRange", "FIND_RANGE", "捞人操作范围(公里)", "20"),
    卡点背景有效操作范围("pkBackRange", "PK_BACK_RANGE", "卡点背景操作范围", "10"),
    用户头像BorderRadius("borderRadius", "USER_BORDER_RADIUS", "用户头像BorderRadius", "10"),
    小图片圆角("postBorderRadius", "USER_BORDER_RADIUS0", "小图片圆角", "10"),
    Post2或4张图圆角("post1BorderRadius", "USER_BORDER_RADIUS1", "2或4张图圆角", "10"),
    Post1张图圆角("post2BorderRadius", "USER_BORDER_RADIUS2", "1张图圆角", "10"),
    文字背景圆角("post3BorderRadius", "USER_BORDER_RADIUS3", "文字背景圆角", "10"),
    PK创建者头像圆角("pkBorderRadius", "PK_CREATOR_BORDER_RADIUS","PK创建者头像圆角","1"),
    操作按钮圆角("buttonBorderRadius", "BUTTON_BORDER_RADIUS", "操作按钮圆角", "1"),
    修改PK背景图时间间隔("updatePkBackTimePeriod", "UPDATE_PK_BACK_TIME_PERIOD", "修改背景时间间隔", "3600");




//    系统默认激活码("defaultActiveCode", "DEFAULT_ACTIVE_CODE", "系统默认激活码","E-000000000000");



    private String tag;
    private String name;
    private String desc;
    private String defaultValue;

    ConfigItem(String tag, String name, String desc, String defaultValue) {
        this.tag = tag;
        this.name = name;
        this.desc = desc;
        this.defaultValue = defaultValue;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public static ConfigItem valueOfConfigItem(String tag)
    {
        for(ConfigItem configItem:ConfigItem.values())
        {
            if(StringUtils.equals(configItem.getTag(),tag)){return configItem;}

        }
        return null;


    }




}
