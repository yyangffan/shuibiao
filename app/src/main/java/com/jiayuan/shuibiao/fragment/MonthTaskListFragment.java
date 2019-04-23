package com.jiayuan.shuibiao.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.activity.EditTaskDetailActivity;
import com.jiayuan.shuibiao.activity.MainActivity;
import com.jiayuan.shuibiao.activity.MonthTaskListMapActivity;
import com.jiayuan.shuibiao.activity.TaskDetailActivity;
import com.jiayuan.shuibiao.adapter.DayTaskAdapter;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.PlanListDto;
import com.jiayuan.shuibiao.entity.PlanResultDto;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.entity.SpinerBean;
import com.jiayuan.shuibiao.eventbus.FragmentEvent;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.PreferencesUtil;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.jiayuan.shuibiao.view.LoadingDialog;
import com.jiayuan.shuibiao.view.SpinerPopWindow;
import com.jiayuan.shuibiao.view.datepicker.widget.CustomDatePicker;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

public class MonthTaskListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MonthTaskListFragment";
    @BindView(R.id.main)
    View main;
    Unbinder unbinder;
    @BindView(R.id.search)
    ImageView search;

    @BindView(R.id.listview)
    ListView listview;

    @BindView(R.id.mapButton)
    ImageView mapButton;

    DayTaskAdapter currDayTaskAdapter;

    @BindView(R.id.swipe_refresh)
    SuperSwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.countTextView)
    TextView countTextView;

    //查询相关
    @BindView(R.id.popup_goods_noview)
    View popupGoodsNoview;
    @BindView(R.id.startDate)
    TextView startDate;
    @BindView(R.id.endDate)
    TextView endDate;
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
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.month_back)
    LinearLayout mMonthBack;

    //刷新flag  1  初始化请求或者下拉刷新  2  加载更多
    private int loadFlag = 1;


    // Header View
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;

    // Footer View
    private ProgressBar footerProgressBar;
    private TextView footerTextView;
    private ImageView footerImageView;

    //请求参数
    private Map<String, String> paramMap = new HashMap<>();

    private static final String GET_TASK_URL = "/plan/queryPlan";

    private int currPage = 1;

    private int pageSize = 25;

    private int taskTotalCnt = 0;

    private List<PlanVo> taskList = new ArrayList<>();

    private String mParam1;
    private String mParam2;

    private Context context;

    private OnFragmentInteractionListener mListener;

    private String empId;

    LoadingDialog progress;

    public MonthTaskListFragment() {
    }

    public static MonthTaskListFragment newInstance(String param1, String param2) {
        MonthTaskListFragment fragment = new MonthTaskListFragment();
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
        empId = PreferencesUtil.readPreference(context, Constant.EMPID);
        View view = inflater.inflate(R.layout.fragment_month_task_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        // init SuperSwipeRefreshLayout
        initListView();

        initSearcheView();

        taskList.clear();
        getData();
        progress.show(false);
        return view;
    }

    /**
     * 查询数据初始化
     */
    private SpinerPopWindow mSpinerPopWindow;
    private List<SpinerBean> list = new ArrayList<>();
    private CustomDatePicker customDatePicker1;
    private CustomDatePicker customDatePicker2;

    private void initSearcheView() {
        //日期选择控件
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDatePicker1.show(startDate.getText().toString());
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDatePicker2.show(endDate.getText().toString());
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        startDate.setText(now.split(" ")[0]);
        endDate.setText(now.split(" ")[0]);
        Bundle arguments = this.getArguments();
        if (arguments != null) {
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String msg = arguments.getString("msg");
            try {
                startDate.setText(s.format(new SimpleDateFormat("yyyyMMdd").parse(msg)));
                endDate.setText(s.format(new SimpleDateFormat("yyyyMMdd").parse(msg)));
            } catch (ParseException e) {
                Log.e(TAG, "initSearcheView:时间日期解析问题");
                startDate.setText(now.split(" ")[0]);
                endDate.setText(now.split(" ")[0]);
            }
            paramMap.put("startDate", msg);
            paramMap.put("endDate", msg);
        }

        customDatePicker1 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                startDate.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2049-12-31 00:00");
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                endDate.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2049-12-31 00:00");
        customDatePicker2.showSpecificTime(false); // 不显示时和分
        customDatePicker2.setIsLoop(false); // 不允许循环滚动

        //完成情况
        list.clear();
        list.add(new SpinerBean("0", "全部"));
        list.add(new SpinerBean("1", "未完成"));
        list.add(new SpinerBean("2", "已完成"));

        mSpinerPopWindow = new SpinerPopWindow(context, list, itemClickListener);
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
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            completedFlag.setText(list.get(position).getValue());
            completedFlagHidden.setText(list.get(position).getKey());
        }
    };


    private PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            setTextImage(R.drawable.icon_down);
        }
    };

    private void setTextImage(int resId) {
        Drawable drawable = context.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// ��������ͼƬ��С��������ʾ
        completedFlag.setCompoundDrawables(null, null, drawable, null);
    }


    public void initListView() {
        swipeRefreshLayout.setHeaderViewBackgroundColor(0xff888888);
        swipeRefreshLayout.setHeaderView(createHeaderView());// add headerView
        swipeRefreshLayout.setFooterView(createFooterView());
        swipeRefreshLayout.setTargetScrollWithLayout(true);
        swipeRefreshLayout
                .setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
                    @Override
                    public void onRefresh() {
                        textView.setText("正在刷新");
                        imageView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        refreash();
                    }

                    @Override
                    public void onPullDistance(int distance) {
                        // pull distance
                    }

                    @Override
                    public void onPullEnable(boolean enable) {
                        textView.setText(enable ? "松开刷新" : "下拉刷新");
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setRotation(enable ? 180 : 0);
                    }
                });

        swipeRefreshLayout
                .setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {

                    @Override
                    public void onLoadMore() {
                        footerTextView.setText("正在加载...");
                        footerImageView.setVisibility(View.GONE);
                        footerProgressBar.setVisibility(View.VISIBLE);
                        loadMore();
                    }

                    @Override
                    public void onPushEnable(boolean enable) {
                        footerTextView.setText(enable ? "松开加载" : "上拉加载");
                        footerImageView.setVisibility(View.VISIBLE);
                        footerImageView.setRotation(enable ? 0 : 180);
                    }

                    @Override
                    public void onPushDistance(int distance) {
                        // TODO Auto-generated method stub

                    }

                });

        currDayTaskAdapter = new DayTaskAdapter(taskList, getActivity());
        listview.setAdapter(currDayTaskAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlanVo planVo = taskList.get(position);
                if ("0".equals(planVo.getCompletedFlag())) {
                    Intent intent = new Intent(context, EditTaskDetailActivity.class);
                    intent.putExtra("planVo", planVo);
                    startActivityForResult(intent, REQUEST_EDIT_TASK);
                } else {
                    Intent intent = new Intent(context, TaskDetailActivity.class);
                    intent.putExtra("planVo", planVo);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 下拉刷新
     */
    private void refreash() {
        taskList.clear();
        loadFlag = 1;
        currPage = 1;
        getData();
    }

    /**
     * 加载更多 查询下一页数据，并且判断，是否为全部数据，全部数据不再加载
     */
    private void loadMore() {
        loadFlag = 2;
        currPage = currPage + 1;
        getData();
    }


    private void getData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("empId", empId);
        jsonObject.addProperty("completedFlag", paramMap.get("completedFlag"));
        jsonObject.addProperty("userNameOrWaterMeterId", paramMap.get("userNameOrWaterMeterId"));
        jsonObject.addProperty("startDate", paramMap.get("startDate"));
        jsonObject.addProperty("endDate", paramMap.get("endDate"));
        jsonObject.addProperty("copySituation", paramMap.get("copySituation"));
        jsonObject.addProperty("queStatus", paramMap.get("queStatus"));
        jsonObject.addProperty("currPage", currPage);
        jsonObject.addProperty("pageSize", pageSize);
        jsonObject.addProperty("playType", "2");

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
                        ToastUtil.toast(getActivity(), "请求异常，请稍后重试！");
                        progress.hide();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            Gson gson = new Gson();
                            PlanResultDto resultDto = gson.fromJson(response, PlanResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                PlanListDto planListDto = resultDto.getReturnData();
                                taskTotalCnt = Integer.parseInt(planListDto.getTaskTotalCnt());
                                if (StringUtils.isEmpty(paramMap.get("completedFlag"))
                                        || "0".equals(paramMap.get("completedFlag"))) {
                                    countTextView.setText("总计" + planListDto.getTotalCnt());
                                } else if ("1".equals(paramMap.get("completedFlag"))) {
                                    countTextView.setText(
                                            "未完成" + planListDto.getTaskTotalCnt() +
                                                    "/总计" + planListDto.getTotalCnt()
                                    );
                                } else if ("2".equals(paramMap.get("completedFlag"))) {
                                    countTextView.setText(
                                            "已完成" + planListDto.getTaskTotalCnt() +
                                                    "/总计" + planListDto.getTotalCnt()
                                    );
                                }
                                if (planListDto.getTasklist() == null || planListDto.getTasklist().size() == 0) {
                                    if (loadFlag == 1) {
                                        taskList.clear();
                                        currDayTaskAdapter.notifyDataSetChanged();
                                    }
                                    ToastUtil.toast(context, "查无数据");
                                    return;
                                }
                                taskList.addAll(planListDto.getTasklist());
                                currDayTaskAdapter.notifyDataSetChanged();

                            } else {
                                ToastUtil.toast(context, resultDto.getMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.toast(context, "数据返回异常");
                        } finally {
                            progress.hide();
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        if (1 == loadFlag) {
                            if (swipeRefreshLayout != null) {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                            }
                        } else if (2 == loadFlag) {
                            if (swipeRefreshLayout != null) {
                                footerImageView.setVisibility(View.VISIBLE);
                                footerProgressBar.setVisibility(View.GONE);
                                swipeRefreshLayout.setLoadMore(false);
                            }
                        }
                    }
                });
    }

    @OnClick({R.id.search, R.id.mapButton, R.id.confirm, R.id.cancel, R.id.month_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.confirm:
                paramMap.put("userNameOrWaterMeterId", userNameOrWaterMeterId.getText().toString());
                if ("0".equals(completedFlagHidden.getText().toString())) {
                    paramMap.put("completedFlag", "");
                } else {
                    paramMap.put("completedFlag", completedFlagHidden.getText().toString());
                }
                paramMap.put("startDate", startDate.getText().toString().replace("-", ""));
                paramMap.put("endDate", endDate.getText().toString().replace("-", ""));
                taskList.clear();
                loadFlag = 1;
                currPage = 1;
                getData();
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.search:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
            case R.id.mapButton:
                Intent intent = new Intent(getActivity(), MonthTaskListMapActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.month_back:
                EventBus.getDefault().post(new FragmentEvent("",MainActivity.TASKK_PRO));
                break;
        }
    }

    private static final int REQUEST_EDIT_TASK = 101;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_TASK) {
            //提交成功刷新页面
            if (resultCode == 1) {
                refreash();
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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
//
//    /**
//     * 设置查询条件确定返回，重新加载列表
//     *
//     * @param map
//     */
//    @Override
//    public void callback(Map<String, String> map) {
//        taskList.clear();
//        paramMap.putAll(map);
//        currPage = 1;
//        getData();
//    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private View createFooterView() {
        View footerView = LayoutInflater.from(swipeRefreshLayout.getContext())
                .inflate(R.layout.layout_footer, null);
        footerProgressBar = (ProgressBar) footerView
                .findViewById(R.id.footer_pb_view);
        footerImageView = (ImageView) footerView
                .findViewById(R.id.footer_image_view);
        footerTextView = (TextView) footerView
                .findViewById(R.id.footer_text_view);
        footerProgressBar.setVisibility(View.GONE);
        footerImageView.setVisibility(View.VISIBLE);
        footerImageView.setImageResource(R.drawable.down_arrow);
        footerTextView.setText("上拉加载更多...");
        return footerView;
    }

    private View createHeaderView() {
        View headerView = LayoutInflater.from(swipeRefreshLayout.getContext())
                .inflate(R.layout.layout_head, null);
        progressBar = (ProgressBar) headerView.findViewById(R.id.pb_view);
        textView = (TextView) headerView.findViewById(R.id.text_view);
        textView.setText("下拉刷新");
        imageView = (ImageView) headerView.findViewById(R.id.image_view);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.down_arrow);
        progressBar.setVisibility(View.GONE);
        return headerView;
    }

    @Override
    public void onStart() {
        LogUtils.d("onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        LogUtils.d("onResume");
//        paramMap.clear();
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtils.d("onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        LogUtils.d("onStop");
        super.onStop();
    }

}
