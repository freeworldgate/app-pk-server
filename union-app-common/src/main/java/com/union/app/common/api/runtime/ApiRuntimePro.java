package com.union.app.common.api.runtime;

import com.union.app.common.api.registry.ApiURI;
import com.union.app.common.api.registry.bean.ServiceBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
public class ApiRuntimePro
{


    @Value("${spring.application.nodeId}")
    private String nodeId;























}
