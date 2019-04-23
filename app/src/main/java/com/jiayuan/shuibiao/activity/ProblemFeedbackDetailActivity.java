package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.entity.Question;
import com.jiayuan.shuibiao.entity.QuestionResultDto;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.callback.BitmapCallback;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.jiayuan.shuibiao.view.ShowPhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 问题反馈详情
 */
public class ProblemFeedbackDetailActivity extends BaseActivity {

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
    TextView comment;
    @BindView(R.id.imageViewContent)
    LinearLayout imageViewContent;


    PlanVo planVo;

    public static final String GET_QUESTION_URL = "/question/getQuestion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_feed_back_detail);
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
            Log.d("ProblemFeedbackDetail",jsonObject.toString());
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
                        ToastUtil.toast(ProblemFeedbackDetailActivity.this, "请求异常，请稍后重试！");
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
                                ToastUtil.toast(ProblemFeedbackDetailActivity.this, resultDto.getMsg());
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            ToastUtil.toast(ProblemFeedbackDetailActivity.this,"数据返回异常");
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
        switch(StringUtils.isEmpty(question.getQueLevel())?
                "":question.getQueLevel()){
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
            imageViewContent.addView(showPhotoView);
        }

        if(!StringUtils.isEmpty(question.getFile2())){
            ShowPhotoView showPhotoView = new ShowPhotoView(this);
            setImage(question.getFile2(),showPhotoView.getImageView());
            imageViewContent.addView(showPhotoView);
        }

        if(!StringUtils.isEmpty(question.getFile3())){
            ShowPhotoView showPhotoView = new ShowPhotoView(this);
            setImage(question.getFile3(),showPhotoView.getImageView());
            imageViewContent.addView(showPhotoView);
        }


    }

    @OnClick({R.id.backBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                Intent intent = getIntent();
                intent.putExtra("planVo",planVo);
                setResult(1,intent);
                finish();
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
