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
import com.baidu.mapapi.utils.DistanceUtil;
import com.blankj.utilcode.util.StringUtils;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.activity.DayTaskListMapActivity;
import com.jiayuan.shuibiao.activity.EditTaskDetailActivity;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.util.ToastUtil;

import java.text.DecimalFormat;
import java.util.List;

public class DayTaskMapAdapter extends BaseAdapter {


    private LayoutInflater mInflater;
    private List<PlanVo> taskList;

    private Context context;

    DecimalFormat df = new DecimalFormat("######0.00");

    public LatLng getCurrLocation() {
        return currLocation;
    }

    public void setCurrLocation(LatLng currLocation) {
        this.currLocation = currLocation;
    }

    private LatLng currLocation;

    public DayTaskMapAdapter(List<PlanVo> taskList, Context context){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.day_task_list_map_item, parent, false); //加载布局
            holder = new ViewHolder();

            holder.userId =  convertView.findViewById(R.id.userId);
            holder.userName =  convertView.findViewById(R.id.userName);
            holder.waterMeterId =  convertView.findViewById(R.id.waterMeterId);
            holder.address =  convertView.findViewById(R.id.address);
            holder.distance = convertView.findViewById(R.id.distince);
            holder.meterReading = convertView.findViewById(R.id.meterReading);

            //跳转抄表页面
            holder.meterReading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.toast(context,taskList.get(position).getAddress());
                }
            });

            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (ViewHolder) convertView.getTag();
        }

        final PlanVo bean = taskList.get(position);
        holder.userId.setText(bean.getUserId());
        holder.userName.setText(bean.getUserName());
        holder.waterMeterId.setText(bean.getWaterMeterId());
        holder.address.setText(bean.getAddress());
        //计算距离
        String latitude = bean.getLatitude();
        String longitude = bean.getLongitude();

        if(currLocation!=null &&
                !StringUtils.isEmpty(latitude) &&
                !StringUtils.isEmpty(longitude)){
            double distance = DistanceUtil.getDistance(currLocation,
                    new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
            holder.distance.setText(df.format(distance/1000)+"km");
        }
        //判断是否展示抄表按钮
        if("0".equals(bean.getCompletedFlag())){
            holder.meterReading.setVisibility(View.VISIBLE);
        }else{
            holder.meterReading.setVisibility(View.GONE);
        }
        holder.meterReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EditTaskDetailActivity.class);
                intent.putExtra("planVo",bean);
                ((Activity)context).startActivityForResult(
                        intent,DayTaskListMapActivity.REQUEST_EDIT_TASK);
            }
        });

        return convertView;
    }

    //这个ViewHolder只能服务于当前这个特定的adapter，因为ViewHolder里会指定item的控件，
    // 不同的ListView，item可能不同，所以ViewHolder写成一个私有的类
    private class ViewHolder {
        TextView userId;
        TextView userName;
        TextView waterMeterId;
        TextView address;
        TextView distance;
        TextView meterReading;
    }





}
