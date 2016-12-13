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

        //Renderer.setApplyBackgroundColor(true);           //�]�w�I���C��
        //Renderer.setBackgroundColor(Color.BLACK);         //�]�w�Ϥ���I���C��

        Renderer.setMarginsColor(Color.WHITE);              //�]�w�ϥ~��I���C��
        Renderer.setTextTypeface(null, Typeface.BOLD);      //�]�w��rstyle



        Renderer.setShowGrid(true);                         //�]�w����
        Renderer.setGridColor(Color.GRAY);                  //�]�w�����C��  
        
        Renderer.setChartTitle(chartTitle);                 //�]�w���Y��r
        Renderer.setLabelsColor(Color.BLACK);               //�]�w���Y��r�C��
        Renderer.setChartTitleTextSize(20);                 //�]�w���Y��r�j�p
        
        Renderer.setAxesColor(Color.BLACK);                 //�]�w���b�C��
        Renderer.setBarSpacing(0.5);                        //�]�wbar�����Z��
        
        Renderer.setXTitle(XTitle);                       //�]�wX�b��r     
        //Renderer.setYTitle("�H");                       //�]�wY�b��r
        
        Renderer.setXLabelsColor(Color.BLACK);              //�]�wX�b��r�C��
        Renderer.setYLabelsColor(0, Color.BLACK);           //�]�wY�b��r�C��
        
        Renderer.setXLabelsAlign(Align.CENTER);             //�]�wX�b��r�m��
        Renderer.setYLabelsAlign(Align.CENTER);             //�]�wY�b��r�m��
        
        //Renderer.setXLabelsAngle(-25);                      //�]�wX�b��r�ɱ׫�
        Renderer.setXLabels(0);                             //�]�wX�b����ܼƦr, ��H�{���]�w��r
        Renderer.setYAxisMin(0);                            //�]�wY�b��̤p��
        yRenderer.setColor(Color.parseColor("#FFCC22"));                      //�]�wSeries�C��
        //yRenderer.setDisplayChartValues(true);            //�i�{Series�ƭ�
        
        Renderer.setPanEnabled(false, false);  			//�T�w�Ϫ���ܽd��

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
