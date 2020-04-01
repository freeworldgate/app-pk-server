package com.union.app.dao.spi.filter;

public class PageLimitFilter implements EntityFilter {

    private int page;

    private int limit;

    public PageLimitFilter(int page, int limit) {
        this.page = page;
        this.limit = limit;
    }

    @Override
    public String getSql() {
        return null;
    }


    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }
}
