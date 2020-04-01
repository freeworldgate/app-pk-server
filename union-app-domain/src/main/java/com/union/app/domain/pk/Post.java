package com.union.app.domain.pk;

import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
public class Post {

    private String postId;

    private String pkId;

    private boolean isQueryerCollect;

    private User creator;

    private String topic;

    private List<PostImage> postImages;

    private PostDynamic dynamic;

    private KeyNameValue statu;









}
