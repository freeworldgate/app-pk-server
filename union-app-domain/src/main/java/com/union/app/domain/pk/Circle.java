package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Circle {

    //标记点 id
    int id;
    //标记点纬度
    double latitude;
    //标记点经度
    double longitude;

    String color = "#00000000";

    String fillColor = "#00000010";

    int radius = 160;

    int strokeWidth =  0;


}
