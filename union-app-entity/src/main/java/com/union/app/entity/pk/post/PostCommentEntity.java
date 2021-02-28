package com.union.app.entity.pk.post;

import com.union.app.entity.pk.PostStatu;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_Post_Comments")
public class PostCommentEntity {

    @Id
    private String commentId;

    private String postId;

    private String userId;

    private String comment;

    private long restores;

    private long likes;

    private long dislikes;

    private long time;









}
