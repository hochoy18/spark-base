package com.hochoy.cobub3_test;

import java.util.List;

public class GroupDataEntity {
    /**
     * 产品id
     */
    private String productId;

    /**
     * 创建人
     */
    private String userName;
    /**
     * 分群id
     */
    private String groupId;

    /**
     * 分群名称
     */
    private String groupName;

    /**
     * 创建方式
     */
    private String crateType;

    /**
     * 更新方式
     */
    private String modifyType;

    /**
     * id列表
     */
    private List<String> idList;

    /**
     * 前端查询json
     * @return
     */
    private String queryJson;

    public String getQueryJson() {
        return queryJson;
    }

    public void setQueryJson(String queryJson) {
        this.queryJson = queryJson;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCrateType() {
        return crateType;
    }

    public void setCrateType(String crateType) {
        this.crateType = crateType;
    }

    public String getModifyType() {
        return modifyType;
    }

    public void setModifyType(String modifyType) {
        this.modifyType = modifyType;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    @Override
    public String toString() {
        return "GroupDataEntity{" +
                "productId='" + productId + '\'' +
                ", userName='" + userName + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", crateType='" + crateType + '\'' +
                ", modifyType='" + modifyType + '\'' +
                ", idList=" + idList +
                ", queryJson='" + queryJson + '\'' +
                '}';
    }
}
