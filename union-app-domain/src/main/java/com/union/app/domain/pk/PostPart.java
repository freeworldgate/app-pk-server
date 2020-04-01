package com.union.app.domain.pk;

public enum PostPart {


    POST信息("DETAIL"),
    POST封面("FRONT"),
    POSTBODY("Body"),
//    POST浏览("VIEW"),
//    POST分享("SHARE"),



    ;

    private String part;

    PostPart(String part) {
        this.part = part;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }
}
