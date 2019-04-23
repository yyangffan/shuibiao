package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.chatui.adapter.ChatAdapter;
import com.jiayuan.shuibiao.chatui.adapter.CommonFragmentPagerAdapter;
import com.jiayuan.shuibiao.chatui.enity.MessageInfo;
import com.jiayuan.shuibiao.chatui.fragment.ChatEmotionFragment;
import com.jiayuan.shuibiao.chatui.fragment.ChatFunctionFragment;
import com.jiayuan.shuibiao.chatui.util.Constants;
import com.jiayuan.shuibiao.chatui.util.GlobalOnItemClickManagerUtils;
import com.jiayuan.shuibiao.chatui.widget.EmotionInputDetector;
import com.jiayuan.shuibiao.chatui.widget.NoScrollViewPager;
import com.jiayuan.shuibiao.chatui.widget.StateButton;
import com.jiayuan.shuibiao.constant.Constant;
import com.jiayuan.shuibiao.entity.CusService;
import com.jiayuan.shuibiao.entity.CusServiceOneResultDto;
import com.jiayuan.shuibiao.entity.CusServiceResultDto;
import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.callback.StringCallback;
import com.jiayuan.shuibiao.util.AndroidDes3Util;
import com.jiayuan.shuibiao.util.ToastUtil;
import com.jude.easyrecyclerview.EasyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class CustomerServiceActivity extends BaseActivity {

    @BindView(R.id.chat_list)
    EasyRecyclerView chatList;
    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.voice_text)
    TextView voiceText;
    @BindView(R.id.emotion_button)
    ImageView emotionButton;
    @BindView(R.id.emotion_add)
    ImageView emotionAdd;
    @BindView(R.id.emotion_send)
    StateButton emotionSend;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewpager;
    @BindView(R.id.emotion_layout)
    RelativeLayout emotionLayout;
    @BindView(R.id.backBtn)
    ImageView backBtn;


    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;

    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<MessageInfo> messageInfos;
    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;

    public static final String GET_HELP_URL = "/cusService/getHelp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initWidget();
        messageInfos = new ArrayList<>();
    }


    private void initWidget() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceText(voiceText)
                .build();

        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chatAdapter.addItemClickListener(itemClickListener);
        LoadData();
    }

    /**
     * item点击事件
     */
    private ChatAdapter.onItemClickListener itemClickListener = new ChatAdapter.onItemClickListener() {
        @Override
        public void onHeaderClick(int position) {
            Toast.makeText(CustomerServiceActivity.this, "onHeaderClick", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onImageClick(View view, int position) {

        }

        @Override
        public void onVoiceClick(final ImageView imageView, final int position) {

        }

        @Override
        public void onListViewItemClick(int position) {

            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setContent(cusServices.get(position).getServTitle());
            messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
            messageInfos.add(messageInfo);
            chatAdapter.add(messageInfo);
            chatList.scrollToPosition(chatAdapter.getCount() - 1);
            chatAdapter.notifyDataSetChanged();

            getHelpById(cusServices.get(position).getServId());


        }
    };

    /**
     * 通过问题ID，获取答案
     *
     * @param servId
     */
    private void getHelpById(String servId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("servId", servId);
        String requestJsonStr = "";
        try {
            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + GET_HELP_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(CustomerServiceActivity.this, "请求异常，请稍后重试！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try{
                            Gson gson = new Gson();
                            CusServiceOneResultDto resultDto = gson.fromJson(response, CusServiceOneResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                CusService cusService = resultDto.getReturnData();
                                if (cusService == null) {
                                    MessageInfo message = new MessageInfo();
                                    message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                                    message.setContent("查无结果");
                                    messageInfos.add(message);
                                    chatAdapter.add(message);
                                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                                } else {
                                    MessageInfo message = new MessageInfo();
                                    message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                                    message.setContent(cusService.getServContent());
                                    messageInfos.add(message);
                                    chatAdapter.add(message);
                                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                                }
                            } else {
                                ToastUtil.toast(CustomerServiceActivity.this, resultDto.getMsg());
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            ToastUtil.toast(CustomerServiceActivity.this, "数据返回异常");
                        }
                    }
                });
    }

    List<CusService> cusServices = new ArrayList<>();

    /**
     * 加载客服初始数据
     */
    private void LoadData() {
//        JsonObject jsonObject = new JsonObject();
        String requestJsonStr = "";
//        try {
//            requestJsonStr = AndroidDes3Util.encode(jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        OkHttpUtils.post().
                addParams("param", requestJsonStr)
                .url(Constant.BASE_URL + GET_HELP_URL)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toast(CustomerServiceActivity.this, "请求异常，请稍后重试！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try{
                            Gson gson = new Gson();
                            CusServiceResultDto resultDto = gson.fromJson(response, CusServiceResultDto.class);
                            if (StringUtils.equals(resultDto.getResult(), "1")) {
                                //获取数据，并加载
                                cusServices = resultDto.getReturnData();
                                if (cusServices == null || cusServices.size() == 0) {
                                    return;
                                }
                                MessageInfo message = new MessageInfo();
                                message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                                message.setCusServiceList(cusServices);
                                messageInfos.add(message);
                                chatAdapter.add(message);
                                chatList.scrollToPosition(chatAdapter.getCount() - 1);

                            } else {
                                ToastUtil.toast(CustomerServiceActivity.this, resultDto.getMsg());
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            ToastUtil.toast(CustomerServiceActivity.this, "数据返回异常");
                        }
                    }
                });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(final MessageInfo messageInfo) {
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
        messageInfos.add(messageInfo);
        chatAdapter.add(messageInfo);
        chatList.scrollToPosition(chatAdapter.getCount() - 1);
        messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
        chatAdapter.notifyDataSetChanged();

        getHelpById("");
    }

    @OnClick({R.id.backBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
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
