package com.union.app.dao.spi.filter;

/**
 *
 * @author root
 */
public class LikeFilter implements EntityFilter {

    private String colum;

    private String paramValue;

    public LikeFilter(String colum, String paramValue) {
        this.colum = colum;
        this.paramValue = paramValue;
    }

    @Override
    public String getSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("p.");
        stringBuffer.append(this.colum);
        stringBuffer.append(" like :");
        stringBuffer.append(paramValue);

        return stringBuffer.toString();
    }




}
