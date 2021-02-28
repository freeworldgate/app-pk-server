package com.union.app.domain.pk.comment;

import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Restore {

    private String restoreId;

    private String commentId;

    private User user;

    private User targetUser;

    private String comment;

    private String time;

    private long likes;
    private long dislikes;

    private int statu;


}
