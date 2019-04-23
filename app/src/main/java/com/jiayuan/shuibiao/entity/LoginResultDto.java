package com.jiayuan.shuibiao.entity;

import java.util.List;

public class LoginResultDto {


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

    public List<WarterEmployee> getReturnData() {
        return returnData;
    }

    public void setReturnData(List<WarterEmployee> returnData) {
        this.returnData = returnData;
    }

    private String result;
    private String msg;
    private List<WarterEmployee> returnData;
}
