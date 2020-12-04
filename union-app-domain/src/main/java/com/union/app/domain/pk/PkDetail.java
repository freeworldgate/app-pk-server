package com.union.app.domain.pk;

import com.union.app.domain.pk.PkDynamic.PkDynamic;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.entity.pk.InviteType;
import com.union.app.entity.pk.PkLocationEntity;
import com.union.app.entity.pk.PkType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class PkDetail implements Serializable{



    private int current = 0;


    private String pkId;

//    private PkLocationEntity location;

    private User user;

    private String sign;

    double latitude;

    double longitude;

    String name;

    String address;

    LocationType type;

    private String time;
    private int approved;



    private String backUrl;


    private String topPostId;
    private Post topPost;
    //距离
    private int userLength = 0;
    private String userLengthStr = "0m";

    private Marker marker;
    private Circle circle;

}
