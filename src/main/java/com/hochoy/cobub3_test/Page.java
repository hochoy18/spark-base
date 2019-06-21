package com.hochoy.cobub3_test;

/**
 * 分页
 */
public class Page {

    private int offset;
    private int limit = 10;
    private int pageNum;
    private String search;
    // sort就是按哪个字段排序
    private String sort;
    // order就是顺序还是倒叙
    private String order;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPageNum() {
        return offset / limit + 1;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Page{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", search='" + search + '\'' +
                ", sort='" + sort + '\'' +
                ", order='" + order + '\'' +
                '}';
    }
}
