package com.mcucsie.flippedclass.exam;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ExamMainTeacherFragment extends Fragment{


	ExamMainTeacherFragment frag_exam_tch;
	ExamTeacherBaseAdapter tch_baseAdapter;
	Context context; 

	ListView exam_list;
	Button btn_create_exam;
	ProgressBar pgb;
	
	ArrayList<HashMap<String, String>> list_item;
	AlertDialog.Builder dialog;

	GetNowCourseInfo course_info;
	
	String  course_ID,course_name;
	
	static Handler mHandler;
	DataFromDatabase MySQL_DB_Exam,MySQL_DB_AllMemberCount,MySQL_DB_ExamCountForDone;
	TeacherWaitForExamList TeacherWaitForExamList;
	private Boolean TeacherWaitForExamList_isRunning=false;
	
	String[] exam_code = null,exam_name = null,exam_openorclose = null,grade_code = null;
	String pre_result = "";	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_main_tch, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		frag_exam_tch = this;
		context =  getActivity();
		
		exam_list = (ListView)getView().findViewById(R.id.list_exam_main_tchlist);
		btn_create_exam = (Button)getView().findViewById(R.id.btn_exam_create);
		pgb=(ProgressBar)getView().findViewById(R.id.pgb_exam_tchmain);
		
		btn_create_exam.setOnClickListener(btn_create_exam_click);
		
		course_info = new GetNowCourseInfo(getActivity());
		
		course_ID = course_info.getNowCourseID();
		course_name = course_info.getNowCourseName();
		
		
		//dialog = new AlertDialog.Builder(getActivity());
		
		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);
		
		mHandler = new Handler();
		
		MySQL_DB_Exam = new DataFromDatabase();
		MySQL_DB_Exam.FC_GetList(course_ID);
		
		MySQL_DB_AllMemberCount = new DataFromDatabase();
		MySQL_DB_AllMemberCount.FC_queryForCourseMemberList("course_memberlist_" + course_ID);
		
		MySQL_DB_ExamCountForDone = new DataFromDatabase();
		
		TeacherWaitForExamList = new TeacherWaitForExamList(MySQL_DB_Exam,MySQL_DB_AllMemberCount,MySQL_DB_ExamCountForDone);
		
		TeacherWaitForExamList_isRunning =true;
		
		dialog = new AlertDialog.Builder(context);

		Thread waitdata = new Thread(TeacherWaitForExamList);
		waitdata.start(); 
	}
	
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		endAllThread();
	}

	

	private OnClickListener btn_create_exam_click= new OnClickListener(){
		
    	@Override
		public void onClick(View v) {
			dialog.setTitle("新建考題");
			dialog.setMessage("請選擇出題方式");
			dialog.setNegativeButton("題庫選取", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					ExamCreatExamFromDatabaseFragment frag_exam_create = new ExamCreatExamFromDatabaseFragment();
		    		getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_exam_create).commit();

				}
			});
			dialog.setPositiveButton("使用者出題", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					ExamCreateExamFragment frag_examcreate = new ExamCreateExamFragment();
		    		getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_examcreate).commit();
				}
			});
    		dialog.show();
		}
	};
	
	class TeacherWaitForExamList implements Runnable
	{
		DataFromDatabase MySQL_DB_Exam,MySQL_DB_AllMemberCount,MySQL_DB_ExamCountForDone;
		int times = 0,counttimes = 0;
		String allmembercount;
		
		public TeacherWaitForExamList(DataFromDatabase MySQL_DB_Exam,DataFromDatabase MySQL_DB_AllMemberCount,DataFromDatabase MySQL_DB_ExamCountForDone)
		{
			this.MySQL_DB_Exam = MySQL_DB_Exam;
			this.MySQL_DB_AllMemberCount = MySQL_DB_AllMemberCount;
			this.MySQL_DB_ExamCountForDone = MySQL_DB_ExamCountForDone;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {				
					while(TeacherWaitForExamList_isRunning)
					{					
						times = 0;
						counttimes = 0;
						
						Thread.sleep(500);
						
						String result_Exam = MySQL_DB_Exam.return_result;
						String result_AllMember = MySQL_DB_AllMemberCount.return_result;
						String[] result_CountForDone;
				
						while(TeacherWaitForExamList_isRunning&&result_Exam==null && result_AllMember==null && times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "ExamFragment Waitfodata 多等了1秒");
							result_Exam = MySQL_DB_Exam.return_result;
							result_AllMember = MySQL_DB_AllMemberCount.return_result;
							times++;
						}
						
						Log.d("=====>", "ExamFragment Waitfodata get result_Exam = " + result_Exam);
						
						
						JSONArray jsonArray_Exam,jsonArray_AllMember;
						
						try {						
									list_item = new ArrayList<HashMap<String, String>>();
									
									jsonArray_Exam = new JSONArray(result_Exam);
									jsonArray_AllMember = new JSONArray(result_AllMember);
									allmembercount = Integer.toString(jsonArray_AllMember.length());
									result_CountForDone =  new String[jsonArray_Exam.length()];
									
									//exam_count = jsonArray_Exam.length();
									
									exam_code = new String[jsonArray_Exam.length()];
									exam_name = new String[jsonArray_Exam.length()];
									exam_openorclose = new String[jsonArray_Exam.length()];							
									
									for(int i = 0 ; i<jsonArray_Exam.length() ; i++)
									{
										JSONObject jsonData = jsonArray_Exam.getJSONObject(i);
										
										exam_code[i] = jsonData.getString("exam_times");
										exam_name[i] = jsonData.getString("exam_name");
			
										MySQL_DB_ExamCountForDone.FC_ExamCountForDone(course_ID,exam_code[i]);
										
										while(result_CountForDone[i]==null && counttimes<=10)
										{
											Thread.sleep(1000);
											Log.d("=====>", "ExamMainTeacherFragment CountForDone 多等了1秒");
											result_CountForDone[i] = MySQL_DB_ExamCountForDone.return_result;
											counttimes++;
										}								
										
										if( jsonData.getString("open_or_close").equals("1"))
											exam_openorclose[i] = "open";
										
										if( jsonData.getString("open_or_close").equals("0"))
											exam_openorclose[i] = "close";						
										
										HashMap<String, String> item = new HashMap<String, String>();
										item.put("exam_code", exam_code[i]);
										item.put("exam_name", exam_name[i]);
									    item.put("exam_state", exam_openorclose[i]);
									    item.put("count_for_done", result_CountForDone[i]);
									    item.put("allmembercount", allmembercount);
									    list_item.add(item);
								    
										Log.d("=====>", "ExamChooseExamFragment Waitfodata set exam_code["+ i + "] = " + exam_code[i]);
									}
															
									if(TeacherWaitForExamList_isRunning == true)
									{	
										mHandler.post(setViewToNormalRunnable);					
										
										Thread.sleep(3000);
									
										MySQL_DB_Exam.FC_GetList(course_ID);
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
			tch_baseAdapter = new ExamTeacherBaseAdapter(context, list_item, frag_exam_tch);
			exam_list.setAdapter(tch_baseAdapter);
			pgb.setVisibility(View.GONE);
		}
	};
	 
	public void endAllThread(){
		
		 mHandler.removeCallbacks(TeacherWaitForExamList);
		 mHandler.removeCallbacks(setViewToNormalRunnable);
		 Log.d("<==ExamMainTeacher==>", "handler and thread have kill");
		 TeacherWaitForExamList_isRunning=false;
//		 refresh_receivepage_Thread.interrupt();
//		 downloadAttendListThread.interrupt();
	}
	
	public void CreateEditFragment()
	{		
		Bundle bundle = ExamTeacherBaseAdapter.bundle;
	
		ExamEditExamFragment frag_edit = new ExamEditExamFragment();
		frag_edit.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_edit).commit();
	}	
}
