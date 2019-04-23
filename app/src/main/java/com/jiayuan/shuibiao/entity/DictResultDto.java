package com.jiayuan.shuibiao.entity;

import java.util.List;

public class DictResultDto {

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

    public List<DictData> getReturnData() {
        return returnData;
    }

    public void setReturnData(List<DictData> returnData) {
        this.returnData = returnData;
    }

    private String result;
    private String msg;
    private List<DictData> returnData;

}

