package com.mcucsie.flippedclass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.exam.ExamMainTeacherFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class BulletinBoardSetPreExamFragment extends Fragment{
	String  course_ID;
	int exam_code,latest_exam_code;
	GetNowCourseInfo course_info;
	
	AlertDialog.Builder dialog;
	
	static Handler mHandler;
	setViewToNormalRunnable setViewToNormalRunnable;
	
	Button btn_nextques,btn_saveall,btn_cancel;
	RadioGroup radiogroup_setans,radiogroup_settype;
	RadioButton rb_ques_choose,rb_ques_truefalse,rb_chooseA,rb_chooseB,rb_chooseC,rb_chooseD;
	EditText edit_input_ques,edit_chooseA,edit_chooseB,edit_chooseC,edit_chooseD,edit_point,edit_exam_name;
	String[] str_input_pages,str_input_ques,str_chooseA,str_chooseB,str_chooseC,str_chooseD,str_point,str_ans,str_type;
	String exam_name;
	String title,filepath,note;
	String btntype = "";
	int count_for_ques = 0,count_member_of_course = 0,LastviewID;
	String[] std_account_ID;
	Bundle bundle;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub	
		return inflater.inflate(R.layout.fragment_exam_create_exam, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		edit_exam_name = (EditText)getView().findViewById(R.id.edit_exam_name);
		
		edit_input_ques = (EditText)getView().findViewById(R.id.edit_input_ques);
		edit_chooseA = (EditText)getView().findViewById(R.id.edit_chooseA);
		edit_chooseB = (EditText)getView().findViewById(R.id.edit_chooseB);
		edit_chooseC = (EditText)getView().findViewById(R.id.edit_chooseC);
		edit_chooseD = (EditText)getView().findViewById(R.id.edit_chooseD);
		edit_point = (EditText)getView().findViewById(R.id.edit_point);
		
		btn_nextques = (Button)getView().findViewById(R.id.btn_nextques);
		btn_saveall = (Button)getView().findViewById(R.id.btn_saveall);
		btn_cancel = (Button)getView().findViewById(R.id.btn_cancel);
		
		btn_nextques.setOnClickListener(btn_nextques_click);
		btn_saveall.setOnClickListener(btn_saveall_click);
		btn_cancel.setOnClickListener(btn_cancel_click);
		
		str_input_pages = new String[100];
		str_input_ques = new String[100];
		str_chooseA = new String[100];
		str_chooseB = new String[100];
		str_chooseC = new String[100];
		str_chooseD = new String[100];
		str_point = new String[100];
		str_ans = new String[100];
		str_type = new String[100];
		bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString("title");
            filepath = bundle.getString("filepath");
            note = bundle.getString("note");
            bundle.clear();
        }
        
		for(int i = 0; i<100; i++)
		str_ans[i] = "Z";
		
		radiogroup_setans = (RadioGroup)getView().findViewById(R.id.radiogroup_setans);
		radiogroup_settype = (RadioGroup)getView().findViewById(R.id.radiogroup_settype);
				
		radiogroup_setans.setOnCheckedChangeListener(radiogroup_setans_click);
		radiogroup_settype.setOnCheckedChangeListener(radiogroup_settype_click);
		
		rb_chooseA = (RadioButton)getView().findViewById(R.id.rb_chooseA);
		rb_chooseB = (RadioButton)getView().findViewById(R.id.rb_chooseB);
		rb_chooseC = (RadioButton)getView().findViewById(R.id.rb_chooseC);
		rb_chooseD = (RadioButton)getView().findViewById(R.id.rb_chooseD);
		
		rb_ques_choose = (RadioButton)getView().findViewById(R.id.rb_ques_choose);
		rb_ques_truefalse = (RadioButton)getView().findViewById(R.id.rb_ques_truefalse);
		
		rb_ques_choose.setChecked(true);
		str_type[count_for_ques] = "1"; 
		
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
		
		mHandler = new Handler();
		
		DataFromDatabase MySQL_DB_GetLatestExamCode = new DataFromDatabase();
		MySQL_DB_GetLatestExamCode.FC_GetLatestPreExamCode(course_ID);
		
		DataFromDatabase MySQL_DB_CourseMember = new DataFromDatabase();
		MySQL_DB_CourseMember.queryForCourseMemberList("course_memberlist_" + course_ID);
		
		Thread waitdata = new Thread(new WaitForLatestExamCode(MySQL_DB_GetLatestExamCode,MySQL_DB_CourseMember));
		waitdata.start();
				
		dialog = new AlertDialog.Builder(getActivity());

	}
	
	private void initDialog() {
		// TODO Auto-generated method stub
		//		dialog.setMessage("Hi this is test dialog");
		
		if(btntype.equals("cancel"))
		{	
			dialog.setTitle("警告！將離開創建測驗");
			dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {  
	                // 按下PositiveButton要做的事  
	                //Toast.makeText(getActivity(), "請繼續作答", Toast.LENGTH_SHORT).show();
	            }  
	        }); 
			dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {  
	                // 按下PositiveButton要做的事  	            	
	            	/*DataFromDatabase MySQL_DB = new DataFromDatabase();
	    			MySQL_DB.FC_DeleteExam(course_ID,exam_code);*/
	    			
	    			//ExamMainTeacherFragment frag_exammaintch=new ExamMainTeacherFragment();
	    			//getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_exammaintch).commit();
	    			//返回上傳頁面
	            	
	            	MainFragment main_frag=new MainFragment();
	            	bundle.putString("title", title);
	            	bundle.putString("filepath", filepath);
	            	bundle.putString("note", note);
	            	bundle.putString("exam_code", String.valueOf(0));
	            	bundle.putString("exam_name", "" );
	            	main_frag.setArguments(bundle);
	        		getFragmentManager().beginTransaction().replace(R.id.container, main_frag).commit();
	    			

	            }
	        });
		}
		
		if(btntype.equals("saveall"))
		{	
			dialog.setTitle("警告！將離開創建測驗");
			dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {  
	                // 按下PositiveButton要做的事  
	                //Toast.makeText(getActivity(), "請繼續作答", Toast.LENGTH_SHORT).show();
	            }  
	        }); 
			dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {  
	                // 按下PositiveButton要做的事  	            	
	            	
	            	DataFromDatabase MySQL_DB = new DataFromDatabase();
	            	MySQL_DB.FC_CreatePreExam(course_ID, exam_code, exam_name);
	            	
	            	Thread waitdata = new Thread(new WaitForInsertExam(MySQL_DB));
	        		waitdata.start();
	            	/*
	            	for(int i = 0;i <= count_for_ques ; i++)
					{
						
						MySQL_DB.FC_InputExam(str_input_ques[i], str_chooseA[i], str_chooseB[i], str_chooseC[i], str_chooseD[i], str_point[i], str_ans[i],str_type[i], table_name);
					}
					
					ExamMainTeacherFragment frag_exammaintch=new ExamMainTeacherFragment();
					getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_exammaintch).commit();*/
	            		            	

	            	
	            }
	        });
		}
		

	}
	
	

	private OnClickListener btn_nextques_click= new OnClickListener(){
		@Override
		public void onClick(View v) {
			
			str_input_ques[count_for_ques] = edit_input_ques.getText().toString();
			str_chooseA[count_for_ques] = edit_chooseA.getText().toString();
			str_chooseB[count_for_ques] = edit_chooseB.getText().toString();
			str_chooseC[count_for_ques] = edit_chooseC.getText().toString();
			str_chooseD[count_for_ques] = edit_chooseD.getText().toString();
			str_point[count_for_ques] = edit_point.getText().toString();
			
			//DataFromDatabase MySQL_DB = new DataFromDatabase();
			//MySQL_DB.FC_InputExam(str_input_ques, str_chooseA, str_chooseB, str_chooseC, str_chooseD, str_point, str_ans, table_name);
			
			
			
			if(str_type[count_for_ques].equals("2"))
			{
				if(!str_chooseC[count_for_ques].equals("") || !str_chooseD[count_for_ques].equals(""))
					Toast.makeText(getActivity()," 所選題行為是非題，選項C、D不能輸入任何文字", Toast.LENGTH_LONG).show();
				
				else if(str_ans[count_for_ques].equals("C") || str_ans[count_for_ques].equals("D"))
					Toast.makeText(getActivity()," 所選題行為是非題，無法將答案設定為C、D", Toast.LENGTH_LONG).show();
				
				else if(str_chooseA[count_for_ques].equals("") || str_chooseB[count_for_ques].equals("") || str_input_ques[count_for_ques].equals("") || str_point[count_for_ques].equals(""))
					Toast.makeText(getActivity()," 無法儲存題目，有欄位尚未填入", Toast.LENGTH_LONG).show();
				
				else if(str_ans[count_for_ques].equals("Z"))
					Toast.makeText(getActivity()," 請選擇答案", Toast.LENGTH_LONG).show();
				
				else
				{
					edit_input_ques.setText("");
					edit_chooseA.setText("");
					edit_chooseB.setText("");
					edit_chooseC.setText("");
					edit_chooseD.setText("");
					edit_point.setText("");
					
					rb_chooseA.setChecked(false);
					rb_chooseB.setChecked(false);
					rb_chooseC.setChecked(false);
					rb_chooseD.setChecked(false);
					
					count_for_ques++;
					
					rb_ques_choose.setChecked(true);
					str_type[count_for_ques] = "1";
				}
			}
			
			else
			{
				if(str_chooseA[count_for_ques].equals("") || str_chooseB[count_for_ques].equals("") || str_chooseC[count_for_ques].equals("") || str_chooseD[count_for_ques].equals("") || str_input_ques[count_for_ques].equals("") || str_point[count_for_ques].equals(""))
					Toast.makeText(getActivity()," 無法儲存題目，有欄位尚未填入", Toast.LENGTH_LONG).show();
				
				else if(str_ans[count_for_ques].equals("Z"))
					Toast.makeText(getActivity()," 請選擇答案", Toast.LENGTH_LONG).show();
				
				else
				{	
					edit_chooseA.setText("");
					edit_input_ques.setText("");
					edit_chooseA.setText("");
					edit_chooseB.setText("");
					edit_chooseC.setText("");
					edit_chooseD.setText("");
					edit_point.setText("");
					
					rb_chooseA.setChecked(false);
					rb_chooseB.setChecked(false);
					rb_chooseC.setChecked(false);
					rb_chooseD.setChecked(false);
					
					count_for_ques++;
					
					rb_ques_choose.setChecked(true);
					str_type[count_for_ques] = "1";
				}
			}	   				
		}
	};
	
	private OnClickListener btn_saveall_click= new OnClickListener(){
		@Override
		public void onClick(View v) {
			
			str_input_ques[count_for_ques] = edit_input_ques.getText().toString();
			str_chooseA[count_for_ques] = edit_chooseA.getText().toString();
			str_chooseB[count_for_ques] = edit_chooseB.getText().toString();
			str_chooseC[count_for_ques] = edit_chooseC.getText().toString();
			str_chooseD[count_for_ques] = edit_chooseD.getText().toString();
			str_point[count_for_ques] = edit_point.getText().toString();
			
			exam_name = edit_exam_name.getText().toString();
			
			if(str_type[count_for_ques].equals("2"))
			{
				if(exam_name.equals(""))
					Toast.makeText(getActivity()," 請輸入測驗名稱", Toast.LENGTH_LONG).show();
				
				else if(!str_chooseC[count_for_ques].equals("") || !str_chooseD[count_for_ques].equals(""))
					Toast.makeText(getActivity()," 所選題行為是非題，選項C、D不能輸入任何文字", Toast.LENGTH_LONG).show();
				
				else if(str_ans[count_for_ques].equals("C") || str_ans[count_for_ques].equals("D"))
					Toast.makeText(getActivity()," 所選題行為是非題，無法將答案設定為C、D", Toast.LENGTH_LONG).show();
				
				else if(str_chooseA[count_for_ques].equals("") || str_chooseB[count_for_ques].equals("") || str_input_ques[count_for_ques].equals("") || str_point[count_for_ques].equals(""))
					Toast.makeText(getActivity()," 無法儲存題目，有欄位尚未填入", Toast.LENGTH_LONG).show();
				
				else if(str_ans[count_for_ques].equals("Z"))
					Toast.makeText(getActivity()," 請選擇答案", Toast.LENGTH_LONG).show();
				
				else
				{
					btntype = "saveall";
					initDialog();
					dialog.setMessage("確定儲存測驗？");
					dialog.show();	
				}							
			}
			
			else
			{
				if(exam_name.equals(""))
					Toast.makeText(getActivity()," 請輸入測驗名稱", Toast.LENGTH_LONG).show();
				
				else if(str_chooseA[count_for_ques].equals("") || str_chooseB[count_for_ques].equals("") || str_chooseC[count_for_ques].equals("") || str_chooseD[count_for_ques].equals("") || str_input_ques[count_for_ques].equals("") || str_point[count_for_ques].equals(""))
					Toast.makeText(getActivity()," 無法儲存題目，有欄位尚未填入", Toast.LENGTH_LONG).show();
				
				else if(str_ans[count_for_ques].equals("Z"))
					Toast.makeText(getActivity()," 請選擇答案", Toast.LENGTH_LONG).show();
				
				else
				{
					btntype = "saveall";
					initDialog();
					dialog.setMessage("確定儲存測驗？");
					dialog.show();
				}	
			}		
	   	}
	};
	
	private OnClickListener btn_cancel_click= new OnClickListener(){
		@Override
		public void onClick(View v) {
			
			btntype = "cancel";
			initDialog();
			dialog.setMessage("確定離開新建測驗？（所有題目將不儲存）");
			dialog.show();

	   	}
	};
	
	private RadioGroup.OnCheckedChangeListener radiogroup_setans_click = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			
			switch(checkedId)
			{
				case R.id.rb_chooseA:
					str_ans[count_for_ques] = "A";
					break;
				case R.id.rb_chooseB:
					str_ans[count_for_ques] = "B";
					break;
				case R.id.rb_chooseC:
					str_ans[count_for_ques] = "C";
					break;
				case R.id.rb_chooseD:
					str_ans[count_for_ques] = "D";
					break;
			}
			
		}
		
	};
	
	private RadioGroup.OnCheckedChangeListener radiogroup_settype_click = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			
			switch(checkedId)
			{
				case R.id.rb_ques_choose:
					str_type[count_for_ques] = "1";
					break;
				case R.id.rb_ques_truefalse:
					str_type[count_for_ques] = "2";				
					break;
			}
			
		}
		
	};
	
	class WaitForLatestExamCode implements Runnable
	{
		DataFromDatabase MySQL_DB_GetLatestExamCode,MySQL_DB_CourseMember;
		int times = 0;
		
		public WaitForLatestExamCode(DataFromDatabase MySQL_DB_GetLatestExamCode,DataFromDatabase MySQL_DB_CourseMember)
		{
			this.MySQL_DB_GetLatestExamCode = MySQL_DB_GetLatestExamCode;
			this.MySQL_DB_CourseMember = MySQL_DB_CourseMember;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					Thread.sleep(500);
					
					String result_GetLatestExamCode = MySQL_DB_GetLatestExamCode.return_result;
					String result_MySQL_DB_CourseMember = MySQL_DB_CourseMember.return_result;
					
					while(result_GetLatestExamCode==null && times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "ExamCreateExamFragment Waitfodata 多等了1秒");
						result_GetLatestExamCode = MySQL_DB_GetLatestExamCode.return_result;
						result_MySQL_DB_CourseMember = MySQL_DB_CourseMember.return_result;
						times++;
					}
					
					Log.d("=====>", "ExamCreateExamFragment Waitfodata GetLatestExamCode get result = " + result_GetLatestExamCode);
					Log.d("=====>", "ExamCreateExamFragment Waitfodata MySQL_DB_CourseMember get result = " + result_MySQL_DB_CourseMember);
					if(result_GetLatestExamCode.equals(""))
					{
						exam_code = 1;
					}
					else{
					latest_exam_code = Integer.valueOf(result_GetLatestExamCode).intValue(); 
					
			        exam_code = latest_exam_code +1;
					}
			        JSONArray jsonArray;
					
					try {
							jsonArray = new JSONArray(result_MySQL_DB_CourseMember);

							std_account_ID = new String[jsonArray.length()];
							count_member_of_course = jsonArray.length();
							
							for(int i = 0 ; i<jsonArray.length() ; i++)
							{
								JSONObject jsonData = jsonArray.getJSONObject(i);
								
								std_account_ID[i] = jsonData.getString("account_ID");
							}
					}
					
					catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        
			        //mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
				} 	

	
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
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
						Thread.sleep(500);
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

				if(result.equals(Integer.toString(exam_code)))				
				{						
					DataFromDatabase MySQL_DB_Insert = new DataFromDatabase();
					for(int i = 0;i <= count_for_ques ; i++)
					{			
						MySQL_DB_Insert.FC_InputPreExam(str_input_ques[i], str_chooseA[i], str_chooseB[i], str_chooseC[i], str_chooseD[i], str_point[i], str_ans[i],str_type[i], course_ID, Integer.toString(exam_code));						
					}
						
					for(int i = 0;i < count_member_of_course ; i++)
					{			
						MySQL_DB_Insert.FC_InputPreExamGradeMember(course_ID, Integer.toString(exam_code), std_account_ID[i]);							
					}
						
					MainFragment main_frag=new MainFragment();
					bundle.putString("title", title);
	            	bundle.putString("filepath", filepath);
	            	bundle.putString("note", note);
	            	bundle.putString("exam_code", String.valueOf(exam_code));
	            	bundle.putString("exam_name", exam_name );
	            	main_frag.setArguments(bundle);
	        		getFragmentManager().beginTransaction().replace(R.id.container, main_frag).commit();
	    			
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
		 Log.d("<==ExamCreate==>", "handler and thread have kill");
//		 refresh_receivepage_Thread.interrupt();
//		 downloadAttendListThread.interrupt();
	}
	
}
