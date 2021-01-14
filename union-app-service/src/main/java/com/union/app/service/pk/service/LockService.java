package com.union.app.service.pk.service;

import com.alibaba.fastjson.JSONObject;
import com.union.app.common.OSS存储.CacheStorage;
import com.union.app.common.OSS存储.OssStorage;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.EntityCacheService;
import com.union.app.common.redis.RedisSortSetService;
import com.union.app.common.微信.WeChatUtil;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.*;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.complain.ComplainEntity;
import com.union.app.entity.pk.complain.ComplainStatu;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.UserKvEntity;
import com.union.app.entity.用户.support.UserType;
import com.union.app.entity.配置表.ColumSwitch;
import com.union.app.entity.配置表.ConfigEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.data.PkDataService;
import com.union.app.service.pk.complain.ComplainService;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import com.union.app.util.time.TimeUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class LockService {


    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public boolean getLock(String lockName, long millisecond) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockName, "lock",
                millisecond, TimeUnit.MILLISECONDS);
        return success != null && success;
    }

    public void releaseLock(String lockName) {
        redisTemplate.delete(lockName);
    }


}
