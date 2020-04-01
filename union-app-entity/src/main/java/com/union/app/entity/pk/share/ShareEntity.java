package com.union.app.entity.pk.share;

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
@Table(name="T_User_Share")
public class ShareEntity {

    @Id
    String id;














}
