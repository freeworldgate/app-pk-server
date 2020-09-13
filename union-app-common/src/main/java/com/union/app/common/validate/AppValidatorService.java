package com.union.app.common.validate;

import com.union.app.common.config.AppConfigService;
import com.union.app.common.spring.context.SpringContextUtil;
import com.union.app.common.validate.validators.ValidateTypeSupport;
import com.union.app.common.validate.validators.Validator;
import com.union.app.common.dao.AppDaoService;
import com.union.app.dao.spi.filter.CompareTag;
import com.union.app.dao.spi.filter.EntityFilterChain;
import com.union.app.entity.校验表.ValidateClassType;
import com.union.app.entity.校验表.ValidateEntity;
import com.union.app.plateform.exception.ValidateException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class AppValidatorService
{
    private static AppDaoService appDao = SpringContextUtil.getBean(AppDaoService.class);

    private static final Map<String,HashMap<String,ValidateEntity>> validateEntities = new ConcurrentHashMap<>();


    public static <T> boolean validate(Class<T> tClass,String validateId,Object object)throws ValidateException
    {
        String className = tClass.getSimpleName();

        ValidateEntity validateEntity = queryValidateEntity(className,validateId,object);

        return validateEntity(validateEntity,object);


    }

    private static boolean validateEntity(ValidateEntity validateEntity, Object object) {




        return true;
    }

    private static void insertValidateEntity(String className, String validateId, Object object)
    {
        Class<?> tClass = object.getClass();
        ValidateClassType validateClassType = ValidateClassType.valueOfClass(tClass);

        List<Validator> validators = ValidateTypeSupport.getClassValidators(validateClassType);

        ValidateEntity validateEntity = new ValidateEntity();

        validateEntity.setValidateClass(className);

        validateEntity.setValidteUniqued(validateId);

        for (Validator validator:validators)
        {
            validator.init(validateEntity);
        }

        appDao.insertEntity(validateEntity);


    }

    private static ValidateEntity queryValidateEntity(String className,String validateId,Object object) {
        /*查询缓存*/
        ValidateEntity validateEntity = queryCacheValidateEntity(className,validateId);
        if(validateEntity == null)
        {
            /*查数据库*/
            validateEntity = queryDataBaseValidateEntity(className,validateId);
            if(validateEntity == null && validateSwitchStatu())
            {
                /*插入obj数据*/
                insertValidateEntity(className,validateId,object);
                /*查询数据库*/
                validateEntity = queryDataBaseValidateEntity(className,validateId);
            }
            /*添加缓存*/
            validateEntities.get(className).put(validateId,validateEntity);
        }
        return validateEntity;
    }

    private static boolean validateSwitchStatu() {
        String vSwitch = AppConfigService.getConfigAsString("validate_write_switch","OFF");
        if(!"ON".equalsIgnoreCase(vSwitch)){return false;}
        else {return true;}
    }

    private static ValidateEntity queryDataBaseValidateEntity(String className, String validateId) {
        EntityFilterChain entityFilterChain = EntityFilterChain.newFilterChain(ValidateEntity.class)
                .compareFilter("validateClass",CompareTag.Equal,className)
                .andFilter()
                .compareFilter("validteUniqued",CompareTag.Equal,validateId);
        List<ValidateEntity> validateEntities = appDao.queryEntities(ValidateEntity.class,entityFilterChain);
        if(validateEntities.isEmpty())
        {
            return null;
        }
        else
        {
            return validateEntities.get(0);
        }
    }

    private static ValidateEntity queryCacheValidateEntity(String className, String validateId) {
        HashMap<String,ValidateEntity> validateEntityHashMap = validateEntities.get(className);
        if(validateEntityHashMap == null){
            validateEntityHashMap = new HashMap<>();
            validateEntities.put(className,validateEntityHashMap);
            return null;
        }
        return validateEntityHashMap.get(validateId);
    }


}
