package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.eventbus.FragmentEvent;
import com.jiayuan.shuibiao.fragment.CurrDayTaskListFragment;
import com.jiayuan.shuibiao.fragment.HistoryTaskListFragment;
import com.jiayuan.shuibiao.fragment.HomeFragment;
import com.jiayuan.shuibiao.fragment.KPIFragement;
import com.jiayuan.shuibiao.fragment.MonthTaskListFragment;
import com.jiayuan.shuibiao.fragment.PersonCenterFragment;
import com.jiayuan.shuibiao.fragment.TaskProFragment;
import com.jiayuan.shuibiao.service.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {


    //    @BindView(R.id.weatherView)
//    TextView mWeatherView;
//    @BindView(R.id.work_number)
//    TextView mWorkNumber;
    @BindView(R.id.frag_container)
    FrameLayout mFragContainer;
    @BindView(R.id.btns_container)
    LinearLayout mBtnsContainer;

    private List<Fragment> list = new ArrayList<Fragment>();
    private Map<String, Fragment> fragMap = new HashMap<String, Fragment>();

    public static final String HOME_CODE = "homepage";
    public static final String PERSON_CENTER_CODE = "personcenterpage";

    public static final String DAY_TASK_CODE = "daytaskpage";
    public static final String MONTH_TASK_CODE = "monthtaskpage";
    public static final String HISTORY_TASK_CODE = "historytaskpage";
    public static final String KPI_CODE = "kpicode";
    public static final String TASKK_PRO = "TASK_PRO";//新增任务进度
    private String fragment_msg = "";


    private HomeFragment home;
    private PersonCenterFragment person;
    private CurrDayTaskListFragment currDay;
    private MonthTaskListFragment monthTask;
    private HistoryTaskListFragment historyTask;
    private KPIFragement kpiFragement;
    private TaskProFragment mTaskProFragment;

    // 填充viewPager的Fragment
    private Class fragArray[];
    private String currentCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setStatusBar();
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
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

    /**
     * 初始化
     */
    private void initView() {
        initPager();
        initBottom();
    }

    /**
     * 初始化Fragment
     */
    private void initPager() {
        home = new HomeFragment();
        person = new PersonCenterFragment();
        currDay = new CurrDayTaskListFragment();
        monthTask = new MonthTaskListFragment();
        mTaskProFragment = new TaskProFragment();
        historyTask = new HistoryTaskListFragment();
        kpiFragement = new KPIFragement();
        //添加“tab_ic_norm_home”到底部页签的第一个
        fragMap.put(HOME_CODE, home);
        fragMap.put(PERSON_CENTER_CODE, person);
        fragMap.put(DAY_TASK_CODE, currDay);
        fragMap.put(MONTH_TASK_CODE, monthTask);
        fragMap.put(HISTORY_TASK_CODE, historyTask);
        fragMap.put(KPI_CODE, kpiFragement);
        fragMap.put(TASKK_PRO, mTaskProFragment);

        fragArray = new Class[7];
        fragArray[0] = home.getClass();
        fragArray[1] = person.getClass();
        fragArray[2] = currDay.getClass();
        fragArray[3] = monthTask.getClass();
        fragArray[4] = historyTask.getClass();
        fragArray[5] = kpiFragement.getClass();
        fragArray[6] = mTaskProFragment.getClass();
        showFragment(HOME_CODE);
    }

    private void showFragment(String code) {
        if (!code.equals(currentCode)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!currentCode.equals("")) {
                boolean bIsHide = false;
                if (bIsHide) {
                    transaction.hide(fragMap.get(currentCode));
                } else {
                    transaction.detach(fragMap.get(currentCode));
                }
            }
            boolean bIsHide = false;

            Fragment fragment = fragMap.get(code);
            if (!fragment_msg.equals("")&&fragment instanceof MonthTaskListFragment) {
                Bundle bundle = new Bundle();
                bundle.putString("msg", fragment_msg);
                fragment.setArguments(bundle);
            }
            if (!bIsHide) {
                if (!fragment.isDetached()) {
                    transaction.add(R.id.frag_container, fragment, code);
                } else {
                    transaction.attach(fragment);
                }
            } else {
                if (!fragment.isHidden()) {
                    transaction.add(R.id.frag_container, fragment, code);
                } else {
                    transaction.show(fragment);
                }

            }
            transaction.commit();
            currentCode = code;
        }
    }

    /**
     * 初始化底部按钮
     */
    private void initBottom() {
        final int count = fragMap.size();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;

        for (int i = 0; i < count; i++) {
            final View btn = getTabItemView(i);
            btn.setLayoutParams(params);
            if (i >= 2) {
                btn.setVisibility(View.GONE);
            }
            mBtnsContainer.addView(btn);
            final int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalI == 0) {
                        showFragment(HOME_CODE);
                        for (int i = 0; i < count; i++) {
                            mBtnsContainer.getChildAt(i).findViewById(R.id.imageview).setSelected(i == finalI);
                            mBtnsContainer.getChildAt(i).findViewById(R.id.textview).setSelected(i == finalI);
                        }
                    } else {
                        showFragment(PERSON_CENTER_CODE);
                        for (int i = 0; i < count; i++) {
                            mBtnsContainer.getChildAt(i).findViewById(R.id.imageview).setSelected(i == finalI);
                            mBtnsContainer.getChildAt(i).findViewById(R.id.textview).setSelected(i == finalI);
                        }
                    }
                }
            });
        }

        //设置初始化状态
        mBtnsContainer.getChildAt(0).findViewById(R.id.imageview).setSelected(true);
        mBtnsContainer.getChildAt(0).findViewById(R.id.textview).setSelected(true);
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {

        View view = View.inflate(this, R.layout.tab_item, null);
//
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        int resId;
        if (index == 0) {
            resId = R.drawable.home_selector;
        } else {
            resId = R.drawable.me_selector;
        }
        imageView.setImageResource(resId);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(index == 0 ? "首页" : "我的");
        return view;
    }


    @Subscribe
    public void onEventMainThread(FragmentEvent fragmentEvent) {
        fragment_msg = fragmentEvent.getMsg();
        showFragment(fragmentEvent.getFragmentCode());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.common_blue), 0);
        StatusBarUtil.setTranslucentForImageViewInFragment(
                MainActivity.this, 0, findViewById(R.id.main_layout));
    }


    @Override
    protected void onResume() {
        super.onResume();

        Intent startIntent = new Intent(MainActivity.this, LocationServices.class);
        startService(startIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
