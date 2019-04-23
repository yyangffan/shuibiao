package com.jiayuan.shuibiao.entity;

import java.util.List;

public class CusServiceResultDto {

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

    public List<CusService> getReturnData() {
        return returnData;
    }

    public void setReturnData(List<CusService> returnData) {
        this.returnData = returnData;
    }

    private String result;
    private String msg;
    private List<CusService> returnData;
}
