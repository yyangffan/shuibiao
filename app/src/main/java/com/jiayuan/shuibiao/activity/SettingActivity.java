package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiayuan.shuibiao.App;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.util.PreferencesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.logout_btn)
    TextView logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.backBtn, R.id.logout_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.logout_btn:
                App.getInstance().exitAllActivity();
                //注销时，清空自动登录标志
                PreferencesUtil.writePreference(getBaseContext(), Constant.AUTO_LOGIN_FLAG, Constant.UNAUTO_LOGIN);
                PreferencesUtil.writePreference(getBaseContext(), Constant.AUTO_LOGIN_REQ, "");
                startActivity(new Intent(this,LoginActivity.class));
                break;

        }
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
