package com.hochoy.cobub3_test;

import java.util.Date;

/**
 * 元数据
 */
public class Metadata {
    private Long id;

    private String productid;

    private String type;

    private String original;

    private String display;

    private Short enabled;

    private Date createtime;

    private String keyWord;

    private Page page;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid == null ? null : productid.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original == null ? null : original.trim();
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display == null ? null : display.trim();
    }

    public Short getEnabled() {
        return enabled;
    }

    public void setEnabled(Short enabled) {
        this.enabled = enabled;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "id=" + id +
                ", productid='" + productid + '\'' +
                ", type='" + type + '\'' +
                ", original='" + original + '\'' +
                ", display='" + display + '\'' +
                ", enabled=" + enabled +
                ", createtime=" + createtime +
                ", keyWord='" + keyWord + '\'' +
                ", page=" + page +
                '}';
    }
}