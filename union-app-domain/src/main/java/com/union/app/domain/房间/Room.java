package com.union.app.domain.房间;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Room
{

    private int roomId;

    private String roomName;

    private String roomType;

    private String roomTypeName;

    private String 房间拥有者ID;

    private String roomOwnerName;

    private String roomOwnerImage;

    private int 房间人数;

    private int 房间人数上限;


    private String viewNum;


    private String commentNum;

    private String roomImage;

    private boolean isVip;

    private double vipFee;


    private int roomPriority;


    /**
     * 用户是否关注这个房间主
     */
    private boolean isFollowed;

    /**
     * 用户是否是这个房间的VIP会员
     */
    private boolean isVipMember;










}
