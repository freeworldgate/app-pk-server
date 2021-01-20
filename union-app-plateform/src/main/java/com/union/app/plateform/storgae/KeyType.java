package com.union.app.plateform.storgae;

public enum  KeyType {

    Ta想认识的人("iLike"),
    想认识的Ta人("likeMe"),



    ;




















    private String name;

    KeyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
