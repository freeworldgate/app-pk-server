package com.union.app.dao.spi.filter;

public class OrderByFilter implements EntityFilter {

    private String colum;
    private OrderTag orderTag;

    public OrderByFilter(String colum, OrderTag orderTag) {
        this.colum = colum;
        this.orderTag = orderTag;
    }

    @Override
    public String getSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" order by");
        stringBuffer.append(" p.");
        stringBuffer.append(this.colum);
        stringBuffer.append(" ");
        stringBuffer.append(orderTag.getTag());
        return stringBuffer.toString();
    }
}
