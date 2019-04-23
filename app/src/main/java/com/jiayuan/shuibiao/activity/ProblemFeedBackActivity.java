package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.DictData;
import com.jiayuan.shuibiao.entity.DictResultDto;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.entity.ResultDto;
import com.jiayuan.shuibiao.entity.SpinerBean;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.builder.PostFormBuilder;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.GlideEngine;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.jiayuan.shuibiao.view.PhotoView;
import com.jiayuan.shuibiao.view.SpinerPopWindow;
import com.jiayuan.shuibiao.view.datepicker.widget.CustomDatePicker;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 问题反馈
 */
public class ProblemFeedBackActivity extends BaseActivity {

    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.feedbackType)
    TextView feedbackType;
    @BindView(R.id.level)
    TextView level;
    @BindView(R.id.deadlineTime)
    TextView deadlineTime;
    @BindView(R.id.orderTime)
    TextView orderTime;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.submit)
    LinearLayout submit;

    PlanVo planVo;
    @BindView(R.id.takePhoto)
    LinearLayout takePhoto;
    @BindView(R.id.imageViewContent)
    LinearLayout imageViewContent;

    private List<SpinerBean> feedbackTypeList = new ArrayList<>();

    private List<SpinerBean> levelList = new ArrayList<>();

    SpinerPopWindow feedbackTypePopup;
    SpinerPopWindow levelPopup;

    CustomDatePicker deadlineTimeDatePicker;
    CustomDatePicker orderTimeDatePicker;

    public static final String SAVE_QUESTION_URL = "/question/saveQuestion";

    private static final String GET_DICT_TYPE = "/sysDictData/getDictData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_feed_back);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        planVo = (PlanVo) intent.getSerializableExtra("planVo");

        //反应类型
        feedbackTypePopup = new SpinerPopWindow(ProblemFeedBackActivity.this, feedbackTypeList, itemClickListener);
        feedbackTypePopup.setOnDismissListener(dismissListener);

        //紧急程度
        levelList.add(new SpinerBean("0", "普通"));
        levelList.add(new SpinerBean("1", "紧急"));
        levelList.add(new SpinerBean("2", "加急"));
        levelPopup = new SpinerPopWindow(ProblemFeedBackActivity.this, levelList, itemClickListener2);
        levelPopup.setOnDismissListener(dismissListener2);


        //日期选择初始化
        deadlineTimeDatePicker = new CustomDatePicker(
                ProblemFeedBackActivity.this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                deadlineTime.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2049-12-31 00:00");
        deadlineTimeDatePicker.showSpecificTime(false); // 不显示时和分
        deadlineTimeDatePicker.setIsLoop(false); // 不允许循环滚动

        //日期选择初始化
        orderTimeDatePicker = new CustomDatePicker(
                ProblemFeedBackActivity.this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                orderTime.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2049-12-31 00:00");
        orderTimeDatePicker.showSpecificTime(false); // 不显示时和分
        orderTimeDatePicker.setIsLoop(false); // 不允许循环滚动

        getQueType();

    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            feedbackTypePopup.dismiss();
            feedbackType.setText(feedbackTypeList.get(position).getValue());
            feedbackTypePopup.setPosition(position);
        }
    };

    private AdapterView.OnItemClickListener itemClickListener2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            levelPopup.dismiss();
            level.setText(levelList.get(position).getValue());
            levelPopup.setPosition(position);
        }
    };

    private PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_down);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            feedbackType.setCompoundDrawables(null, null, drawable, null);
        }
    };

    private PopupWindow.OnDismissListener dismissListener2 = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_down);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            level.setCompoundDrawables(null, null, drawable, null);
        }
    };

    private void setTextImage(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        feedbackType.setCompoundDrawables(null, null, drawable, null);
        level.setCompoundDrawables(null, null, drawable, null);
    }

    @OnClick({R.id.backBtn, R.id.feedbackType, R.id.level,
            R.id.deadlineTime, R.id.orderTime, R.id.submit, R.id.takePhoto})
    public void onViewClicked(View view) {
        Drawable drawable = getResources().getDrawable(R.drawable.icon_up);
        switch (view.getId()) {
            case R.id.backBtn:
                Intent intent = getIntent();
                intent.putExtra("planVo",planVo);
                setResult(1,intent);
                finish();
                break;
            case R.id.feedbackType:
                feedbackTypePopup.setWidth(feedbackType.getWidth());
                feedbackTypePopup.showAsDropDown(feedbackType);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                feedbackType.setCompoundDrawables(null, null, drawable, null);
                break;
            case R.id.level:
                levelPopup.setWidth(level.getWidth());
                levelPopup.showAsDropDown(level);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                level.setCompoundDrawables(null, null, drawable, null);
                break;
            case R.id.deadlineTime:
                if (StringUtils.isEmpty(deadlineTime.getText().toString())) {
                    deadlineTimeDatePicker.show(getDateStr());
                } else {
                    deadlineTimeDatePicker.show(deadlineTime.getText().toString());
                }
                break;
            case R.id.orderTime:
                if (StringUtils.isEmpty(orderTime.getText().toString())) {
                    orderTimeDatePicker.show(getDateStr());
                } else {
                    orderTimeDatePicker.show(orderTime.getText().toString());
                }
                break;
            case R.id.submit:
                submit();
                break;
            case R.id.takePhoto:
                EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.jiayuan.shuibiao.fileprovider")
                        .start(REQUEST_GET_PHOTO);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = getIntent();
            intent.putExtra("planVo",planVo);
            setResult(1,intent);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static final int REQUEST_GET_PHOTO = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_GET_PHOTO){
                ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                //返回图片地址集合：如果你只需要获取图片的地址，可以用这个
                ArrayList<String> resultPaths = data.getStringArrayListExtra(EasyPhotos.RESULT_PATHS);
                //返回图片地址集合时如果你需要知道用户选择图片时是否选择了原图选项，用如下方法获取
                boolean selectedOriginal = data.getBooleanExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, false);
                if (resultPhotos != null && resultPhotos.size() > 0) {
                    PhotoView photoView = new PhotoView(this);
                    photoView.setImageView(resultPhotos.get(0));
                    photoView.setParentLayout(imageViewContent);

                    imageViewContent.addView(photoView,imageViewContent.getChildCount()-1);
                    if(imageViewContent.getChildCount()==4){
                        imageViewContent.getChildAt(
                                imageViewContent.getChildCount()-1).setVisibility(View.GONE);
                    }else{
                        imageViewContent.getChildAt(
                                imageViewContent.getChildCount()-1).setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    /**
     * 获取抄表类型
     */
    public void getQueType() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("dictType", "meter_que_type");

        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + GET_DICT_TYPE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(ProblemFeedBackActivity.this, "请求异常，请稍后重试！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            Gson gson = new Gson();
                            DictResultDto resultDto = gson.fromJson(response, DictResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                List<DictData> dictDataList = resultDto.getReturnData();
                                List<SpinerBean> list = new ArrayList<>();
                                //加载kpi类型
                                for (int i = 0; i < dictDataList.size(); i++) {
                                    DictData dictData = dictDataList.get(i);
                                    list.add(new SpinerBean(dictData.getDictValue()
                                            , dictData.getDictLabel()));
                                }
                                feedbackTypeList.clear();
                                feedbackTypeList.addAll(list);
                                feedbackTypePopup.refreshList();
                            } else {
                                ToastUtil.toast(ProblemFeedBackActivity.this, resultDto.getMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.toast(ProblemFeedBackActivity.this, "数据返回异常");
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                    }
                });
    }

    /**
     * 提交
     */
    private void submit() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", planVo.getUserId());
        jsonObject.addProperty("waterMeterId", planVo.getWaterMeterId());
        jsonObject.addProperty("planId", planVo.getPlanId());
        jsonObject.addProperty("planSubId",planVo.getPlanSubId());
        jsonObject.addProperty("meterId",planVo.getMeterId());


        if (feedbackTypePopup.getPosition() == -1) {
            jsonObject.addProperty("feedbackType","");
        } else {
            jsonObject.addProperty("feedbackType",
                    feedbackTypeList.get(feedbackTypePopup.getPosition()).getKey());
        }
        if (levelPopup.getPosition() == -1) {
            jsonObject.addProperty("queLevel","");
        } else {
            jsonObject.addProperty("queLevel",
                    levelList.get(levelPopup.getPosition()).getKey());
        }

        jsonObject.addProperty("deadlineTime", deadlineTime.getText().toString());
        jsonObject.addProperty("orderTime", orderTime.getText().toString());
        jsonObject.addProperty("comment", comment.getText().toString());
        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        PostFormBuilder postFormBuilder = OkHttpUtils.post();

        //处理照片
        for(int i=0; i<imageViewContent.getChildCount()-1; i++){
            PhotoView photoView = (PhotoView) imageViewContent.getChildAt(i);
            Photo photo = photoView.getPhoto();
            postFormBuilder.addFile("file"+(i+1),photo.name,
                    CompressHelper.getDefault(this).compressToFile(new File(photo.path)));
        }
        showProgress(false);
        postFormBuilder.addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + SAVE_QUESTION_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(ProblemFeedBackActivity.this, "请求异常，请稍后重试！");
                        hideProgress();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        hideProgress();
                        try{
                            Gson gson = new Gson();
                            ResultDto resultDto = gson.fromJson(response, ResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                setResult(1);
                                finish();
                            } else {
                                ToastUtil.toast(ProblemFeedBackActivity.this, resultDto.getMsg());
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            ToastUtil.toast(ProblemFeedBackActivity.this, "数据返回异常");
                        }
                    }
                });
    }

    private String getDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        return sdf.format(new Date());
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
