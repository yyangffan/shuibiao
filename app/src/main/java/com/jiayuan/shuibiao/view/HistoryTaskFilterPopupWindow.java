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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
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


public class HistoryTaskFilterPopupWindow extends PopupWindow {

    private View contentView;
    private Context context;

    private TextView confirm;
    private TextView cancel;

    private EditText userNameOrWaterMeterId;
    private TextView completedFlag;
    private TextView startDate;
    private TextView endDate;
    private TextView copySituation;
    private TextView queStatus;


    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private Callback callback;

    //完成情况下拉框
    private SpinerPopWindow mSpinerPopWindow;
    private List<SpinerBean> list = new ArrayList<>();

    //抄准情况下拉框
    private SpinerPopWindow copySituationPopupWindow;
    private List<SpinerBean> copySituationList = new ArrayList<>();

    //问题水表下拉框
    private SpinerPopWindow queStatusPopupWindow;
    private List<SpinerBean> queStatusList = new ArrayList<>();

    private CustomDatePicker customDatePicker1;

    private CustomDatePicker customDatePicker2;

    /**
     * 月任务搜索弹出框
     */
    public HistoryTaskFilterPopupWindow(final Activity context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.history_task_filter_popup, null);

        confirm = contentView.findViewById(R.id.confirm);
        cancel = contentView.findViewById(R.id.cancel);
        userNameOrWaterMeterId = contentView.findViewById(R.id.userNameOrWaterMeterId);
        completedFlag = contentView.findViewById(R.id.completedFlag);
        copySituation = contentView.findViewById(R.id.copySituation);
        queStatus = contentView.findViewById(R.id.queStatus);
        startDate = contentView.findViewById(R.id.startDate);

        //日期选择控件
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDatePicker1.show(startDate.getText().toString());
            }
        });
        endDate = contentView.findViewById(R.id.endDate);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDatePicker2.show(endDate.getText().toString());
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        startDate.setText(now.split(" ")[0]);
        endDate.setText(now.split(" ")[0]);

        customDatePicker1 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                startDate.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2049-12-31 00:00");
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                endDate.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2049-12-31 00:00");
        customDatePicker2.showSpecificTime(false); // 不显示时和分
        customDatePicker2.setIsLoop(true); // 不允许循环滚动

        //完成情况
        list.add(new SpinerBean("0","全部"));
        list.add(new SpinerBean("1","未完成"));
        list.add(new SpinerBean("2","已完成"));

        mSpinerPopWindow = new SpinerPopWindow(context, list,itemClickListener);
        mSpinerPopWindow.setOnDismissListener(dismissListener);

        completedFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinerPopWindow.setWidth(completedFlag.getWidth());
                mSpinerPopWindow.showAsDropDown(completedFlag);
                setTextImage(R.drawable.icon_up);
            }
        });

        //抄准情况
        copySituationList.add(new SpinerBean("0","全部"));
        copySituationList.add(new SpinerBean("1","抄准"));
        copySituationList.add(new SpinerBean("2","抄错"));
        copySituationPopupWindow = new SpinerPopWindow(context, copySituationList,itemClickListener2);
        copySituationPopupWindow.setOnDismissListener(dismissListener);

        copySituation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copySituationPopupWindow.setWidth(copySituation.getWidth());
                copySituationPopupWindow.showAsDropDown(copySituation);
                setTextImage(R.drawable.icon_up);
            }
        });

        //问题水表
        queStatusList.add(new SpinerBean("0","全部"));
        queStatusList.add(new SpinerBean("1","未处理"));
        queStatusList.add(new SpinerBean("2","已处理"));
        queStatusPopupWindow = new SpinerPopWindow(context, queStatusList,itemClickListener3);
        queStatusPopupWindow.setOnDismissListener(dismissListener);

        queStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queStatusPopupWindow.setWidth(queStatus.getWidth());
                queStatusPopupWindow.showAsDropDown(queStatus);
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
                map.put("userNameOrWaterMeterId",userNameOrWaterMeterId.getText().toString());
                map.put("startDate",startDate.getText().toString().replace("-",""));
                map.put("endDate",endDate.getText().toString().replace("-",""));

                String completedFlagDesc = completedFlag.getText().toString();
                if(StringUtils.isEmpty(completedFlagDesc)||"全部".equals(completedFlagDesc)){
                    map.put("completedFlag","");
                }else if("未完成".equals(completedFlagDesc)){
                    map.put("completedFlag","1");
                }else if("已完成".equals(completedFlagDesc)){
                    map.put("completedFlag","2");
                }

                String copySituationDesc = copySituation.getText().toString();
                if(StringUtils.isEmpty(copySituationDesc)||"全部".equals(copySituationDesc)){
                    map.put("copySituation","");
                }else if("抄准".equals(copySituationDesc)){
                    map.put("copySituation","1");
                }else if("抄错".equals(copySituationDesc)){
                    map.put("copySituation","2");
                }

                String queStatusDesc = queStatus.getText().toString();
                if(StringUtils.isEmpty(queStatusDesc)||"全部".equals(queStatusDesc)){
                    map.put("queStatus","");
                }else if("未处理".equals(queStatusDesc)){
                    map.put("queStatus","1");
                }else if("已处理".equals(queStatusDesc)){
                    map.put("queStatus","2");
                }

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
            completedFlag.setText(list.get(position).getValue());
        }
    };

    /**
     * 抄准情况选择回调
     */
    private AdapterView.OnItemClickListener itemClickListener2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            copySituationPopupWindow.dismiss();
            copySituation.setText(copySituationList.get(position).getValue());
        }
    };

    /**
     * 问题水表选择回调
     */
    private AdapterView.OnItemClickListener itemClickListener3 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            queStatusPopupWindow.dismiss();
            queStatus.setText(queStatusList.get(position).getValue());
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
        completedFlag.setCompoundDrawables(null, null, drawable, null);
        copySituation.setCompoundDrawables(null, null, drawable, null);
        queStatus.setCompoundDrawables(null, null, drawable, null);
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
