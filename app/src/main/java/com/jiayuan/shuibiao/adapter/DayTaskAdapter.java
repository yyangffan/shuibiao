package com.jiayuan.shuibiao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.entity.PlanVo;

import java.util.List;

public class DayTaskAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<PlanVo> taskList;

    private Context context;


    public DayTaskAdapter(List<PlanVo> taskList, Context context){
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
            convertView = mInflater.inflate(R.layout.day_task_list_item, null); //加载布局

            holder = new ViewHolder();
            holder.userId =  convertView.findViewById(R.id.userId);
            holder.userName =  convertView.findViewById(R.id.userName);
            holder.waterMeterId =  convertView.findViewById(R.id.waterMeterId);
            holder.address =  convertView.findViewById(R.id.address);
            holder.meterReading = convertView.findViewById(R.id.meterReading);

            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (ViewHolder) convertView.getTag();
        }

        PlanVo bean = taskList.get(position);
        holder.userId.setText(bean.getUserId());
        holder.userName.setText(bean.getUserName());
        holder.waterMeterId.setText(bean.getWaterMeterId());
        holder.address.setText(bean.getAddress());
        //计算距离

        //判断是否展示抄表按钮
        if("0".equals(bean.getCompletedFlag())){
            holder.meterReading.setVisibility(View.VISIBLE);
        }else{
            holder.meterReading.setVisibility(View.GONE);
        }

        return convertView;
    }

    //这个ViewHolder只能服务于当前这个特定的adapter，因为ViewHolder里会指定item的控件，不同的ListView，item可能不同，所以ViewHolder写成一个私有的类
    private class ViewHolder {
        TextView userId;
        TextView userName;
        TextView waterMeterId;
        TextView address;
        TextView meterReading;
    }
}
