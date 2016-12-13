package com.mcucsie.flippedclass.exam;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ExamMainStudentFragment extends Fragment{

	ListView exam_list;
	ProgressBar pgb;
	
	ExamMainStudentFragment frag_exam = this;
	
	ArrayList<HashMap<String, String>> list_item;
	
	AlertDialog.Builder dialog;
	
	Handler mHandler;
	boolean StudentWaitForExamList_isRunning = false;
	
	String  course_ID,course_name,account_Type,account_ID;
	
	String[] exam_code = null,exam_name = null,exam_openorclose = null,exam_grade = null,grade_code = null;
	
	GetNowCourseInfo course_info;
	GetNowAccountInfo account_info;
	
	DataFromDatabase MySQL_DB_Exam,MySQL_DB_Grade;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_main_std, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		exam_list = (ListView)getView().findViewById(R.id.list_exam_main_stdlist);
		pgb=(ProgressBar)getView().findViewById(R.id.pgb_exam_stdmain);
		
		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);
		
		course_info = new GetNowCourseInfo(getActivity());
		account_info = new GetNowAccountInfo(getActivity());
		
		course_ID = course_info.getNowCourseID();
		course_name = course_info.getNowCourseName();
		
		account_Type = account_info.getNowAccountType();
		account_ID = account_info.getNowAccountID();
		
		dialog = new AlertDialog.Builder(getActivity());
		
		mHandler = new Handler();
		
		MySQL_DB_Exam = new DataFromDatabase();
		MySQL_DB_Exam.FC_GetList(course_ID);
				
		MySQL_DB_Grade = new DataFromDatabase();
		MySQL_DB_Grade.FC_GetGrade(account_ID, course_ID);
				
		StudentWaitForExamList_isRunning = true;
		
		Thread waitdata = new Thread(new StudentWaitForExamList(MySQL_DB_Exam,MySQL_DB_Grade));
		waitdata.start(); 
	}
	
	class StudentWaitForExamList implements Runnable
	{
		DataFromDatabase MySQL_DB_Exam,MySQL_DB_Grade;
		int times = 0;
		
		public StudentWaitForExamList(DataFromDatabase MySQL_DB_Exam,DataFromDatabase MySQL_DB_Grade)
		{
			this.MySQL_DB_Exam = MySQL_DB_Exam;
			this.MySQL_DB_Grade = MySQL_DB_Grade;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					Thread.sleep(500);
					
					String result_Exam = MySQL_DB_Exam.return_result;
					String result_Grade = MySQL_DB_Grade.return_result;
			
					while(result_Exam==null && result_Grade==null && times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "ExamFragment Waitfodata 多等了1秒");
						result_Exam = MySQL_DB_Exam.return_result;
						result_Grade = MySQL_DB_Grade.return_result;
						times++;
					}
					
					Log.d("=====>", "ExamFragment Waitfodata get result_Exam = " + result_Exam);
					Log.d("=====>", "ExamFragment Waitfodata get result_Grade = " + result_Grade);
					
					
					JSONArray jsonArray_Exam,jsonArray_Grade;
					
					try {
							list_item = new ArrayList<HashMap<String, String>>();
						
							jsonArray_Exam = new JSONArray(result_Exam);
							jsonArray_Grade = new JSONArray(result_Grade);
							
							exam_code = new String[jsonArray_Exam.length()];
							exam_name = new String[jsonArray_Exam.length()];
							exam_openorclose = new String[jsonArray_Exam.length()];	
							exam_grade = new String[jsonArray_Exam.length()];
							grade_code = new String[jsonArray_Exam.length()];
							
							for(int i = 0 ; i<jsonArray_Exam.length() ; i++)
							{								
								grade_code[i] = "";
								exam_grade[i] = "未作答";
							}
							
							
							for(int i = 0 ; i<jsonArray_Grade.length() ; i++)
							{
								JSONObject jsonData = jsonArray_Grade.getJSONObject(i);
								
								grade_code[i] = jsonData.getString("exam_times");
							}
							
							for(int i = 0 ; i<jsonArray_Exam.length() ; i++)
							{
								JSONObject jsonData = jsonArray_Exam.getJSONObject(i);
								
								exam_code[i] = jsonData.getString("exam_times");
								exam_name[i] = jsonData.getString("exam_name");

								
								if( jsonData.getString("open_or_close").equals("1"))
									exam_openorclose[i] = "open";
								
								if( jsonData.getString("open_or_close").equals("0"))
									exam_openorclose[i] = "close";
								
								for(int j = 0; j<jsonArray_Grade.length() ; j++)
								{
									JSONObject jsonData_Grade = jsonArray_Grade.getJSONObject(j);
									
									if(String.valueOf(i+1).equals(grade_code[j]))
									{
										exam_grade[i] = jsonData_Grade.getString("grade");
									}									
								}
								
								HashMap<String, String> item = new HashMap<String, String>();
								item.put("exam_code", exam_code[i]);
								item.put("exam_name", exam_name[i]);
							    item.put("exam_state", exam_openorclose[i]);
							    item.put("exam_grade", exam_grade[i]);
							    list_item.add(item);
						    
								Log.d("=====>", "ExamFragment Waitfodata set exam_code["+ i + "] = " + exam_code[i]);
								Log.d("=====>", "ExamFragment Waitfodata set grade["+ i + "] = " + exam_grade[i]);
							}
							
							if(StudentWaitForExamList_isRunning)
							mHandler.post(setViewToNormalRunnable);
					
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
						
						try {
								list_item = new ArrayList<HashMap<String, String>>();
							
								jsonArray_Exam = new JSONArray(result_Exam);
								
								exam_code = new String[jsonArray_Exam.length()];
								exam_name = new String[jsonArray_Exam.length()];
								exam_openorclose = new String[jsonArray_Exam.length()];	
								exam_grade = new String[jsonArray_Exam.length()];
								grade_code = new String[jsonArray_Exam.length()];
								
								for(int i = 0 ; i<jsonArray_Exam.length() ; i++)
								{								
									grade_code[i] = "";
									exam_grade[i] = "未作答";
								}

								for(int i = 0 ; i<jsonArray_Exam.length() ; i++)
								{
									JSONObject jsonData = jsonArray_Exam.getJSONObject(i);
									
									exam_code[i] = jsonData.getString("exam_times");
									exam_name[i] = jsonData.getString("exam_name");

									
									if( jsonData.getString("open_or_close").equals("1"))
										exam_openorclose[i] = "open";
									
									if( jsonData.getString("open_or_close").equals("0"))
										exam_openorclose[i] = "close";
									
									HashMap<String, String> item = new HashMap<String, String>();
									item.put("exam_code", exam_code[i]);
									item.put("exam_name", exam_name[i]);
								    item.put("exam_state", exam_openorclose[i]);
								    item.put("exam_grade", exam_grade[i]);
								    list_item.add(item);
							    
									Log.d("=====>", "ExamFragment Waitfodata set exam_code["+ i + "] = " + exam_code[i]);
									Log.d("=====>", "ExamFragment Waitfodata set grade["+ i + "] = " + exam_grade[i]);
								}
								
								if(StudentWaitForExamList_isRunning)
								mHandler.post(setViewToNormalRunnable);
						
						} catch (JSONException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}									
					
				} 	

	
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	private Runnable setViewToNormalRunnable = new Runnable()
	{		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			exam_list.setAdapter(new ExamStudentBaseAdapter(getActivity(), list_item, frag_exam));
			pgb.setVisibility(View.GONE);

		}
	};
	
	public void CreateQuestionFragment()
	{		
		Bundle bundle = ExamStudentBaseAdapter.bundle;
	
		ExamQuestionFragment frag_ques = new ExamQuestionFragment();
		frag_ques.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_ques).commit();
	}
	
	public void initDialog() {
		// TODO Auto-generated method stub
		//		dialog.setMessage("Hi this is test dialog");
		
		dialog.setTitle("將開啟新的測驗");
		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {  
	    	// 按下PositiveButton要做的事  
	   
        	}  
	    }); 
		dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		    // 按下PositiveButton要做的事  	            	
		    	CreateQuestionFragment();
		    }
	    });
	}
	
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		endAllThread();
	}

	public void endAllThread(){
		
		 //mHandler.removeCallbacks(setViewToNormalRunnable);
		 StudentWaitForExamList_isRunning=false;
		 Log.d("<==ExamSturentMain==>", "AllThread  DIE");

	}
}
