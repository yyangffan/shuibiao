/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jiayuan.shuibiao.entity;

import java.util.Date;

public class Question{
	
	private static final long serialVersionUID = 1L;
	private String queId;		// que_id
	private String userId;		// user_id
	private String waterMeterId;		// water_meter_id
	private String planId;		// plan_id
	private String comment;		// comment
	private String queStatus;		// que_status
	private String queLevel;		// que_level
	private String deadlineTime;		// deadline_time
	private String orderTime;		// order_time
	private String feedbackType;		// feedback_type
	private String solve;		// solve
	private String result;		// result
	private String deleteFlag;		// delete_flag
	private String createUser;		// create_user
	private Date createTime;		// create_time
	private String updateUser;		// update_user
	private Date updateTime;		// update_time

	private String file1;
	private String file2;
	private String file3;
	private String file4;
	private String file5;
	private String file6;

	public String getFile1() {
		return file1;
	}

	public void setFile1(String file1) {
		this.file1 = file1;
	}

	public String getFile2() {
		return file2;
	}

	public void setFile2(String file2) {
		this.file2 = file2;
	}

	public String getFile3() {
		return file3;
	}

	public void setFile3(String file3) {
		this.file3 = file3;
	}

	public String getFile4() {
		return file4;
	}

	public void setFile4(String file4) {
		this.file4 = file4;
	}

	public String getFile5() {
		return file5;
	}

	public void setFile5(String file5) {
		this.file5 = file5;
	}

	public String getFile6() {
		return file6;
	}

	public void setFile6(String file6) {
		this.file6 = file6;
	}

	public Question() {

	}

	public String getQueId() {
		return queId;
	}

	public void setQueId(String queId) {
		this.queId = queId;
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
	
	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getQueStatus() {
		return queStatus;
	}

	public void setQueStatus(String queStatus) {
		this.queStatus = queStatus;
	}
	
	public String getQueLevel() {
		return queLevel;
	}

	public void setQueLevel(String queLevel) {
		this.queLevel = queLevel;
	}
	
	public String getDeadlineTime() {
		return deadlineTime;
	}

	public void setDeadlineTime(String deadlineTime) {
		this.deadlineTime = deadlineTime;
	}
	
	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	
	public String getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}
	
	public String getSolve() {
		return solve;
	}

	public void setSolve(String solve) {
		this.solve = solve;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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