package com.union.app.entity.pk.post;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_Comment_Restore")
public class CommentRestoreEntity {

    @Id
    private String restoreId;

    private String commentId;

    private String userId;

    private String targetUserId;

    private String comment;

    private long time;

    private long likes;

    private long dislikes;








}
