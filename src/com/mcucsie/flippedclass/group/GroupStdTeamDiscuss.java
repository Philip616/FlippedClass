package com.mcucsie.flippedclass.group;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;
import com.mcucsie.flippedclass.group.GroupTchPublicDiscuss.CreateRunnable;
import com.mcucsie.flippedclass.group.GroupTchPublicDiscuss.createDicussList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class GroupStdTeamDiscuss extends Fragment {
	
	private ListView mListView;
	private ProgressBar pgb;
	private Spinner mSpinner;
	private Button create_discuss,ask_share_discuss;
	private AlertDialog.Builder CreateDialog,ShareDialog;
	private EditText txt_discuss_name,shareTo_groupNum;
	
	private String course_ID,Account,set_discuss_name,sharegroupNum;;
	private String[] discuss_name,discuss_number,Spinnername,groupCount;
	private String[] group_openTo,share_group,getSpinnername;
	private int[] shareGroup_data;
	private int last_dicuss_code=0,discuss_code=0,groupnum,JsonListLength;
	private boolean WaitForDicussList_isRunning;
	private ArrayList<HashMap<String, String>> List = null;
	private ArrayAdapter<String> lunchList;
	private Handler mHandler;
	private Bundle bundle;
	
	GetNowCourseInfo gnci;
	GetNowAccountInfo gnai;
	WaitForDicussList WaitForDicussList;
	WaitForSpinner WaitForSpinner;
	GroupDiscussAdapter mAdapter;
	CreateRunnable CreateRunnable;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_group_team_discuss_std, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		gnci = new GetNowCourseInfo(getActivity());
		gnai = new GetNowAccountInfo(getActivity());
		
		Account=gnai.getNowAccountID();
		course_ID = gnci.getNowCourseID();
		
		mListView = (ListView)getView().findViewById(R.id.std_team_listView);
		pgb = (ProgressBar)getView().findViewById(R.id.std_team_pgb);
		mSpinner = (Spinner)getView().findViewById(R.id.std_team_spinner);
		create_discuss = (Button)getView().findViewById(R.id.std_create_discuss_btn);
		ask_share_discuss = (Button)getView().findViewById(R.id.ask_share_discuss);
		
		create_discuss.setOnClickListener(btn_create_discuss);
		ask_share_discuss.setOnClickListener(btn_ask_share_discuss);
		mSpinner.setOnItemSelectedListener(SpinnerSelect);
		
		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);
		
		mHandler = new Handler();
		bundle = getArguments();
		CreateDialog = new AlertDialog.Builder(getActivity());
		ShareDialog = new AlertDialog.Builder(getActivity());
		
		getGroupmember_data();

		DataFromDatabase My_SQL_OpenDiscuss = new DataFromDatabase();
		My_SQL_OpenDiscuss.FC_CheckGroupDiscussOpen("discusslist_publicshare_"+course_ID+"_team", String.valueOf(groupnum));
		
		DataFromDatabase My_SQL_PrivateList = new DataFromDatabase();
		My_SQL_PrivateList.FC_GetPrivateShareList("discusslist_privateshare_"+course_ID+"_team");
		
		WaitForSpinner = new WaitForSpinner(My_SQL_OpenDiscuss,My_SQL_PrivateList);
		Thread waitSpinner = new Thread(WaitForSpinner);
		waitSpinner.start();

	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();			
		endAllThread();
	}
	
	private OnClickListener btn_ask_share_discuss = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			initShareDialog();
			ShareDialog.show();
		}
		
	};
	
	private OnClickListener btn_create_discuss = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			initCreateDialog();
			CreateDialog.show();
		}
		
	};
	
		private void initShareDialog(){
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			final View v = inflater.inflate(R.layout.dialog_group_ask_share_std, null);
			
			ShareDialog.setCancelable(false);	
			
			shareTo_groupNum = (EditText)v.findViewById(R.id.shareTo_groupNum);
	
			ShareDialog.setTitle("分享討論區").setView(v);
			ShareDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			
			ShareDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sharegroupNum = shareTo_groupNum.getText().toString();
					
					DataFromDatabase MySQL_DB = new DataFromDatabase();
					MySQL_DB.GCM_sendMessageToCourseTeacher(course_ID,"TAG5 : 第【"+groupnum+"】組 "+"請求開放討論區給第【"+sharegroupNum+"】組");
				}
			});
		}
	
	//創建討論區所跳出的dialog
		private void initCreateDialog(){
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			final View v = inflater.inflate(R.layout.dialog_group_create_dicuss, null);
			
			CreateDialog.setCancelable(false);
			
			txt_discuss_name = (EditText)v.findViewById(R.id.create_dicuss_name);
	
			CreateDialog.setTitle("請輸入討論區名稱").setView(v);
			CreateDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {  
	           
	            }  
	        }); 
			
			CreateDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					set_discuss_name = txt_discuss_name.getText().toString();
					
					Log.d("groupnum","result="+groupnum );
					
					DataFromDatabase MySQL_DB = new DataFromDatabase();
					MySQL_DB.FC_createDiscuss(course_ID,"team",String.valueOf(groupnum));
					
					DataFromDatabase MySQL_DB_GetLastCode = new DataFromDatabase();
					MySQL_DB_GetLastCode.FC_GetLatestDiscussCode(course_ID,"team",String.valueOf(groupnum));
					
					DataFromDatabase My_SQL_List = new DataFromDatabase();
					My_SQL_List.FC_GetDiscussList(course_ID,"team",String.valueOf(groupnum));
					       	
					Thread inserdata = new Thread(new createDicussList(MySQL_DB,MySQL_DB_GetLastCode,My_SQL_List));
	            	inserdata.start();
	            	
	            	DataFromDatabase My_SQL_OpenDiscuss = new DataFromDatabase();
	        		My_SQL_OpenDiscuss.FC_CheckGroupDiscussOpen("discusslist_publicshare_"+course_ID+"_team", String.valueOf(groupnum));  		
	        		DataFromDatabase My_SQL_PrivateList = new DataFromDatabase();
	        		My_SQL_PrivateList.FC_GetPrivateShareList("discusslist_privateshare_"+course_ID+"_team");
	        		
	        		WaitForSpinner = new WaitForSpinner(My_SQL_OpenDiscuss,My_SQL_PrivateList);
	        		Thread waitSpinner = new Thread(WaitForSpinner);
	        		waitSpinner.start();
				}
		
			});
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
								
						//Log.d("=====>", "DiscussStdTeamFragment Waitfodata get result" + result);
						
						while(result==null && times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "DiscussStdTeamFragment Waitfodata 多等了1秒");
							result = MySQL_DB.return_result;
							result_GetLastCode = MySQL_DB_GetLastCode.return_result;
							times++;
						}
						
						Log.d("=====>", "DiscussStdTeamFragment Waitfodata get result" + result);
						
				
						 last_dicuss_code = Integer.valueOf(result_GetLastCode).intValue();
						 discuss_code = last_dicuss_code +1;
						
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
				

					if(result.equals(Integer.toString(discuss_code-1)))				
					{				
						
						//先行定義時間格式
			            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			            //取得現在日期
			            Date current = new Date();
			            //透過SimpleDateFormat的format方法將Date轉為字串
			            
			            String data=sdf.format(current);
	        
						DataFromDatabase MySQL_DB_Insert = new DataFromDatabase();		
						MySQL_DB_Insert.FC_insertDiscuss(course_ID, set_discuss_name, data, Integer.toString(discuss_code),"team",String.valueOf(groupnum));						
				
						/*GroupMainTchDicuss frag_dicussmaintch=new GroupMainTchDicuss();
						getFragmentManager().beginTransaction().replace(R.id.frame_group_content, frag_dicussmaintch).commit();*/
					}
			}
		} 
	
	@SuppressWarnings("resource")
	public void getGroupmember_data() {
		// TODO Auto-generated method stub
		int time=0;
		
		SQLiteDatabase db=getActivity().openOrCreateDatabase("FlippedClass_DataBase", getActivity().MODE_PRIVATE, null);
		Cursor cursor=db.rawQuery("SELECT groupnum FROM GroupMember_"+course_ID + " WHERE member='"+Account+"'", null);
		
			try{
				while(time<=10 && cursor.getCount()==0){
					Thread.sleep(1000);
					db=getActivity().openOrCreateDatabase("FlippedClass_DataBase", getActivity().MODE_PRIVATE, null);
					cursor=db.rawQuery("SELECT groupnum FROM GroupMember_"+course_ID + " WHERE member='"+Account+"'", null);
					time++;
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
		cursor.moveToFirst();
		//Log.d("Count","result="+cursor.getCount());
		String count=cursor.getString(0);	
		groupnum = Integer.valueOf(count) + 1;

	}
	
	class WaitForSpinner implements Runnable
	{
		DataFromDatabase My_SQL_OpenDiscuss,My_SQL_PrivateList;
		int time = 0,counttimes = 0;
		
		public WaitForSpinner(DataFromDatabase My_SQL_OpenDiscuss,DataFromDatabase My_SQL_PrivateList)
		{
			this.My_SQL_OpenDiscuss = My_SQL_OpenDiscuss;
			this.My_SQL_PrivateList = My_SQL_PrivateList;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {								
					time = 0;
					
					Thread.sleep(500);
					String result_Open = My_SQL_OpenDiscuss.return_result;
					String result_list = My_SQL_PrivateList.return_result;
			
					while(result_Open == null && time<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "DicussWaitSpinner Waitfodata 多等了1秒");
						result_Open = My_SQL_OpenDiscuss.return_result;
						result_list = My_SQL_PrivateList.return_result;
						time++;
					}
					
					//Log.d("=====>", "DicussTeamStdFragment Waitfodata get result_Dicuss = " + result_Dicuss);
					
					JSONArray jsonArray_Discuss_Open,jsonArray_List = null;
					result_Open = result_Open.trim();
					
					if(!result_Open.equals("null")){	
					try {						
								jsonArray_Discuss_Open = new JSONArray(result_Open);	
								
								//exam_count = jsonArray_Exam.length();
								
								result_list = result_list.trim();
								
								if(!result_list.equals("null")){
									jsonArray_List = new JSONArray(result_list);
			
									group_openTo = new String[jsonArray_List.length()];
									share_group = new String[jsonArray_List.length()];
									JsonListLength = jsonArray_List.length();
									
									for(int i = 0 ; i < jsonArray_List.length() ; i++){
										JSONObject jsonData = jsonArray_List.getJSONObject(i);					
										group_openTo[i] = jsonData.getString("openTo"); 
										share_group[i] = jsonData.getString("groupNumber");
										
									}
								}
									else
										JsonListLength = 0;
								
								Spinnername = new String[((int)jsonArray_Discuss_Open.length()+JsonListLength)];
								groupCount = new String[((int)jsonArray_Discuss_Open.length()+JsonListLength)];
								
								int j;			
								
								for(j = 0 ; j<jsonArray_Discuss_Open.length() ; j++){
									JSONObject jsonData = jsonArray_Discuss_Open.getJSONObject(j);
									Spinnername[j] = "第"+jsonData.getString("groupNum")+"組";
									groupCount[j] = jsonData.getString("groupNum");
								}
								
								if(!result_list.equals("null"))
								for(int k = 0 ; k<JsonListLength ; k++){
									JSONObject jsonData = jsonArray_List.getJSONObject(k);	
									
									if(Integer.valueOf(group_openTo[k]) == groupnum){				
										Spinnername[j] = "第"+jsonData.getString("groupNumber")+"組";
										groupCount[j] = jsonData.getString("groupNumber");	
										j++;
									}
									
								}
								
								getSpinnername = new String[j];
								for(int i=0;i<(int)jsonArray_Discuss_Open.length()+JsonListLength;i++)
									if(Spinnername[i] != null)
										getSpinnername[i] = Spinnername[i];
								
									mHandler.post(setSpinner);
			
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
	
	Runnable setSpinner = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(getActivity()!=null)
			lunchList = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, getSpinnername);
			lunchList.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
			mSpinner.setAdapter(lunchList);
					
		}
		
	};
	
	private OnItemSelectedListener SpinnerSelect = new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			
			pgb.setVisibility(View.VISIBLE);
			pgb.setVisibility(View.FOCUS_FORWARD);
			
			if(List != null)
				mListView.setAdapter(null);
				
			if(groupnum != Integer.valueOf(groupCount[position]))
				create_discuss.setEnabled(false);
			else
				create_discuss.setEnabled(true);
			
			bundle.putString("groupNum",groupCount[position]);
			
			DataFromDatabase My_SQL_Discuss = new DataFromDatabase();
			My_SQL_Discuss.FC_GetDiscussList(course_ID,"team",groupCount[position]);
			WaitForDicussList_isRunning = true;
			WaitForDicussList = new WaitForDicussList(My_SQL_Discuss);
			Thread waitData = new Thread(WaitForDicussList);
			waitData.start();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	class WaitForDicussList implements Runnable
	{
		DataFromDatabase MySQL_DB_Dicuss;
		int times = 0,counttimes=0;
		
		public WaitForDicussList(DataFromDatabase MySQL_DB_Dicuss)
		{
			this.MySQL_DB_Dicuss = MySQL_DB_Dicuss;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {				
					while(WaitForDicussList_isRunning)
					{					
						times = 0;
						
						Thread.sleep(500);
						
						String result_Discuss = MySQL_DB_Dicuss.return_result;
				
						while(WaitForDicussList_isRunning && result_Discuss==null && times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "DicussMainTchFragment Waitfodata 多等了1秒");
							result_Discuss = MySQL_DB_Dicuss.return_result;
							times++;
						}
						
						//Log.d("=====>", "DicussTeamStdFragment Waitfodata get result_Dicuss = " + result_Dicuss);
						
						JSONArray jsonArray_Discuss;
						
						result_Discuss = result_Discuss.trim();
						
						if(!result_Discuss.equals("null")){
						try {						
									List = new ArrayList<HashMap<String, String>>();
									
									jsonArray_Discuss = new JSONArray(result_Discuss);
									
									//exam_count = jsonArray_Exam.length();
									
									discuss_name = new String[jsonArray_Discuss.length()];
									discuss_number = new String[jsonArray_Discuss.length()];
	
									
									for(int i = 0 ; i<jsonArray_Discuss.length() ; i++)
									{
										JSONObject jsonData = jsonArray_Discuss.getJSONObject(i);
																				
										discuss_name[i] = jsonData.getString("discuss_name");
										discuss_number[i] = jsonData.getString("discuss_number");
	
										HashMap<String, String> item = new HashMap<String, String>();
										item.put("discuss_name", discuss_name[i]);
										item.put("discuss_number", discuss_number[i]);
									    List.add(item);
								
									}					
					
										mHandler.post(setViewToAdapter);					
										WaitForDicussList_isRunning = false;
								
									
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			
				}
					
			} catch (InterruptedException e) {
				 //TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	private Runnable setViewToAdapter = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			bundle.putString("DiscussType", "team");
		
			mAdapter = new GroupDiscussAdapter(getActivity(), List, bundle);
			mListView.setAdapter(mAdapter);
			
			pgb.setVisibility(View.GONE);
		}
	};
	
	public void endAllThread(){
		
		 mHandler.removeCallbacks(WaitForDicussList);
		 mHandler.removeCallbacks(setViewToAdapter);
		 mHandler.removeCallbacks(WaitForSpinner);
		 
		 Log.d("<==DicussMainTeacher==>", "handler and thread have kill");
		 WaitForDicussList_isRunning=false;
	}

}
