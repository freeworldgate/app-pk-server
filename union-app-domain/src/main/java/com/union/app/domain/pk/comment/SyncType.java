package com.union.app.domain.pk.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncType {

    private String id;

    private int type;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
