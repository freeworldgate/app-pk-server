package com.union.app.plateform.constant;

import org.apache.commons.lang.StringUtils;

public enum ConfigItem {

    单个群最大人数("groupMaxMembers","SINGLE_GROUP_MAX_MEMBERS","单个群最大人数","400"),
    当前系统收款金额("feeNumber","CURRENT_FEECODE_POLICY","当前系统收款金额","4"),
    群组最长上传时间间隔("groupMaxAliveTime","GROUP_MAX_TIME_UPDATE","群组最长上传时间间隔",String.valueOf(6 * 24)),
    媒体图片最大过期时间("mediaMaxOutTime","MEDIA_MAX_OUT_TIME","微信图片最大过期时间,默认是3天",String.valueOf(3 * 24)),
    系统当前是否客服模式("isWechatMsgService","SYSTEM_MSG_MODE","系统当前是否客服模式,图片是拉取还是下载","true"),
    系统展示的模式("mode","SYSTEM_SHOW_MODE","审查模式/运营模式/开放模式","1"),

    审核榜帖最大等待时间("approveWaitingTime","APPROVE_WAITING_TIME","审核榜帖最大等待时间单位分钟","10"),


    对所有用户展示审核系统("showApproveSys", "SHOW_APPROVE_SYS", "对所有用户展示审核系统","true");

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
