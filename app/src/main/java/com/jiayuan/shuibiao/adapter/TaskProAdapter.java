package com.jiayuan.shuibiao.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.entity.TaskProEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskProAdapter extends RecyclerView.Adapter<TaskProAdapter.ViewHolder> {
    private Context mContext;
    private List<TaskProEntity.ReturnDataBean.TasklistBean> mLists;
    private LayoutInflater mInflater;
    private OnRecyItemClickListener mOnRecyItemClickListener;

    public TaskProAdapter(Context context, List<TaskProEntity.ReturnDataBean.TasklistBean> stringList) {
        mContext = context;
        mLists = stringList;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setOnRecyItemClickListener(OnRecyItemClickListener onRecyItemClickListener) {
        mOnRecyItemClickListener = onRecyItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_taskpro, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, final int position) {
        TaskProEntity.ReturnDataBean.TasklistBean bean = mLists.get(position);
        String allready = bean.getNum();
        String all = bean.getTotalnum();
        vh.mItemTaskproDate.setText((position+1)+"号");
        vh.mItemTaskproAllready.setText(allready);
        vh.mItemTaskproAll.setText(all);

        vh.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnRecyItemClickListener != null) {
                    mOnRecyItemClickListener.onRecyItemClickListener(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLists == null ? 0 : mLists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_taskpro_date)
        TextView mItemTaskproDate;
        @BindView(R.id.item_taskpro_allready)
        TextView mItemTaskproAllready;
        @BindView(R.id.item_taskpro_all)
        TextView mItemTaskproAll;
        private View mView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    public interface OnRecyItemClickListener {
        void onRecyItemClickListener(int position);
    }
}
