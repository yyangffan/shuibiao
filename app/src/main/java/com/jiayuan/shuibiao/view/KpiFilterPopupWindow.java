package com.jiayuan.shuibiao.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.entity.SpinerBean;
import com.jiayuan.shuibiao.view.datepicker.widget.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class KpiFilterPopupWindow extends PopupWindow {

    private View contentView;
    private Context context;

    private TextView confirm;
    private TextView cancel;

    private TextView empKpiType;
    private TextView empKpiTypeHidden;
    private TextView startMonth;
    private TextView endMonth;


    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private Callback callback;

    private SpinerPopWindow mSpinerPopWindow;


    public void setList(List<SpinerBean> list) {
        this.list.clear();
        this.list.addAll(list);
        mSpinerPopWindow.refreshList();
        if(list.size()>0){
            empKpiType.setText(list.get(0).getValue());
            empKpiTypeHidden.setText(list.get(0).getKey());
        }
    }

    private List<SpinerBean> list = new ArrayList<>();


    private CustomDatePicker customDatePicker1;

    private CustomDatePicker customDatePicker2;

    /**
     * kpi搜索弹出框
     */
    public KpiFilterPopupWindow(final Activity context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.kpi_filter_popup, null);

        confirm = contentView.findViewById(R.id.confirm);
        cancel = contentView.findViewById(R.id.cancel);
        empKpiType = contentView.findViewById(R.id.empKpiType);
        empKpiTypeHidden = contentView.findViewById(R.id.empKpiTypeHidden);
        startMonth = contentView.findViewById(R.id.startMonth);

        //日期选择控件
        startMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDatePicker1.show(startMonth.getText().toString());
            }
        });
        endMonth = contentView.findViewById(R.id.endMonth);
        endMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDatePicker2.show(endMonth.getText().toString());
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        startMonth.setText(now.split(" ")[0]);
        endMonth.setText(now.split(" ")[0]);

        customDatePicker1 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                startMonth.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2049-12-31 00:00");
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                endMonth.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2049-12-31 00:00");
        customDatePicker2.showSpecificTime(false); // 不显示时和分
        customDatePicker2.setIsLoop(true); // 不允许循环滚动

        mSpinerPopWindow = new SpinerPopWindow(context, list,itemClickListener);
        mSpinerPopWindow.setOnDismissListener(dismissListener);

        empKpiType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinerPopWindow.setWidth(empKpiType.getWidth());
                mSpinerPopWindow.showAsDropDown(empKpiType);
                setTextImage(R.drawable.icon_up);

            }
        });

        contentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return true;
            }
        });

        // 确定监听按钮，调用查询返回结果
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> map = new HashMap<>();
                map.put("empKpiType",empKpiTypeHidden.getText().toString());
                map.put("empKpiTypeLabel",empKpiType.getText().toString());
                map.put("startDate",startMonth.getText().toString().replace("-",""));
                map.put("endDate",endMonth.getText().toString().replace("-",""));
                callback.callback(map);
                dismiss();
            }
        });
        // 取消按钮，关闭查询框
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });

        this.setContentView(contentView);
        this.setWidth(ActionBar.LayoutParams.MATCH_PARENT);
        this.setHeight(ActionBar.LayoutParams.MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(00000000);
        this.setBackgroundDrawable(dw);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.update();

    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            mSpinerPopWindow.dismiss();
            empKpiType.setText(list.get(position).getValue());
            empKpiTypeHidden.setText(list.get(position).getKey());
        }
    };


    private OnDismissListener  dismissListener=new OnDismissListener() {
        @Override
        public void onDismiss() {
            setTextImage(R.drawable.icon_down);
        }
    };

    private void setTextImage(int resId) {
        Drawable drawable = context.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());// ��������ͼƬ��С��������ʾ
        empKpiType.setCompoundDrawables(null, null, drawable, null);
    }

    public class CancelOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    }

    public boolean onKeyDown(Context context, int keyCode, KeyEvent event) {
        this.context = context;
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
        }
        return true;
    }

    public void showFilterPopup(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent);
        } else {
            this.dismiss();
        }
    }

    public interface Callback{
        void callback(Map<String, String> map);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if(Build.VERSION.SDK_INT == 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if(Build.VERSION.SDK_INT == 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }

}
