package com.jiayuan.shuibiao.entity;

import java.util.List;

public class PlanListDto {

    private String taskTotalCnt;

    //总数
    private String totalCnt;

    public String getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(String totalCnt) {
        this.totalCnt = totalCnt;
    }

    private List<PlanVo> tasklist;

    public String getTaskTotalCnt() {
        return taskTotalCnt;
    }

    public void setTaskTotalCnt(String taskTotalCnt) {
        this.taskTotalCnt = taskTotalCnt;
    }

    public List<PlanVo> getTasklist() {
        return tasklist;
    }

    public void setTasklist(List<PlanVo> tasklist) {
        this.tasklist = tasklist;
    }
}
