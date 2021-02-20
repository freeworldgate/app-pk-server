package com.union.app.domain.pk;

import com.union.app.domain.pk.PkDynamic.PkDynamic;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PkDetail implements Serializable{



    private int current = 0;


    private String pkId;

    private String city;

//    private PkLocationEntity location;

    private User user;

    private String sign;

    double latitude;

    double longitude;

    String name;

    String address;

    LocationType type;

    private String time;



    private String codeUrl;
    private String backUrl;


    private String topPostId;

    private Post topPost;
    private long topPostSetTime;
    private long topPostTimeLength;
    //距离
    private int userLength = 0;
    private String userLengthStr = "0m";

    private Marker marker;
    private Circle circle;

    private PkDynamic pkDynamic;

    private String totalUsers;
    private long totalUserNumber;

    private boolean set;
    private boolean citySet;
    private boolean countrySet;
    private boolean findSet;
    private boolean rangeLock;
    private boolean lock;

}
