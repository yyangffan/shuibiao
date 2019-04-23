package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.PlanDetailDto;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.callback.BitmapCallback;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class TaskDetailActivity extends BaseActivity {

    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.userId)
    TextView userId;
    @BindView(R.id.waterMeterId)
    TextView waterMeterId;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.userType)
    TextView userType;
    @BindView(R.id.changeMeterFlag)
    TextView changeMeterFlag;
    @BindView(R.id.lastMonthUseNum)
    TextView lastMonthUseNum;
    @BindView(R.id.beforeLastMonthUseNum)
    TextView beforeLastMonthUseNum;
    @BindView(R.id.meterNum)
    TextView meterNum;

    public static final String GET_TASK_DETAIL = "/plan/planDetail";
    @BindView(R.id.problemFeedback)
    TextView problemFeedback;
    @BindView(R.id.location)
    TextView location;

    PlanVo planVo;
    @BindView(R.id.clickDialPic)
    ImageView clickDialPic;
    @BindView(R.id.waterMeterPic)
    ImageView waterMeterPic;
    @BindView(R.id.scenePic)
    ImageView scenePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        planVo = (PlanVo) intent.getSerializableExtra("planVo");
        getData(planVo);
    }

    /**
     * 请求数据
     *
     * @param planVo
     */
    private void getData(final PlanVo planVo) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("planId", planVo.getPlanId());
        jsonObject.addProperty("planSubId", planVo.getPlanSubId());
        jsonObject.addProperty("userId", planVo.getUserId());

        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + GET_TASK_DETAIL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(TaskDetailActivity.this, "请求异常，请稍后重试！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            Gson gson = new Gson();
                            PlanDetailDto resultDto = gson.fromJson(response, PlanDetailDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                loadData(resultDto.getReturnData());


                            } else {
                                ToastUtil.toast(TaskDetailActivity.this, resultDto.getMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.toast(TaskDetailActivity.this, "数据返回异常");
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                    }
                });

    }

    private void loadData(PlanVo planVo) {
        userId.setText(planVo.getUserId());
        waterMeterId.setText(planVo.getWaterMeterId());
        userName.setText(planVo.getUserName());
        address.setText(planVo.getAddress());
        //电话
        phone.setText(planVo.getPhone());
        //用水性质
        userType.setText(planVo.getUserType());
        //换表
        changeMeterFlag.setText(planVo.getChangeMeterFlg());
        //上一
        lastMonthUseNum.setText("上一:"+planVo.getLastMonthUserNum());
        //上二
        beforeLastMonthUseNum.setText("上二:"+planVo.getBeforeLastMonthUseNum());
        meterNum.setText(planVo.getMeterNum());
        //图片

        if (!StringUtils.isEmpty(planVo.getClockDialPic())) {
            setImage(planVo.getClockDialPic(),clickDialPic);
        }
        if (!StringUtils.isEmpty(planVo.getWaterMeterPic())) {
            setImage(planVo.getWaterMeterPic(),waterMeterPic);
        }
        if (!StringUtils.isEmpty(planVo.getScenePic())) {
            setImage(planVo.getScenePic(),scenePic);
        }

        //更新为题状态和经纬度信息
        this.planVo.setQueId(planVo.getQueId());
        this.planVo.setQueStatus(planVo.getQueStatus());

        this.planVo.setLatitude(planVo.getLatitude());
        this.planVo.setLongitude(planVo.getLongitude());

        //问题反馈后  抄表数据生成新记录，再提交抄表数据，通过meterId 修改
        this.planVo.setMeterId(planVo.getMeterId());
    }


    @OnClick({R.id.backBtn, R.id.problemFeedback, R.id.location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.problemFeedback:
                if (StringUtils.isEmpty(planVo.getQueStatus()) ||
                        StringUtils.equals("0", planVo.getQueStatus())) {
                    Intent intent = new Intent(TaskDetailActivity.this, ProblemFeedBackActivity.class);
                    intent.putExtra("planVo", planVo);
                    startActivityForResult(intent, REQUEST_CODE_PROBLEM);
                } else {
                    Intent intent = new Intent(TaskDetailActivity.this, ProblemFeedbackDetailActivity.class);
                    intent.putExtra("planVo", planVo);
                    startActivity(intent);
                }
                break;
            case R.id.location:
                Intent intent = new Intent(TaskDetailActivity.this, WaterMeterLocationActivity.class);
                intent.putExtra("planVo", planVo);
                startActivityForResult(intent, REQUEST_CODE_LOCATION);
                break;
        }
    }

    private static final int REQUEST_CODE_PROBLEM = 1;

    private static final int REQUEST_CODE_LOCATION = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROBLEM) {
            if (1 == resultCode) {
                //返回数据
                //返回数据 问题反馈后，重新查询planvo，更新问题状态
                getData(planVo);
            }
        } else if (requestCode == REQUEST_CODE_LOCATION) {
            if (1 == resultCode) {
                //返回数据
                LatLng latLng = data.getParcelableExtra("location");
                planVo.setLatitude(latLng.latitude + "");
                planVo.setLatitude(latLng.longitude + "");
            }
        }
    }

    private void setImage(String url, final ImageView imageView) {
        OkHttpUtils.get().url(url).tag(this)
                .build()
                .connTimeOut(20000).readTimeOut(20000).writeTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
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
