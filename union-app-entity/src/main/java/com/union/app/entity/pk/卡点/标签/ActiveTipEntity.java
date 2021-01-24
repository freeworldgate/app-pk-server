package com.union.app.entity.pk.卡点.标签;

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
@Table(name="T_Active_Tip")
public class ActiveTipEntity {


    @Id
    private String id;

    private String tip;











}