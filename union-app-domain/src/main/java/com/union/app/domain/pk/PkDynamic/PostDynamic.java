package com.union.app.domain.pk.PkDynamic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostDynamic {

    private String postId;

    /**
     * 收藏数量
     */
    private long follow;

    /**
     * 浏览次数
     */
    private long view;




}
