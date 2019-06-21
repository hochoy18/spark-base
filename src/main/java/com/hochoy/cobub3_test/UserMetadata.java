package com.hochoy.cobub3_test;

import java.util.Date;
import java.util.List;

/**
 * 元数据
 */
public class UserMetadata implements Comparable {
    private Long id;

    private String productid;

    private String type;

    private String original;

    private String display;

    private Short enabled;

    private Date createtime;

    private Date lastUpdateTime;

    private String keyWord;

    private Page page;

    private List<UserProperty> userPropertyList;

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

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
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

    public List<UserProperty> getUserPropertyList() {
        return userPropertyList;
    }

    public void setUserPropertyList(List<UserProperty> userPropertyList) {
        this.userPropertyList = userPropertyList;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int compareTo(Object o) {

        UserMetadata userMetadata = (UserMetadata) o;

        int productResult = userMetadata.getProductid().compareTo(productid);

        int metaTypeResult = userMetadata.getType().compareTo(type);

        int displayResult = userMetadata.getDisplay().compareTo(display);

        int originalResult = userMetadata.getOriginal().compareTo(original);

        return productResult == 0 && metaTypeResult == 0 && displayResult == 0 && originalResult == 0 ? 0 : 1;
    }

    @Override
    public String toString() {
        return "UserMetadata{" +
                "id=" + id +
                ", productid='" + productid + '\'' +
                ", type='" + type + '\'' +
                ", original='" + original + '\'' +
                ", display='" + display + '\'' +
                ", enabled=" + enabled +
                ", createtime=" + createtime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", keyWord='" + keyWord + '\'' +
                ", page=" + page +
                ", userPropertyList=" + userPropertyList +
                '}';
    }
}