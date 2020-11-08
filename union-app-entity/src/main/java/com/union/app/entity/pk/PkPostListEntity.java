package com.union.app.entity.pk;

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
@Table(name="T_Post_List")
public class PkPostListEntity {
    @Id
    private String postId;

    private String userId;

    private String pkId;

    @Enumerated(EnumType.STRING)
    private PostStatu statu;

    private long time;


}
