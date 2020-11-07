package com.union.app.domain.pk;

import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Comment {

    String commentId;

    String id;

    User user;

    String text;

    String time;



}
