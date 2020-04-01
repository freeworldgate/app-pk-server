package com.union.app.dao.spi.filter;

public class AndFilter implements EntityFilter {

    @Override
    public String getSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" and ");
        return stringBuffer.toString();
    }
}
