package com.jiayuan.shuibiao.entity;


import java.util.Date;

public class Meterdata {
	
	private static final long serialVersionUID = 1L;
	private String meterId;		// meter_id
	private String planId;		// plan_id
	private String planSubId;		// plan_sub_id
	private String userId;		// user_id
	private String userName;		// user_name
	private String contractId;		// contract_id
	private String waterMeterId;		// water_meter_id
	private String address;		// address
	private Integer oldMeterNum;		// old_meter_num
	private Integer meterNum;		// meter_num
	private Integer meterPicNum;		// meter_pic_num
	private Integer meterRealNum;		// meter_real_num
	private Integer useNum;		// use_num
	private Integer yearUseNum;		// year_use_num
	private Integer changeTableNum;		// change_table_num
	private String lineId;		// line_id
	private String lineName;		// line_name
	private String empId;		// emp_id
	private String empName;		// emp_name
	private String meterType;		// meter_type
	private String meterDate;		// meter_date
	private String meterStatus;		// meter_status
	private String queId;		// que_id
	private String clockDialPicId;		// clock_dial_pic_id
	private String clockDialPicPath;		// clock_dial_pic_path
	private String waterMeterPicId;		// water_meter_pic_id
	private String waterMeterPicPath;		// water_meter_pic_path
	private String scenePicId;		// scene_pic_id
	private String scenePicPath;		// scene_pic_path
	private Integer overNum;		// over_num
	private String deleteFlag;		// delete_flag
	private String createUser;		// create_user
	private Date createTime;		// create_time
	private String updateUser;		// update_user
	private Date updateTime;		// update_time
	

	public Meterdata(String meterId, String planId, String planSubId, String userId, String userName){
		this.meterId = meterId;
		this.planId = planId;
		this.planSubId = planSubId;
		this.userId = userId;
		this.userName = userName;
	}
	
	public String getMeterId() {
		return meterId;
	}

	public void setMeterId(String meterId) {
		this.meterId = meterId;
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
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	
	public String getWaterMeterId() {
		return waterMeterId;
	}

	public void setWaterMeterId(String waterMeterId) {
		this.waterMeterId = waterMeterId;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public Integer getOldMeterNum() {
		return oldMeterNum;
	}

	public void setOldMeterNum(Integer oldMeterNum) {
		this.oldMeterNum = oldMeterNum;
	}
	
	public Integer getMeterNum() {
		return meterNum;
	}

	public void setMeterNum(Integer meterNum) {
		this.meterNum = meterNum;
	}
	
	public Integer getMeterPicNum() {
		return meterPicNum;
	}

	public void setMeterPicNum(Integer meterPicNum) {
		this.meterPicNum = meterPicNum;
	}
	
	public Integer getMeterRealNum() {
		return meterRealNum;
	}

	public void setMeterRealNum(Integer meterRealNum) {
		this.meterRealNum = meterRealNum;
	}
	
	public Integer getUseNum() {
		return useNum;
	}

	public void setUseNum(Integer useNum) {
		this.useNum = useNum;
	}
	
	public Integer getYearUseNum() {
		return yearUseNum;
	}

	public void setYearUseNum(Integer yearUseNum) {
		this.yearUseNum = yearUseNum;
	}
	
	public Integer getChangeTableNum() {
		return changeTableNum;
	}

	public void setChangeTableNum(Integer changeTableNum) {
		this.changeTableNum = changeTableNum;
	}
	
	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	
	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
	
	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}
	
	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}
	
	public String getMeterType() {
		return meterType;
	}

	public void setMeterType(String meterType) {
		this.meterType = meterType;
	}
	
	public String getMeterDate() {
		return meterDate;
	}

	public void setMeterDate(String meterDate) {
		this.meterDate = meterDate;
	}
	
	public String getMeterStatus() {
		return meterStatus;
	}

	public void setMeterStatus(String meterStatus) {
		this.meterStatus = meterStatus;
	}
	
	public String getQueId() {
		return queId;
	}

	public void setQueId(String queId) {
		this.queId = queId;
	}
	
	public String getClockDialPicId() {
		return clockDialPicId;
	}

	public void setClockDialPicId(String clockDialPicId) {
		this.clockDialPicId = clockDialPicId;
	}
	
	public String getClockDialPicPath() {
		return clockDialPicPath;
	}

	public void setClockDialPicPath(String clockDialPicPath) {
		this.clockDialPicPath = clockDialPicPath;
	}
	
	public String getWaterMeterPicId() {
		return waterMeterPicId;
	}

	public void setWaterMeterPicId(String waterMeterPicId) {
		this.waterMeterPicId = waterMeterPicId;
	}
	
	public String getWaterMeterPicPath() {
		return waterMeterPicPath;
	}

	public void setWaterMeterPicPath(String waterMeterPicPath) {
		this.waterMeterPicPath = waterMeterPicPath;
	}
	
	public String getScenePicId() {
		return scenePicId;
	}

	public void setScenePicId(String scenePicId) {
		this.scenePicId = scenePicId;
	}
	
	public String getScenePicPath() {
		return scenePicPath;
	}

	public void setScenePicPath(String scenePicPath) {
		this.scenePicPath = scenePicPath;
	}
	
	public Integer getOverNum() {
		return overNum;
	}

	public void setOverNum(Integer overNum) {
		this.overNum = overNum;
	}
	
	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}