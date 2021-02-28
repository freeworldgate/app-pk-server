package com.union.app.domain.pk.comment;

import com.union.app.domain.user.User;
import com.union.app.entity.pk.PostColumEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {

    private String commentId;

    private User user;

    private String postId;

    private String comment;

    private String time;

    private long likes;
    private long dislikes;
    private long restores;

    private int statu;

    private PostColumEntity postColum;

}
