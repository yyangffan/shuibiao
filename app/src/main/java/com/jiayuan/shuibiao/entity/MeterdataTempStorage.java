package com.jiayuan.shuibiao.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 抄表数据提交暂存
 */
@Entity(
        nameInDb = "METERDATA_TEMP_STORAGE",
        createInDb = false,
        indexes = {
        @Index(value = "planId DESC, planSubId DESC, userId DESC, waterMeterId DESC", unique = true)
})
public class MeterdataTempStorage{

    @Id(autoincrement = true)
    private Long id;

    private String planId;
    private String planSubId;
    private String meterId;
    private String userId;
    private String waterMeterId;
    private String meterNum;
    private String clockDialPic;
    private String waterMeterPic;
    private String scenePic;
@Generated(hash = 1345552157)
public MeterdataTempStorage(Long id, String planId, String planSubId, String meterId, String userId,
        String waterMeterId, String meterNum, String clockDialPic, String waterMeterPic,
        String scenePic) {
    this.id = id;
    this.planId = planId;
    this.planSubId = planSubId;
    this.meterId = meterId;
    this.userId = userId;
    this.waterMeterId = waterMeterId;
    this.meterNum = meterNum;
    this.clockDialPic = clockDialPic;
    this.waterMeterPic = waterMeterPic;
    this.scenePic = scenePic;
}
@Generated(hash = 1609544927)
public MeterdataTempStorage() {
}
public String getPlanId() {
    return this.planId;
}
public void setPlanId(String planId) {
    this.planId = planId;
}
public String getPlanSubId() {
    return this.planSubId;
}
public void setPlanSubId(String planSubId) {
    this.planSubId = planSubId;
}
public String getMeterId() {
    return this.meterId;
}
public void setMeterId(String meterId) {
    this.meterId = meterId;
}
public String getMeterNum() {
    return this.meterNum;
}
public void setMeterNum(String meterNum) {
    this.meterNum = meterNum;
}
public String getClockDialPic() {
    return this.clockDialPic;
}
public void setClockDialPic(String clockDialPic) {
    this.clockDialPic = clockDialPic;
}
public String getWaterMeterPic() {
    return this.waterMeterPic;
}
public void setWaterMeterPic(String waterMeterPic) {
    this.waterMeterPic = waterMeterPic;
}
public String getScenePic() {
    return this.scenePic;
}
public void setScenePic(String scenePic) {
    this.scenePic = scenePic;
}
public String getUserId() {
    return this.userId;
}
public void setUserId(String userId) {
    this.userId = userId;
}
public String getWaterMeterId() {
    return this.waterMeterId;
}
public void setWaterMeterId(String waterMeterId) {
    this.waterMeterId = waterMeterId;
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}


}
