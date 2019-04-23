package com.jiayuan.shuibiao.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 无需等候的Toast工具类
 * Created by gaoxz on 15/11/17.
 */
public class ToastUtil {
    private static Toast toast;

    public static void toast(Context context,String content){
        if(toast != null){
            toast.setText(content);
        }else{
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void toast(Context context,int resId){
        if(toast != null){
            toast.setText(context.getResources().getString(resId));
        }else{
            toast = Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
