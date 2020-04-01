package com.union.app.service.pk.dynamic;

public enum DynamicLevel {

    APP("APP"),

    PK("PK"),

    USER("USER"),

    PK_POST("PKPOST"),

    PK_USER("PKUSER"),

    ;

    private String name;

    DynamicLevel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
