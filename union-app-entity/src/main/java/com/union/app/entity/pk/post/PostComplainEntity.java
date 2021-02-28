package com.union.app.entity.pk.post;

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
@Table(name="T_Post_Complain")
public class PostComplainEntity {

    @Id
    private String complainId;

    private String userId;
    private String postId;

    @Enumerated(EnumType.STRING)
    private ComplainType complainType;

    private long time;









}
