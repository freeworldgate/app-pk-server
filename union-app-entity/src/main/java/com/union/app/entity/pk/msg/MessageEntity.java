package com.union.app.entity.pk.msg;

import com.union.app.entity.pk.kadian.捞人.FindStatu;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.awt.*;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@Entity
@Table(name="T_MESSAGE_ENTITY")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)//mysql数据库主键策略
    private int msgId;

    private String pkId;

    private String postId;

    private String commentId;

    private String restoreId;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private boolean isNew;

    private String toUser;

    private String fromUser;

    private String message;

    private long time;


}
