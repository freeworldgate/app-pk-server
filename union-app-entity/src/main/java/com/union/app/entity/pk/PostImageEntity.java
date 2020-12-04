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
@Table(name="T_Post_Image")
public class PostImageEntity {
    @Id
    private String imgId;

    private String postId;

    private String pkId;

    private String imgUrl;

//    private String createTime;
    private long time;


}
