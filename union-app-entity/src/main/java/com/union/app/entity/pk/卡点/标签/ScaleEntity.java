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
@Table(name="T_SCALE_ENTITY")
public class ScaleEntity {


    @Id
    private int typeRange;

    private int scale;














}