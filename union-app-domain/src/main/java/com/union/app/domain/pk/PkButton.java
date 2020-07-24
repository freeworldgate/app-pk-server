package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PkButton implements Serializable {

    private String icon;

    private String name;

    private String linkMethod;

    private int num;


    public PkButton(String icon, String name, String linkMethod) {
        this.icon = icon;
        this.name = name;
        this.linkMethod = linkMethod;
    }

    public PkButton(String icon, String name, String linkMethod, int num) {
        this.icon = icon;
        this.name = name;
        this.linkMethod = linkMethod;
        this.num = num;
    }
}
