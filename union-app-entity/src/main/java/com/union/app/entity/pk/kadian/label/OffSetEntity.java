package com.union.app.entity.pk.kadian.label;

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
@Table(name="T_OFFSET_ENTITY")
public class OffSetEntity {


    @Id
    private int scale;
    private double offset;














}