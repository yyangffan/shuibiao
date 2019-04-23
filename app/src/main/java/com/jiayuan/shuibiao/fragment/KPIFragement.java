package com.jiayuan.shuibiao.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.activity.TaskDetailActivity;
import com.jiayuan.shuibiao.adapter.HistoryTaskAdapter;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.DictData;
import com.jiayuan.shuibiao.entity.DictResultDto;
import com.jiayuan.shuibiao.entity.LineDataDto;
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
import com.jiayuan.shuibiao.view.chart.LineChartView;
import com.jiayuan.shuibiao.view.datepicker.widget.CustomDatePicker;

import org.xclcharts.chart.LineData;
import org.xclcharts.renderer.XEnum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

public class KPIFragement extends Fragment implements LineChartView.OnPointClickCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.main)
    View main;
    @BindView(R.id.lineChartView)
    LineChartView lineChartView;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.swipe_refresh)
    SuperSwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.month)
    TextView month;
    @BindView(R.id.rate)
    TextView rate;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.top)
    LinearLayout top;
    @BindView(R.id.popup_goods_noview)
    View popupGoodsNoview;
    @BindView(R.id.startMonth)
    TextView startMonth;
    @BindView(R.id.endMonth)
    TextView endMonth;
    @BindView(R.id.empKpiType)
    TextView empKpiType;
    @BindView(R.id.empKpiTypeHidden)
    TextView empKpiTypeHidden;
    @BindView(R.id.confirm)
    TextView confirm;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.ll_popup)
    LinearLayout llPopup;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.completedFlag)
    TextView completedFlag;
    @BindView(R.id.completedLayout)
    LinearLayout completedLayout;
    @BindView(R.id.arrivedFlag)
    TextView arrivedFlag;
    @BindView(R.id.arrivedLayout)
    LinearLayout arrivedLayout;
    @BindView(R.id.rightFlag)
    TextView rightFlag;
    @BindView(R.id.rightLayout)
    LinearLayout rightLayout;

    private Context context;

    private String empId;

    LoadingDialog progress;

    Unbinder unbinder;

    private String mParam1;
    private String mParam2;

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

    HistoryTaskAdapter historyTaskAdapter;

    //请求参数
    private Map<String, String> paramMap = new HashMap<>();

    private static final String GET_TASK_LIST = "/plan/queryPlan";

    private static final String GET_EMPLOYEE_KPI = "/kpi/getEmployeeKpi";

    private static final String GET_DICT_TYPE = "/sysDictData/getDictData";


    private int currPage = 1;

    private int pageSize = 25;

    private int taskTotalCnt = 0;

    private List<PlanVo> taskList = new ArrayList<>();


    private OnFragmentInteractionListener mListener;

    public KPIFragement() {

    }

    public static KPIFragement newInstance(String param1, String param2) {
        KPIFragement fragment = new KPIFragement();
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
        View view = inflater.inflate(R.layout.fragment_kpifragement, container, false);
        unbinder = ButterKnife.bind(this, view);

        initListView();
        initSearcheView();

        lineChartView.setOnPointClickCallback(this);
        taskList.clear();
        getData();
        progress.show(false);
        getEmpKpiType();
        return view;
    }

    private SpinerPopWindow mSpinerPopWindow;


    public void setList(List<SpinerBean> list) {
        this.list.clear();
        this.list.addAll(list);
        mSpinerPopWindow.refreshList();
        if (list.size() > 0) {
            empKpiType.setText(list.get(0).getValue());
            empKpiTypeHidden.setText(list.get(0).getKey());
        }
    }

    private List<SpinerBean> list = new ArrayList<>();


    private CustomDatePicker customDatePicker1;

    private CustomDatePicker customDatePicker2;

    //完成情况下拉框
    private SpinerPopWindow completedPopupWindow;
    private List<SpinerBean> completedList = new ArrayList<>();

    //抄准情况下拉框
    private SpinerPopWindow rightPopupWindow;
    private List<SpinerBean> rightList = new ArrayList<>();

    //到达下拉框
    private SpinerPopWindow arrivedPopupWindow;
    private List<SpinerBean> arrivedList = new ArrayList<>();



    private void initSearcheView() {
        //日期选择控件
        startMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDatePicker1.show(startMonth.getText().toString());
            }
        });
        endMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDatePicker2.show(endMonth.getText().toString());
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        startMonth.setText(now.split(" ")[0]);
        endMonth.setText(now.split(" ")[0]);

        customDatePicker1 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                startMonth.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2049-12-31 00:00");
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                endMonth.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", "2049-12-31 00:00");
        customDatePicker2.showSpecificTime(false); // 不显示时和分
        customDatePicker2.setIsLoop(true); // 不允许循环滚动

        mSpinerPopWindow = new SpinerPopWindow(context, list, itemClickListener);
        mSpinerPopWindow.setOnDismissListener(dismissListener);

        empKpiType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinerPopWindow.setWidth(empKpiType.getWidth());
                mSpinerPopWindow.showAsDropDown(empKpiType);
                setTextImage(R.drawable.icon_up);
            }
        });

        completedList.clear();
        //完成情况
        completedList.add(new SpinerBean("0", "全部"));
        completedList.add(new SpinerBean("1", "未完成"));
        completedList.add(new SpinerBean("2", "已完成"));
        completedPopupWindow = new SpinerPopWindow(context, completedList, itemClickListener2);
        completedPopupWindow.setOnDismissListener(dismissListener);

        completedFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completedPopupWindow.setWidth(completedFlag.getWidth());
                completedPopupWindow.showAsDropDown(completedFlag);
            }
        });

        rightList.clear();
        //抄准情况
        rightList.add(new SpinerBean("0", "全部"));
        rightList.add(new SpinerBean("1", "抄准"));
        rightList.add(new SpinerBean("2", "抄错"));
        rightPopupWindow = new SpinerPopWindow(context, rightList, itemClickListener3);
        rightPopupWindow.setOnDismissListener(dismissListener);

        rightFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightPopupWindow.setWidth(rightFlag.getWidth());
                rightPopupWindow.showAsDropDown(rightFlag);
            }
        });

        arrivedList.clear();
        //到达情况
        arrivedList.add(new SpinerBean("0", "全部"));
        arrivedList.add(new SpinerBean("1", "未到达"));
        arrivedList.add(new SpinerBean("2", "已到达"));
        arrivedPopupWindow = new SpinerPopWindow(context, rightList, itemClickListener4);
        arrivedPopupWindow.setOnDismissListener(dismissListener);

        arrivedFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrivedPopupWindow.setWidth(arrivedFlag.getWidth());
                arrivedPopupWindow.showAsDropDown(arrivedFlag);
            }
        });

    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            empKpiType.setText(list.get(position).getValue());
            empKpiTypeHidden.setText(list.get(position).getKey());

            if("1".equals(list.get(position).getKey())){
                completedLayout.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.GONE);
                arrivedLayout.setVisibility(View.GONE);
            }else if("2".equals(list.get(position).getKey())){
                completedLayout.setVisibility(View.GONE);
                rightLayout.setVisibility(View.VISIBLE);
                arrivedLayout.setVisibility(View.GONE);
            }else if("3".equals(list.get(position).getKey())){
                completedLayout.setVisibility(View.GONE);
                rightLayout.setVisibility(View.GONE);
                arrivedLayout.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * 完成情况回调
     */
    private AdapterView.OnItemClickListener itemClickListener2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            completedPopupWindow.dismiss();
            completedFlag.setText(completedList.get(position).getValue());
        }
    };

    /**
     * 抄准情况回调
     */
    private AdapterView.OnItemClickListener itemClickListener3 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            rightPopupWindow.dismiss();
            rightFlag.setText(rightList.get(position).getValue());
        }
    };

    /**
     * 到达情况回调
     */
    private AdapterView.OnItemClickListener itemClickListener4 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            arrivedPopupWindow.dismiss();
            arrivedFlag.setText(arrivedList.get(position).getValue());
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
        empKpiType.setCompoundDrawables(null, null, drawable, null);
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
                    //未抄表不查看

                } else {
                    Intent intent = new Intent(context, TaskDetailActivity.class);
                    intent.putExtra("planVo", planVo);
                    startActivity(intent);
                }
            }
        });
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

    @OnClick({R.id.search, R.id.cancel, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                paramMap.put("empKpiType", empKpiTypeHidden.getText().toString());
                paramMap.put("empKpiTypeLabel", empKpiType.getText().toString());
                paramMap.put("startDate", startMonth.getText().toString().replace("-", ""));
                paramMap.put("endDate", endMonth.getText().toString().replace("-", ""));

                String completedFlagDesc = completedFlag.getText().toString();
                if (StringUtils.isEmpty(completedFlagDesc) || "全部".equals(completedFlagDesc)) {
                    paramMap.put("completedFlag", "");
                } else if ("未完成".equals(completedFlagDesc)) {
                    paramMap.put("completedFlag", "1");
                } else if ("已完成".equals(completedFlagDesc)) {
                    paramMap.put("completedFlag", "2");
                }

                String rightDesc = rightFlag.getText().toString();
                if (StringUtils.isEmpty(rightDesc) || "全部".equals(rightDesc)) {
                    paramMap.put("copySituation", "");
                } else if ("抄准".equals(rightDesc)) {
                    paramMap.put("copySituation", "1");
                } else if ("抄错".equals(rightDesc)) {
                    paramMap.put("copySituation", "2");
                }

                String arrivedDesc = arrivedFlag.getText().toString();
                if (StringUtils.isEmpty(arrivedDesc) || "全部".equals(arrivedDesc)) {
                    paramMap.put("arrived", "");
                } else if ("未到达".equals(arrivedDesc)) {
                    paramMap.put("arrived", "1");
                } else if ("已到达".equals(arrivedDesc)) {
                    paramMap.put("arrived", "2");
                }

                taskList.clear();
                loadFlag = 1;
                currPage = 1;
                getData();
                getKpiData();
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.cancel:
                drawerLayout.closeDrawer(GravityCompat.END);
                break;
            case R.id.search:
                drawerLayout.openDrawer(GravityCompat.END);
                break;

        }
    }

    /**
     * 获取抄表kpi数据
     */
    private void getKpiData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("empId", empId);
        String startDate = paramMap.get("startDate");
        if (!StringUtils.isEmpty(startDate) && startDate.length() > 6) {
            jsonObject.addProperty("startMonth", paramMap.get("startDate").substring(0, 6));
        } else {
            jsonObject.addProperty("startMonth", paramMap.get("startDate"));
        }

        String endDate = paramMap.get("endDate");
        if (!StringUtils.isEmpty(endDate) && endDate.length() > 6) {
            jsonObject.addProperty("endMonth", paramMap.get("endDate").substring(0, 6));
        } else {
            jsonObject.addProperty("endMonth", paramMap.get("endDate"));
        }

        jsonObject.addProperty("empKpiType", paramMap.get("empKpiType"));

        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + GET_EMPLOYEE_KPI)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(getActivity(), "请求异常，请稍后重试！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            Gson gson = new Gson();
                            LineDataDto resultDto = gson.fromJson(response, LineDataDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                initKpiLineChart(resultDto.getReturnData());
                            } else {
                                ToastUtil.toast(context, resultDto.getMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.toast(context, "数据返回异常");
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                    }
                });
    }


    public void initKpiLineChart(com.jiayuan.shuibiao.entity.LineData resultLineData) {

        LinkedList<String> labels = lineChartView.getLabels();

        labels.clear();

        labels.addAll(resultLineData.getxAxis());

        LinkedList<LineData> chartData = lineChartView.getChartData();

        chartData.clear();

        LinkedList<Double> dataSeries6 = new LinkedList<>();

        for (int i = 0; i < resultLineData.getSeries().size(); i++) {
            dataSeries6.add(Double.valueOf(resultLineData.getSeries().get(i)));
        }

        LineData lineData6 = new LineData(paramMap.get("empKpiTypeLabel"), dataSeries6, Color.WHITE);
        lineData6.setDotStyle(XEnum.DotStyle.RING);
        lineData6.getPlotLine().getDotPaint().setColor(Color.WHITE);
        lineData6.setLabelVisible(true);
        lineData6.getDotLabelPaint().setColor(Color.BLUE);
        lineData6.getLabelOptions().getBox().getBackgroundPaint().setColor(Color.WHITE);
        lineData6.getLabelOptions().getBox().setBorderLineColor(Color.WHITE);

        chartData.add(lineData6);

        lineChartView.refreshChart();
    }

    /**
     * 列表数据
     */
    private void getData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("empId", empId);
        jsonObject.addProperty("completedFlag", paramMap.get("completedFlag"));
        jsonObject.addProperty("userNameOrWaterMeterId", paramMap.get("userNameOrWaterMeterId"));
        jsonObject.addProperty("startDate", paramMap.get("startDate"));
        jsonObject.addProperty("endDate", paramMap.get("endDate"));
        jsonObject.addProperty("copySituation", paramMap.get("copySituation"));
        jsonObject.addProperty("queStatus", paramMap.get("queStatus"));
        jsonObject.addProperty("arrived", paramMap.get("arrived"));
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


    /**
     * 获取抄表类型
     */
    public void getEmpKpiType() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("dictType", "emp_kpi_indicator_type");

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
                        ToastUtil.toast(getActivity(), "请求异常，请稍后重试！");
                        progress.hide();
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

                                setList(list);

                                if (list != null && list.size() > 0) {
                                    SpinerBean spinerBean = list.get(0);
                                    paramMap.put("empKpiType", spinerBean.getKey());
                                    paramMap.put("empKpiTypeLabel", spinerBean.getValue());
                                    //设置指标类型，查询kpi折线图
                                    getKpiData();
                                }
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
                    }
                });
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void pointClickCallback(Map<String, String> map) {
        month.setText(map.get("label"));
        rate.setText(paramMap.get("empKpiTypeLabel") + ":" + map.get("value") + "%");

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
