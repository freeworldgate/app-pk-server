package com.union.app.domain.pk.PkDynamic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPkDynamic
{
    private String userId;

    /**
     * 收藏数量
     */
    private long follow;

    /**
     * 当天分享次数
     */
    private long share;

    /**
     * 总的分享次数
     */
    private long totalShare;











}
