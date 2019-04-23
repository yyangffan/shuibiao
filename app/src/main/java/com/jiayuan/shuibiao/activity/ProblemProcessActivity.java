package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.entity.Question;
import com.jiayuan.shuibiao.entity.QuestionResultDto;
import com.jiayuan.shuibiao.entity.ResultDto;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.builder.PostFormBuilder;
import com.jiayuan.shuibiao.okhttp.callback.BitmapCallback;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.GlideEngine;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.jiayuan.shuibiao.view.PhotoView;
import com.jiayuan.shuibiao.view.ShowPhotoView;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 问题处理
 */
public class ProblemProcessActivity extends BaseActivity {

    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.title_layout)
    LinearLayout titleLayout;
    @BindView(R.id.feedbackType)
    TextView feedbackType;
    @BindView(R.id.level)
    TextView level;
    @BindView(R.id.deadlineTime)
    TextView deadlineTime;
    @BindView(R.id.orderTime)
    TextView orderTime;
    @BindView(R.id.comment)
    TextView comment;
    @BindView(R.id.submit)
    LinearLayout submit;

    @BindView(R.id.takePhoto)
    LinearLayout takePhoto;
    @BindView(R.id.imageViewContent)
    LinearLayout imageViewContent;
    @BindView(R.id.imageViewContent2)
    LinearLayout imageViewContent2;

    PlanVo planVo;

    public static final String GET_QUESTION_URL = "/question/getQuestion";

    public static final String SAVE_QUESTION_URL = "/question/saveQuestion";
    @BindView(R.id.solve)
    EditText solve;
    @BindView(R.id.result)
    EditText result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_process);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        planVo = (PlanVo) intent.getSerializableExtra("planVo");

        getData();
    }

    private void getData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("queId", planVo.getQueId());

        String requestJsonStr = "";
        try {
            Log.d("ProblemFeedbackDetail", jsonObject.toString());
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + GET_QUESTION_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(ProblemProcessActivity.this, "请求异常，请稍后重试！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try{
                            Gson gson = new Gson();
                            QuestionResultDto resultDto = gson.fromJson(response, QuestionResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                loadData(resultDto.getReturnData());
                            } else {
                                ToastUtil.toast(ProblemProcessActivity.this, resultDto.getMsg());
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            ToastUtil.toast(ProblemProcessActivity.this,"数据返回异常");
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                    }
                });
    }

    /**
     * 加载数据显示戒面
     *
     * @param question
     */
    private void loadData(Question question) {

        if(!StringUtils.isEmpty(question.getDeadlineTime())){
            deadlineTime.setText(question.getDeadlineTime().substring(0,10));
        }
        if(!StringUtils.isEmpty(question.getOrderTime())){
            orderTime.setText(question.getOrderTime().substring(0,10));
        }
        comment.setText(question.getComment());
        feedbackType.setText(question.getFeedbackType());
        solve.setText(question.getSolve());

        switch (StringUtils.isEmpty(question.getQueLevel())?
                "":question.getQueLevel()) {
            case "0":
                level.setText("普通");
                break;
            case "1":
                level.setText("紧急");
                break;
            case "2":
                level.setText("加急");
                break;
        }

        //加载照片
        if(!StringUtils.isEmpty(question.getFile1())){
            ShowPhotoView showPhotoView = new ShowPhotoView(this);
            setImage(question.getFile1(),showPhotoView.getImageView());
            imageViewContent2.addView(showPhotoView);
        }

        if(!StringUtils.isEmpty(question.getFile2())){
            ShowPhotoView showPhotoView = new ShowPhotoView(this);
            setImage(question.getFile2(),showPhotoView.getImageView());
            imageViewContent2.addView(showPhotoView);
        }

        if(!StringUtils.isEmpty(question.getFile3())){
            ShowPhotoView showPhotoView = new ShowPhotoView(this);
            setImage(question.getFile3(),showPhotoView.getImageView());
            imageViewContent2.addView(showPhotoView);
        }

    }

    @OnClick({R.id.backBtn, R.id.takePhoto, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.takePhoto:
                EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.jiayuan.shuibiao.fileprovider")
                        .start(REQUEST_GET_PHOTO);
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    /**
     * 提交
     */
    private void submit() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("queId", planVo.getQueId());
        if(StringUtils.isEmpty(solve.getText().toString())){
            ToastUtil.toast(ProblemProcessActivity.this,"请输入处理意见");
            return;
        }
        jsonObject.addProperty("solve", solve.getText().toString());
        jsonObject.addProperty("result", result.getText().toString());
        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, File> fileMap = new HashMap<>();

        PostFormBuilder postFormBuilder = OkHttpUtils.post();

        //处理照片
        for(int i=0; i<imageViewContent.getChildCount()-1; i++){
            PhotoView photoView = (PhotoView) imageViewContent.getChildAt(i);
            Photo photo = photoView.getPhoto();
            postFormBuilder.addFile("file"+(i+4),photo.name,
                    CompressHelper.getDefault(this).compressToFile(new File(photo.path)));
        }
        showProgress(false);

        postFormBuilder.addParams("param", requestJsonStr)
                .files("file", fileMap)
                .url(Constant.BASE_URL + SAVE_QUESTION_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(ProblemProcessActivity.this, "请求异常，请稍后重试！");
                        hideProgress();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        hideProgress();
                        Gson gson = new Gson();
                        ResultDto resultDto = gson.fromJson(response, ResultDto.class);
                        if (StringUtils.equals(resultDto.getResult(), "1")) {
                            //获取数据，并加载
                            setResult(1);
                            finish();
                        } else {
                            ToastUtil.toast(ProblemProcessActivity.this, resultDto.getMsg());
                        }
                    }
                });
    }


    private static final int REQUEST_GET_PHOTO = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GET_PHOTO) {
                ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                //返回图片地址集合：如果你只需要获取图片的地址，可以用这个
                ArrayList<String> resultPaths = data.getStringArrayListExtra(EasyPhotos.RESULT_PATHS);
                //返回图片地址集合时如果你需要知道用户选择图片时是否选择了原图选项，用如下方法获取
                boolean selectedOriginal = data.getBooleanExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, false);
                if (resultPhotos != null && resultPhotos.size() > 0) {
                    PhotoView photoView = new PhotoView(this);
                    photoView.setImageView(resultPhotos.get(0));
                    photoView.setParentLayout(imageViewContent);

                    imageViewContent.addView(photoView, imageViewContent.getChildCount() - 1);
                    if (imageViewContent.getChildCount() == 4) {
                        imageViewContent.getChildAt(
                                imageViewContent.getChildCount() - 1).setVisibility(View.GONE);
                    } else {
                        imageViewContent.getChildAt(
                                imageViewContent.getChildCount() - 1).setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void setImage(String url, final ImageView imageView)
    {
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
