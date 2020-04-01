package com.union.app.entity.share;

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
@Table(name="T_Share_Fee")
public class ShareFeeEntity {


    @Id
    private String userId;

    private int shareTimes;

    private int feeTimes;



}
