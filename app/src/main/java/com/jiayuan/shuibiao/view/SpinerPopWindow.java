package com.jiayuan.shuibiao.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.entity.SpinerBean;

import java.util.List;


/**
 */
public class SpinerPopWindow extends PopupWindow {
	private LayoutInflater inflater;
	private ListView mListView;
	private List<SpinerBean> list;
	private MyAdapter  mAdapter;
	private int position = -1;

	public ListView getmListView() {
		return mListView;
	}

	public void setmListView(ListView mListView) {
		this.mListView = mListView;
	}

	public MyAdapter getmAdapter() {
		return mAdapter;
	}

	public void setmAdapter(MyAdapter mAdapter) {
		this.mAdapter = mAdapter;
	}

	public void refreshList(){
		mAdapter.notifyDataSetChanged();
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public SpinerPopWindow(Context context,List<SpinerBean> list,OnItemClickListener clickListener) {
		super(context);
		inflater=LayoutInflater.from(context);
		this.list=list;
		init(clickListener);
	}
	
	private void init(OnItemClickListener clickListener){
		View view = inflater.inflate(R.layout.spiner_window_layout, null);
		setContentView(view);		
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
    		ColorDrawable dw = new ColorDrawable(0x00);
		setBackgroundDrawable(dw);
		mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setAdapter(mAdapter=new MyAdapter());
		mListView.setOnItemClickListener(clickListener);
	}
	
	private class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder=null;
			if(convertView==null){
				holder=new ViewHolder();
				convertView=inflater.inflate(R.layout.spiner_item_layout, null);
				holder.tvName=(TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			holder.tvName.setText(list.get(position).getValue());
			return convertView;
		}
	}
	
	private class ViewHolder{
		private TextView tvName;
	}


	@Override
	public void showAsDropDown(View anchor) {
		if (Build.VERSION.SDK_INT >= 24) {
			Rect visibleFrame = new Rect();
			anchor.getGlobalVisibleRect(visibleFrame);
			int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
			setHeight(height);
		}
		super.showAsDropDown(anchor);
	}
}
