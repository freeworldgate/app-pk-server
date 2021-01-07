package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LocationType {


    private String typeName;
    //翻译成语言长度
    private String range;

    private int rangeLength = 200;

    private int scale = 16;

}