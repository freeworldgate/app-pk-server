package com.union.app.entity.用户;

public enum UserSex {

    男(0),

    女(1);

    private int sex;

    UserSex(int sex) {
        this.sex = sex;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public static UserSex valueOf(int sex)
    {
        if(sex == UserSex.男.getSex())
        {
            return UserSex.男;
        }else {
            return UserSex.女;
        }
    }



}
