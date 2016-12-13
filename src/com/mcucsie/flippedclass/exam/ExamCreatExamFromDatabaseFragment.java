package com.mcucsie.flippedclass.exam;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;
import com.mcucsie.flippedclass.exam.ExamCreateExamFragment.WaitForInsertExam;
import com.mcucsie.flippedclass.exam.ExamCreateExamFragment.setViewToNormalRunnable;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ExamCreatExamFromDatabaseFragment extends Fragment{

	String course_ID,exam_name;
	String[] question_content,choose_A,choose_B,choose_C,choose_D,answer,type;
	ListView exam_list;
	Button btn_set_grade,btn_reset_choose;
	ArrayList<HashMap<String, String>> List,List_2;
	
	Handler mHandler = new Handler();
	setViewToNormalRunnable setViewToNormalRunnable;
	ExamCreatExamFromDatabaseAdapter ExmCreatFromDatabase;

	private AlertDialog.Builder dialog;
	
	int situation=1,exam_code,latest_exam_code;
	int count_for_ques = 0,count_member_of_course = 0;
	String[] std_account_ID;

	private View ques_title;
	static Bundle bundle;
	private boolean ExamCreatfromDatabase = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub	
		return inflater.inflate(R.layout.fragment_exam_fromdatabase, container,false);
		
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	bundle = new Bundle();
	exam_list = (ListView)getView().findViewById(R.id.list_exam_database);
	btn_set_grade = (Button)getView().findViewById(R.id.btn_set_grade);
	btn_reset_choose = (Button)getView().findViewById(R.id.btn_reset_choose);
	

	btn_set_grade.setOnClickListener(select_ok);
	btn_reset_choose.setOnClickListener(resetAdapter);
	GetNowCourseInfo course_info = new GetNowCourseInfo(getActivity());
	course_ID = course_info.getNowCourseID();
	
	
	DataFromDatabase MySQL_DB = new DataFromDatabase();
	MySQL_DB.Exam_getExamFromDatabase(course_ID);
	
	DataFromDatabase MySQL_DB_GetLatestExamCode = new DataFromDatabase();
	MySQL_DB_GetLatestExamCode.FC_GetLatestExamCode(course_ID);
	
	DataFromDatabase MySQL_DB_CourseMember = new DataFromDatabase();
	MySQL_DB_CourseMember.queryForCourseMemberList("course_memberlist_" + course_ID);
	
	ExamCreatfromDatabase = true;
	Thread waitfordata = new Thread(new getDatabaseInfo(MySQL_DB,MySQL_DB_GetLatestExamCode,MySQL_DB_CourseMember));
	waitfordata.start();
	}
	
	public android.view.View.OnClickListener select_ok = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(situation==1){
				List_2=ExmCreatFromDatabase.getList2();
				//Log.d("situation==1", List_2.get(0).get("question_content"));
				bundle.putInt("code", 2);
				ExmCreatFromDatabase = new ExamCreatExamFromDatabaseAdapter(getActivity(), List_2 ,bundle);
				exam_list.setAdapter(ExmCreatFromDatabase);
				btn_set_grade.setText("上傳");
				situation=2;
			}
			if(ExmCreatFromDatabase.getlistpoint()){
				//輸入考題名稱上傳
				List_2=ExmCreatFromDatabase.getList2();
				Log.d("situation==2", List_2.toString());
				ques_title = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_database_grade, null);
				dialog = new AlertDialog.Builder(getActivity());
				dialog.setTitle("輸入考題名稱");
				dialog.setView(ques_title);
				dialog.setPositiveButton("確認",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText text = (EditText)ques_title.findViewById(R.id.edit_exam_grade);
						exam_name=text.getText().toString();
						DataFromDatabase MySQL_DB = new DataFromDatabase();
		            	MySQL_DB.FC_CreateExam(course_ID, exam_code, exam_name);
		            	
		            	Thread waitdata = new Thread(new WaitForInsertExam(MySQL_DB));
		        		waitdata.start();
					}
				});
				dialog.show();
			}
			else{
				Toast.makeText(getActivity(),"請設定分數", Toast.LENGTH_LONG).show();
			}
			
		}
	};
	
	public OnClickListener resetAdapter = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(situation==1){
			bundle.putInt("code", 1);
			ExmCreatFromDatabase = new ExamCreatExamFromDatabaseAdapter(getActivity(), List ,bundle);
			exam_list.setAdapter(ExmCreatFromDatabase);
			}
			if(situation==2)
			{
			bundle.putInt("code", 1);
			ExmCreatFromDatabase = new ExamCreatExamFromDatabaseAdapter(getActivity(), List ,bundle);
			exam_list.setAdapter(ExmCreatFromDatabase);	
			situation=1;
			}

		}
	};
	
	
	
		
	class getDatabaseInfo implements Runnable{

		private DataFromDatabase MySQL_DB,MySQL_DB_GetLatestExamCode,MySQL_DB_CourseMember;
		// 建構子，設定要傳的字串
		public getDatabaseInfo(DataFromDatabase MySQL_DB_1,DataFromDatabase MySQL_DB_GetLatestExamCode,DataFromDatabase MySQL_DB_CourseMember)
		{
			this.MySQL_DB=MySQL_DB_1;
			this.MySQL_DB_CourseMember=MySQL_DB_CourseMember;
			this.MySQL_DB_GetLatestExamCode=MySQL_DB_GetLatestExamCode;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String result = null;
			String result_GetLatestExamCode = null;
			String result_MySQL_DB_CourseMember = null;
			int times = 0;
			try{
				result=MySQL_DB.return_result;
				result_GetLatestExamCode = MySQL_DB_GetLatestExamCode.return_result;
				result_MySQL_DB_CourseMember = MySQL_DB_CourseMember.return_result;
				
				while(ExamCreatfromDatabase&&times<=100&&result==null&&result_GetLatestExamCode==null){
				   Thread.sleep(1000);
				   result=MySQL_DB.return_result;
				   result_GetLatestExamCode = MySQL_DB_GetLatestExamCode.return_result;
				   result_MySQL_DB_CourseMember = MySQL_DB_CourseMember.return_result;
				   Log.d("=====>", "getDatabaseInfo多休息了"+times+"秒");	
				   times++;
				}
				
				latest_exam_code = Integer.valueOf(result_GetLatestExamCode).intValue(); 
				
		        exam_code = latest_exam_code +1;
		        Log.d("getExam_code", String.valueOf(exam_code));
				
				if(times<=100)
				{
					Log.d("getDatabaseInfo", "getDatabaseInfo抓取成功");
					JSONArray jsonarray,jsonArray_coursemem;
					try{
						List = new ArrayList<HashMap<String, String>>();
						jsonarray = new JSONArray(result);
						question_content = new String[jsonarray.length()];
						choose_A = new String[jsonarray.length()];
						choose_B = new String[jsonarray.length()];
						choose_C = new String[jsonarray.length()];
						choose_D = new String[jsonarray.length()];
						answer = new String[jsonarray.length()];
						type = new String[jsonarray.length()];
						for(int i=0 ; i<jsonarray.length() ; i++)
						{
							JSONObject jsondata = jsonarray.getJSONObject(i);
							question_content[i] = jsondata.getString("question_content");
							choose_A[i] = jsondata.getString("choose_A");
							choose_B[i] = jsondata.getString("choose_B");
							if(jsondata.getString("type").equals("1"))//如果是1則為選擇則有CD選項
							{
							type[i] = "1";
							choose_C[i] = jsondata.getString("choose_C");
							choose_D[i] = jsondata.getString("choose_D");
							}
							if(jsondata.getString("type").equals("2"))
							{
							type[i]="2";
							choose_C[i] = "";
							choose_D[i] = "";
							}
							answer[i] = jsondata.getString("answer");
							
							HashMap<String, String> item = new HashMap<String,String>();
							item.put("question_content", question_content[i]);
							item.put("choose_A", choose_A[i]);
							item.put("choose_B", choose_B[i]);
							item.put("choose_C", choose_C[i]);
							item.put("choose_D", choose_D[i]);
							item.put("type", type[i]);
							item.put("answer", answer[i]);
							List.add(item);
						}
						jsonArray_coursemem = new JSONArray(result_MySQL_DB_CourseMember);

						std_account_ID = new String[jsonArray_coursemem.length()];
						count_member_of_course = jsonArray_coursemem.length();
						
						for(int i = 0 ; i<jsonArray_coursemem.length() ; i++)
						{
							JSONObject jsonData = jsonArray_coursemem.getJSONObject(i);
							
							std_account_ID[i] = jsonData.getString("account_ID");
						}
						mHandler.post(setExamAdapter);
						ExamCreatfromDatabase = false;
					}
					catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
		}
		catch (Exception e) {
				// TODO: handle exception
			}
		
		
		
	}
	}
	
	private Runnable setExamAdapter = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			bundle.putInt("code", 1);
			ExmCreatFromDatabase = new ExamCreatExamFromDatabaseAdapter(getActivity(), List ,bundle);
			exam_list.setAdapter(ExmCreatFromDatabase);
		}
	};
	class WaitForInsertExam implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		
		public WaitForInsertExam(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					Thread.sleep(500);
					
					String result = MySQL_DB.return_result;
					Log.d("=====>", "ExamCreateExamFragment Waitfodata get result" + result);
					
					while(result==null && times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "ExamCreateExamFragment Waitfodata 多等了1秒");
						result = MySQL_DB.return_result;
						times++;
					}
					
					Log.d("=====>", "ExamCreateExamFragment Waitfodata get result" + result);
					
					setViewToNormalRunnable = new setViewToNormalRunnable(result);
					
					mHandler.post(setViewToNormalRunnable);
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

				//Log.d("Exam",""+result);
				
				if(result.equals(Integer.toString(exam_code)))				
				{						
					DataFromDatabase MySQL_DB_Insert = new DataFromDatabase();
					for(int i = 0;i < List_2.size() ; i++)
					{			
						MySQL_DB_Insert.FC_InputExam(List_2.get(i).get("question_content"), List_2.get(i).get("choose_A"), List_2.get(i).get("choose_B"), List_2.get(i).get("choose_C"), List_2.get(i).get("choose_D"), List_2.get(i).get("point"), List_2.get(i).get("answer"),List_2.get(i).get("type"), course_ID, Integer.toString(exam_code));						
					}
						
					for(int i = 0;i < count_member_of_course ; i++)
					{			
						MySQL_DB_Insert.FC_InputGradeMember(course_ID, Integer.toString(exam_code), std_account_ID[i]);							
					}
						
					ExamMainTeacherFragment frag_exammaintch=new ExamMainTeacherFragment();
					getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_exammaintch).commit();
				}
		}
	}
	
	}
