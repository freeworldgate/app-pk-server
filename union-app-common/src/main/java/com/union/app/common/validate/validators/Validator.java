package com.union.app.common.validate.validators;

import com.union.app.entity.校验表.ValidateEntity;
import com.union.app.plateform.exception.ValidateException;

/**
 * @author root
 */
public interface Validator<T>
{


    void init(ValidateEntity validateEntity);


    boolean validate(ValidateEntity validateEntity,T t)throws ValidateException;



}
