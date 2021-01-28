package com.union.app.domain.pk.文字背景;

import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class TextBack implements Serializable{


    private String backId;

    private String backColor;

    private String backUrl;

    private String fontColor;
    

}
