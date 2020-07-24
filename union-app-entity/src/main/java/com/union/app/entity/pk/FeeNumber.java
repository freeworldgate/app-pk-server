package com.union.app.entity.pk;

public enum FeeNumber {

    九快九(0,9.9F),
    十九块九(1,19.9F),
    二九块九(2,29.9F),
    三九块九(3,39.9F),
    四九块九(4,49.9F),
    五九块九(5,59.9F),


        ;


    private int tag;

    private float fee;


    FeeNumber(int tag, float fee) {
        this.tag = tag;
        this.fee = fee;
    }

    public static FeeNumber valueOfNumber(int feeNumber) {
        for(FeeNumber feeNumber1:FeeNumber.values())
        {
            if(feeNumber1.getTag() == feeNumber){return feeNumber1;}
        }



        return null;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }
}
