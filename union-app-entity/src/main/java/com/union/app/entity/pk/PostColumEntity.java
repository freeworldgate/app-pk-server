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

    private int postType;

    private String pkId;

    private String pkName;

    private String text;

    private String contentUrl;









}
