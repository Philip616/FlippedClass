package com.mcucsie.flippedclass.group;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

public class GroupTchPublicDiscuss extends Fragment{
	
	GroupTchPublicDiscuss group_dicuss;
	GroupDiscussAdapter myAdapter;
	GetNowCourseInfo course_info;
	CreateRunnable CreateRunnable;
	TeacherWaitForDicussList TeacherWaitForDicussList;
	
	int dicuss_code=0,last_dicuss_code=0;
	String course_ID,set_dicuss_name=null;
	String[] discuss_name,discuss_number;
	Boolean TeacherWaitForDicussList_isRunning;
	
	EditText txt_dicuss_name;
	ListView myList;
	Button btn_createDicuss;
	ProgressBar pgb;
	AlertDialog.Builder dialog;
	
	Context context;
	ArrayList<HashMap<String, String>> List = null;
	static Handler mHandler;
	Bundle bundle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_group_public_discuss_tch, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		  
		bundle = getArguments();
		
		myList = (ListView)getView().findViewById(R.id.dicuss_list);
		btn_createDicuss = (Button)getView().findViewById(R.id.btn_create_dicuss);
		pgb = (ProgressBar)getView().findViewById(R.id.pgb_group_tch_dicuss);
		
		btn_createDicuss.setOnClickListener(btn_createDicuss_Click);
		
		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);
		
		context = getActivity();
			
		course_info = new GetNowCourseInfo(context);
		course_ID = course_info.getNowCourseID();
		
		mHandler = new Handler();
		
		dialog = new AlertDialog.Builder(context);
		
		initData();
	}
	
	private void initData(){
		DataFromDatabase MySQL_DB_Dicuss = new DataFromDatabase();
		MySQL_DB_Dicuss.FC_GetDiscussList(course_ID,"public","0");
		
    	TeacherWaitForDicussList = new TeacherWaitForDicussList(MySQL_DB_Dicuss);
		
    	TeacherWaitForDicussList_isRunning =true;
    	
    	Thread waitdata = new Thread(TeacherWaitForDicussList);
		waitdata.start(); 
	}
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();			
		endAllThread();
	}
	
	private OnClickListener btn_createDicuss_Click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			initCreateDialog();
			dialog.show();
		}
		
	};
	
	//創建討論區所跳出的dialog
	private void initCreateDialog(){
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.dialog_group_create_dicuss, null);
		
		dialog.setCancelable(false);
		
		txt_dicuss_name = (EditText)v.findViewById(R.id.create_dicuss_name);
		
		dialog.setTitle("請輸入討論區名稱").setView(v);
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
           
            }  
        }); 
		
		dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				set_dicuss_name = txt_dicuss_name.getText().toString();
				
				DataFromDatabase MySQL_DB = new DataFromDatabase();
				MySQL_DB.FC_createDiscuss(course_ID,"public","0");
				
				DataFromDatabase MySQL_DB_GetLastCode = new DataFromDatabase();
				MySQL_DB_GetLastCode.FC_GetLatestDiscussCode(course_ID,"public","0");
				
				DataFromDatabase My_SQL_List = new DataFromDatabase();
				My_SQL_List.FC_GetDiscussList(course_ID,"public","0");
				       	
				Thread inserdata = new Thread(new createDicussList(MySQL_DB,MySQL_DB_GetLastCode,My_SQL_List));
            	inserdata.start(); 	
            	
            	initData();
			}
	
		});
	}
	
	//等待討論區清單
	class TeacherWaitForDicussList implements Runnable
	{
		DataFromDatabase MySQL_DB_Dicuss;
		int times = 0,counttimes=0;
		
		public TeacherWaitForDicussList(DataFromDatabase MySQL_DB_Dicuss)
		{
			this.MySQL_DB_Dicuss = MySQL_DB_Dicuss;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {				
					while(TeacherWaitForDicussList_isRunning)
					{					
						times = 0;
						
						Thread.sleep(500);
						
						String result_Dicuss = MySQL_DB_Dicuss.return_result;
				
						while(TeacherWaitForDicussList_isRunning && result_Dicuss==null && times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "DicussMainTchFragment Waitfodata 多等了1秒");
							result_Dicuss = MySQL_DB_Dicuss.return_result;
							times++;
						}
						
						Log.d("=====>", "DicussMainTchFragment Waitfodata get result_Dicuss = " + result_Dicuss);
						
						JSONArray jsonArray_Dicuss;
						
						try {						
									List = new ArrayList<HashMap<String, String>>();
									
									jsonArray_Dicuss = new JSONArray(result_Dicuss);
									
									//exam_count = jsonArray_Exam.length();
									
									discuss_name = new String[jsonArray_Dicuss.length()];
									discuss_number = new String[jsonArray_Dicuss.length()];
									
									for(int i = 0 ; i<jsonArray_Dicuss.length() ; i++)
									{
										JSONObject jsonData = jsonArray_Dicuss.getJSONObject(i);
										
										discuss_name[i] = jsonData.getString("discuss_name");
										discuss_number[i] = jsonData.getString("discuss_number");
										
										HashMap<String, String> item = new HashMap<String, String>();
										item.put("discuss_name", discuss_name[i]);
										item.put("discuss_number", discuss_number[i]);
									    List.add(item);
								    
										//Log.d("=====>", "GroupChooseDicussFragment Waitfodata set dicuss_number["+ i + "] = " + discuss_number[i]);
									}
															
									if(TeacherWaitForDicussList_isRunning == true && counttimes < 5)
									{	
										
										mHandler.post(setViewToAdapter);					
										
										Thread.sleep(1000);
										counttimes++;

										MySQL_DB_Dicuss.FC_GetDiscussList(course_ID,"public","0");
									}
									
									else
										TeacherWaitForDicussList_isRunning = false;
								
									
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			
					}
					
			} catch (InterruptedException e) {
				 //TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	
	//確認創建討論區
	class createDicussList implements Runnable
	{
	
		DataFromDatabase MySQL_DB,MySQL_DB_GetLastCode,MySQL_DB_GetList;
		
		int times = 0;
		
		public createDicussList(DataFromDatabase MySQL_DB,DataFromDatabase MySQL_DB_GetLastCode,DataFromDatabase MySQL_DB_GetList)
		{
			this.MySQL_DB = MySQL_DB;
			this.MySQL_DB_GetLastCode = MySQL_DB_GetLastCode;
			this.MySQL_DB_GetList = MySQL_DB_GetList;
			
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					Thread.sleep(500);
					
					String result = MySQL_DB.return_result;
					String result_GetLastCode = MySQL_DB_GetLastCode.return_result;
							
					Log.d("=====>", "DicussMainTchFragment Waitfodata get result" + result);
					
					while(result==null && times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "DicussMainTchFragment Waitfodata 多等了1秒");
						result = MySQL_DB.return_result;
						result_GetLastCode = MySQL_DB_GetLastCode.return_result;
						times++;
					}
					
					Log.d("=====>", "DicussMainTchFragment Waitfodata get result" + result);
					
					
					//Log.d("result","a" + result);
			
					 last_dicuss_code = Integer.valueOf(result_GetLastCode).intValue();
					 dicuss_code = last_dicuss_code +1;
					
					//Log.d("=====>", "DicussMainTchFragment Waitfordata get dicuss_code" + dicuss_code);
		
					 CreateRunnable = new CreateRunnable(result);
					
					mHandler.post(CreateRunnable);
					
				} 
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	
	//
	@SuppressLint("SimpleDateFormat")
	 class CreateRunnable implements Runnable
	{
		String result;
		
		public CreateRunnable(String result)
		{
			this.result = result;
		
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			

				if(result.equals(Integer.toString(dicuss_code-1)))				
				{				
					
					//先行定義時間格式
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		            //取得現在日期
		            Date current = new Date();
		            //透過SimpleDateFormat的format方法將Date轉為字串
		            
		            String data=sdf.format(current);
        
					DataFromDatabase MySQL_DB_Insert = new DataFromDatabase();		
					MySQL_DB_Insert.FC_insertDiscuss(course_ID, set_dicuss_name, data, Integer.toString(dicuss_code),"public","0");						
			
					/*GroupMainTchDicuss frag_dicussmaintch=new GroupMainTchDicuss();
					getFragmentManager().beginTransaction().replace(R.id.frame_group_content, frag_dicussmaintch).commit();*/
				}
		}
	} 
	
	
	//顯示Adapter
	private Runnable setViewToAdapter = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			bundle.putString("DiscussType", "public");
			bundle.putString("groupNum","0");
			
			myAdapter = new GroupDiscussAdapter(context, List, bundle);
			myList.setAdapter(myAdapter);
			
			pgb.setVisibility(View.GONE);
		}
		
	};
	
	public void endAllThread(){
		
		 mHandler.removeCallbacks(TeacherWaitForDicussList);
		 mHandler.removeCallbacks(CreateRunnable);
		 mHandler.removeCallbacks(setViewToAdapter);
		 Log.d("<==DicussMainTeacher==>", "handler and thread have kill");
		 TeacherWaitForDicussList_isRunning=false;
	}
}

	
	