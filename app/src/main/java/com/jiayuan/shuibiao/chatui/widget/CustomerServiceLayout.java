package com.jiayuan.shuibiao.chatui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.chatui.adapter.CustomerServiceListAdapter;
import com.jiayuan.shuibiao.entity.CusService;

import java.util.List;

public class CustomerServiceLayout extends LinearLayout {

    private List<CusService> cusServices;

    CustomerServiceListAdapter customerServiceListAdapter;

    public List<CusService> getCusServices() {
        return cusServices;
    }

    public void setCusServices(List<CusService> cusServices) {
        customerServiceListAdapter =
                new CustomerServiceListAdapter(cusServices, context);
        listView.setAdapter(customerServiceListAdapter);
        setListViewHeightBasedOnChildren(listView);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = dp2px(context,80 )
                + listView.getLayoutParams().height;
        this.setLayoutParams(params);
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    private ListView listView;

    private TextView customerServiceTip;

    private Context context;

    public CustomerServiceLayout(Context context) {
        super(context);
    }

    public CustomerServiceLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_customer_service_list, this);
        listView = findViewById(R.id.customerServiceList);
        customerServiceTip = findViewById(R.id.customerServiceTip);
    }

    public CustomerServiceLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();
            // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}