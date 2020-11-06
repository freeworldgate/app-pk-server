package com.union.app.plateform.constant;

import org.apache.commons.lang.StringUtils;

public enum ConfigItem {

    单个群最大人数("groupMaxMembers","SINGLE_GROUP_MAX_MEMBERS","单个群最大人数","200"),
    当前系统收款金额("feeNumber","CURRENT_FEECODE_POLICY","当前系统收款金额","4"),
    群组最长上传时间间隔("groupMaxAliveTime","GROUP_MAX_TIME_UPDATE","群组最长上传时间间隔",String.valueOf(6 * 24)),
    媒体图片最大过期时间("mediaMaxOutTime","MEDIA_MAX_OUT_TIME","微信图片最大过期时间,默认是3天",String.valueOf(3 * 24)),
    系统当前是否客服模式("isWechatMsgService","SYSTEM_MSG_MODE","系统当前是否客服模式,图片是拉取还是下载","true"),
    系统展示的模式("mode","SYSTEM_SHOW_MODE","审查模式/运营模式/开放模式","1"),
//    审核榜帖最大等待时间("approveWaitingTime","APPROVE_WAITING_TIME","审核榜帖最大等待时间单位分钟","120"),
    榜帖可发起投诉的等待时间("canComplainWaitingTime","CAN_COMPLAIN_WAITING_TIME","图册可发起投诉的等待时间","60"),
//    对所有用户展示审核系统("showApproveSys", "SHOW_APPROVE_SYS", "对所有用户展示审核系统","true"),
    用户最大建榜数量("maxPksUserCreate", "MAX_PKS_USER_CREATE", "用户最大建榜数量","3"),


//    用户最多未激活榜单数量("maxNoActivePks", "MAX_NO_ACTIVE_PKS","用户最多未激活榜单数量" , "2"),
    PK最大修改次数("maxPkModifyTime", "MAX_PK_MODIFY_TIMES", "PK最大修改次数","3" ),
    Post最大修改次数("maxRejectTimes", "MAX_POST_MODIFY_TIMES", "Post最大修改次数","5" ),
    遗传相册("buttonPolicy1", "VIP_ALBUM", "遗传相册","false" ),
    内置相册("buttonPolicy2", "INNER_ALBUM_PUBLIC", "内置相册","false" ),
    普通相册("buttonPolicy3", "INNER_ALBUM_INVITE", "普通相册","false" ),
    VIP用户("buttonPolicy4", "VIP_USER", "VIP用户","false" ),
    普通用户("buttonPolicy5", "COMMON_USER", "普通用户","false" ),
    激活码使用次数("activeCodeTimes","ACTIVE_CODE_TIMES" , "激活码使用次数", "5"),
//    非遗传用户默认榜单数量("defaultPostTimes", "DEFAULT_POST_TIMES", "非遗传用户默认榜单数量","5" ),
    单个PK页面的帖子数("singlePkPagePosts", "SINGLE_PK_PAGE_POSTS", "单个PK页面的帖子数","50" ),



    PKEntity缓存数量("pkentityCacheSize", "PKENTITY_CACHE_SIZE", "PKEntity缓存数量", "1000"),
    POSTIMAGEEntity缓存数量("postImageentityCacheSize", "POSTIMAGEENTITY_CACHE_SIZE", "POSTIMAGEEntity缓存数量", "1000"),
    USEREntity缓存数量("userentityCacheSize", "USERENTITY_CACHE_SIZE", "USEREntity缓存数量", "1000"),
    APPROVEMESSAGEEntity缓存数量("approveMessageentityCacheSize", "APPROVEMESSAGEENTITY_CACHE_SIZE", "APPROVEMESSAGE缓存数量", "1000"),
    APPROVECOMMENTEntity缓存数量("approveCommententityCacheSize", "APPROVECOMMENTENTITY_CACHE_SIZE", "APPROVEMESSAGE缓存数量", "1000"),
    刷新主题个数("pageTopics", "REFRESH_PAGE_TOPICS", "刷新主题个数", "5"),
    普通用户发帖后解锁更多主题("allUserRealease", "USER_MODE_REALEASE","普通用户发帖后解锁更多主题" , "true"),
    留言方式("commentStyle", "COMMENT_STYLE", "留言方式", "0"),
    PK有效投诉最大数量("activeComplains", "MAX_ACTIVE_COMPLAIN", "PK有效投诉最大数量", "3"),
    主题和榜帖数量绑定("commonUserCreatePk", "COMMON_USER_CRAETE_PK", "主题和榜帖数量绑定", "true"),
    是否允许客服会话("messageSession", "MESSAGE_SESSION", "是否允许客服会话", "false"),

    RAM秘钥ID("accessKeyID", "accessKeyID", "RAM秘钥ID", "accessKeyID"),
    RAM秘钥("accessKeySecret", "accessKeySecret", "RAM秘钥", "accessKeySecret"),
    OSS基础地址("ossBaseUrl", "ossBaseUrl", "OSS基础地址", "ossBaseUrl"),
    Bucket名字("bucketName", "bucketName", "Bucket名字", "211shop"),
    遗传用户是否收费("vipchargePolicy", "VIP_CHARGE_POLICY", "遗传用户是否收费", "false"),
    遗传用户单个主题费用("vipsinglePkPrice", "VIP_SINGLE_PK_PRICE", "遗传用户单个主题费用", "10"),
    遗传用户12个主题打包费用("vip12PkPrice", "VIP_12_PK_PRICE", "遗传用户12个主题打包费用", "49.9"),
    普通用户是否收费("chargePolicy", "CHARGE_POLICY", "普通用户是否收费", "false"),
    普通用户单个主题费用("singlePkPrice", "SINGLE_PK_PRICE", "遗传用户单个主题费用", "10"),
    普通用户12个主题打包费用("n12PkPrice", "12_PK_PRICE", "遗传用户12个主题打包费用", "49.9"),
    邀请和可解锁主题倍数关系("inviteAndRelease", "INVITE_AND_RELAESE", "邀请和可解锁主题倍数关系", "3"),
    普通用户主题是否显示分享按钮和群组按钮("isShowshareButton", "IS_SHOW_SHARE_BUTTON","普通用户主题是否显示分享按钮", "false"),
    单个PK的页面缓存数量("pkCachePost", "PK_CACHE_POST", "单个PK的页面缓存数量", "10"),
    缓存时间("postCacheTime", "POST_CACHE_TIME", "缓存时间(时间秒)", "60"),
    PK缓存数量("pkCacheNumbers", "PK_CACHE_NUMBERS", "PK缓存数量", "100"),
    首页图片选择榜主图册("imgSelectPkCreator", "IS_PK_CREATOR_IMG", "首页图片选择榜主图册", "true");










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
