package com.jiayuan.shuibiao.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.StringUtils;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.activity.ProblemProcessActivity;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.fragment.HistoryTaskListFragment;

import java.util.List;

public class HistoryTaskAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<PlanVo> taskList;

    private Context context;
    public LatLng getCurrLocation() {
        return currLocation;
    }

    public void setCurrLocation(LatLng currLocation) {
        this.currLocation = currLocation;
    }

    private LatLng currLocation;

    public HistoryTaskAdapter(List<PlanVo> taskList, Context context){
        this.taskList = taskList;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //这个方法才是重点，我们要为它编写一个ViewHolder
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.history_task_list_item, null); //加载布局

            holder = new ViewHolder();
            holder.userId =  convertView.findViewById(R.id.userId);
            holder.processFlag = convertView.findViewById(R.id.processFlag);
            holder.userName =  convertView.findViewById(R.id.userName);
            holder.waterMeterId =  convertView.findViewById(R.id.waterMeterId);
            holder.address =  convertView.findViewById(R.id.address);
            holder.process = convertView.findViewById(R.id.process);

            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (ViewHolder) convertView.getTag();
        }

        final PlanVo bean = taskList.get(position);
        holder.userId.setText(bean.getUserId());
        if(StringUtils.equals("1",bean.getQueStatus())){
            holder.processFlag.setText("已提交");
        }else if(StringUtils.equals("2",bean.getQueStatus())){
            holder.processFlag.setText("已反馈");
        }else if(StringUtils.equals("3",bean.getQueStatus())){
            holder.processFlag.setText("已处理");
        }else{
            holder.processFlag.setText("");
        }

        holder.userName.setText(bean.getUserName());
        holder.waterMeterId.setText(bean.getWaterMeterId());
        holder.address.setText(bean.getAddress());

        //判断是否展示处理按钮
        //状态为已反馈时，显示处理按钮
        if("2".equals(bean.getQueStatus())){
            holder.process.setVisibility(View.VISIBLE);
        }else{
            holder.process.setVisibility(View.GONE);
        }
        holder.process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ProblemProcessActivity.class);
                intent.putExtra("planVo",bean);
                ((Activity)context).startActivityForResult(
                        intent,HistoryTaskListFragment.REQUEST_QUESTION_PROCESS);
            }
        });
        return convertView;
    }

    //这个ViewHolder只能服务于当前这个特定的adapter，因为ViewHolder里会指定item的控件，不同的ListView，item可能不同，所以ViewHolder写成一个私有的类
    private class ViewHolder {
        TextView userId;
        TextView processFlag;
        TextView userName;
        TextView waterMeterId;
        TextView address;
        TextView process;
    }
}
