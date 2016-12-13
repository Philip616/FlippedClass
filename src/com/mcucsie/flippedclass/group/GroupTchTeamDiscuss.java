package com.mcucsie.flippedclass.group;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;


public class GroupTchTeamDiscuss extends Fragment {

	
	private ListView mListView;
	private TextView share_groupNum;
	private ProgressBar pgb;
	private Spinner mSpinner,Spinner_openTo;
	private Button share_group_discuss,close_discuss_share;
	private RadioGroup closeGroup,openGroup;
	private RadioButton close_this_share,close_other_share,share_this_discuss,share_other_discuss;
	
	private String course_ID,groupNum,share_groupNumber; //share_groupNumber==存關閉分享時被開放的組別
	private String[] discuss_name,discuss_number,group_name,group_openTo,share_group; //group_openTo=得到分享的組別,share_group=被開放的組別
	private String[] data={"無"};          //存關閉分享Spinner的資料
	private ArrayAdapter<String> lunchList; 
	private int JsonLength,JsonListLength;
	private int[] groupCount_data={0},shareGroup_data={0};
	private boolean WaitForDicussList_isRunning;
	private Handler mHandler;
	private float count = 0;
	
	AlertDialog.Builder dialog;
	
	GetNowCourseInfo gnci;
	GroupDiscussAdapter mAdapter;
	WaitForDicussList WaitForDicussList;
	UpdataSpinner UpdataSpinner;
	Bundle bundle;
	
	ArrayList<HashMap<String, String>> List = null;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_group_team_discuss_tch, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stubs
		super.onActivityCreated(savedInstanceState);
		
		gnci = new GetNowCourseInfo(getActivity());
		course_ID = gnci.getNowCourseID();
		
		mSpinner = (Spinner)getView().findViewById(R.id.tch_team_spinner);
		mListView = (ListView)getView().findViewById(R.id.tch_team_listView);
		pgb = (ProgressBar)getView().findViewById(R.id.tch_team_pgb);
		share_group_discuss = (Button)getView().findViewById(R.id.share_group_discuss);
		close_discuss_share = (Button)getView().findViewById(R.id.close_discuss_share);
		
