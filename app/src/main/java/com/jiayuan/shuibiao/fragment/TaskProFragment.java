package com.jiayuan.shuibiao.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.activity.MainActivity;
import com.jiayuan.shuibiao.adapter.TaskProAdapter;
import com.jiayuan.shuibiao.entity.TaskProEntity;
import com.jiayuan.shuibiao.eventbus.FragmentEvent;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.RecycleGridDivider;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.jiayuan.shuibiao.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

//新增 任务进度界面
public class TaskProFragment extends Fragment {
    private View mView;
    private RecyclerView mRecyclerView;

    private List<TaskProEntity.ReturnDataBean.TasklistBean> mList;
    private TaskProAdapter mTaskProAdapter;
    LoadingDialog progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_task, container, false);
        init();
        return mView;
    }

    private void init() {
        progress = new LoadingDialog(this.getActivity());
        mRecyclerView = mView.findViewById(R.id.task_pro_recy);
        mList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mTaskProAdapter = new TaskProAdapter(this.getActivity(), mList);
        mRecyclerView.addItemDecoration(new RecycleGridDivider(12));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mTaskProAdapter);
        mTaskProAdapter.setOnRecyItemClickListener(new TaskProAdapter.OnRecyItemClickListener() {
            @Override
            public void onRecyItemClickListener(int position) {
                TaskProEntity.ReturnDataBean.TasklistBean tasklistBean = mList.get(position);
                String meterdate = tasklistBean.getMeterdate();
                EventBus.getDefault().post(new FragmentEvent(meterdate, MainActivity.MONTH_TASK_CODE));
            }
        });
        getData();
    }

    //获取数据
    private void getData() {
        progress.show(true);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("empid", "106");
        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url("http://10.10.30.165:8090/shuibiao/f/mobile/meterdata/queryMeterDayCountList")
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
                            TaskProEntity resultDto = gson.fromJson(response, TaskProEntity.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                mList.addAll(resultDto.getReturnData().getTasklist());
                                mTaskProAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.toast(TaskProFragment.this.getActivity(), "数据返回异常");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.toast(TaskProFragment.this.getActivity(), "数据返回异常");
                        } finally {
                            progress.hide();
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        progress.hide();
                    }
                });
    }

}
