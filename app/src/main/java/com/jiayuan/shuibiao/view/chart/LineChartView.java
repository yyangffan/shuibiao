/**
 * Copyright 2014  XCL-Charts
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 	
 * @Project XCL-Charts 
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @Copyright Copyright (c) 2014 XCL-Charts (www.xclcharts.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */
package com.jiayuan.shuibiao.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XEnum;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @ClassName LineChart01View
 * @Description  折线图的例子
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 */
public class LineChartView extends BaseChartView {

	private String TAG = "LineChartView";
	private LineChart chart = new LineChart();

	//标签集合
	private LinkedList<String> labels = new LinkedList<>();
	private LinkedList<LineData> chartData = new LinkedList<>();

	private Paint mPaintTooltips = new Paint(Paint.ANTI_ALIAS_FLAG);

	private OnPointClickCallback onPointClickCallback;

	public OnPointClickCallback getOnPointClickCallback() {
		return onPointClickCallback;
	}

	public void setOnPointClickCallback(OnPointClickCallback onPointClickCallback) {
		this.onPointClickCallback = onPointClickCallback;
	}

	public LineChartView(Context context) {
		super(context);
		initView();
	}

	public LineChartView(Context context, AttributeSet attrs){
        super(context, attrs);
        initView();
	 }

