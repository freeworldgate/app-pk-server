package com.union.app.service.pk.service;

public enum LockType {

    群组锁("userGroup"),
    用户粉丝数量("userFans"),
    创建卡点锁("BuildPk"),
    城市锁("CityLock");


    private String name;

    LockType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
