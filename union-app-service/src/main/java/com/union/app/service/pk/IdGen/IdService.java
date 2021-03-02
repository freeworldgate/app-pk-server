package com.union.app.service.pk.IdGen;

import com.alibaba.fastjson.JSONObject;
import com.union.app.common.config.AppConfigService;
import com.union.app.common.dao.AppDaoService;
import com.union.app.common.dao.KeyService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.dao.spi.filter.OrderTag;
import com.union.app.domain.pk.ActiveTip;
import com.union.app.domain.pk.PkDetail;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.cashier.PkCashier;
import com.union.app.domain.pk.捞人.ScaleRange;
import com.union.app.domain.user.User;
import com.union.app.entity.pk.*;
import com.union.app.entity.pk.city.CityEntity;
import com.union.app.entity.pk.kadian.label.ActiveTipEntity;
import com.union.app.entity.pk.kadian.label.RangeEntity;
import com.union.app.entity.user.UserEntity;
import com.union.app.entity.user.support.UserType;
import com.union.app.entity.配置表.ColumSwitch;
import com.union.app.entity.配置表.ConfigEntity;
import com.union.app.plateform.constant.ConfigItem;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.DataSet;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.service.pk.dynamic.CacheKeyName;
import com.union.app.service.pk.dynamic.DynamicService;
import com.union.app.service.pk.service.*;
import com.union.app.service.pk.service.pkuser.UserDynamicService;
import com.union.app.service.user.UserService;
import com.union.app.util.idGenerator.IdGenerator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class IdService {


    @Resource
    private RedisTemplate redisTemplate;


    public long gennerateSortId(){
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(initPrimaryId(),redisTemplate.getConnectionFactory());
        return redisAtomicLong.incrementAndGet();
    }

    private String initPrimaryId() {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            String key = simpleDateFormat.format(Calendar.getInstance().getTime()).substring(2);
                 //自定义编号规则
            String hashColVal = key + "000001";
            redisTemplate.opsForValue().setIfAbsent(key, Long.valueOf(hashColVal),2, TimeUnit.HOURS);
            return key;
    }

}
