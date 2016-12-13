package com.mcucsie.flippedclass.group;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class GroupStdPublicDiscuss extends Fragment{

	StdWaitForDicussList StdWaitForDicussList;
	GetNowCourseInfo course_info;
	
	GroupDiscussAdapter myAdapter;
	ListView myList;
	ProgressBar pgb;
	Handler myHandler;
	
	Context context;
	ArrayList<HashMap<String, String>> List = null;
	
	String course_ID,Account;
	Byte check_ID;
	String[] discuss_name,discuss_number;
	Bundle bundle;
	boolean isStdWaitListRuning;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_group_public_discuss_std, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		bundle = getArguments();
		
		myList = (ListView)getView().findViewById(R.id.list_std);
		pgb = (ProgressBar)getView().findViewById(R.id.pgb_group_std_dicuss);
		
		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);
		
		context = getActivity();
		myHandler = new Handler();
		
		course_info = new GetNowCourseInfo(context);
		course_ID = course_info.getNowCourseID();
		
		DataFromDatabase MySQL_DB_Dicuss = new DataFromDatabase();
		MySQL_DB_Dicuss.FC_GetDiscussList(course_ID,"public","0");
		
		StdWaitForDicussList = new StdWaitForDicussList(MySQL_DB_Dicuss);
		
		isStdWaitListRuning = true;
		
		Thread waitData = new Thread(StdWaitForDicussList);
		waitData.start();
		
		
		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		endAllThread();
	}
	
	class StdWaitForDicussList implements Runnable
	{
		DataFromDatabase MySQL_DB_Dicuss;
		int times = 0,counttimes=0;
		
		public StdWaitForDicussList(DataFromDatabase MySQL_DB_Dicuss)
		{
			this.MySQL_DB_Dicuss = MySQL_DB_Dicuss;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {				
					while(isStdWaitListRuning)
					{					
						times = 0;
						
						Thread.sleep(500);
						
						String result_Dicuss = MySQL_DB_Dicuss.return_result;
				
						while(isStdWaitListRuning && result_Dicuss==null && times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "DicussMainStdFragment Waitfodata 多等了1秒");
							result_Dicuss = MySQL_DB_Dicuss.return_result;
							times++;
						}
						
						Log.d("=====>", "DicussMainStdFragment Waitfodata get result_Dicuss = " + result_Dicuss);
						
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
								    
										Log.d("=====>", "GroupChooseDicussFragment Waitfodata set discuss_number["+ i + "] = " + discuss_number[i]);
									}
															
									if(isStdWaitListRuning == true && counttimes < 5)
									{	
										
										myHandler.post(setViewToAdapter);					
										
										Thread.sleep(3000);
										counttimes++;

										MySQL_DB_Dicuss.FC_GetDiscussList(course_ID,"public","0");
									}
									
									else
										isStdWaitListRuning = false;
								
									
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
	
	//顯示Adapter
		private Runnable setViewToAdapter = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub	
				bundle.putString("DiscussType", "public");
				bundle.putString("groupNum","0");
				
				myAdapter = new GroupDiscussAdapter(context,List,bundle);
				myList.setAdapter(myAdapter);	
				pgb.setVisibility(View.GONE);
			}
			
		};
		
		public void endAllThread(){	
			
			 myHandler.removeCallbacks(StdWaitForDicussList);
			 myHandler.removeCallbacks(setViewToAdapter);
			 Log.d("<==DicussMainTeacher==>", "handler and thread have kill");
			 isStdWaitListRuning=false;
		}
	

}
