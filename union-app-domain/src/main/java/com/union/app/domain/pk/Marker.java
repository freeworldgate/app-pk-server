package com.union.app.domain.pk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Marker {

    //标记点 id
    int id;
    //标记点纬度
    double latitude;
    //标记点经度
    double longitude;

    Callout callout;

    String iconPath = "/images/marker.png";

    String width = "34px";
    String height = "34px";
    int borderRadius = 34;
    int rotate = 0;
    float alpha = 1;



}
