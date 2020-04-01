package com.union.app.dao.spi.filter;

public class CompareFilter implements EntityFilter {

    private String colum;
    private CompareTag compareTag;
    private String paramName;

    public CompareFilter(String colum, CompareTag compareTag, String paramName) {
        this.colum = colum;
        this.compareTag = compareTag;
        this.paramName = paramName;
    }

    @Override
    public String getSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("p.");
        stringBuffer.append(this.colum);
        stringBuffer.append(compareTag.getSymbol());
        stringBuffer.append(":");
        stringBuffer.append(this.paramName);
        return stringBuffer.toString();
    }
}
