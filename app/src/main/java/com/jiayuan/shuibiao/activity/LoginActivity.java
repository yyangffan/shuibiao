package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jaeger.library.StatusBarUtil;
import com.jiayuan.shuibiao.App;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.LoginResultDto;
import com.jiayuan.shuibiao.entity.WarterEmployee;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.PreferencesUtil;
import com.jiayuan.shuibiao.util.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 登陆页面
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.login_usernameEditText)
    EditText loginUsernameEditText;
    @BindView(R.id.login_passwordEditText)
    EditText loginPasswordEditText;
    @BindView(R.id.loginpanel)
    LinearLayout loginpanel;
//    @BindView(R.id.forgetPassword)
//    TextView forgetPassword;
    @BindView(R.id.login_btn)
    TextView loginBtn;
    @BindView(R.id.autoLoginBtn)
    CheckBox autoLoginBtn;
    @BindView(R.id.ip)
    EditText ip;
    @BindView(R.id.port)
    EditText port;


    private static final String LOGIN_URL = "/employee/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBar();
        ButterKnife.bind(this);

        Drawable drawable = getResources().getDrawable(R.drawable.name);
        drawable.setBounds(0, 0, 40, 40);
        //第一个 0 是距左边距离，第二个 0 是距上边距离，40 分别是长宽
        loginUsernameEditText.setCompoundDrawables(drawable, null, null, null);//只放左边
        Drawable drawable2 = getResources().getDrawable(R.drawable.password);
        drawable2.setBounds(0, 0, 40, 40);
        loginPasswordEditText.setCompoundDrawables(drawable2, null, null, null);

        judgeAutoLogin();

    }

    private void judgeAutoLogin() {
        String flag = PreferencesUtil.readPreference(getBaseContext(), Constant.AUTO_LOGIN_FLAG);
        if (StringUtils.equals(flag, Constant.AUTO_LOGIN)) {
            App.getInstance().exitAllActivity();
            startActivity(MainActivity.class);




//            requestJsonStr = PreferencesUtil.readPreference(getBaseContext(), Constant.AUTO_LOGIN_REQ);
//            OkHttpUtils.post().
//                    addParams("param", requestJsonStr)
//                    .url(Constant.BASE_URL + LOGIN_URL)
//                    .build()
//                    .execute(new StringCallback() {
//                        @Override
//                        public void onError(Call call, Exception e, int id) {
//                            showToast("登陆异常，请稍后重试！");
//                        }
//
//                        @Override
//                        public void onResponse(String response, int id) {
//                            try{
//                                Gson gson = new Gson();
//                                LoginResultDto resultDto = gson.fromJson(response, LoginResultDto.class);
//                                if (StringUtils.equals(resultDto.getResult(), "1")) {
//                                    App.getInstance().exitAllActivity();
//                                    startActivity(MainActivity.class);
//                                } else {
//                                    showToast(resultDto.getMsg());
//                                }
//                            }catch(Exception e){
//                                e.printStackTrace();
//                                ToastUtil.toast(LoginActivity.this, "数据返回异常");
//                            }
//
//                        }
//                    });

        }
    }

    @OnClick({R.id.login_usernameEditText, R.id.login_passwordEditText, R.id.login_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_usernameEditText:
                break;
            case R.id.login_passwordEditText:
                break;
//            case R.id.forgetPassword:
//                Intent intent = new Intent(this, ForgetPasswordActivity.class);
//                startActivity(intent);
//                break;
            case R.id.login_btn:
                String ipStr = ip.getText().toString();
                String portStr = port.getText().toString();
                if(!StringUtils.isEmpty(ipStr) && !StringUtils.isEmpty(portStr)){
                    Constant.BASE_URL = "http://"+ipStr+":"+portStr+"/shuibiao/f/mobile";
                }
                login();
                break;
        }
    }

    private String requestJsonStr = "";

    private String empId = "";

    public void login() {
        loginBtn.setEnabled(false);
        empId = loginUsernameEditText.getText().toString();
        String pwd = loginPasswordEditText.getText().toString();
        if (StringUtils.isEmpty(empId)) {
            Toast.makeText(LoginActivity.this, "请输入工号!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "请输入密码!", Toast.LENGTH_SHORT).show();
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("empId", empId);
        jsonObject.addProperty("pwd", pwd);
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
//            requestJsonStr = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //result 1 是正常    msg  描述
        //returnDate
        progress.show(false);

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + LOGIN_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showToast("登陆异常，请稍后重试！");
                        loginBtn.setEnabled(true);
                        progress.hide();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("response:", response);
                        try {
                            Gson gson = new Gson();
                            LoginResultDto resultDto = gson.fromJson(response, LoginResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //登陆成功，判断是否自动登陆，如果选中自动登陆，缓存账号密码（加密后的）
                                updateAutoLoginFlag(resultDto);
                                App.getInstance().exitAllActivity();
                                startActivity(MainActivity.class);
                            } else {
                                showToast(resultDto.getMsg());
                            }
                        } catch (Exception e) {
                            ToastUtil.toast(LoginActivity.this, "数据返回解析异常");
                        }finally {
                            progress.hide();
                        }
                        loginBtn.setEnabled(true);

                    }
                });
    }

    private void updateAutoLoginFlag(LoginResultDto loginResultDto) {
        //登陆成功将工号缓存
        PreferencesUtil.writePreference(getBaseContext(), Constant.EMPID, empId);

        List<WarterEmployee> warterEmployee = loginResultDto.getReturnData();

        PreferencesUtil.writePreference(getBaseContext(), Constant.TEAM,warterEmployee.get(0).getTeam());

        PreferencesUtil.writePreference(getBaseContext(), Constant.EMPNAME, warterEmployee.get(0).getEmpName());



        if (autoLoginBtn.isChecked()) {
            PreferencesUtil.writePreference(getBaseContext(), Constant.AUTO_LOGIN_FLAG, Constant.AUTO_LOGIN);
            PreferencesUtil.writePreference(getBaseContext(), Constant.AUTO_LOGIN_REQ, requestJsonStr);
        } else {
            PreferencesUtil.writePreference(getBaseContext(), Constant.AUTO_LOGIN_FLAG, Constant.UNAUTO_LOGIN);
            PreferencesUtil.writePreference(getBaseContext(), Constant.AUTO_LOGIN_REQ, "");
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

    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.common_blue), 0);
    }

    //退出时的时间
    private long mExitTime; //对返回键进行监听

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //退出方法
    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(LoginActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            //用户退出处理
            finish();
            App.getInstance().exitAllActivity();
            System.exit(0);
        }
    }

}
