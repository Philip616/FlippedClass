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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ExamGradeTeacherFragment extends Fragment{

	private Spinner spinner;
	private GridView gv_title;
	private GridView gv_info;
	private Button btn_tobarchart;
	private ProgressBar pgb;
	
	static Handler mHandler;
	static Handler mHandler_GlidView;
	
	downloadExamList_runnable downloadExamList_runnable;
	boolean downloadExamList_runnable_isRunning = false;
	downloadGradeinfo_runnable downloadGradeinfo_runnable;
	boolean downloadGradeinfo_runnable_isRunning = false;
	
	private String[] info_account_ID;
	private String[] info_grade;
	
	private GetNowCourseInfo course_info;
	private String course_ID;
	
	String[] exam_name,exam_code;
	
	String now_exam_index;
	
	int count_for_member=0;	
	
	DataFromDatabase MySQL_DB_Exam,MySQL_DB_Grade;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_teacher_grade, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		btn_tobarchart = (Button) getView().findViewById(R.id.btn_exam_tobarchart);
		btn_tobarchart.setOnClickListener(btn_tobarchart_click);
		
		gv_title=(GridView)getView().findViewById(R.id.gv_exam_gradetitle);
		gv_info=(GridView)getView().findViewById(R.id.gv_exam_gradeinfo);
		
		spinner = (Spinner)getView().findViewById(R.id.spinner_exam);
		
		pgb=(ProgressBar)getView().findViewById(R.id.pgb_exam_tchgrade);
		
		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);
		
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
		
		mHandler = new Handler();
		mHandler_GlidView= new Handler();
		
		MySQL_DB_Exam = new DataFromDatabase();
		MySQL_DB_Exam.FC_GetList(course_ID);
		
		downloadExamList_runnable_isRunning = true;
		downloadExamList_runnable = new downloadExamList_runnable(MySQL_DB_Exam);
		
		Thread setSpinnerThread=new Thread(downloadExamList_runnable);
		
		if(course_info.getNowCourseID().equals("null"))
		{
			Toast.makeText(getActivity(), "쬋줄쮁울쌀",Toast.LENGTH_LONG).show();
		}
		else
		{
			setSpinnerThread.start();
			spinner.setOnItemSelectedListener(spinner_item_selected);
		}
	}
	
	private OnClickListener btn_tobarchart_click= new OnClickListener(){
		
    	@Override
		public void onClick(View v) {			  		
    		Bundle bundle = new Bundle();
    		bundle.putString("now_exam_index",now_exam_index);
    		ExamGradeBarChartFragment frag_gradebarchart = new ExamGradeBarChartFragment();
    		frag_gradebarchart.setArguments(bundle);
    		getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_gradebarchart).commit();	
		}
	};
	
	private OnItemSelectedListener spinner_item_selected=new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
