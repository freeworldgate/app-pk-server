package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


public enum PkButtonType {

    群组("/images/group1.png","主题群","groupCode","",""),

    榜帖("/images/pic.png","我的图册","publish","",""),

    审核("/images/appro.png","记录","approveList","",""),

    审核中("/images/waiting2.png","待发布","approvingList","",""),

    投诉("/images/complain.png","投诉","complain","",""),


    发布图册("/images/post.png","发布图册","checkUserPost","rcc b-publish w-400 h-100 br-100",""),
    邀请图册("/images/invite.png","邀请","","rcc b-share w-400 h-100 br-100","share"),

    群组已更新("/images/group1.png","主题群","groupCode","rus fs-25 fw-400 c-white",""),
    群组未更新("/images/group.png","主题群","groupCode","rus fs-25 fw-400 c-white",""),
    时间("","刚刚","","rus fs-25 fw-400 c-white",""),



    ;
    private String icon;

    private String name;

    private String linkMethod;

    private String style;

    private String type;

    PkButtonType(String icon, String name, String linkMethod, String style, String type) {
        this.icon = icon;
        this.name = name;
        this.linkMethod = linkMethod;
        this.style = style;
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkMethod() {
        return linkMethod;
    }

    public void setLinkMethod(String linkMethod) {
        this.linkMethod = linkMethod;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
