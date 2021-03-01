package com.union.app.domain.pk.message;

import com.union.app.domain.user.User;
import com.union.app.entity.pk.PostColumEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    private int messageId;

    private User fromUser;

    private User toUser;

    private String message;

    private int type;

    private String pkId;

    private String postId;

    private String commentId;

    private String restoreId;

    private String time;

    private PostColumEntity postColum;

    private boolean isNew;


}
