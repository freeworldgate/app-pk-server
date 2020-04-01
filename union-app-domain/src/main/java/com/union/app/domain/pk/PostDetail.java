package com.union.app.domain.pk;

import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostDetail {

    private String postId;
    private String userId;

    private User user;

    private String title;

    private long time;

}
