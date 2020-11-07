package com.union.app.entity.评论;

import com.union.app.entity.pk.PkStatu;
import com.union.app.entity.pk.PkType;
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
@Table(name="T_PK_COMMENT")
public class PkCommentEntity {
    @Id
    private String commentId;

    private String pkId;

    private String userId;

    private String text;

    private long time;





}
