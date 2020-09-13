package com.union.app.domain.pk;


public class ApproveButton {

    public static ApproveButton 转发审核群 = new ApproveButton(0,"/images/approve3.png","转发审核群","","share","rcc message h-100 br-100 b-white w-500");

    public static ApproveButton 请求审核有效 = new ApproveButton(1,"/images/approve3.png","发布图贴","approvePost","","rcc message h-100 br-100 b-white w-500");

    public static ApproveButton 请求审核无效 = new ApproveButton(2,"/images/approve1.png","审核中","","","rcc h-100 br-100 b-post c-3-1 w-500");



    ;
    private int button;

    private String icon;

    private String name;

    private String linkMethod;

    private String type;

    private String style;


    public ApproveButton(int button, String icon, String name, String linkMethod, String type, String style) {
        this.button = button;
        this.icon = icon;
        this.name = name;
        this.linkMethod = linkMethod;
        this.type = type;
        this.style = style;
    }


    public int getButton() {
        return button;
    }

    public void setButton(int button) {
        this.button = button;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
