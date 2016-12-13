package com.mcucsie.flippedclass.exam;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.view.View;

public class GradeBarChart {
	
	private String[] xTitle_gradename = {"10","20","30","40","50","60","70","80","90","100"};
	
	public View GetBarChart(String chartTitle, String XTitle, String YTitle, String[] xy,Context context){

        XYSeries Series = new XYSeries(YTitle);

        XYMultipleSeriesDataset Dataset = new XYMultipleSeriesDataset();            
        Dataset.addSeries(Series);

        XYMultipleSeriesRenderer Renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer yRenderer = new XYSeriesRenderer();       
        Renderer.addSeriesRenderer(yRenderer);

        //Renderer.setApplyBackgroundColor(true);           //設定背景顏色
        //Renderer.setBackgroundColor(Color.BLACK);         //設定圖內圍背景顏色

        Renderer.setMarginsColor(Color.WHITE);              //設定圖外圍背景顏色
        Renderer.setTextTypeface(null, Typeface.BOLD);      //設定文字style



        Renderer.setShowGrid(true);                         //設定網格
        Renderer.setGridColor(Color.GRAY);                  //設定網格顏色  
        
        Renderer.setChartTitle(chartTitle);                 //設定標頭文字
        Renderer.setLabelsColor(Color.BLACK);               //設定標頭文字顏色
        Renderer.setChartTitleTextSize(20);                 //設定標頭文字大小
        
        Renderer.setAxesColor(Color.BLACK);                 //設定雙軸顏色
        Renderer.setBarSpacing(0.5);                        //設定bar間的距離
        
        Renderer.setXTitle(XTitle);                       //設定X軸文字     
        //Renderer.setYTitle("人");                       //設定Y軸文字
        
        Renderer.setXLabelsColor(Color.BLACK);              //設定X軸文字顏色
        Renderer.setYLabelsColor(0, Color.BLACK);           //設定Y軸文字顏色
        
        Renderer.setXLabelsAlign(Align.CENTER);             //設定X軸文字置中
        Renderer.setYLabelsAlign(Align.CENTER);             //設定Y軸文字置中
        
        //Renderer.setXLabelsAngle(-25);                      //設定X軸文字傾斜度
        Renderer.setXLabels(0);                             //設定X軸不顯示數字, 改以程式設定文字
        Renderer.setYAxisMin(0);                            //設定Y軸文最小值
        yRenderer.setColor(Color.parseColor("#FFCC22"));                      //設定Series顏色
        //yRenderer.setDisplayChartValues(true);            //展現Series數值
        
        Renderer.setPanEnabled(false, false);  			//固定圖表顯示範圍

        Series.add(0, 0);
        Renderer.addXTextLabel(0, "");
        for(int r=0; r<xy.length; r++) {
            //Log.i("DEBUG", (r+1)+" "+xy[r][0]+"; "+xy[r][1]);
            Renderer.addXTextLabel(r+1, xTitle_gradename[r]);
            Series.add(r+1, Integer.parseInt(xy[r]));    
        }
       
        Series.add(11, 0);
        Renderer.addXTextLabel(xy.length+1, "");
        View view = ChartFactory.getBarChartView(context, Dataset, Renderer, Type.DEFAULT);                           
        return view;
}
}
