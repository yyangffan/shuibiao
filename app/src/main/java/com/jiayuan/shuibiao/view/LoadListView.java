package com.jiayuan.shuibiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import com.jiayuan.shuibiao.R;
import com.yinglan.scrolllayout.content.ContentListView;

public class LoadListView extends ContentListView implements AbsListView.OnScrollListener {
    private View footer;
    // 底部布局
    int totalItemCount;
    // 总数量
    int lastVisibieItem;
    // 最后一个可见的item;
    boolean isLoading;
    // 判断变量
    IloadListener iLoadListener;

    // 接口变量
    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
        // TODO Auto-generated constructor stub
    }

    // listview加载底部布局
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.footermore, null);
        // 设置隐藏底部布局
        footer.findViewById(R.id.footer_layout).setVisibility(View.GONE);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (totalItemCount == lastVisibieItem && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                footer.findViewById(R.id.footer_layout).setVisibility(View.VISIBLE);
                // 加载更多（获取接口）
                iLoadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibieItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    public void setInterface(IloadListener iLoadListener) {
        this.iLoadListener = iLoadListener;
    }

    // 加载更多数据的回调接口
    public interface IloadListener {
        void onLoad();
    }

    // 加载完成通知隐藏
    public void loadComplete() {
        isLoading = false;
        footer.findViewById(R.id.footer_layout).setVisibility(View.GONE);
    }
}