package com.jiayuan.shuibiao;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.jiayuan.shuibiao.greendao.GreenDaoManager;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 */
public class App extends MultiDexApplication {

    private static App app;

    private static List<Activity> activityList = new LinkedList<Activity>();

    /**
     * 屏幕宽度
     */
    public static int screenWidth;
    /**
     * 屏幕高度
     */
    public static int screenHeight;
    /**
     * 屏幕密度
     */
    public static float screenDensity;


    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * setApiKey是静态方法,内部引用了Context，建议放在Application中
         * 如果你在meta-data中配置了key，那么以meta-data中的为准，此行代码
         * 可以忽略，这个方法主要是为那些不想在xml里配置key的用户使用。
         * **/
//        AMapNavi.setApiKey(this, "你的KEY");
        //百度地图初始化
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        app = this;

        //初始化greendao
        GreenDaoManager.getInstance();

        initOkhttpClient();

        initScreenSize();
    }


    public static App getInstance(){
        return app;
    }

    private void initOkhttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(60000L, TimeUnit.MILLISECONDS) //链接超时
            .readTimeout(60000L,TimeUnit.MILLISECONDS) //读取超时
            .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    public void addActivity(Activity activity)  {
        activityList.add(activity);
    }

    public void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    public void exitAllActivity(){
        for(Activity activity:activityList) {
            activity.finish();
        }
        activityList.clear();
    }

    /**
     * 初始化当前设备屏幕宽高
     */
    private void initScreenSize() {
        DisplayMetrics curMetrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = curMetrics.widthPixels;
        screenHeight = curMetrics.heightPixels;
        screenDensity = curMetrics.density;
    }
}
