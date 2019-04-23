package com.jiayuan.shuibiao.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.tts.tools.SharedPreferencesUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.activity.CustomerServiceActivity;
import com.jiayuan.shuibiao.activity.SettingActivity;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.MeterdataTempStorage;
import com.jiayuan.shuibiao.entity.ResultDto;
import com.jiayuan.shuibiao.greendao.MeterdataDao;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.builder.PostFormBuilder;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.PreferencesUtil;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.jiayuan.shuibiao.view.LoadingDialog;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;


public class PersonCenterFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.settingBtn)
    ImageView settingBtn;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.workNumber)
    TextView workNumber;
    @BindView(R.id.companyName)
    TextView companyName;
    @BindView(R.id.customerService)
    RelativeLayout customerService;
    Unbinder unbinder;
    @BindView(R.id.count)
    TextView count;
    @BindView(R.id.submitLayout)
    RelativeLayout submitLayout;
    @BindView(R.id.submitTempStorage)
    TextView submitTempStorage;

    TextView alertView;

    View dialogView;

    LoadingDialog progress;

    private String mParam1;
    private String mParam2;


    private String empId = "";

    public static final String SUBMIT_METERDATA = "/meterdata/saveMeterdata";

    private Context context;

    private OnFragmentInteractionListener mListener;

    public PersonCenterFragment() {
    }

    public static PersonCenterFragment newInstance(String param1, String param2) {
        PersonCenterFragment fragment = new PersonCenterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();
        progress = new LoadingDialog(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        empId = SharedPreferencesUtils.getString(context, "empId");
        View view = inflater.inflate(R.layout.fragment_person_center, container, false);
        unbinder = ButterKnife.bind(this, view);

        workNumber.setText(PreferencesUtil.readPreference(getActivity(), Constant.EMPID));
        name.setText(PreferencesUtil.readPreference(getActivity(), Constant.EMPNAME));
        companyName.setText(PreferencesUtil.readPreference(getActivity(), Constant.TEAM));
        getTempStorageCnt();
        submitTempStorage.setText("点击提交暂存数据（"+meterdataTempStorages.size()+"条)");
        return view;

    }

    private void initDialogView() {

        dialogView = LayoutInflater.from(
                context).inflate(
                R.layout.alert_view_layout, null);
        alertView = dialogView.findViewById(R.id.alertView);

        normalDialog = new AlertDialog.Builder(getContext());
        normalDialog.setTitle("提示");
        normalDialog.setView(dialogView);
        alertView.setText("数据数据提交中(0/"+meterdataTempStorages.size()+")");
//        normalDialog.setMessage("数据数据提交中(0/"+meterdataTempStorages.size()+")");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitFlag = false;
                    }
                });
    }


    @OnClick({R.id.settingBtn, R.id.customerService, R.id.submitLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.settingBtn:
                Intent intent = new Intent(context, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.customerService:
                Intent intent2 = new Intent(context, CustomerServiceActivity.class);
                startActivity(intent2);
                break;
            case R.id.submitLayout:
                if (meterdataTempStorages == null ||
                        meterdataTempStorages.size() == 0) {
                    ToastUtil.toast(getActivity(), "没有暂存数据");
                } else {
                    submitFlag = true;
                    showUploadDialog();

                }
                break;
        }
    }

    AlertDialog.Builder normalDialog;

    /**
     * 上传数据dialog
     */
    private void showUploadDialog(){
        initDialogView();
        // 显示
        normalDialog.show();

        //提交数据
        for(int i=0; i<meterdataTempStorages.size(); i++){
            if(submitFlag){
                MeterdataTempStorage meterdataTempStorage = meterdataTempStorages.get(i);
                submit(meterdataTempStorage);
            }else{
                getTempStorageCnt();
                break;
            }
        }
    }

    List<MeterdataTempStorage> meterdataTempStorages = new ArrayList<>();

    private void getTempStorageCnt() {
        meterdataTempStorages = MeterdataDao.getInstance().loadAll();
    }

    int sumbitNum=0;

    boolean submitFlag = true;


    //提交抄表详情
    private void submit(final MeterdataTempStorage meterdataTempStorage) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("planId", meterdataTempStorage.getPlanId());
        jsonObject.addProperty("planSubId", meterdataTempStorage.getPlanSubId());
        jsonObject.addProperty("meterId", meterdataTempStorage.getMeterId());

        jsonObject.addProperty("meterNum", meterdataTempStorage.getMeterNum());
        jsonObject.addProperty("waterMeterId", meterdataTempStorage.getWaterMeterId());
        jsonObject.addProperty("userId", meterdataTempStorage.getUserId());

        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        PostFormBuilder postFormBuilder =  OkHttpUtils.post();
        File file = new File(meterdataTempStorage.getClockDialPic());
        File clockDialPicFile = new CompressHelper.Builder(getContext())
                .setMaxWidth(880)  // 默认最大宽度为720
                .setMaxHeight(300) // 默认最大高度为960
                .setQuality(80)    // 默认压缩质量为80
                .setFileName(file.getName()) // 设置你需要修改的文件名
                .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .build()
                .compressToFile(new File(meterdataTempStorage.getClockDialPic()));

        postFormBuilder.addFile("clockDialPic",clockDialPicFile.getName(),clockDialPicFile);


        //压缩图片 水表钢号图片
        if(!StringUtils.isEmpty(meterdataTempStorage.getWaterMeterPic())){
            File waterMeterPicFile =
                    CompressHelper.getDefault(getContext()).compressToFile(new File(meterdataTempStorage.getWaterMeterPic()));
            postFormBuilder.addFile("waterMeterPic",waterMeterPicFile.getName(),waterMeterPicFile);
        }
        //压缩图片 现场图片
        if(!StringUtils.isEmpty(meterdataTempStorage.getScenePic())){
            File scenePicFile =
                    CompressHelper.getDefault(getContext()).compressToFile(new File(meterdataTempStorage.getScenePic()));
            postFormBuilder.addFile("scenePic",scenePicFile.getName(),scenePicFile);
        }

        postFormBuilder.
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + SUBMIT_METERDATA)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try{
                            Gson gson = new Gson();
                            ResultDto resultDto = gson.fromJson(response, ResultDto.class);
                            //提交成功
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                alertView.setText("数据数据提交中("+(sumbitNum++)+
                                        "/"+meterdataTempStorages.size()+")");
                                deleteTempStorage(meterdataTempStorage);

                            } else {

                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                    }
                });
    }


    private void deleteTempStorage(MeterdataTempStorage meterdataTempStorage) {
        String where = "where plan_id = ? and plan_sub_id = ? and user_id = ? and water_meter_id = ?";
        List<MeterdataTempStorage> list =
                MeterdataDao.getInstance()
                        .queryMeterdataTempStorageByParams(where,
                                meterdataTempStorage.getPlanId(),meterdataTempStorage.getPlanSubId(),
                                meterdataTempStorage.getUserId(),meterdataTempStorage.getWaterMeterId());
        if(list!=null && list.size()>0){
            for(int i=0;i<list.size();i++){
                MeterdataDao.getInstance().deleteMeterdata(list.get(i));
            }
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
