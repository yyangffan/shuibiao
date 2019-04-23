/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jiayuan.shuibiao.entity;

/**
 * 抄表人员Entity
 * @author zyl
 * @version 2018-11-19
 */
public class WarterEmployee {
	
	private static final long serialVersionUID = 1L;
	private String empId;		// emp_id
	private String empName;		// emp_name
	private String team;		// team
	private String sex;		// sex
	private Integer age;		// age
	private String empStatus;		// emp_status
	private String businessPlace;		// business_place
	private String meterTeam;		// meter_team
	private String phone;		// phone
	private String empNature;		// emp_nature
	private Integer historyAccuracyRate;		// history_accuracy_rate
	private Integer historyLookRate;		// history_look_rate
	private Integer historyArriveRate;		// history_arrive_rate
	private String comment;		// comment
	private String pwd;		// pwd
	private String isleader;		// isleader
	private String roleCode;		// role_code
	
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
	
	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	
	public String getEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
	}
	
	public String getBusinessPlace() {
		return businessPlace;
	}

	public void setBusinessPlace(String businessPlace) {
		this.businessPlace = businessPlace;
	}
	
	public String getMeterTeam() {
		return meterTeam;
	}

	public void setMeterTeam(String meterTeam) {
		this.meterTeam = meterTeam;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getEmpNature() {
		return empNature;
	}

	public void setEmpNature(String empNature) {
		this.empNature = empNature;
	}
	
	public Integer getHistoryAccuracyRate() {
		return historyAccuracyRate;
	}

	public void setHistoryAccuracyRate(Integer historyAccuracyRate) {
		this.historyAccuracyRate = historyAccuracyRate;
	}
	
	public Integer getHistoryLookRate() {
		return historyLookRate;
	}

	public void setHistoryLookRate(Integer historyLookRate) {
		this.historyLookRate = historyLookRate;
	}
	
	public Integer getHistoryArriveRate() {
		return historyArriveRate;
	}

	public void setHistoryArriveRate(Integer historyArriveRate) {
		this.historyArriveRate = historyArriveRate;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String getIsleader() {
		return isleader;
	}

	public void setIsleader(String isleader) {
		this.isleader = isleader;
	}
	
	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
}