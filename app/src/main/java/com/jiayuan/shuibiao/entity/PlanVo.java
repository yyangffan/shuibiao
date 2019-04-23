package com.jiayuan.shuibiao.entity;

import java.io.Serializable;

public class PlanVo implements Serializable {
    private String planId;
    private String planSubId;
    private String userId;
    private String address;
    private String userName;
    private String waterMeterId;

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    private String completedFlag;
    private String queStatus;
    private String phone;
    private String userType;
    private String changeMeterFlg;
    private String meterNum;
    private String longitude;
    private String latitude;
    private String meterId;

    private String lastMonthUserNum;
    private String beforeLastMonthUseNum;

    private String queId;

    private String clockDialPic;

    private String waterMeterPic;

    private String scenePic;

    public String getClockDialPic() {
        return clockDialPic;
    }

    public void setClockDialPic(String clockDialPic) {
        this.clockDialPic = clockDialPic;
    }

    public String getWaterMeterPic() {
        return waterMeterPic;
    }

    public void setWaterMeterPic(String waterMeterPic) {
        this.waterMeterPic = waterMeterPic;
    }

    public String getScenePic() {
        return scenePic;
    }

    public void setScenePic(String scenePic) {
        this.scenePic = scenePic;
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
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getWaterMeterId() {
        return waterMeterId;
    }
    public void setWaterMeterId(String waterMeterId) {
        this.waterMeterId = waterMeterId;
    }


    public String getCompletedFlag() {
        return completedFlag;
    }
    public void setCompletedFlag(String completedFlag) {
        this.completedFlag = completedFlag;
    }
    public String getQueStatus() {
        return queStatus;
    }
    public void setQueStatus(String queStatus) {
        this.queStatus = queStatus;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getChangeMeterFlg() {
        return changeMeterFlg;
    }
    public void setChangeMeterFlg(String changeMeterFlg) {
        this.changeMeterFlg = changeMeterFlg;
    }
    public String getMeterNum() {
        return meterNum;
    }
    public void setMeterNum(String meterNum) {
        this.meterNum = meterNum;
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
    public String getQueId() {
        return queId;
    }
    public void setQueId(String queId) {
        this.queId = queId;
    }
    public String getLastMonthUserNum() {
        return lastMonthUserNum;
    }
    public void setLastMonthUserNum(String lastMonthUserNum) {
        this.lastMonthUserNum = lastMonthUserNum;
    }
    public String getBeforeLastMonthUseNum() {
        return beforeLastMonthUseNum;
    }
    public void setBeforeLastMonthUseNum(String beforeLastMonthUseNum) {
        this.beforeLastMonthUseNum = beforeLastMonthUseNum;
    }



}
