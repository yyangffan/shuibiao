package com.jiayuan.shuibiao.entity;

public class PlanDetailDto {

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

    public PlanVo getReturnData() {
        return returnData;
    }

    public void setReturnData(PlanVo returnData) {
        this.returnData = returnData;
    }

    private String result;
    private String msg;
    private PlanVo returnData;


}
