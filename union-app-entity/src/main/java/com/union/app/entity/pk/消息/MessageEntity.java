package com.union.app.entity.pk.消息;

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
@Table(name="T_UNION_MESSAGE")
public class MessageEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int messageId;

    @Enumerated(EnumType.STRING)
    MessageType messageType;

    String hostId;

    String userId;

    byte[] message;

    String imgUrl;

    String mediaId;

    String date;

    String time;
}
