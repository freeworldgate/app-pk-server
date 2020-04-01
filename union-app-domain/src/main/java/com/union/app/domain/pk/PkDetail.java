package com.union.app.domain.pk;

import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PkDetail implements Serializable{


    private String pkId;

    private User user;

    private String topic;

    private String watchWord;

    private int shareMode;


}
