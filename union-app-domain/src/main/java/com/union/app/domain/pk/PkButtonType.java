package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


public enum PkButtonType {

    群组("/images/group1.png","主题群","groupCode"),

    榜帖("/images/mypic.png","我的图帖","publish"),

    审核("/images/appro.png","记录","approveList"),

    审核中("/images/waiting1.png","待审核","approvingList"),


    ;
    private String icon;

    private String name;

    private String linkMethod;


    PkButtonType(String icon, String name, String linkMethod) {
        this.icon = icon;
        this.name = name;
        this.linkMethod = linkMethod;
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
}
