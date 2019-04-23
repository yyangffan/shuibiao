package com.jiayuan.shuibiao.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.jiayuan.shuibiao.activity.HistoryTaskListMapActivity;
import com.jiayuan.shuibiao.activity.TaskDetailActivity;
import com.jiayuan.shuibiao.adapter.HistoryTaskAdapter;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.DictData;
import com.jiayuan.shuibiao.entity.DictResultDto;
import com.jiayuan.shuibiao.entity.PlanListDto;
import com.jiayuan.shuibiao.entity.PlanResultDto;
import com.jiayuan.shuibiao.entity.PlanVo;
import com.jiayuan.shuibiao.entity.SpinerBean;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.PreferencesUtil;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.jiayuan.shuibiao.view.LoadingDialog;
import com.jiayuan.shuibiao.view.SpinerPopWindow;
import com.jiayuan.shuibiao.view.datepicker.widget.CustomDatePicker;

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

public class HistoryTaskListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.main)
    View main;
    Unbinder unbinder;
    @BindView(R.id.search)
    ImageView search;

    @BindView(R.id.listview)
    ListView listview;

    HistoryTaskAdapter historyTaskAdapter;

    @BindView(R.id.swipe_refresh)
    SuperSwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.mapButton)
    ImageView mapButton;
    @BindView(R.id.countTextView)
    TextView countTextView;
    @BindView(R.id.popup_goods_noview)
    View popupGoodsNoview;
    @BindView(R.id.startDate)
    TextView startDate;
    @BindView(R.id.endDate)
    TextView endDate;
    @BindView(R.id.completedFlag)
    TextView completedFlag;
    @BindView(R.id.copySituation)
    TextView copySituation;
    @BindView(R.id.queStatus)
    TextView queStatus;
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
    @BindView(R.id.queType)
    TextView queType;

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

    private static final String GET_TASK_LIST = "/plan/queryPlan";

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

    public HistoryTaskListFragment() {
    }

    public static HistoryTaskListFragment newInstance(String param1, String param2) {
        HistoryTaskListFragment fragment = new HistoryTaskListFragment();
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
        View view = inflater.inflate(R.layout.fragment_history_task_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        initListView();
        initSearcheView();

        taskList.clear();
        getData();
        progress.show(false);
        getQueTypeDictData();
        return view;
    }

    //完成情况下拉框
    private SpinerPopWindow mSpinerPopWindow;
    private List<SpinerBean> list = new ArrayList<>();

    //抄准情况下拉框
    private SpinerPopWindow copySituationPopupWindow;
    private List<SpinerBean> copySituationList = new ArrayList<>();

    //问题水表下拉框
    private SpinerPopWindow queStatusPopupWindow;
    private List<SpinerBean> queStatusList = new ArrayList<>();

    //问题类型下拉框
    private SpinerPopWindow queTypePopupWindow;
    private List<SpinerBean> queTypeList = new ArrayList<>();

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
        customDatePicker2.setIsLoop(true); // 不允许循环滚动

        list.clear();
        //完成情况
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

        copySituationList.clear();
        //抄准情况
        copySituationList.add(new SpinerBean("0", "全部"));
        copySituationList.add(new SpinerBean("1", "抄准"));
        copySituationList.add(new SpinerBean("2", "抄错"));
        copySituationPopupWindow = new SpinerPopWindow(context, copySituationList, itemClickListener2);
        copySituationPopupWindow.setOnDismissListener(dismissListener);

        copySituation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copySituationPopupWindow.setWidth(copySituation.getWidth());
                copySituationPopupWindow.showAsDropDown(copySituation);
                setTextImage(R.drawable.icon_up);
            }
        });

        queStatusList.clear();
        //问题水表
        queStatusList.add(new SpinerBean("0", "全部"));
        queStatusList.add(new SpinerBean("1", "已提交"));
        queStatusList.add(new SpinerBean("2", "未处理"));
        queStatusList.add(new SpinerBean("3", "已处理"));
        queStatusPopupWindow = new SpinerPopWindow(context, queStatusList, itemClickListener3);
        queStatusPopupWindow.setOnDismissListener(dismissListener);

        queStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queStatusPopupWindow.setWidth(queStatus.getWidth());
                queStatusPopupWindow.showAsDropDown(queStatus);
                setTextImage(R.drawable.icon_up);
            }
        });

        //问题类型
        queTypePopupWindow = new SpinerPopWindow(context, queTypeList, itemClickListener4);
        queTypePopupWindow.setOnDismissListener(dismissListener);

        queType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queTypePopupWindow.setWidth(queType.getWidth());
                queTypePopupWindow.showAsDropDown(queType);
                setTextImage(R.drawable.icon_up);
            }
        });
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            completedFlag.setText(list.get(position).getValue());
        }
    };

    /**
     * 抄准情况选择回调
     */
    private AdapterView.OnItemClickListener itemClickListener2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            copySituationPopupWindow.dismiss();
            copySituation.setText(copySituationList.get(position).getValue());
        }
    };

    /**
     * 问题水表选择回调
     */
    private AdapterView.OnItemClickListener itemClickListener3 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            queStatusPopupWindow.dismiss();
            queStatus.setText(queStatusList.get(position).getValue());
        }
    };

    /**
     * 问题类型选择回调
     */
    private AdapterView.OnItemClickListener itemClickListener4 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            queTypePopupWindow.dismiss();
            queType.setText(queTypeList.get(position).getValue());
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
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        switch (resId) {
            case R.id.completedFlag:
                completedFlag.setCompoundDrawables(null, null, drawable, null);
                break;
            case R.id.copySituation:
                copySituation.setCompoundDrawables(null, null, drawable, null);
                break;
            case R.id.queStatus:
                queStatus.setCompoundDrawables(null, null, drawable, null);
                break;
            case R.id.queType:
                queType.setCompoundDrawables(null, null, drawable, null);
                break;
        }
    }

    public void initListView() {
        // init SuperSwipeRefreshLayout
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

                    }

                });

        historyTaskAdapter = new HistoryTaskAdapter(taskList, context);
        listview.setAdapter(historyTaskAdapter);
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
        jsonObject.addProperty("queType", paramMap.get("queType"));
        jsonObject.addProperty("currPage", currPage);
        jsonObject.addProperty("pageSize", pageSize);
        jsonObject.addProperty("playType", "3");

        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + GET_TASK_LIST)
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
                                if (StringUtils.isEmpty(paramMap.get("queStatus"))
                                        || "0".equals(paramMap.get("queStatus"))) {
                                    countTextView.setText("总计" + planListDto.getTotalCnt());
                                } else if ("1".equals(paramMap.get("queStatus"))) {
                                    countTextView.setText(
                                            "未处理" + planListDto.getTaskTotalCnt() +
                                                    "/总计" + planListDto.getTotalCnt()
                                    );
                                } else if ("2".equals(paramMap.get("queStatus"))) {
                                    countTextView.setText(
                                            "已处理" + planListDto.getTaskTotalCnt() +
                                                    "/总计" + planListDto.getTotalCnt()
                                    );
                                }
                                if (planListDto.getTasklist() == null || planListDto.getTasklist().size() == 0) {
                                    if (loadFlag == 1) {
                                        taskList.clear();
                                        historyTaskAdapter.notifyDataSetChanged();
                                    }
                                    ToastUtil.toast(context, "查无数据");
                                    return;
                                }
                                taskList.addAll(planListDto.getTasklist());
                                historyTaskAdapter.notifyDataSetChanged();

                            } else {
                                ToastUtil.toast(context, resultDto.getMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.toast(context, "数据返回异常");
                        }finally {
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
    private static final String GET_DICT_TYPE = "/sysDictData/getDictData";

    /**
     * 获取问题类型字典数据
     */
    private void getQueTypeDictData() {
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
                        ToastUtil.toast(getContext(), "请求异常，请稍后重试！");
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
                                queTypeList.clear();
                                SpinerBean spinerBean = new SpinerBean("","全部");
                                queTypeList.add(spinerBean);
                                queTypeList.addAll(list);
                                queTypePopupWindow.refreshList();
                            } else {
                                ToastUtil.toast(getContext(), resultDto.getMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.toast(getContext(), "数据返回异常");
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                    }
                });
    }

    @OnClick({R.id.search, R.id.mapButton, R.id.confirm, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                paramMap.put("userNameOrWaterMeterId", userNameOrWaterMeterId.getText().toString());
                paramMap.put("startDate", startDate.getText().toString().replace("-", ""));
                paramMap.put("endDate", endDate.getText().toString().replace("-", ""));

                String completedFlagDesc = completedFlag.getText().toString();
                if (StringUtils.isEmpty(completedFlagDesc) || "全部".equals(completedFlagDesc)) {
                    paramMap.put("completedFlag", "");
                } else if ("未完成".equals(completedFlagDesc)) {
                    paramMap.put("completedFlag", "1");
                } else if ("已完成".equals(completedFlagDesc)) {
                    paramMap.put("completedFlag", "2");
                }

                String copySituationDesc = copySituation.getText().toString();
                if (StringUtils.isEmpty(copySituationDesc) || "全部".equals(copySituationDesc)) {
                    paramMap.put("copySituation", "");
                } else if ("抄准".equals(copySituationDesc)) {
                    paramMap.put("copySituation", "1");
                } else if ("抄错".equals(copySituationDesc)) {
                    paramMap.put("copySituation", "2");
                }

                String queStatusDesc = queStatus.getText().toString();
                if (StringUtils.isEmpty(queStatusDesc) || "全部".equals(queStatusDesc)) {
                    paramMap.put("queStatus", "");
                } else if ("未处理".equals(queStatusDesc)) {
                    paramMap.put("queStatus", "1");
                } else if ("已处理".equals(queStatusDesc)) {
                    paramMap.put("queStatus", "2");
                }

                String queTypeDesc = queType.getText().toString();
                paramMap.put("queType",getSpinerBeanValueByLabel(queTypeList,queTypeDesc));

                taskList.clear();
                loadFlag = 1;
                currPage = 1;
                getData();
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.cancel:
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.search:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
            case R.id.mapButton:
                Intent intent = new Intent(getActivity(), HistoryTaskListMapActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    private static final int REQUEST_EDIT_TASK = 101;

    public static final int REQUEST_QUESTION_PROCESS = 102;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_TASK) {
            //提交成功刷新页面
            if (resultCode == 1) {
                refreash();
            }
        }
        if (requestCode == REQUEST_QUESTION_PROCESS) {
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

    public String getSpinerBeanValueByLabel(List<SpinerBean> list,String label){
        for(int i=0; i<list.size(); i++){
            SpinerBean spinerBean = list.get(i);
            if(label.equals(spinerBean.getValue())){
                return spinerBean.getKey();
            }
        }
        return "";
    }


}
