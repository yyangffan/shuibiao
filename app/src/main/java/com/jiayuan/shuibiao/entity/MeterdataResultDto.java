package com.jiayuan.shuibiao.entity;


public class MeterdataResultDto {

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Meterdata getReturnData() {
		return returnData;
	}

	public void setReturnData(Meterdata returnData) {
		this.returnData = returnData;
	}

	private String result;
	private String msg;
	private Meterdata returnData;


}