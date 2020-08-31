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
@Table(name="T_BUILD_IN_PK")
public class BuildInPkEntity {


    @Id
    private String pkId;

    @Enumerated(EnumType.STRING)
    private InviteType isInvite;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private long createTime;




}
