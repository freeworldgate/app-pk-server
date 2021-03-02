package com.union.app.service.pk.IdGen;

public enum IdType {

    PKID("PKID"),
    POSTID("POSTID"),




    ;


    private String name;

    IdType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
