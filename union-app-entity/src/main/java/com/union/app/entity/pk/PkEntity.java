package com.union.app.entity.pk;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_PK")
public class PkEntity {
    @Id
    private String pkId;
    //二维码场景值。
    private String scene;

    private String codeUrl;

    private String userId;

    private String sign;

    double latitude;

    double longitude;

    String name;

    String address;

    String city;

    String type;
    //范围
    int typeRange;

    //创建时间
    long time;

    long updateTime;

    private String backUrl;
//    顶置贴
    private String topPostId;
    private long topPostSetTime;

    private long topPostTimeLength;

    private long totalUsers;

    private long totalImgs;


    private boolean rangeSet;
    private boolean citySet;
    private boolean countrySet;
    private boolean findSet;


}
