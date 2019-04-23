package com.jiayuan.shuibiao.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBNTTSManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.blankj.utilcode.util.LogUtils;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.guide.NormalUtils;
import com.jiayuan.shuibiao.overlayutil.BikingRouteOverlay;
import com.jiayuan.shuibiao.overlayutil.DrivingRouteOverlay;
import com.jiayuan.shuibiao.overlayutil.OverlayManager;
import com.jiayuan.shuibiao.overlayutil.WalkingRouteOverlay;
import com.jiayuan.shuibiao.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jiayuan.shuibiao.activity.GuideActivity.ROUTE_PLAN_NODE;

/**
 * 此demo用来展示如何进行驾车、步行、公交、骑行、跨城综合路线搜索并在地图使用RouteOverlay、TransitOverlay绘制
 * 同时展示如何进行节点浏览并弹出泡泡
 */
public class RoutePlanActivity extends BaseActivity implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener {

    private static final String APP_FOLDER_NAME = "SHUIBIAO";


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.drive)
    TextView drive;
    @BindView(R.id.bike)
    TextView bike;
    @BindView(R.id.walk)
    TextView walk;
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.navButton)
    LinearLayout navButton;

    //百度地图
    BaiduMap baiduMap;
    //搜索服务
    RoutePlanSearch search;

    RouteLine route = null;

    OverlayManager routeOverlay = null;

    //导航类型 0驾车  1自行车  2步行
    int nowSearchType = 0;

    private String mSDCardPath = null;
    private boolean hasInitSuccess = false;

    private static final String[] authBaseArr = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int authBaseRequestCode = 1;

    private BNRoutePlanNode mStartNode = null;

    //跳转传入数据
    private PlanVo task;
    private LatLng currLocation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_plan);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        initParms(intent.getExtras());

//        ZoomControls zoomControls = (ZoomControls) map.getChildAt(2);
        map.getChildAt(2).setPadding(0,0,0,150);
        //mapView.removeViewAt(2);
        //调整缩放控件的位置
//        zoomControls.setPadding(0, 0, 0, 100);

        baiduMap = map.getMap();
        baiduMap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        search = RoutePlanSearch.newInstance();
        search.setOnGetRoutePlanResultListener(this);

        searchProcess();

        if (initDirs()) {
            initNavi();
        }
    }

    /**
     * 路径规划
     */
    public void searchProcess() {
        route = null;
        baiduMap.clear();
        // 处理搜索按钮响应
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withLocation(currLocation);
        PlanNode enNode = PlanNode.withLocation(
                new LatLng(Double.parseDouble(task.getLatitude()),
                        Double.parseDouble(task.getLongitude())));

        //驾车导航
        if (nowSearchType == 0) {
            search.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode).to(enNode));
        } else if (nowSearchType == 1) {
            search.bikingSearch((new BikingRoutePlanOption())
                    .from(stNode).to(enNode));
        } else if (nowSearchType == 2) {
            search.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode).to(enNode));
        }
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initNavi() {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }

        BaiduNaviManagerFactory.getBaiduNaviManager().init(this,
                mSDCardPath, APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String result;
                        if (0 == status) {
                            result = "key校验成功!";
                            // 初始化tts
                            initTTS();
                        } else {
                            result = "key校验失败, " + msg;
                        }
                        ToastUtil.toast(RoutePlanActivity.this,result);
                    }

                    @Override
                    public void initStart() {
                        ToastUtil.toast(RoutePlanActivity.this,"百度导航引擎初始化开始");
                    }

                    @Override
                    public void initSuccess() {
                        ToastUtil.toast(RoutePlanActivity.this,"百度导航引擎初始化成功");
                        hasInitSuccess = true;

                    }

                    @Override
                    public void initFailed() {
                        ToastUtil.toast(RoutePlanActivity.this,"百度导航引擎初始化失败");
                    }
                });

    }

    private void initTTS() {
        // 使用内置TTS
        BaiduNaviManagerFactory.getTTSManager().initTTS(getApplicationContext(),
                getSdcardDir(), APP_FOLDER_NAME, NormalUtils.getTTSAppID());

        // 不使用内置TTS
//         BaiduNaviManagerFactory.getTTSManager().initTTS(mTTSCallback);

        // 注册同步内置tts状态回调
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedListener(
                new IBNTTSManager.IOnTTSPlayStateChangedListener() {
                    @Override
                    public void onPlayStart() {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayStart");
                    }

                    @Override
                    public void onPlayEnd(String speechId) {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayEnd");
                    }

                    @Override
                    public void onPlayError(int code, String message) {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayError");
                    }
                }
        );

        // 注册内置tts 异步状态消息
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedHandler(
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.e("BNSDKDemo", "ttsHandler.msg.what=" + msg.what);
                    }
                }
        );
    }

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void initParms(Bundle parms) {
        task = (PlanVo) parms.getSerializable("task");
        currLocation = parms.getParcelable("currLocation");
    }

    @OnClick({R.id.back, R.id.drive, R.id.bike, R.id.walk, R.id.navButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.drive:
                nowSearchType = 0;
                searchProcess();
                break;
            case R.id.bike:
                nowSearchType = 1;
                searchProcess();
                break;
            case R.id.walk:
                nowSearchType = 2;
                searchProcess();
                break;
            case R.id.navButton:
//                if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                    routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
//                }
                break;
        }
    }

    private void routeplanToNavi(final int coType) {
//        if (!hasInitSuccess) {
//            ToastUtil.toast(RoutePlanActivity.this,"还未初始化");
//        }

        BNRoutePlanNode sNode = new BNRoutePlanNode(
                currLocation.longitude, currLocation.latitude, "我的位置", "我的位置", coType);
        BNRoutePlanNode eNode = new BNRoutePlanNode(
                Double.parseDouble(task.getLongitude()), Double.parseDouble(task.getLatitude()), task.getUserName(), task.getAddress(), coType);
        mStartNode = sNode;
        List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
        list.add(sNode);
        list.add(eNode);

        BaiduNaviManagerFactory.getRoutePlanManager().routeplanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                ToastUtil.toast(RoutePlanActivity.this,"");
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                ToastUtil.toast(RoutePlanActivity.this,"算路成功");
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                ToastUtil.toast(RoutePlanActivity.this,"算路失败");
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                ToastUtil.toast(RoutePlanActivity.this,"算路成功准备进入导航");
                                Intent intent = new Intent(RoutePlanActivity.this,
                                        GuideActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ROUTE_PLAN_NODE, mStartNode);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    Toast.makeText(RoutePlanActivity.this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initNavi();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    /**
     * 驾车导航回调
     *
     * @param result
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.getRouteLines() == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (result.getRouteLines().size() > 0) {
                route = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
                routeOverlay = overlay;
                baiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            } else {
                LogUtils.e("未返回导航数据");
            }
        }
    }

    /**
     * 骑行回调
     *
     * @param result
     */
    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        if (result == null || result.getRouteLines() == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            if (result.getRouteLines().size() > 0) {
                route = result.getRouteLines().get(0);
                BikingRouteOverlay overlay = new BikingRouteOverlay(baiduMap);
                routeOverlay = overlay;
                baiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            } else {
                Log.d("route result", "结果数<0");
                return;
            }
        }
    }

    /**
     * 步行回调
     *
     * @param result
     */
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.getRouteLines() == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }

        if (result.getRouteLines().size() > 0) {
            // 直接显示
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
            baiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        } else {
            Log.d("route result", "结果数<0");
            return;
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }


    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return 0;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected void onPause() {
        map.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        map.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (search != null) {
            search.destroy();
        }
        map.onDestroy();
        super.onDestroy();
    }


}
