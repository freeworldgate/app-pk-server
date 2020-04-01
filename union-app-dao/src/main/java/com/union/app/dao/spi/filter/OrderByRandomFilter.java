package com.union.app.dao.spi.filter;

public class OrderByRandomFilter implements EntityFilter {


    public OrderByRandomFilter() {

    }

    @Override
    public String getSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" order by rand() ");
        return stringBuffer.toString();
    }
}
