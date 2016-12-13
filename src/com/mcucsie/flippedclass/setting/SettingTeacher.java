package com.mcucsie.flippedclass.setting;

import android.os.Bundle;
import android.os.Handler;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;
import com.mcucsie.flippedclass.remind.upload;


public class SettingTeacher extends Fragment{

	AlertDialog.Builder dialog;
	
	Button btn_create_course,btn_updata_course,btn_delete_course;
	
	private String  course_ID,course_name,start_Date,end_Date,Account_ID,remind_day,next_date;
	
	private GetNowCourseInfo course_info;
	private GetNowAccountInfo account_info;
	
	static Handler mHandler;
	WaitForInsertCourse WaitForInsertCourse;
 	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_setting_tch, container, false);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		
		mHandler = new Handler();
	
		btn_create_course = (Button)getView().findViewById(R.id.new_course);
		btn_updata_course = (Button)getView().findViewById(R.id.updata_course);
		btn_delete_course = (Button)getView().findViewById(R.id.delete_course);
		
			
		btn_create_course.setOnClickListener(btn_create_course_click);
		btn_updata_course.setOnClickListener(btn_updata_course_click);
		btn_delete_course.setOnClickListener(btn_delete_course_click);
		
		
		course_info = new GetNowCourseInfo(getActivity());
		account_info = new GetNowAccountInfo(getActivity());
		
		course_ID = course_info.getNowCourseID(); 

		dialog = new AlertDialog.Builder(getActivity());
		
		initView();
		
	     int a = course_info.getAllCourseCount();
	     Log.d("設定測試","設定: " + a);
	    
		
	}
	
	private OnClickListener btn_create_course_click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			initCreateDialog();
			dialog.show();
		}
		
	};
	
	private OnClickListener btn_updata_course_click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			initUpdateDialog();
			dialog.show();
		}
		
	};
	
	private OnClickListener btn_delete_course_click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			initDeleteDialog();
			dialog.show();
		}
		
	};
	
	private void initDeleteDialog(){
		dialog.setCancelable(false);
		
		dialog.setTitle("確定刪除此課程?");
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                // 按下PositiveButton要做的事  
                //Toast.makeText(getActivity(), "請繼續作答", Toast.LENGTH_SHORT).show();
            }  
        }); 
		
		dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				DataFromDatabase MySQL_DB = new DataFromDatabase();
            	
				MySQL_DB.FC_DeleteCourse(course_ID);
            	
				Thread inserdata = new Thread(new WaitForInsertCourse(MySQL_DB));
            	inserdata.start();
            	
            	Toast.makeText(getActivity(), "請重新開啟程式已更新資料", Toast.LENGTH_SHORT).show();
			}
	
		});
	}
	
	private void initUpdateDialog(){
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.dialog_setting_update_course, null);
		dialog.setCancelable(false);
		
		final EditText txt_nextDate;
		final EditText txt_endDate;
		final EditText txt_remindDay;
		String classdate;
		final String[] data;
    	classdate = course_info.getSettingCourseInfo(course_ID);
		data = classdate.split(":");

		txt_nextDate = (EditText)v.findViewById(R.id.Updata_next_date);
		txt_endDate = (EditText)v.findViewById(R.id.Updata_end_date);
		txt_remindDay = (EditText)v.findViewById(R.id.Updata_remind_day);
		
		txt_nextDate.setText(data[1]);
		txt_endDate.setText(data[2]);
		txt_remindDay.setText(data[0]);
	
		dialog.setTitle("請輸入修改資料").setView(v);
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                // 按下PositiveButton要做的事  
            	
            }  
        }); 
		
		dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			
			
				remind_day = txt_remindDay.getText().toString();
				next_date = txt_nextDate.getText().toString();
				end_Date = txt_endDate.getText().toString();
				
				
				if(remind_day.isEmpty())
					remind_day = data[0];		
				if(next_date.isEmpty())
					next_date = data[1];	
				if(end_Date.isEmpty())
					end_Date = data[2];
			
				//Log.d("Setting Updata", next_date);
				
				//Toast.makeText(getActivity(), "課程代碼" + course_ID + "課程名稱" + course_name, Toast.LENGTH_SHORT).show();
				
				
				if(!remind_day.isEmpty() || !next_date.isEmpty() || !end_Date.isEmpty()){
				DataFromDatabase MySQL_DB = new DataFromDatabase();      	
				Thread inserdata = new Thread(new WaitForInsertCourse(MySQL_DB));
				MySQL_DB.FC_UpdataCourse(next_date, end_Date, course_ID, remind_day);
            	inserdata.start();
            	
            	Toast.makeText(getActivity(), "請重新開啟程式已更新資料", Toast.LENGTH_SHORT).show();
				}
			}
	
		});
	}
	
	private void initCreateDialog(){
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.dialog_setting_create_course, null);
		dialog.setCancelable(false);
		
		dialog.setTitle("請輸入課程資料").setView(v);
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                // 按下PositiveButton要做的事  
                //Toast.makeText(getActivity(), "請繼續作答", Toast.LENGTH_SHORT).show();
            }  
        }); 
		
		dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EditText courseName,courseID,txt_startDate,txt_endDate,txt_remindDay;
				
				txt_startDate = (EditText)v.findViewById(R.id.start_Date);
				txt_endDate = (EditText)v.findViewById(R.id.end_Date);
				courseName = (EditText)v.findViewById(R.id.courseName);
				courseID = (EditText)v.findViewById(R.id.courseID);	
				txt_remindDay = (EditText)v.findViewById(R.id.remind_day);
				
				remind_day = txt_remindDay.getText().toString();
				start_Date = txt_startDate.getText().toString();
				end_Date = txt_endDate.getText().toString();
				
				course_ID = courseID.getText().toString();
				course_name = courseName.getText().toString();
				Account_ID = account_info.getNowAccountID();
				
				
				
				if(remind_day.isEmpty())
					remind_day = "3";
				
				if(!course_ID.isEmpty()){
				DataFromDatabase MySQL_DB = new DataFromDatabase();
            	
				MySQL_DB.FC_insertCourse(course_ID, course_name, start_Date, end_Date,Account_ID,remind_day);
            	
				Thread inserdata = new Thread(new WaitForInsertCourse(MySQL_DB));
            	inserdata.start();
        		
        		Thread waitdata = new Thread(new WaitForInsertCourse(MySQL_DB));
        		MySQL_DB.FC_insertPersonalCourse(course_ID, Account_ID);
        		waitdata.start();
        		
        		Toast.makeText(getActivity(), "請重新開啟程式已更新資料", Toast.LENGTH_SHORT).show();
				}
				
			}
	
		});
	}
	
	class WaitForInsertCourse implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		
		public WaitForInsertCourse(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
				try {
						Thread.sleep(500);
						
						String result = MySQL_DB.return_result;
						Log.d("=====>", "SettingTeacher Waitfordata get result" + result);
						
						while(result==null && times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "SettingTeacher Waitfordata 多等了1秒");
							result = MySQL_DB.return_result;
							times++;
						}
						
						Log.d("=====>", "SettingTeacher Waitfordata get result" + result);
						
					} 
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		
		
	}
	
	public void initView(){
		TextView start_time,end_time,remind_time;
		String classinfo;
		String[] data; 
		
		start_time = (TextView)getView().findViewById(R.id.start_time);
		end_time = (TextView)getView().findViewById(R.id.end_time);
		remind_time = (TextView)getView().findViewById(R.id.remind_time);
		
		classinfo = course_info.getSettingCourseInfo(course_ID);
		data = classinfo.split(":");
		
		remind_time.setText(data[0]);
		start_time.setText(data[1]);
		end_time.setText(data[2]);
		
		
		//Log.d("<==SettingMainTeacher==>", classinfo);
	}
	
	public void endAllThread(){
		
		 mHandler.removeCallbacks(WaitForInsertCourse);
		 Log.d("<==SettingMainTeacher==>", "handler and thread have kill");
//		 refresh_receivepage_Thread.interrupt();
//		 downloadAttendListThread.interrupt();
	}

}