			initSpinner();
			mHandler = new Handler();	
			bundle = getArguments();
			dialog = new AlertDialog.Builder(getActivity());
		
		
			share_group_discuss.setOnClickListener(btn_share_group_discuss);
			close_discuss_share.setOnClickListener(btn_close_discuss_share);
			mSpinner.setOnItemSelectedListener(SpinnerClick); 
			WaitForDicussList_isRunning =true;
		

	}
	
	private void initSpinner(){
		DataFromDatabase MySQL_DB_Spinner = new DataFromDatabase();
		MySQL_DB_Spinner.FC_GetGroupCount(course_ID);
		   	
		DataFromDatabase My_SQL_OpenDiscuss = new DataFromDatabase();
		My_SQL_OpenDiscuss.FC_CheckGroupDiscussOpen("discusslist_publicshare_"+course_ID+"_team", "0");
		
		DataFromDatabase My_SQL_PrivateList = new DataFromDatabase();
		My_SQL_PrivateList.FC_GetPrivateShareList("discusslist_privateshare_"+course_ID+"_team");
		
    	UpdataSpinner = new UpdataSpinner(MySQL_DB_Spinner,My_SQL_OpenDiscuss,My_SQL_PrivateList);	
    	Thread spinnerdata = new Thread(UpdataSpinner);
    	spinnerdata.start();
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();			
		endAllThread();
	}
	
	private OnClickListener btn_share_group_discuss = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			initShareDialog();
			dialog.show();
			//Log.d("Position","result="+groupNum);
		}
		
	};
	
	private OnClickListener btn_close_discuss_share = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			initCloseDialog();
			dialog.create().show();
		}
		
	};
	
	private void initShareDialog(){
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.dialog_group_share_discusss_tch, null);
		
		dialog.setCancelable(false);
		
		openGroup = (RadioGroup)v.findViewById(R.id.openGroup);
		share_groupNum = (TextView)v.findViewById(R.id.share_groupNum);
		share_this_discuss = (RadioButton)v.findViewById(R.id.share_this_discuss);
		share_other_discuss = (RadioButton)v.findViewById(R.id.share_other_discuss);
		
		dialog.setTitle("請選擇要開放的方式").setView(v);
		
		
		
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {     
            }  
        });
		
		dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
							
						if(share_this_discuss.isChecked()){				
									DataFromDatabase MySQL_DB = new DataFromDatabase();
									MySQL_DB.FC_ShareThisGroupDiscuss("discusslist_publicshare_"+course_ID+"_team", groupNum , "open");
									initSpinner();
						}
							
						if(share_other_discuss.isChecked()){		
								String share_group;
								share_group = share_groupNum.getText().toString();
								DataFromDatabase MySQL_DB1 = new DataFromDatabase();
								MySQL_DB1.FC_ShareOtherGroupDiscuss("discusslist_PrivateShare_"+course_ID+"_team", groupNum , share_group, "open");
								initSpinner();
							
							}
						}


		});
	}
	
	
	private void initCloseDialog(){
		
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View v = inflater.inflate(R.layout.dialog_group_close_share_tch, null);
			
		dialog.setCancelable(false);
		dialog.setTitle("請選擇要關閉的分享方式").setView(v);
		
		closeGroup = (RadioGroup)v.findViewById(R.id.closeGroup);
		Spinner_openTo = (Spinner)v.findViewById(R.id.Spinner_opeTo);
		close_this_share = (RadioButton)v.findViewById(R.id.close_this_share);
		close_other_share = (RadioButton)v.findViewById(R.id.close_other_share);
		
		int count=0;
		
		for(int i=0;i<JsonListLength;i++)
			if(groupNum.equals(share_group[i]))		
				data[count++] = group_openTo[i];
			
		
		lunchList = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, data);
		lunchList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner_openTo.setAdapter(lunchList);
		
		Spinner_openTo.setOnItemSelectedListener(Spinner_openTo_select);
		
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {     
            }  
        });
		
		dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				if(close_this_share.isChecked()){
					DataFromDatabase MySQL_DB = new DataFromDatabase();				
					MySQL_DB.FC_ShareThisGroupDiscuss("discusslist_"+course_ID+"_team", groupNum , "close");
					initSpinner();
				}
				
				if(close_other_share.isChecked()){
					DataFromDatabase MySQL_DB1 = new DataFromDatabase();				
					MySQL_DB1.FC_ShareOtherGroupDiscuss("discusslist_privateshare_"+course_ID+"_team", groupNum , share_groupNumber, "close");
					initSpinner();
				}
			}
		});
	}

	
	private OnItemSelectedListener Spinner_openTo_select = new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			share_groupNumber = data[position];
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	class UpdataSpinner implements Runnable
	{
		DataFromDatabase MySQL_DB,My_SQL_OpenDiscuss,My_SQL_PrivateList;
		int time = 0 ,counttimes = 0;
		String[] groupCount;
		
		
		public UpdataSpinner(DataFromDatabase MySQL_DB_Spinner,DataFromDatabase My_SQL_OpenDiscuss,DataFromDatabase My_SQL_PrivateList)
		{
			this.MySQL_DB = MySQL_DB_Spinner;
			this.My_SQL_OpenDiscuss = My_SQL_OpenDiscuss;
			this.My_SQL_PrivateList = My_SQL_PrivateList;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				time=0;
				JsonListLength = 0;
				JsonLength = 0;
				
				Thread.sleep(500);
				
				String result = MySQL_DB.return_result;
				String result_open = My_SQL_OpenDiscuss.return_result;
				String result_list = My_SQL_PrivateList.return_result;
				
				while(result==null && time<=10)
				{
					Thread.sleep(1000);
					Log.d("=====>", "DicussSpinner Waitfodata 多等了1秒");
					result = MySQL_DB.return_result;
					result_open = My_SQL_OpenDiscuss.return_result;
					result_list = My_SQL_PrivateList.return_result;
					time++;
				}
									
				if(result != null)
					count = Float.parseFloat(result);				
						
				JSONArray jsonArray_Discuss_Open,jsonArray_List;
				
				try {				
						
							share_group = new String[(int)(count+1)];
							groupCount = new String[(int)(count+1)];
							groupCount_data = new int[(int)(count+1)];
												
							for(int i = 0 ; i<(int)(count+1) ; i++)
								groupCount[i] = "null";
						
							result_open = result_open.trim();						
							
							if(!result_open.equals("null")){
								Log.d("error","result="+result_open);
								jsonArray_Discuss_Open = new JSONArray(result_open);
								JsonLength = jsonArray_Discuss_Open.length();							
								for(int j = 0 ; j<jsonArray_Discuss_Open.length() ; j++){
								JSONObject jsonData = jsonArray_Discuss_Open.getJSONObject(j);
								groupCount[j] = jsonData.getString("groupNum");
								
								groupCount_data[Integer.valueOf(groupCount[j])-1] = Integer.valueOf(groupCount[j]);
								//Log.d("error1","result="+groupCount_data[Integer.valueOf(groupCount[j])-1]);
								}
							}
							
							result_list = result_list.trim();
							
							if(!result_list.equals("null")){
								//Log.d("error","result="+result_list);
								jsonArray_List = new JSONArray(result_list);
								JsonListLength = jsonArray_List.length();
	
								group_openTo = new String[jsonArray_List.length()];
								shareGroup_data = new int[(int)(count+1)];
														
							
								for(int k = 0 ; k<jsonArray_List.length() ; k++){
									JSONObject jsonData1 = jsonArray_List.getJSONObject(k);
									group_openTo[k] = jsonData1.getString("openTo"); 
									share_group[k] = jsonData1.getString("groupNumber");
									shareGroup_data[Integer.valueOf(share_group[k])-1] = Integer.valueOf(share_group[k]);
							}
						}
							
							
		
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("Error","result="+e);
				}
			
					mHandler.post(setSpinner);
				
				
				}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
}

	
	Runnable setSpinner = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub

			group_name = new String[(int)(count+1)];

			for(int i = 0 ; i <= count; i++)	
				group_name[i] = "第"+ (i+1) + "組";

			if(getActivity()!=null)
			lunchList = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, group_name);
			lunchList.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
			mSpinner.setAdapter(lunchList);
			
		}
		
	};
	
	public OnItemSelectedListener SpinnerClick = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			
			pgb.setVisibility(View.VISIBLE);
			pgb.setVisibility(View.FOCUS_FORWARD);
					
			try {
				Thread.sleep(500);
				
				if(List != null)
					mListView.setAdapter(null);
								
				//Log.d("Error", "result="+shareGroup_data[position]);
				Log.d("Error2", "result="+groupCount_data[position]);
				
				if(JsonLength != 0){
					if((position+1) == groupCount_data[position])
						close_discuss_share.setEnabled(true);
					else
						close_discuss_share.setEnabled(false);
				}
				
				else if(JsonListLength != 0){
					if((position+1) == shareGroup_data[position])
						close_discuss_share.setEnabled(true);
				else
						close_discuss_share.setEnabled(false);
				}
				
				else
					close_discuss_share.setEnabled(false);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			groupNum = String.valueOf(position+1);
					
			DataFromDatabase MySQL_DB_Dicuss = new DataFromDatabase();
			MySQL_DB_Dicuss.FC_GetDiscussList(course_ID,"team",groupNum);
			WaitForDicussList_isRunning = true;
			WaitForDicussList = new WaitForDicussList(MySQL_DB_Dicuss);
			Thread waitdata = new Thread(WaitForDicussList);
			waitdata.start();  
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
						
						String result_Dicuss = MySQL_DB_Dicuss.return_result;
						
						
				
						while(WaitForDicussList_isRunning && result_Dicuss==null && times<=5)
						{
							Thread.sleep(1000);
							Log.d("=====>", "DicussMainTchFragment Waitfodata 多等了1秒");
							result_Dicuss = MySQL_DB_Dicuss.return_result;
							times++;
						}
						
						//Log.d("=====>", "DicussMainTchFragment Waitfodata get result_Dicuss = " + result_Dicuss);
						
						JSONArray jsonArray_Dicuss;
						
						try {						
									List = new ArrayList<HashMap<String, String>>();
									Thread.sleep(500);
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
								    
										Log.d("=====>", "GroupChooseDicussFragment Waitfodata set dicuss_number["+ i + "] = " + discuss_number[i]);
									}
	
										mHandler.post(setViewToAdapter);					
										WaitForDicussList_isRunning = false;
								
									
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
	
	private Runnable setViewToAdapter = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			bundle.putString("DiscussType","team");
			bundle.putString("groupNum",groupNum);
			
			mAdapter = new GroupDiscussAdapter(getActivity(), List,bundle);
			mListView.setAdapter(mAdapter);
			
			pgb.setVisibility(View.GONE);
		}
		
	};
	
	public void endAllThread(){
		
		 mHandler.removeCallbacks(WaitForDicussList);
		 mHandler.removeCallbacks(setViewToAdapter);
		 mHandler.removeCallbacks(UpdataSpinner);
		 mHandler.removeCallbacks(setSpinner);
		 
		 Log.d("<==DicussMainTeacher==>", "handler and thread have kill");
		 WaitForDicussList_isRunning=false;
	}
	
}


