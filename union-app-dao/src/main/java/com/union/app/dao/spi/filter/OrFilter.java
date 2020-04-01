package com.union.app.dao.spi.filter;

public class OrFilter implements EntityFilter {

    @Override
    public String getSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" or ");
        return stringBuffer.toString();
    }
}
