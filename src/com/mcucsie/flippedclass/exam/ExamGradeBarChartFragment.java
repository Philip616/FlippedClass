package com.mcucsie.flippedclass.exam;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SlidingPaneLayout.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class ExamGradeBarChartFragment extends Fragment{

	private ProgressBar pgb;
	
	String now_exam_index;
	String[] grade,str_count_for_people;
	int[] int_grade,count_for_people;
	
	private GetNowCourseInfo course_info;
	private String course_ID;
	
	private LinearLayout barchart_gradeBarChart;
	private Button btn_changetodetailgrade;
    private View vChart;
    
    static Handler mHandler;
    setViewToNormalRunnable setViewToNormalRunnable;
    boolean setViewToNormalRunnable_isRunning = false;
    boolean WaitForData_isRunning = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_barchart, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		barchart_gradeBarChart = (LinearLayout)getView().findViewById(R.id.barchart_gradeBarChart);
		btn_changetodetailgrade = (Button) getView().findViewById(R.id.btn_changetodetailgrade);
		pgb=(ProgressBar)getView().findViewById(R.id.pgb_exam_tchgradeBarChart);
		
		btn_changetodetailgrade.setOnClickListener(btn_changetodetailgrade_click);
		
		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);
		
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
		
		Bundle bundle = getArguments();
        if (bundle != null) {
        	now_exam_index = bundle.getString("now_exam_index");
        }
        
        mHandler = new Handler();
        WaitForData_isRunning = true;
        setViewToNormalRunnable_isRunning = true;
		
		DataFromDatabase MySQL_DB = new DataFromDatabase();
		MySQL_DB.FC_GetAllStdGrade(course_ID, now_exam_index);
		
		Thread waitdata = new Thread(new WaitForData(MySQL_DB));
		waitdata.start();
	}
	
private OnClickListener btn_changetodetailgrade_click= new OnClickListener(){
		
    	@Override
		public void onClick(View v) {
    		ExamGradeTeacherFragment frag_tchgrade = new ExamGradeTeacherFragment();
			getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_tchgrade).commit();	
		}
	};
	
	class WaitForData implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		
		public WaitForData(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					if(WaitForData_isRunning)
					{
						Thread.sleep(500);
						
						String result = MySQL_DB.return_result;
						
						while(result==null && times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "ExamGradeBarChartFragment Waitfodata 多等了1秒");
							result = MySQL_DB.return_result;
							times++;
						}
						
						Log.d("=====>", "ExamGradeBarChartFragment Waitfodata get result = " + result);
						
						setViewToNormalRunnable = new setViewToNormalRunnable(result);
						
						mHandler.post(setViewToNormalRunnable);
					}
				} 	

	
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	class setViewToNormalRunnable implements Runnable
	{
		String result;
		int times = 0;
		
		public setViewToNormalRunnable(String result)
		{
			this.result = result;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			JSONArray jsonArray;
			
			try {
					if(setViewToNormalRunnable_isRunning)
					{
						jsonArray = new JSONArray(result);
						
						grade = new String[jsonArray.length()];
						int_grade = new int[jsonArray.length()];
						
						count_for_people = new int[10];
						str_count_for_people = new String[10];
						
						for(int i = 0 ; i<10 ; i++)
						{
							count_for_people[i] = 0;
						}
						
						for(int i = 0 ; i<jsonArray.length() ; i++)
						{
							JSONObject jsonData = jsonArray.getJSONObject(i);
							
							grade[i] = jsonData.getString("grade");
							
							int_grade[i] = Integer.valueOf(grade[i]).intValue();
							
																
							if(int_grade[i] >= 0 && int_grade[i] <= 10)
								count_for_people[0]++;
							
							if(int_grade[i] > 10 && int_grade[i] <= 20)
								count_for_people[1]++;
							
							if(int_grade[i] > 20 && int_grade[i] <= 30)
								count_for_people[2]++;
							
							if(int_grade[i] > 30 && int_grade[i] <= 40)
								count_for_people[3]++;
							
							if(int_grade[i] > 40 && int_grade[i] <= 50)
								count_for_people[4]++;
							
							if(int_grade[i] > 50 && int_grade[i] <= 60)
								count_for_people[5]++;
							
							if(int_grade[i] > 60 && int_grade[i] <= 70)
								count_for_people[6]++;
							
							if(int_grade[i] > 70 && int_grade[i] <= 80)
								count_for_people[7]++;
							
							if(int_grade[i] > 80 && int_grade[i] <= 90)
								count_for_people[8]++;
							
							if(int_grade[i] > 90 && int_grade[i] <= 100)
								count_for_people[9]++;
						}
						
						for(int i = 0 ; i<10 ; i++)
						{
							str_count_for_people[i] = String.valueOf(count_for_people[i]);
							
							Log.d("=====>", "ExamGradeBarChartFragment 第" + i + "項人數 = " + str_count_for_people[i]);
						}
						
						try{           
	
				        	GradeBarChart getBarChart = new GradeBarChart();
				        	
				        	vChart = getBarChart.GetBarChart("成績分布情況", "分", "人數", str_count_for_people,getActivity());
	
				        	barchart_gradeBarChart.removeAllViews();
	
				            //llBarChart.addView(vChart);
	
				        	pgb.setVisibility(View.GONE);
				        	
				        	barchart_gradeBarChart.addView(vChart, new LayoutParams(LayoutParams.WRAP_CONTENT, 300));
	
				        }
						catch(Exception e)
				        {
												
				        }
					}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		endAllThread();
	}

	public void endAllThread(){
		
		 mHandler.removeCallbacks(setViewToNormalRunnable);
		 Log.d("<==ExamGradeBarChart==>", "handler and thread have kill");
		 
		 WaitForData_isRunning = false;
		 setViewToNormalRunnable_isRunning = false;
//		 refresh_receivepage_Thread.interrupt();
//		 downloadAttendListThread.interrupt();
	}
}
