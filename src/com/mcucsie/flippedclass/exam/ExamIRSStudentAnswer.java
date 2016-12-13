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
import android.widget.GridView;
import android.widget.ProgressBar;

public class ExamIRSStudentAnswer extends Fragment{

	private ProgressBar pgb;
	
	private GridView gv_title;
	private GridView gv_info;
	
	DataFromDatabase MySQL_DB;
	
	static Handler mHandler;
	TeacherWaitForIRSGrid TeacherWaitForIRSGrid;
	boolean TeacherWaitForIRSGrid_isRunning = false;
	
	GetNowCourseInfo course_info;
	String course_ID;
	
	String[] account_ID,std_ans;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_irs_stdans, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		gv_title=(GridView)getView().findViewById(R.id.gv_exam_irsanstitle);
		gv_info=(GridView)getView().findViewById(R.id.gv_exam_irsansinfo);
		
		pgb=(ProgressBar)getView().findViewById(R.id.pgb_exam_tchirsstdans);
		
		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);
		
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
		
		MySQL_DB = new DataFromDatabase();
		MySQL_DB.FC_GetStdIRSAns(course_ID);
		
		mHandler = new Handler();
		
		TeacherWaitForIRSGrid_isRunning = true;
		
		TeacherWaitForIRSGrid = new TeacherWaitForIRSGrid(MySQL_DB);
		
		Thread waitdata = new Thread(TeacherWaitForIRSGrid);
		waitdata.start();
	}
	
	class TeacherWaitForIRSGrid implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		
		public TeacherWaitForIRSGrid(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {				
					if(TeacherWaitForIRSGrid_isRunning)	
					{
						Thread.sleep(500);
									
						times = 0;
							
						String result = MySQL_DB.return_result;
					
						while(TeacherWaitForIRSGrid_isRunning && result==null && times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "ExamFragment Waitfodata ¦hµ¥¤F1¬í");
							result = MySQL_DB.return_result;
							times++;
						}
							
						Log.d("=====>", "ExamFragment Waitfodata get result = " + result);
							
							
						JSONArray jsonArray;
							
						try {											
									jsonArray = new JSONArray(result);
	
										
									account_ID = new String[jsonArray.length()];
									std_ans = new String[jsonArray.length()];
										
									for(int i = 0 ; i<jsonArray.length() ; i++)
									{
										JSONObject jsonData = jsonArray.getJSONObject(i);
											
										account_ID[i] = jsonData.getString("account_ID");
										std_ans[i] = jsonData.getString("answer");
									}
																
									if(TeacherWaitForIRSGrid_isRunning)
									{	
										mHandler.post(setViewToNormalRunnable);					
											
										Thread.sleep(1500);
										
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
			initGridView();
		}
	};
	
	private void initGridView() {
		// TODO Auto-generated method stub
		
		Log.d("=====>", "ExamGradeTeacherFragment get in initGridView");
		
		pgb.setVisibility(View.GONE);
		
		gv_title.setNumColumns(2);
		gv_title.setAdapter(new ExamGridIRSListTitleAdapter(getActivity(),2));
		gv_info.setNumColumns(2);
		gv_info.setAdapter(new ExamGridIRSInfoAdapter(getActivity(),2,account_ID,std_ans));
		gv_info.setVisibility(View.VISIBLE);
	}
	
	public void endAllThread(){
		
		 mHandler.removeCallbacks(TeacherWaitForIRSGrid);
		 mHandler.removeCallbacks(setViewToNormalRunnable);
		 Log.d("<==ExamMainTeacher==>", "handler and thread have kill");
		 TeacherWaitForIRSGrid_isRunning=false;
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
