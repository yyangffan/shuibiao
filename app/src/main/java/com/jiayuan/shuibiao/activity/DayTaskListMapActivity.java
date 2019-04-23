package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.adapter.DayTaskMapAdapter;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.PlanListDto;
import com.jiayuan.shuibiao.entity.PlanResultDto;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.entity.SpinerBean;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.PreferencesUtil;
import com.jiayuan.shuibiao.util.ScreenUtil;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.jiayuan.shuibiao.view.LoadListView;
import com.jiayuan.shuibiao.view.SpinerPopWindow;
import com.yinglan.scrolllayout.ScrollLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class DayTaskListMapActivity extends BaseActivity
        implements LoadListView.IloadListener {

    @BindView(R.id.main)
    View main;

    @BindView(R.id.mapView)
    MapView mapView;

    BaiduMap baiduMap;
    @BindView(R.id.text_foot)
    TextView textFoot;
    @BindView(R.id.scroll_down_layout)
    ScrollLayout scrollDownLayout;
    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.searchBtn)
    ImageView searchBtn;
    @BindView(R.id.listview)
    LoadListView listview;

    //底部导航相关UI
    @BindView(R.id.navButton)
    LinearLayout navButton;
    @BindView(R.id.userId)
    TextView userId;
    @BindView(R.id.close)
    TextView close;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.waterMeterId)
    TextView waterMeterId;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.navLayout)
    LinearLayout navLayout;
    @BindView(R.id.distance)
    TextView distance;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    //查询条件部分
    @BindView(R.id.popup_goods_noview)
    View popupGoodsNoview;
    @BindView(R.id.completedFlag)
    TextView completedFlag;
    @BindView(R.id.completedFlagHidden)
    TextView completedFlagHidden;
    @BindView(R.id.userNameOrWaterMeterId)
    EditText userNameOrWaterMeterId;
    @BindView(R.id.confirm)
    TextView confirm;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.ll_popup)
    LinearLayout llPopup;

    DayTaskMapAdapter dayTaskMapAdapter;

    private static final String GET_TASK_URL = "/plan/queryPlan";

    private int currPage = 1;

    private int pageSize = 25;

    private int taskTotalCnt = 0;

    private int currPosition = -1;

    //刷新flag  1  初始化请求或者下拉刷新  2  加载更多
    private int loadFlag = 1;

    //任务
    List<PlanVo> taskList = new ArrayList<>();
    //用户坐标
    List<LatLng> latLngList = new ArrayList<>();
    //绘制坐标点集合
    List<OverlayOptions> overlayOptionsList = new ArrayList<>();
    //定位坐标
    LatLng currLocation;

    DecimalFormat df = new DecimalFormat("######0.00");

    BitmapDescriptor bitmap = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);

    private Map<String, String> paramMap = new HashMap<String, String>();

    String empId;

    private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
                float precent = 255 * currentProgress;
                if (precent > 255) {
                    precent = 255;
                } else if (precent < 0) {
                    precent = 0;
                }
                scrollDownLayout.getBackground().setAlpha(255 - (int) precent);
            }
            if (textFoot.getVisibility() == View.VISIBLE)
                textFoot.setVisibility(View.GONE);
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                textFoot.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onChildScroll(int top) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_task_list_map);
        empId =  PreferencesUtil.readPreference(this, Constant.EMPID);
        ButterKnife.bind(this);
        baiduMap = mapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        baiduMap.setMapStatus(msu);

        /**设置 setting*/
        scrollDownLayout.setMinOffset(ScreenUtil.dip2px(this, 60));
        scrollDownLayout.setMaxOffset((int) (ScreenUtil.getScreenHeight(this) * 0.5));
        scrollDownLayout.setExitOffset(ScreenUtil.dip2px(this, 50));
        scrollDownLayout.setIsSupportExit(true);
        scrollDownLayout.setAllowHorizontalScroll(true);
        scrollDownLayout.setOnScrollChangedListener(mOnScrollChangedListener);
        scrollDownLayout.setToExit();
        scrollDownLayout.getBackground().setAlpha(0);

        dayTaskMapAdapter = new DayTaskMapAdapter(taskList, this);
        listview.setAdapter(dayTaskMapAdapter);
        listview.setInterface(this);

        //行点击
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlanVo task = taskList.get(position);
                userId.setText(task.getUserId());
                userName.setText(task.getUserName());
                waterMeterId.setText(task.getWaterMeterId());
                address.setText(task.getAddress());
                currPosition = position;

                //判断是否有经纬度信息，如果没有经纬度信息，则隐藏导航按钮
                if(!StringUtils.isEmpty(task.getLatitude())
                        && !StringUtils.isEmpty(task.getLongitude())){
                    navButton.setVisibility(View.VISIBLE);
                    double distanceDouble = DistanceUtil.getDistance(currLocation,
                            new LatLng(Double.parseDouble(task.getLatitude()),
                                    Double.parseDouble(task.getLongitude())));
                    distance.setText(df.format(distanceDouble/1000)+"km");
                }else{
                    navButton.setVisibility(View.GONE);
                }

                navLayout.setVisibility(View.VISIBLE);
                scrollDownLayout.setVisibility(View.GONE);
            }
        });

        initSearchView();
        initLocationOption();
        getData();

        showProgress(false);
    }

    private SpinerPopWindow mSpinerPopWindow;

    private List<SpinerBean> list = new ArrayList<>();

    //初始化查询数据
    private void initSearchView() {
        //搜索点击点击监控
        DrawerLayout.SimpleDrawerListener simpleDrawerListener = new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };
        drawerLayout.addDrawerListener(simpleDrawerListener);


        list.clear();
        list.add(new SpinerBean("0","全部"));
        list.add(new SpinerBean("1","未完成"));
        list.add(new SpinerBean("2","已完成"));

        mSpinerPopWindow = new SpinerPopWindow(this, list,itemClickListener);
        mSpinerPopWindow.setOnDismissListener(dismissListener);

        completedFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinerPopWindow.setWidth(completedFlag.getWidth());
                mSpinerPopWindow.showAsDropDown(completedFlag);
                setTextImage(R.drawable.icon_up);

            }
        });
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            mSpinerPopWindow.dismiss();
            completedFlag.setText(list.get(position).getValue());
            completedFlagHidden.setText(list.get(position).getKey());
        }
    };

    private PopupWindow.OnDismissListener dismissListener=new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            setTextImage(R.drawable.icon_down);
        }
    };

    private void setTextImage(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());// ��������ͼƬ��С��������ʾ
        completedFlag.setCompoundDrawables(null, null, drawable, null);
    }
    //查询条件部分结束

    /**
     * 获取列表数据
     */
    private void getData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("empId", empId);
        jsonObject.addProperty("completedFlag", paramMap.get("completedFlag"));
        jsonObject.addProperty("userNameOrWaterMeterId",paramMap.get("userNameOrWaterMeterId"));
        jsonObject.addProperty("startDate",paramMap.get("startDate"));
        jsonObject.addProperty("endDate",paramMap.get("endDate"));
        jsonObject.addProperty("copySituation",paramMap.get("copySituation"));
        jsonObject.addProperty("queStatus",paramMap.get("queStatus"));
        jsonObject.addProperty("currPage", currPage);
        jsonObject.addProperty("pageSize", pageSize);
        jsonObject.addProperty("playType", "1");
        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + GET_TASK_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(DayTaskListMapActivity.this, "请求异常，请稍后重试！");
                        hideProgress();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try{
                            Gson gson = new Gson();
                            PlanResultDto resultDto = gson.fromJson(response, PlanResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                PlanListDto planListDto = resultDto.getReturnData();
                                taskTotalCnt = Integer.parseInt(planListDto.getTaskTotalCnt());

                                if(planListDto.getTasklist()==null || planListDto.getTasklist().size()==0){
                                    if(loadFlag==1){
                                        taskList.clear();
                                        dayTaskMapAdapter.notifyDataSetChanged();
                                        listview.loadComplete();
                                        ToastUtil.toast(DayTaskListMapActivity.this,"查无数据");
                                    }else{
                                        listview.loadComplete();
                                        ToastUtil.toast(DayTaskListMapActivity.this,"没有更多数据");
                                    }
                                    return;
                                }
                                taskList.addAll(planListDto.getTasklist());
                                dayTaskMapAdapter.notifyDataSetChanged();

                                loadUserMarker(taskList);
                            } else {
                                ToastUtil.toast(DayTaskListMapActivity.this, resultDto.getMsg());
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            ToastUtil.toast(DayTaskListMapActivity.this, "数据返回异常");
                        }finally {
                            hideProgress();
                        }

                    }
                });
    }

    /**
     * 加载用户坐标
     */
    private void loadUserMarker(List<PlanVo> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            PlanVo task = tasks.get(i);
            String latitude = task.getLatitude();
            String longitude = task.getLongitude();
            if (!StringUtils.isEmpty(task.getLatitude())
                    && !StringUtils.isEmpty(task.getLongitude())) {
                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                latLngList.add(latLng);
                OverlayOptions overlayOptions = new MarkerOptions().position(latLng).icon(bitmap);
                overlayOptionsList.add(overlayOptions);
            }
        }

        baiduMap.clear();
        baiduMap.addOverlays(overlayOptionsList);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng p : latLngList) {
            builder = builder.include(p);
        }
        LatLngBounds latlngBounds = builder.build();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(
                latlngBounds, mapView.getWidth(), mapView.getHeight());
        baiduMap.animateMapStatus(u);
    }

    @OnClick({R.id.text_foot, R.id.backBtn, R.id.searchBtn, R.id.navButton, R.id.close,
            R.id.confirm, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_foot:
                scrollDownLayout.setToOpen();
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.cancel:
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.confirm:
                paramMap.put("userNameOrWaterMeterId",userNameOrWaterMeterId.getText().toString());
                if("0".equals(completedFlagHidden.getText().toString())){
                    paramMap.put("completedFlag","");
                }else{
                    paramMap.put("completedFlag",completedFlagHidden.getText().toString());
                }
                taskList.clear();
                loadFlag = 1;
                currPage = 1;
                getData();
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.searchBtn:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
            case R.id.navButton:
                Bundle bundle = new Bundle();
                bundle.putSerializable("task",taskList.get(currPosition));
                bundle.putParcelable("currLocation",currLocation);
                startActivity(RoutePlanActivity.class,bundle);
                break;
            case R.id.close:
                navLayout.setVisibility(View.GONE);
                scrollDownLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    /**
     * 初始化定位参数配置
     */
    private void initLocationOption() {
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        LocationClient locationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(0);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //开始定位
        locationClient.start();
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            currLocation = new LatLng(latitude, longitude);
            latLngList.add(currLocation);

            OverlayOptions overlayOptions = new MarkerOptions().position(currLocation).icon(bitmap);
            overlayOptionsList.add(overlayOptions);
            dayTaskMapAdapter.setCurrLocation(currLocation);


            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng p : latLngList) {
                builder = builder.include(p);
            }
            LatLngBounds latlngBounds = builder.build();
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(
                    latlngBounds, mapView.getWidth(), mapView.getHeight());
            baiduMap.animateMapStatus(u);
        }
    }

    public static final int REQUEST_EDIT_TASK = 101;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_EDIT_TASK){
            //提交成功刷新页面
            if(resultCode==1){
                refreash();
            }

        }
    }

    @Override
    public void initParms(Bundle parms) {

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

    public void refreash(){
        taskList.clear();
        currPage = 1;
        loadFlag = 1;
        getData();
    }

    @Override
    public void onLoad() {
        currPage = currPage + 1;
        loadFlag = 2;
        getData();
    }
}
