package com.jiayuan.shuibiao.view;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.jiayuan.shuibiao.R;


/**
 * Created by luwt on 16/5/11.
 */
public class LoadingDialog extends Dialog {

    private boolean mCanCancel = true;

    private TextView messageTextView;


    public LoadingDialog(Context context) {
        this(context, true);
    }

    public LoadingDialog(Context context,boolean flag){
        super(context, R.style.LoadingDialogStyle);
        mCanCancel = flag;

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);//得到加载的view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_loading_view);
        ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.spin_kit);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);

        super.setCancelable(flag);
        super.setCanceledOnTouchOutside(flag);
        this.setContentView(layout);
        WindowManager.LayoutParams a = this.getWindow().getAttributes();
        a.dimAmount = 0.0f;
        this.getWindow().setAttributes(a);
    }

    /**
     * 创建对象时，传入参数
     * @param flag
     */
    @Override
    public void setCanceledOnTouchOutside(boolean flag){
        super.setCanceledOnTouchOutside(mCanCancel);
    }

    /**
     *
     * @param flag
     */
    @Override
    public void setCancelable(boolean flag){
       super.setCancelable(mCanCancel);
    }


    public void setMessage(String message){
        if(messageTextView != null){
            messageTextView.setText(message);
        }
    }

    public void setIndeterminate(boolean flag){
        
    }

    /**
     * @param cancelable 是否可以取消请求
     */
    public void show(boolean cancelable){
        super.setCancelable(cancelable);
        super.setCanceledOnTouchOutside(cancelable);
        try {//防止回调中调用，提前销毁Activity导致Window对象不存在
            super.show();
        } catch (Exception e) {
            LogUtils.e("页面销毁，loading动画不需要显示");
        }
    }

    @Override
    public void show(){
        super.setCancelable(true);
        super.setCanceledOnTouchOutside(true);
        try {//防止回调中调用，提前销毁Activity导致Window对象不存在
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
