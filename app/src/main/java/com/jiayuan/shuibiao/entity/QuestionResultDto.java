package com.jiayuan.shuibiao.entity;

public class QuestionResultDto {

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

    public Question getReturnData() {
        return returnData;
    }

    public void setReturnData(Question returnData) {
        this.returnData = returnData;
    }

    private String result;
    private String msg;
    private Question returnData;


}
