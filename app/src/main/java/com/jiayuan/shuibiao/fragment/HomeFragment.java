package com.jiayuan.shuibiao.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.activity.MainActivity;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.HomePageData;
import com.jiayuan.shuibiao.entity.HomePageResultDto;
import com.jiayuan.shuibiao.eventbus.FragmentEvent;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.PreferencesUtil;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.jiayuan.shuibiao.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String HOME_DATA_URL = "/employee/mainIndex";


    @BindView(R.id.currDayTaskLayout)
    LinearLayout currDayTaskLayout;
    @BindView(R.id.currMonthTaskLayout)
    LinearLayout currMonthTaskLayout;
    @BindView(R.id.historyTaskLayout)
    LinearLayout historyTaskLayout;
    @BindView(R.id.kpiLayout)
    LinearLayout kpiLayout;

    Unbinder unbinder;
    @BindView(R.id.monthCnt)
    TextView monthCnt;
    @BindView(R.id.todayCnt)
    TextView todayCnt;
    @BindView(R.id.nextDayCnt)
    TextView nextDayCnt;
    @BindView(R.id.monthCompleted)
    TextView monthCompleted;
    @BindView(R.id.todayCompleted)
    TextView todayCompleted;
    @BindView(R.id.nextDayCompleted)
    TextView nextDayCompleted;
    @BindView(R.id.monthRate)
    TextView monthRate;
    @BindView(R.id.todayRate)
    TextView todayRate;
    @BindView(R.id.nextDayRate)
    TextView nextDayRate;
    @BindView(R.id.monthRightRate)
    TextView monthRightRate;
    @BindView(R.id.todayRightRate)
    TextView todayRightRate;
    @BindView(R.id.nextDayRightRate)
    TextView nextDayRightRate;

    LoadingDialog progress;


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Context context;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    //获取首页数据
    private void getData() {
        String empId = PreferencesUtil.readPreference(context, Constant.EMPID);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("empId", empId);
        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        progress.show(false);

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + HOME_DATA_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(context, "请求异常，请稍后重试！");
                        progress.hide();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try{
                            Gson gson = new Gson();
                            HomePageResultDto resultDto = gson.fromJson(response, HomePageResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                HomePageData homePageData =
                                        resultDto.getReturnData();
                                if(homePageData!=null){
                                    monthCnt.setText(
                                            StringUtils.isEmpty(homePageData.getMonthCnt())?
                                                    "":homePageData.getMonthCnt()+"项");
                                    todayCnt.setText(
                                            StringUtils.isEmpty(homePageData.getTodayCnt())?
                                                    "":homePageData.getTodayCnt()+"项");
                                    nextDayCnt.setText(
                                            StringUtils.isEmpty(homePageData.getNextDayCnt())?
                                                    "":homePageData.getNextDayCnt()+"项");

                                    monthCompleted.setText(
                                            StringUtils.isEmpty(homePageData.getMonthCompleted())?
                                                    "":homePageData.getMonthCompleted()+"项");
                                    todayCompleted.setText(
                                            StringUtils.isEmpty(homePageData.getTodayCompleted())?
                                                    "":homePageData.getTodayCompleted()+"项");
                                    nextDayCompleted.setText(
                                            StringUtils.isEmpty(homePageData.getNextDayCompleted())?
                                                    "":homePageData.getNextDayCompleted()+"项");

                                    monthRate.setText(
                                            StringUtils.isEmpty(homePageData.getMonthRate())?
                                                    "":homePageData.getMonthRate()+"%");
                                    todayRate.setText(
                                            StringUtils.isEmpty(homePageData.getTodayRate())?
                                                    "":homePageData.getTodayRate()+"%");
                                    nextDayRate.setText(
                                            StringUtils.isEmpty(homePageData.getNextDayRate())?
                                                    "":homePageData.getNextDayRate()+"%");

                                    monthRightRate.setText(
                                            StringUtils.isEmpty(homePageData.getMonthRightRate())?
                                                    "":homePageData.getMonthRightRate()+"%");
                                    todayRightRate.setText(
                                            StringUtils.isEmpty(homePageData.getTodayRightRate())?
                                                    "":homePageData.getTodayRightRate()+"%");
                                    nextDayRightRate.setText(
                                            StringUtils.isEmpty(homePageData.getNextDayRightRate())?
                                                    "":homePageData.getNextDayRightRate()+"%");
                                }
                            } else {
                                ToastUtil.toast(context, resultDto.getMsg());
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            ToastUtil.toast(context, "数据返回异常");
                        }finally {
                            progress.hide();
                        }

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        //获取数据
        getData();
        return view;
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

    @OnClick({R.id.currDayTaskLayout, R.id.currMonthTaskLayout, R.id.historyTaskLayout, R.id.kpiLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.currDayTaskLayout:
                EventBus.getDefault().post(new FragmentEvent("",MainActivity.DAY_TASK_CODE));
                break;
            case R.id.currMonthTaskLayout:
//                EventBus.getDefault().post(new FragmentEvent("",MainActivity.MONTH_TASK_CODE));
                EventBus.getDefault().post(new FragmentEvent("",MainActivity.TASKK_PRO));
                break;
            case R.id.historyTaskLayout:
                EventBus.getDefault().post(new FragmentEvent("",MainActivity.HISTORY_TASK_CODE));
                break;
            case R.id.kpiLayout:
                EventBus.getDefault().post(new FragmentEvent( "",MainActivity.KPI_CODE));
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
