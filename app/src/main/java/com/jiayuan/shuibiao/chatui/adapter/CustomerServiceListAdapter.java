package com.jiayuan.shuibiao.chatui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.entity.CusService;

import java.util.List;

public class CustomerServiceListAdapter extends BaseAdapter {


    private LayoutInflater mInflater;
    private List<CusService> cusServices;

    private Context context;

    public CustomerServiceListAdapter(List<CusService> cusServices, Context context){
        this.cusServices = cusServices;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cusServices.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.customer_service_list_item, null); //加载布局

            holder = new ViewHolder();
            holder.customerServiceTitle =  convertView.findViewById(R.id.customerServiceTitle);

            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (ViewHolder) convertView.getTag();
        }

        CusService bean = cusServices.get(position);
        holder.customerServiceTitle.setText(position+1+"."+bean.getServTitle());

        return convertView;
    }

    //这个ViewHolder只能服务于当前这个特定的adapter，因为ViewHolder里会指定item的控件，不同的ListView，item可能不同，所以ViewHolder写成一个私有的类
    private class ViewHolder {
        TextView customerServiceTitle;
    }
}
