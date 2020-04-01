package com.union.app.dao.spi.filter;

public class InFilter implements EntityFilter {
    private String colum;

    private String paramName;

    public InFilter(String colum, String paramName) {
        this.colum = colum;
        this.paramName = paramName;
    }

    @Override
    public String getSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("p.");
        stringBuffer.append(this.colum);
        stringBuffer.append(" in");
        stringBuffer.append("(:");
        stringBuffer.append(this.paramName);
        stringBuffer.append(")");
        return stringBuffer.toString();
    }
}
