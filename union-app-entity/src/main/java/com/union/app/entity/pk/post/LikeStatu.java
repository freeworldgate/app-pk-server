package com.union.app.entity.pk.post;

public enum LikeStatu {

    LIKE(1),

    DISLIKE(2),
    ;


    private int statu;

    LikeStatu(int statu) {
        this.statu = statu;
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }
}
