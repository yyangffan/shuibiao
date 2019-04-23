package com.jiayuan.shuibiao.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.MeterdataTempStorage;
import com.jiayuan.shuibiao.entity.PlanDetailDto;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.entity.ResultDto;
import com.jiayuan.shuibiao.greendao.MeterdataDao;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.builder.PostFormBuilder;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.GlideEngine;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static com.jiayuan.shuibiao.okhttp.OkHttpUtils.post;

public class EditTaskDetailActivity extends BaseActivity {


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
    EditText meterNum;

    public static final String GET_TASK_DETAIL = "/plan/planDetail";
    public static final String SUBMIT_METERDATA = "/meterdata/saveMeterdata";
    @BindView(R.id.problemFeedback)
    TextView problemFeedback;
    @BindView(R.id.location)
    TextView location;

    PlanVo planVo;
    @BindView(R.id.submit)
    LinearLayout submit;
    @BindView(R.id.clockDialPic)
    ImageView clockDialPic;
    @BindView(R.id.waterMeterPic)
    ImageView waterMeterPic;
    @BindView(R.id.scenePic)
    ImageView scenePic;
    @BindView(R.id.clockDialPicBtn)
    ImageView clockDialPicBtn;
    @BindView(R.id.waterMeterPicBtn)
    ImageView waterMeterPicBtn;
    @BindView(R.id.scenePicBtn)
    ImageView scenePicBtn;

    private RequestManager mGlide;

    private Map<String,Photo> photoMap = new HashMap<>();

