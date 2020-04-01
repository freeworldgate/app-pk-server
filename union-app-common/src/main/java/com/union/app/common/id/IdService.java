package com.union.app.common.id;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Component
public class IdService {
    public String 生成订单ID() {

        return UUID.randomUUID().toString();
    }
}
