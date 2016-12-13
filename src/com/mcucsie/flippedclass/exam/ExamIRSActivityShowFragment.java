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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ExamIRSActivityShowFragment extends Fragment{

	TextView txt_irs_show;
	ProgressBar pgb;
	
	DataFromDatabase MySQL_DB;
	
	static Handler mHandler;
	TeacherWaitForIRSText TeacherWaitForIRSText;
	boolean TeacherWaitForIRSText_isRunning = false;
	
	GetNowCourseInfo course_info;
	String course_ID;
	
	String[] account_ID,std_ans;
	int count_A = 0,count_B = 0,count_C = 0,count_D = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_irs_activityshow, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		txt_irs_show = (TextView)getView().findViewById(R.id.txt_irsshow);
		pgb=(ProgressBar)getView().findViewById(R.id.pgb_exam_tchirsactivity);
		
		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);
		
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
		
		MySQL_DB = new DataFromDatabase();
		MySQL_DB.FC_GetStdIRSAns(course_ID);
		
		mHandler = new Handler();
		
		TeacherWaitForIRSText_isRunning = true;
		
		TeacherWaitForIRSText = new TeacherWaitForIRSText(MySQL_DB);
		
		Thread waitdata = new Thread(TeacherWaitForIRSText);
		waitdata.start();
	}
	
	class TeacherWaitForIRSText implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		
		public TeacherWaitForIRSText(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {				
					Thread.sleep(500);
					
					while(TeacherWaitForIRSText_isRunning)
					{				
						times = 0;
						count_A = 0;
						count_B = 0;
						count_C = 0;
						count_D = 0;
						
						String result = MySQL_DB.return_result;
				
						while(TeacherWaitForIRSText_isRunning && result==null && times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "ExamFragment Waitfodata 多等了1秒");
							result = MySQL_DB.return_result;
							times++;
						}
						
						Log.d("=====>", "ExamFragment Waitfodata get result = " + result);
						
						
						JSONArray jsonArray;
						
						try {											
									jsonArray = new JSONArray(result);

									//exam_count = jsonArray_Exam.length();
									
									account_ID = new String[jsonArray.length()];
									std_ans = new String[jsonArray.length()];
									
									for(int i = 0 ; i<jsonArray.length() ; i++)
									{
										JSONObject jsonData = jsonArray.getJSONObject(i);
										
										account_ID[i] = jsonData.getString("account_ID");
										std_ans[i] = jsonData.getString("answer");
									
										if(std_ans[i].equals("A"))
											count_A = count_A+1;
										else if(std_ans[i].equals("B"))
											count_B = count_B+1;
										else if(std_ans[i].equals("C"))
											count_C = count_C+1;
										else if(std_ans[i].equals("D"))
											count_D = count_D+1;
									}
															
									if(TeacherWaitForIRSText_isRunning)
									{	
										mHandler.post(setViewToNormalRunnable);					
										
										Thread.sleep(3000);
									
										MySQL_DB.FC_GetStdIRSAns(course_ID);
									}
								
									
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			} 	
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
	}
	
	private Runnable setViewToNormalRunnable= new Runnable() {
		
		@Override
		public void run() {
			txt_irs_show.setText("A選項：" + count_A + "人\n" + "B選項：" + count_B + "人\n" + "C選項：" + count_C + "人\n" + "D選項：" + count_D + "人");
			pgb.setVisibility(View.GONE);
		}
	};
	
	public void endAllThread(){
		
		 mHandler.removeCallbacks(TeacherWaitForIRSText);
		 mHandler.removeCallbacks(setViewToNormalRunnable);
		 Log.d("<==ExamMainTeacher==>", "handler and thread have kill");
		 TeacherWaitForIRSText_isRunning=false;
//		 refresh_receivepage_Thread.interrupt();
//		 downloadAttendListThread.interrupt();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		endAllThread();
	}
	
	
}
