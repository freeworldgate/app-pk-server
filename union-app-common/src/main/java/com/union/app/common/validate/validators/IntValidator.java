package com.union.app.common.validate.validators;

import com.union.app.entity.校验表.ValidateEntity;
import com.union.app.plateform.exception.ValidateException;

public class IntValidator implements Validator<Integer> {


    @Override
    public void init(ValidateEntity validateEntity) {


    }





    @Override
    public boolean validate(ValidateEntity validateEntity, Integer integer) throws ValidateException {
        return false;
    }




}
