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
@Table(name="T_Post_Colum")
public class PostColumEntity {
    @Id
    private String postId;

    private String pkId;

    private String pkName;

    private String text;

    private String imgUrl;









}