//			Toast.makeText(getActivity(), "쬋�{쫇울ず촑"+index[position]+attendlist_date[position],Toast.LENGTH_LONG).show();
			now_exam_index=exam_code[position];
			gv_info.setVisibility(View.GONE);
			pgb.setVisibility(View.VISIBLE);
			pgb.setVisibility(View.FOCUS_FORWARD);
			MySQL_DB_Grade = new DataFromDatabase();
			MySQL_DB_Grade.FC_GetAllStdGrade(course_ID, now_exam_index);			
			
			downloadGradeinfo_runnable_isRunning = true;
			downloadGradeinfo_runnable = new downloadGradeinfo_runnable(MySQL_DB_Grade);
			
			Thread setGridViewThread=new Thread(downloadGradeinfo_runnable);
			setGridViewThread.start();			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void initGridView() {
		// TODO Auto-generated method stub
		
		Log.d("=====>", "ExamGradeTeacherFragment get in initGridView");
		
		gv_title.setNumColumns(2);
		gv_title.setAdapter(new ExamGridListTitleAdapter(getActivity(),2));
		gv_info.setNumColumns(2);
		gv_info.setAdapter(new ExamGridInfoAdapter(getActivity(),2,info_account_ID,info_grade));
		gv_info.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.GONE);
	}
	
	
	class downloadExamList_runnable implements Runnable
	{
		DataFromDatabase MySQL_DB_Exam;
		int times = 0;
		
		public downloadExamList_runnable(DataFromDatabase MySQL_DB_Exam)
		{
			this.MySQL_DB_Exam = MySQL_DB_Exam;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					
					Thread.sleep(500);
						
					String result_Exam = MySQL_DB_Exam.return_result;
				
					while(result_Exam==null && times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "ExamFragment Waitfodata 쫐데짦1ы");
						result_Exam = MySQL_DB_Exam.return_result;
						times++;
					}
						
					Log.d("=====>", "ExamFragment Waitfodata get result_Exam = " + result_Exam);
						
						
					JSONArray jsonArray_Exam;
						
					try {
							jsonArray_Exam = new JSONArray(result_Exam);
							exam_name = new String[jsonArray_Exam.length()];
							exam_code = new String[jsonArray_Exam.length()];
								
							for(int i = 0 ; i<jsonArray_Exam.length() ; i++)
							{
								JSONObject jsonData = jsonArray_Exam.getJSONObject(i);
									
								exam_code[i] = jsonData.getString("exam_times");
								exam_name[i] = jsonData.getString("exam_name");
							}
								
							if(downloadExamList_runnable_isRunning)
							{
								mHandler.post(setSpinnerRunnable);
							}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}													
				} 	

	
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	private Runnable setSpinnerRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ArrayAdapter<String> ad = new ArrayAdapter<String>
	           (getActivity(), android.R.layout.simple_spinner_item, exam_name);
			spinner.setAdapter(ad);
			Log.d("<==TeacherAttendInfo==>", "setAdapter Done!");
		}
	}; 
	
	class downloadGradeinfo_runnable implements Runnable
	{
		DataFromDatabase MySQL_DB_Grade;
		int times = 0;
		
		public downloadGradeinfo_runnable(DataFromDatabase MySQL_DB_Grade)
		{
			this.MySQL_DB_Grade = MySQL_DB_Grade;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {	
						
					Thread.sleep(500);
						
					String result_Grade = MySQL_DB_Grade.return_result;
				
					while(result_Grade==null && times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "ExamFragment Waitfodata 쫐데짦1ы");
						result_Grade = MySQL_DB_Grade.return_result;
						times++;
					}
						
					Log.d("=====>", "ExamFragment Waitfodata get result_Grade = " + result_Grade);
						
						
					JSONArray jsonArray_Grade;
						
					try {
							
							jsonArray_Grade = new JSONArray(result_Grade);
								
							info_account_ID = new String[jsonArray_Grade.length()];
							info_grade = new String[jsonArray_Grade.length()];
								
							count_for_member = jsonArray_Grade.length();
								
							for(int i = 0 ; i<jsonArray_Grade.length() ; i++)
							{
								JSONObject jsonData = jsonArray_Grade.getJSONObject(i);
									
								info_account_ID[i] = jsonData.getString("account_ID");
								info_grade[i] = jsonData.getString("grade");
							}
	
								if(downloadGradeinfo_runnable_isRunning)
								{		
									mHandler_GlidView.post(setGlidViewRunnable);
								}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}									
						
				} 	

	
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	private Runnable setGlidViewRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			initGridView();
		}
	};
	
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		endAllThread();
		
	}

	public void endAllThread(){
		 mHandler.removeCallbacks(downloadExamList_runnable);
		 downloadExamList_runnable_isRunning = false;
		 Log.d("<==AttendTeacherAttendinfo==>", "setSpinnerThread  DIE");
		 mHandler_GlidView.removeCallbacks(downloadGradeinfo_runnable);
		 downloadGradeinfo_runnable_isRunning = false;
		 Log.d("<==AttendTeacherAttendinfo==>", "setGridViewThread  DIE");
	}
}
