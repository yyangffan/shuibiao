package com.jiayuan.shuibiao.entity;

public class PlanResultDto {

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

    public PlanListDto getReturnData() {
        return returnData;
    }

    public void setReturnData(PlanListDto returnData) {
        this.returnData = returnData;
    }

    private String result;
    private String msg;
    private PlanListDto returnData;


}
