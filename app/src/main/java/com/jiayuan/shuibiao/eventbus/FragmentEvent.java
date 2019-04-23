package com.jiayuan.shuibiao.eventbus;

/**
 * Fragment跳转
 */
public class FragmentEvent {

    private String msg;

    private String fragmentCode;


    public FragmentEvent(String msg,String fragmentCode){
        this.msg = msg;
        this.fragmentCode = fragmentCode;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFragmentCode() {
        return fragmentCode;
    }

    public void setFragmentCode(String fragmentCode) {
        this.fragmentCode = fragmentCode;
    }


}
