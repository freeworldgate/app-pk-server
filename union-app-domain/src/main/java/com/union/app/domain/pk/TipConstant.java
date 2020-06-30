package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipConstant {

//    public static final TipConstant 激活相册 = new TipConstant("激活相册","转发今日值班榜主,激活相册","","/pages/pk/selectPker/selectPker?pkId=");


    ;



    private String title;
    private String desc;
    private String icon;
    private String pageUrl;

    public TipConstant(String title, String desc, String icon, String pageUrl) {
        this.title = title;
        this.desc = desc;
        this.icon = icon;
        this.pageUrl = pageUrl;
    }
}
