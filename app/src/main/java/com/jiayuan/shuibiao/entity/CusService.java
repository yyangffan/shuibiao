/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jiayuan.shuibiao.entity;

import java.util.Date;

/**
 * 客服信息表Entity
 * @author zyl
 * @version 2018-11-06
 */
public class CusService{
	
	private static final long serialVersionUID = 1L;
	private String servId;		// 序号
	private String servTitle;		// 标题
	private String servContent;		// 内容
	private String createUser;		// 创建人
	private Date createTime;		// 创建时间
	private String updateUser;		// 修改人
	private Date updateTime;		// 修改时间
	
	public CusService() {
	}

	public String getServId() {
		return servId;
	}

	public void setServId(String servId) {
		this.servId = servId;
	}

	public String getServTitle() {
		return servTitle;
	}

	public void setServTitle(String servTitle) {
		this.servTitle = servTitle;
	}

	public String getServContent() {
		return servContent;
	}

	public void setServContent(String servContent) {
		this.servContent = servContent;
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