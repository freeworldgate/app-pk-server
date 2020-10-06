package com.union.app.domain.pk;

public enum IconUrl {


    有效微信标识("/images/group1.png"),
    无效微信标识("/images/group.png"),



    ;



    private String url;

    IconUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
