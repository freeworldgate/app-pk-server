package com.union.app.dao.spi.filter;

public class NullFilter implements EntityFilter {
    private String colum;

    private boolean isNull;

    public NullFilter(String colum, boolean isNull) {
        this.colum = colum;
        this.isNull = isNull;
    }

    @Override
    public String getSql()
    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" p.");
        stringBuffer.append(this.colum);
        if(this.isNull)
        {
            stringBuffer.append(" is null ");
        }
        else
        {
            stringBuffer.append(" is not null ");
        }
        return stringBuffer.toString();
    }
}
