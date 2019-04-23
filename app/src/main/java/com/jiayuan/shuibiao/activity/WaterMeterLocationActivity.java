package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.PipeData;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.entity.ResultDto;
import com.jiayuan.shuibiao.greendao.PipeDao;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 水表定位
 */
public class WaterMeterLocationActivity extends BaseActivity implements SensorEventListener {

    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.submit)
    LinearLayout submit;

    BaiduMap baiduMap;

    private PlanVo planVo;

    public static final String SUBMIT_POSITION_URL = "/watermeterinfo/getPosition";

    BitmapDescriptor bitmap = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);

    private LatLng waterMeterLoaction;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    // UI相关
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_meter_location);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        planVo = (PlanVo) intent.getSerializableExtra("planVo");
        //设置地图缩放按钮位置
        mapView.getChildAt(2).setPadding(0,0,0,150);

        baiduMap = mapView.getMap();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0);
        mLocClient.setLocOption(option);
        mLocClient.start();


        if (!StringUtils.isEmpty(planVo.getLongitude())
                && !StringUtils.isEmpty(planVo.getLatitude())) {
            OverlayOptions waterMeterOptions = new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(planVo.getLatitude()),
                            Double.parseDouble(planVo.getLongitude())))
                    .icon(bitmap);
            baiduMap.addOverlay(waterMeterOptions);
        }

        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                baiduMap.clear();
                OverlayOptions option = new MarkerOptions()
                        .position(latLng)
                        .icon(bitmap);
                //在地图上添加Marker，并显示
                baiduMap.addOverlay(option);
                waterMeterLoaction = latLng;
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                // 地图状态改变结束
                //target地图操作的中心点。
                LatLng target = baiduMap.getMapStatus().target;
                final List<PipeData> pipeDataList = getPipeData(target.latitude, target.longitude);
                //绘制中心点附近管线数据

                //异步加载管线数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        drawLine(pipeDataList);
                    }
                }).start();
            }
        });

    }

    private void drawLine(List<PipeData> pipeDataList) {
        baiduMap.clear();
        if(pipeDataList==null){
            return;
        }

        for(PipeData pipeData : pipeDataList){
            String[] strArr = pipeData.getCoordinates().split(";");
            List<LatLng> latLngs = new ArrayList<>();
            for(int i=0;i<strArr.length;i++){
                String pointStr = strArr[i];
                String[] pointStrArr = pointStr.split(",");
                latLngs.add(new LatLng(Double.parseDouble(pointStrArr[1]),
                        Double.parseDouble(pointStrArr[0])));
            }
            //绘制折线
            OverlayOptions ooPolyline = new PolylineOptions().width(3).color(0xAAFF0000).points(latLngs);
            baiduMap.addOverlay(ooPolyline);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            baiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {

        }
    }


    @OnClick({R.id.submit, R.id.backBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit:
                submit();
                break;
            case R.id.backBtn:
                finish();
                break;
        }
    }

    //提交水表坐标
    private void submit() {
        if (waterMeterLoaction == null) {
            ToastUtil.toast(WaterMeterLocationActivity.this, "请点击地图标记坐标");
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId",planVo.getUserId());
        jsonObject.addProperty("waterMeterId", planVo.getWaterMeterId());
        jsonObject.addProperty("longitude", waterMeterLoaction.longitude);
        jsonObject.addProperty("latitude", waterMeterLoaction.latitude);
        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + SUBMIT_POSITION_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(WaterMeterLocationActivity.this, "请求异常，请稍后重试！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try{
                            Gson gson = new Gson();
                            ResultDto resultDto = gson.fromJson(response, ResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                Intent intent = new Intent();
                                intent.putExtra("location", waterMeterLoaction);
                                ToastUtil.toast(WaterMeterLocationActivity.this,"定位成功");
                                setResult(1, intent);
                                finish();
                            } else {
                                ToastUtil.toast(WaterMeterLocationActivity.this, resultDto.getMsg());
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            ToastUtil.toast(WaterMeterLocationActivity.this,"数据返回异常");
                        }

                    }
                });
    }

    //查询中心点附近的管线数据
    public List<PipeData> getPipeData(double pointLat, double pointLon) {
        String where = "where lat > ? and lat < ? and lon > ? and lon <?";

        List<PipeData> pipeDataList =
                PipeDao.getInstance().queryPipeDataByParams(
                        where,
                        new String[]{
                                pointLat - 0.0025 + "", pointLat + 0.0025 + "",
                                pointLon - 0.002 + "", pointLon + 0.002 + ""
                        });
        return pipeDataList;
    }


    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
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
}
