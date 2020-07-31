package com.union.app.service.pk.dynamic;

import com.alibaba.fastjson.JSON;
import com.union.app.common.config.AppConfigService;
import com.union.app.dao.spi.AppDaoService;
import com.union.app.domain.pk.OperType;
import com.union.app.domain.pk.PkDynamic.FactualInfo;
import com.union.app.domain.pk.Post;
import com.union.app.domain.pk.apply.KeyNameValue;
import com.union.app.domain.pk.integral.UserIntegral;
import com.union.app.domain.pk.审核.ApproveMessage;
import com.union.app.domain.user.User;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.plateform.constant.常量值;
import com.union.app.plateform.data.resultcode.AppException;
import com.union.app.plateform.data.resultcode.Level;
import com.union.app.plateform.data.resultcode.PageAction;
import com.union.app.plateform.storgae.redis.RedisStringUtil;
import com.union.app.service.pk.dynamic.imp.RedisMapService;
import com.union.app.service.pk.dynamic.imp.RedisSortSetService;
import com.union.app.service.pk.service.ApproveService;
import com.union.app.service.pk.service.PkService;
import com.union.app.service.pk.service.PostService;
import com.union.app.service.pk.service.UserInfoService;
import com.union.app.service.user.UserService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class CacheService {


    @Autowired
    RedisMapService redisMapService;

    @Autowired
    RedisSortSetService redisSortSetService;

//    public long 获取收藏积分(String pkId,String userId){ return redisMapService.getIntValue(CacheKeyName.打榜Follow(pkId),userId) * AppConfigService.getConfigAsInteger(常量值.收藏一次的积分,100); }
//
//    public long 获取今日分享积分(String pkId,String userId){ return redisMapService.getIntValue(CacheKeyName.打榜Share(pkId),userId) * AppConfigService.getConfigAsInteger(常量值.分享一次的积分,100) ;}
//
//    public long 获取用户排名(String pkId,String userId) { return redisSortSetService.getIndex(CacheKeyName.打榜排名列表(pkId),userId); }

//    public void 更新今日用户排名(String pkId,String userId){ double score = (获取收藏积分(pkId,userId) + 获取今日分享积分(pkId,userId)) * -1.0D;redisSortSetService.addEle(CacheKeyName.打榜排名列表(pkId),userId,score); }







}




