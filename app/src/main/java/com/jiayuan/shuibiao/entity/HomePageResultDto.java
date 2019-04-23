package com.jiayuan.shuibiao.entity;

public class HomePageResultDto {

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

    public HomePageData getReturnData() {
        return returnData;
    }

    public void setReturnData(HomePageData returnData) {
        this.returnData = returnData;
    }

    private String result;
    private String msg;
    private HomePageData returnData;

}

