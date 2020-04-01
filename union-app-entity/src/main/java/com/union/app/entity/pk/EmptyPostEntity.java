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
@Table(name="T_Order")
public class EmptyPostEntity {
    @Id
    private String emptyPostId;

    private String userId;

    private String pkId;









}
