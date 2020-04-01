package com.union.app.service.user;


import com.union.app.dao.spi.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.domain.user.City;
import com.union.app.domain.工具.RandomUtil;
import com.union.app.domain.user.User;
import com.union.app.entity.用户.UserEntity;
import com.union.app.entity.用户.UserSex;
import com.union.app.entity.用户.support.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    @Autowired
    AppDaoService appDaoService;


    /**
     * 查询用户
     * @param userId
     * @return
     */
    public User queryUser(String userId)
    {
        if(StringUtils.isEmpty(userId))
        {
            return null;
        }

        EntityFilterChain filter = EntityFilterChain.newFilterChain(UserEntity.class)
                .compareFilter("userId",CompareTag.Equal,userId);
        UserEntity result = appDaoService.querySingleEntity(UserEntity.class,filter);
        if(!ObjectUtils.isEmpty(result))
        {
            User user = new User();
            user.setUserId(result.getUserId());
            user.setUserType(ObjectUtils.isEmpty(result.getUserType())?UserType.普通用户.getType():result.getUserType().getType());
            user.setImgUrl(RandomUtil.getRandomImage());
            user.setUserName(RandomUtil.getRandomName());
            user.setAge(RandomUtil.getRandomNumber());

            user.setUserSex(RandomUtil.getRandomNumber() %2 == 1?UserSex.男.getSex():UserSex.女.getSex());



            return user;
        }
        return null;
    }


    public boolean isUserVip(String userId){
        User user = queryUser(userId);
        if(ObjectUtils.isEmpty(user)){return false;}
        return user.getUserType() == UserType.重点用户.getType();
    }




    public boolean isUserExist(String userId) {
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(userId,"null")){
            return false;
        }
        if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(userId,"undifined")){
            return false;
        }
        User user = this.queryUser(userId);
        if(!ObjectUtils.isEmpty(user)){
            return true;
        }
        return false;
    }
}
