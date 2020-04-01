package com.union.app.dao.spi.filter;

public class BetweenFilter implements EntityFilter {

    private String colum;
    private String lowyerName;
    private String higherName;

    public BetweenFilter(String colum, String lowyerName, String higherName) {
        this.colum = colum;
        this.lowyerName = lowyerName;
        this.higherName = higherName;
    }

    @Override
    public String getSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("p.");
        stringBuffer.append(this.colum);
        stringBuffer.append(" between :");
        stringBuffer.append(this.lowyerName);
        stringBuffer.append(" and :");
        stringBuffer.append(this.higherName);
        return stringBuffer.toString();
    }
}
