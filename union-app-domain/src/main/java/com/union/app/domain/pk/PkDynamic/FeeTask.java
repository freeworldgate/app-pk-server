package com.union.app.domain.pk.PkDynamic;

import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FeeTask
{
    private String taskId;

    private String orderId;

    private String pkId;

    private User creator;

    private User cashier;

    private long integral;

    private long index;

    private KeyNameValue statu;


}
