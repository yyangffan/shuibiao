package com.jiayuan.shuibiao.entity;

import java.io.Serializable;

public class Task implements Serializable {


    private String planId;

    private String planSubId;

    private String userId;

    private String waterMeterId;

    private String userName;

    private String address;

    private String completedFlag;

    //经度
    private String longitude;
    //纬度
    private String latitude;


    public Task(){}

    public Task(String planId, String planSubId, String userId, String waterMeterId, String userName,
                String address, String completedFlag, String longitude, String latitude) {
        this.planId = planId;
        this.planSubId = planSubId;
        this.userId = userId;
        this.waterMeterId = waterMeterId;
        this.userName = userName;
        this.address = address;
        this.completedFlag = completedFlag;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanSubId() {
        return planSubId;
    }

    public void setPlanSubId(String planSubId) {
        this.planSubId = planSubId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWaterMeterId() {
        return waterMeterId;
    }

    public void setWaterMeterId(String waterMeterId) {
        this.waterMeterId = waterMeterId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompletedFlag() {
        return completedFlag;
    }

    public void setCompletedFlag(String completedFlag) {
        this.completedFlag = completedFlag;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }



}
