package com.union.app.domain.pk.PkDynamic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PkDynamic
{
    private String pkId;
    //总打卡人数
    private long totalUsers;
    //发帖总数
    private long totalPosts;
    //图片数量
    private int totalImages;

    //群组
    private int pkGroups;

    //捞人
    private int pkFinds;











}