    //是否为第一次打开
    private boolean first = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_detail);
        ButterKnife.bind(this);
        mGlide = Glide.with(this);
        Intent intent = getIntent();
        planVo = (PlanVo) intent.getSerializableExtra("planVo");
        getData(planVo);
        showProgress(false);

    }

    private void checkTempStorage() {
        String where = "where plan_id = ? and plan_sub_id = ? and user_id = ? and water_meter_id = ?";
        List<MeterdataTempStorage> list =
                MeterdataDao.getInstance()
                        .queryMeterdataTempStorageByParams(where,
                                planVo.getPlanId(),planVo.getPlanSubId(),
                                planVo.getUserId(),planVo.getWaterMeterId());
        if(list!=null && list.size()>0){
            showIfUseTempDialog(list.get(0));
        }
    }

    /**
     * 请求数据
     *
     * @param planVo
     */
    private void getData(PlanVo planVo) {
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

        post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + GET_TASK_DETAIL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(EditTaskDetailActivity.this, "请求异常，请稍后重试！");
                        hideProgress();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        hideProgress();
                        try{
                            Gson gson = new Gson();
                            PlanDetailDto resultDto = gson.fromJson(response, PlanDetailDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                PlanVo planVo1 = resultDto.getReturnData();
                                loadData(planVo1);
                                if(first){
                                    //检查是否有暂存数据，如果有提示是否使用暂存数据
                                    checkTempStorage();
                                    first = false;
                                }

                            } else {
                                ToastUtil.toast(EditTaskDetailActivity.this, resultDto.getMsg());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            ToastUtil.toast(EditTaskDetailActivity.this, "数据返回异常");
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                    }
                });
    }

    private void loadData(PlanVo planVo1) {
        userId.setText(planVo1.getUserId());
        waterMeterId.setText(planVo1.getWaterMeterId());
        userName.setText(planVo1.getUserName());
        address.setText(planVo1.getAddress());
        //电话
        phone.setText(planVo1.getPhone());
        //用水性质
        userType.setText(planVo1.getUserType());
        //换表
        changeMeterFlag.setText(planVo1.getChangeMeterFlg());
        //上一
        lastMonthUseNum.setText("上一:"+planVo1.getLastMonthUserNum());
        //上二
        beforeLastMonthUseNum.setText("上二:"+planVo1.getBeforeLastMonthUseNum());
        if(!StringUtils.isEmpty(planVo1.getMeterNum())
                && !"0".equals(planVo1.getMeterNum())){
            meterNum.setText(planVo1.getMeterNum());
        }
        //更新为题状态和经纬度信息
        planVo.setQueId(planVo1.getQueId());
        planVo.setQueStatus(planVo1.getQueStatus());

        planVo.setLatitude(planVo1.getLatitude());
        planVo.setLongitude(planVo1.getLongitude());
        //问题反馈后  抄表数据生成新记录，再提交抄表数据，通过meterId 修改
        planVo.setMeterId(planVo1.getMeterId());
    }

    //提交抄表详情
    private void submit() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("planId", planVo.getPlanId());
        jsonObject.addProperty("planSubId", planVo.getPlanSubId());
        jsonObject.addProperty("meterId", planVo.getMeterId());

        if(StringUtils.isEmpty(meterNum.getText().toString())){
            ToastUtil.toast(EditTaskDetailActivity.this,"请填写当期抄见数");
            return;
        }else{
            jsonObject.addProperty("meterNum", meterNum.getText().toString());
        }
        jsonObject.addProperty("waterMeterId", planVo.getWaterMeterId());
        jsonObject.addProperty("userId", planVo.getUserId());

        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        PostFormBuilder postFormBuilder =  OkHttpUtils.post();

        if(photoMap.get("clockDialPic")!=null){
            File clockDialPicFile = new CompressHelper.Builder(this)
                    .setMaxWidth(880)  // 默认最大宽度为720
                    .setMaxHeight(300) // 默认最大高度为960
                    .setQuality(80)    // 默认压缩质量为80
                    .setFileName(photoMap.get("clockDialPic").name) // 设置你需要修改的文件名
                    .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .build()
                    .compressToFile(new File(photoMap.get("clockDialPic").path));

            postFormBuilder.addFile("clockDialPic",clockDialPicFile.getName(),clockDialPicFile);
        }else{
            ToastUtil.toast(EditTaskDetailActivity.this,"请拍摄水表照片");
            return;
        }


        //判断网络环境  网络不可用时，暂存数据
        if(!NetworkUtils.isAvailableByPing()){
            showNormalDialog();
            return;
        }

        //压缩图片 水表钢号图片
        if(photoMap.get("waterMeterPic")!=null){
            File waterMeterPicFile =
                    CompressHelper.getDefault(this).compressToFile(new File(photoMap.get("waterMeterPic").path));
            postFormBuilder.addFile("waterMeterPic",waterMeterPicFile.getName(),waterMeterPicFile);
        }
        //压缩图片 现场图片
        if(photoMap.get("scenePic")!=null){
            File scenePicFile =
                    CompressHelper.getDefault(this).compressToFile(new File(photoMap.get("scenePic").path));
            postFormBuilder.addFile("scenePic",scenePicFile.getName(),scenePicFile);
        }
        showProgress(false);
        postFormBuilder.
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + SUBMIT_METERDATA)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(EditTaskDetailActivity.this, "请求异常，请稍后重试！");
                        tempStorage();
                        showProgress(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        showProgress(false);
                        try{
                            Gson gson = new Gson();
                            ResultDto resultDto = gson.fromJson(response, ResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                setResult(1);
                                //提交成功 清除暂存数据
                                deleteTempStorage();
                                finish();
                            } else {
                                ToastUtil.toast(EditTaskDetailActivity.this, resultDto.getMsg());
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            ToastUtil.toast(EditTaskDetailActivity.this, "数据返回异常");
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                    }
                });
    }


    private void deleteTempStorage() {
        String where = "where plan_id = ? and plan_sub_id = ? and user_id = ? and water_meter_id = ?";
        List<MeterdataTempStorage> list =
                MeterdataDao.getInstance()
                        .queryMeterdataTempStorageByParams(where,
                                planVo.getPlanId(),planVo.getPlanSubId(),
                                planVo.getUserId(),planVo.getWaterMeterId());
        if(list!=null && list.size()>0){
            for(int i=0;i<list.size();i++){
                MeterdataDao.getInstance().deleteMeterdata(list.get(i));
            }
        }
    }

    @OnClick({R.id.backBtn, R.id.problemFeedback, R.id.location, R.id.submit,
            R.id.clockDialPic, R.id.waterMeterPic, R.id.scenePic,
            R.id.clockDialPicBtn, R.id.waterMeterPicBtn, R.id.scenePicBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.problemFeedback:
                planVo.setMeterNum(meterNum.getText().toString());
                //问题状态为0时，提交问题
                if (StringUtils.equals("0", planVo.getQueStatus())
                        || StringUtils.isEmpty(planVo.getQueStatus())) {
                    Intent intent = new Intent(EditTaskDetailActivity.this, ProblemFeedBackActivity.class);
                    intent.putExtra("planVo", planVo);
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(EditTaskDetailActivity.this, ProblemFeedbackDetailActivity.class);
                    intent.putExtra("planVo", planVo);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.location:
                Intent intent = new Intent(EditTaskDetailActivity.this, WaterMeterLocationActivity.class);
                intent.putExtra("planVo", planVo);
                startActivityForResult(intent, 2);
                break;
            case R.id.submit:
                submit();
                break;
            case R.id.clockDialPicBtn:
            case R.id.clockDialPic:
                startActivityForResult(new Intent(EditTaskDetailActivity.this,
                        TakeWaterMeterNumberActivity.class),REQUEST_CODE_CLOCK_DIAL_PIC);
                break;
            case R.id.waterMeterPicBtn:
            case R.id.waterMeterPic:
                EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.jiayuan.shuibiao.fileprovider")
                        .start(REQUEST_CODE_WATER_METER_PIC);
                break;
            case R.id.scenePicBtn:
            case R.id.scenePic:
                EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.jiayuan.shuibiao.fileprovider")
                        .start(REQUEST_CODE_SCENE_PIC);
                break;
        }
    }

    private static final int REQUEST_CODE_PROBLEM = 1;

    private static final int REQUEST_CODE_LOCATION = 2;

    private static final int REQUEST_CODE_CLOCK_DIAL_PIC = 101;

    private static final int REQUEST_CODE_WATER_METER_PIC = 102;

    private static final int REQUEST_CODE_SCENE_PIC = 103;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROBLEM) {
            if (1 == resultCode) {
                PlanVo planVoData = (PlanVo) data.getSerializableExtra("planVo");
                meterNum.setText(planVoData.getMeterNum());
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
        } else if (requestCode == REQUEST_CODE_CLOCK_DIAL_PIC) {
            if (resultCode == 1) {
                Photo photo = data.getParcelableExtra("photo");
                if (photo != null) {
                    clockDialPicBtn.setVisibility(View.GONE);
                    clockDialPic.setVisibility(View.VISIBLE);
                    mGlide.load(photo.path).into(clockDialPic);
                    photoMap.put("clockDialPic",photo);
                }
            }

        } else if (requestCode == REQUEST_CODE_WATER_METER_PIC) {
            if (resultCode == RESULT_OK) {
                ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                //返回图片地址集合：如果你只需要获取图片的地址，可以用这个
                ArrayList<String> resultPaths = data.getStringArrayListExtra(EasyPhotos.RESULT_PATHS);
                //返回图片地址集合时如果你需要知道用户选择图片时是否选择了原图选项，用如下方法获取
                boolean selectedOriginal = data.getBooleanExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, false);
                if (resultPhotos != null && resultPhotos.size() > 0) {
                    waterMeterPicBtn.setVisibility(View.GONE);
                    waterMeterPic.setVisibility(View.VISIBLE);
                    mGlide.load(resultPhotos.get(0).path).into(waterMeterPic);
                    photoMap.put("waterMeterPic",resultPhotos.get(0));
                }
            }

        } else if (requestCode == REQUEST_CODE_SCENE_PIC) {
            if (resultCode == RESULT_OK) {
                ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);

                //返回图片地址集合：如果你只需要获取图片的地址，可以用这个
                ArrayList<String> resultPaths = data.getStringArrayListExtra(EasyPhotos.RESULT_PATHS);
                //返回图片地址集合时如果你需要知道用户选择图片时是否选择了原图选项，用如下方法获取
                boolean selectedOriginal = data.getBooleanExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, false);
                if (resultPhotos != null && resultPhotos.size() > 0) {
                    scenePicBtn.setVisibility(View.GONE);
                    scenePic.setVisibility(View.VISIBLE);
                    mGlide.load(resultPhotos.get(0).path).into(scenePic);
                    photoMap.put("scenePic",resultPhotos.get(0));
                }
            }
        }
    }

    /**
     * 是否使用暂存数据Dialog
     */
    private void showIfUseTempDialog(final MeterdataTempStorage meterdataTempStorage){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(EditTaskDetailActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("检测到暂存数据，是否使用?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        useTempStorage(meterdataTempStorage);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }

    private void useTempStorage(MeterdataTempStorage meterdataTempStorage) {

        //设置缓存数据
        meterNum.setText(meterdataTempStorage.getMeterNum());

        String clockDialPicPath = meterdataTempStorage.getClockDialPic();

        String waterMeterPicPath = meterdataTempStorage.getWaterMeterPic();

        String scenePicPath = meterdataTempStorage.getScenePic();


        if(!StringUtils.isEmpty(clockDialPicPath)){
            clockDialPicBtn.setVisibility(View.GONE);
            clockDialPic.setVisibility(View.VISIBLE);
            mGlide.load(clockDialPicPath).into(clockDialPic);
            File file = new File(clockDialPicPath);
            Photo photo = new Photo(file.getName(),clockDialPicPath,
                    0,0,0,0,"");
            photoMap.put("clockDialPic",photo);
        }

        if(!StringUtils.isEmpty(waterMeterPicPath)){
            waterMeterPicBtn.setVisibility(View.GONE);
            waterMeterPic.setVisibility(View.VISIBLE);
            mGlide.load(waterMeterPicPath).into(waterMeterPic);
            File file = new File(waterMeterPicPath);
            Photo photo = new Photo(file.getName(),waterMeterPicPath,
                    0,0,0,0,"");
            photoMap.put("waterMeterPic",photo);
        }

        if(!StringUtils.isEmpty(scenePicPath)){
            scenePicBtn.setVisibility(View.GONE);
            scenePic.setVisibility(View.VISIBLE);
            mGlide.load(scenePicPath).into(scenePic);
            File file = new File(scenePicPath);
            Photo photo = new Photo(file.getName(),scenePicPath,
                    0,0,0,0,"");
            photoMap.put("scenePic",photo);
        }
    }


    /**
     * 网络不好是否暂存dialog
     */
    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(EditTaskDetailActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("网络状况不佳无法提交,是否暂存数据?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempStorage();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }


    //暂存数据
    public void tempStorage(){

        String where = "where plan_id = ? and plan_sub_id = ? and user_id = ? and water_meter_id = ?";
        List<MeterdataTempStorage> list =
                MeterdataDao.getInstance()
                        .queryMeterdataTempStorageByParams(where,
                                planVo.getPlanId(),planVo.getPlanSubId(),
                                planVo.getUserId(),planVo.getWaterMeterId());
        MeterdataTempStorage meterdataTempStorage;

        if(list!=null && list.size()>0){
            meterdataTempStorage = list.get(0);
        }else{
            meterdataTempStorage = new MeterdataTempStorage();
        }

        meterdataTempStorage.setPlanId(planVo.getPlanId());
        meterdataTempStorage.setPlanSubId(planVo.getPlanSubId());
        meterdataTempStorage.setMeterId(planVo.getMeterId());
        meterdataTempStorage.setUserId(planVo.getUserId());
        meterdataTempStorage.setWaterMeterId(planVo.getWaterMeterId());
        meterdataTempStorage.setMeterNum(meterNum.getText().toString());

        Photo clickDialPhoto = photoMap.get("clockDialPic");
        if(clickDialPhoto!=null){
            meterdataTempStorage.setClockDialPic(clickDialPhoto.path);
        }
        Photo waterMeterPhoto = photoMap.get("waterMeterPic");
        if(waterMeterPhoto!=null){
            meterdataTempStorage.setWaterMeterPic(waterMeterPhoto.path);
        }
        Photo scenePicPhoto = photoMap.get("scenePic");
        if(scenePicPhoto!=null){
            meterdataTempStorage.setScenePic(scenePicPhoto.path);
        }
        MeterdataDao.getInstance().insertOrReplaceData(meterdataTempStorage);
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