	 public LineChartView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			initView();
	 }

	 public void initView()
	 {
		 	chartLabels();
			chartDataSet();
			chartRender();
		 	chart.disableScale();

			//綁定手势滑动事件
			this.bindTouch(this,chart);
	 }


	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
       //图所占范围大小
        chart.setChartRange(w,h);
    }

	private void chartRender()
	{
		try {

			//设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			int [] ltrb = getBarLnDefaultSpadding();
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

			//限制Tickmarks可滑动偏移范围
			chart.setXTickMarksOffsetMargin(ltrb[2] - 20.f);
			chart.setYTickMarksOffsetMargin(ltrb[3] - 20.f);


			//显示边框
//			chart.showRoundBorder();

			//设定数据源
			chart.setCategories(labels);
			chart.setDataSource(chartData);

			//数据轴最大值
			chart.getDataAxis().setAxisMax(100);
			//数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(25);


			//横纵坐标颜色
			chart.getDataAxis().getAxisPaint().setColor(Color.WHITE);
			chart.getCategoryAxis().getAxisPaint().setColor(Color.WHITE);

			chart.getDataAxis().getTickMarksPaint().setColor(Color.WHITE);
			chart.getCategoryAxis().getTickMarksPaint().setColor(Color.WHITE);

			chart.getDataAxis().getTickLabelPaint().setColor(Color.WHITE);
			chart.getCategoryAxis().getTickLabelPaint().setColor(Color.WHITE);

			chart.getDataAxis().getTickLabelPaint().setTextSize(25);
			chart.getCategoryAxis().getTickLabelPaint().setTextSize(25);

			//背景网格
			chart.getPlotGrid().showHorizontalLines();

			chart.getPlotArea().setBackgroundColor(true, Color.parseColor("#1e87f2"));

			chart.getPlotGrid().getHorizontalLinePaint().setStrokeWidth(2);
			chart.getPlotGrid().setHorizontalLineStyle(XEnum.LineStyle.DASH);
			chart.getPlotGrid().setVerticalLineStyle(XEnum.LineStyle.DOT);

			chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.WHITE);
			chart.getPlotGrid().getVerticalLinePaint().setColor(Color.BLUE);

			//激活点击监听
			chart.ActiveListenItemClick();
			//为了让触发更灵敏，可以扩大5px的点击监听范围
			chart.extPointClickRange(10);
			chart.showClikedFocus();

			//绘制十字交叉线
			chart.showDyLine();
			chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);

			chart.getPlotArea().extWidth(100.f);

			//调整轴显示位置
			chart.setDataAxisLocation(XEnum.AxisLocation.LEFT);
			chart.setCategoryAxisLocation(XEnum.AxisLocation.BOTTOM);

			//收缩绘图区右边分割的范围，让绘图区的线不显示出来
			chart.getClipExt().setExtRight(0.f);


		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	private void chartDataSet()
	{
		//Line 2
//		LinkedList<Double> dataSeries6= new LinkedList<>();
//		dataSeries6.add((double)50);
//		dataSeries6.add((double)52);
//		dataSeries6.add((double)53);
//		dataSeries6.add((double)55);
//		dataSeries6.add((double)40);
//		LineData lineData6 = new LineData("抄准率",dataSeries6,Color.WHITE);
//		lineData6.setDotStyle(XEnum.DotStyle.RING);
//		lineData6.getPlotLine().getDotPaint().setColor(Color.WHITE);
//		lineData6.setLabelVisible(true);
//		lineData6.getDotLabelPaint().setColor(Color.BLUE);
//		lineData6.getLabelOptions().getBox().getBackgroundPaint().setColor(Color.WHITE);
//		lineData6.getLabelOptions().getBox().setBorderLineColor(Color.WHITE);

//		chartData.add(lineData6);
	}

	private void chartLabels()
	{
//		labels.add("6月");
//		labels.add("7月");
//		labels.add("8月");
//		labels.add("9月");
//		labels.add("10月");
	}

	@Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
        	Log.e(TAG, e.toString());
        }
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			triggerClick(event.getX(),event.getY());
		}
		super.onTouchEvent(event);
		return true;
	}


	//触发监听
	private void triggerClick(float x,float y)
	{

		//交叉线
		if(chart.getDyLineVisible())chart.getDyLine().setCurrentXY(x,y);
		if(!chart.getListenItemClickStatus()){
			//交叉线
			if(chart.getDyLineVisible())
				this.invalidate();
		}else{
			PointPosition record = chart.getPositionRecord(x,y);
			if( null == record){
				if(chart.getDyLineVisible())this.invalidate();
				return;
			}

			LineData lData = chartData.get(record.getDataID());
			Double lValue = lData.getLinePoint().get(record.getDataChildID());

			//根据 dataChildID 获取月份 回调查询 该月份数据

			float r = record.getRadius();
			chart.showFocusPointF(record.getPosition(),r + r*0.5f);
			chart.getFocusPaint().setStyle(Paint.Style.STROKE);
			chart.getFocusPaint().setStrokeWidth(3);
			if(record.getDataID() >= 3){
				chart.getFocusPaint().setColor(Color.BLUE);
			}else{
				chart.getFocusPaint().setColor(Color.RED);
			}

			//在点击处显示tooltip
			mPaintTooltips.setColor(Color.RED);
			//chart.getToolTip().setCurrentXY(x,y);
			chart.getToolTip().setCurrentXY(record.getPosition().x,record.getPosition().y);

//			chart.getToolTip().addToolTip(" Key:"+lData.getLineKey(),mPaintTooltips);
//			chart.getToolTip().addToolTip(" Label:"+lData.getLabel(),mPaintTooltips);
//			chart.getToolTip().addToolTip(" Current Value:" +Double.toString(lValue),mPaintTooltips);

			//当前标签对应的其它点的值
			int cid = record.getDataChildID();
			String xLabels = "";
			for(LineData data : chartData)
			{
				if(cid < data.getLinePoint().size())
				{
					xLabels = Double.toString(data.getLinePoint().get(cid));
					chart.getToolTip().addToolTip("Line:"+data.getLabel()+","+ xLabels,mPaintTooltips);

				}
			}
			Map<String,String> map = new HashMap<>();
			map.put("value",String.valueOf(lValue));
			map.put("label",labels.get(cid));
			onPointClickCallback.pointClickCallback(map);
			this.invalidate();
		}


	}

	public LinkedList<String> getLabels() {
		return labels;
	}

	public void setLabels(LinkedList<String> labels) {
		this.labels = labels;
	}

	public LinkedList<LineData> getChartData() {
		return chartData;
	}

	public void setChartData(LinkedList<LineData> chartData) {
		this.chartData = chartData;
	}

	public interface OnPointClickCallback{
		void pointClickCallback(Map<String,String> map);
	}
	
	
}
