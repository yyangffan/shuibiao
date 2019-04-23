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

import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.entity.SpinerBean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DayTaskFilterPopupWindow extends PopupWindow {

    private View contentView;
    private Context context;

    private TextView confirm;
    private TextView cancel;

    private EditText userNameOrWaterMeterId;
    private TextView completedFlag;
    private TextView completedFlagHidden;


    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private Callback callback;

    private SpinerPopWindow mSpinerPopWindow;

    private List<SpinerBean> list = new ArrayList<>();


    /**
     * 商品属性选择的popupwindow
     */
    public DayTaskFilterPopupWindow(final Activity context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.day_task_filter_popup, null);

        confirm = contentView.findViewById(R.id.confirm);
        cancel = contentView.findViewById(R.id.cancel);
        userNameOrWaterMeterId = contentView.findViewById(R.id.userNameOrWaterMeterId);
        completedFlag = contentView.findViewById(R.id.completedFlag);
        completedFlagHidden = contentView.findViewById(R.id.completedFlagHidden);

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
                if("0".equals(completedFlagHidden.getText().toString())){
                    map.put("completedFlag","");
                }else{
                    map.put("completedFlag",completedFlagHidden.getText().toString());
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
            completedFlagHidden.setText(list.get(position).getKey());
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
        void callback(Map<String,String> map);
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


    public static void fitPopupWindowOverStatusBar(PopupWindow mPopupWindow, boolean needFullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(needFullScreen);
                mLayoutInScreen.set(mPopupWindow, needFullScreen);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }



}
