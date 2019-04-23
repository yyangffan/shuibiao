package com.jiayuan.shuibiao.guide;

import android.app.Activity;
import android.content.Intent;

public class NormalUtils {

    public static void gotoSettings(Activity activity) {
        Intent it = new Intent(activity, NaviSettingActivity.class);
        activity.startActivity(it);
    }

    public static String getTTSAppID() {
        return "14757366";
    }
}
