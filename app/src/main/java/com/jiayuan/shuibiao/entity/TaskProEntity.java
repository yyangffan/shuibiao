package com.jiayuan.shuibiao.entity;

import java.util.List;

public class TaskProEntity {

    /**
     * result : 1
     * returnData : {"tasklist":[{"totalnum":34182,"num":72,"meterdate":"20190401"},{"totalnum":18,"num":0,"meterdate":"20190402"},{"totalnum":0,"num":0,"meterdate":"20190403"},{"totalnum":0,"num":0,"meterdate":"20190404"},{"totalnum":0,"num":0,"meterdate":"20190405"},{"totalnum":0,"num":0,"meterdate":"20190406"},{"totalnum":0,"num":0,"meterdate":"20190407"},{"totalnum":0,"num":0,"meterdate":"20190408"},{"totalnum":0,"num":0,"meterdate":"20190409"},{"totalnum":450,"num":18,"meterdate":"20190410"},{"totalnum":900,"num":9,"meterdate":"20190411"},{"totalnum":90,"num":9,"meterdate":"20190412"},{"totalnum":0,"num":0,"meterdate":"20190413"},{"totalnum":0,"num":0,"meterdate":"20190414"}]}
     */

    private String result;
    private ReturnDataBean returnData;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ReturnDataBean getReturnData() {
        return returnData;
    }

    public void setReturnData(ReturnDataBean returnData) {
        this.returnData = returnData;
    }

    public static class ReturnDataBean {
        private List<TasklistBean> tasklist;

        public List<TasklistBean> getTasklist() {
            return tasklist;
        }

        public void setTasklist(List<TasklistBean> tasklist) {
            this.tasklist = tasklist;
        }

        public static class TasklistBean {
            /**
             * totalnum : 34182
             * num : 72
             * meterdate : 20190401
             */

            private String totalnum;
            private String num;
            private String meterdate;

            public String getTotalnum() {
                return totalnum;
            }

            public void setTotalnum(String totalnum) {
                this.totalnum = totalnum;
            }

            public String getNum() {
                return num;
            }

            public void setNum(String num) {
                this.num = num;
            }

            public String getMeterdate() {
                return meterdate;
            }

            public void setMeterdate(String meterdate) {
                this.meterdate = meterdate;
            }
        }
    }
}
