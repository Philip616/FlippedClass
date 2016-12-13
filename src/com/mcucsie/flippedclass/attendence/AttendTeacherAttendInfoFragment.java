package com.mcucsie.flippedclass.attendence;


import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AttendTeacherAttendInfoFragment extends Fragment {
	private CheckBox cb;
	private Spinner spinner;
	private GridView gv_title;
	private GridView gv_info;
	private ProgressBar pgb;
	private AlertDialog.Builder dialog;
	private static Handler mhandler=new Handler();
	private GetNowCourseInfo gnci;
	private int notcheck_count=0;
	private String[] attendlist_date;
	private String[] index;
	private String[] start_time;
	private String[] end_time;
	private String[] info_account_ID;
	private String[] info_attend_tag;
	private String[] info_attend_time;
	private String[] info_uncheck_account_ID;
	private String[] info_uncheck_attend_tag;
	private String[] info_uncheck_attend_time;
	private Thread setSpinnerThread;
	private Thread setGridViewThread;
	private String now_attend_index;
	private String now_course_ID;
	private Boolean isRefreshThread=true;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_attend_teacherattendinfo, container, false); 
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		gnci=new GetNowCourseInfo(getActivity());
		now_course_ID=gnci.getNowCourseID();
		
		cb=(CheckBox)getView().findViewById(R.id.cb_attend_teacherinfo_only);
		gv_title=(GridView)getView().findViewById(R.id.gv_attend_teacherinfotitle);
		gv_info=(GridView)getView().findViewById(R.id.gv_attend_teacherinfo);
		pgb=(ProgressBar)getView().findViewById(R.id.pgb_attend_teacherattendinfo);
		spinner = (Spinner)getView().findViewById(R.id.spinner_attend);
		cb.setVisibility(View.GONE);
		cb.setOnCheckedChangeListener(checkbox_change_listener);
		setSpinnerThread=new Thread(downloadAttenList_runnable);
		if(gnci.getNowCourseID().equals("null"))
		{
			Toast.makeText(getActivity(), "你還沒選課",Toast.LENGTH_LONG).show();
			pgb.setVisibility(View.GONE);
		}
		else
		{
		 setSpinnerThread.start();
		 spinner.setOnItemSelectedListener(spinner_item_selected);
		}
	}
	private OnCheckedChangeListener checkbox_change_listener=new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			pgb.setVisibility(View.VISIBLE);
			pgb.setVisibility(View.FOCUS_FORWARD);
			initGridView();
		}
	};
	
	private OnItemSelectedListener spinner_item_selected=new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
//			Toast.makeText(getActivity(), "你現在選的是"+index[position]+attendlist_date[position],Toast.LENGTH_LONG).show();
			now_attend_index=index[position];
			pgb.setVisibility(View.VISIBLE);
			pgb.setVisibility(View.FOCUS_FORWARD);
			cb.setVisibility(View.GONE);
			gv_info.setVisibility(View.GONE);
			//下載某次的全部出席紀錄
			setGridViewThread=new Thread(downloadAttendinfo_runnable);
			setGridViewThread.run();
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
	private Runnable downloadAttenList_runnable=new Runnable() {
		DataFromDatabase db=new DataFromDatabase();
		String table_name;
		@Override
		public void run() {
			now_course_ID=gnci.getNowCourseID();
			// TODO Auto-generated method stub
			table_name="attendlist_"+now_course_ID;
//			Log.d("<==TeacherAttendInfo==>", "table_name="+table_name);
			db.FC_getAttendList(table_name);
			String result=null;
			int times=0;
			try {
					Thread.sleep(1000);
					result=db.return_result;
					while(times<=10&&result==null){
					   Thread.sleep(1000);
					   result=db.return_result;
					   Log.d("<==TeacherAttendInfo==>", "downloadAttenList_runnable多休息了1秒");	
					   times++;
				   }
					if(times<=10){
						Log.d("<==TeacherAttendInfo==>", "成功抓取點名清單"+result);
						JSONArray jsonArray;
						try {
								jsonArray = new JSONArray(result);
								
								index = new String[jsonArray.length()];
								attendlist_date = new String[jsonArray.length()];
								start_time = new String[jsonArray.length()];
								end_time = new String[jsonArray.length()];
								
								
								for(int i = 0 ; i<jsonArray.length() ; i++)
								{
									JSONObject jsonData = jsonArray.getJSONObject(i);
									index[i] = jsonData.getString("indax");
									attendlist_date[i] = jsonData.getString("date");
									start_time[i] = jsonData.getString("start_time");
									end_time[i] = jsonData.getString("end_time");
//									Log.d("=====>", "GroupFragment 找到的Json = "+course_member[i]);
								}
								
								try {
									if(isRefreshThread)
								    mhandler.post(setSpinnerRunnable);
				  				} catch (Exception e) {
				  					// TODO: handle exception
				  				}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}
	};
	private Runnable downloadAttendinfo_runnable=new Runnable() {
		DataFromDatabase db2=new DataFromDatabase();
		String table_name;
		@Override
		public void run() {
			now_course_ID=gnci.getNowCourseID();
			// TODO Auto-generated method stub
			table_name="attend_"+now_course_ID+"_"+now_attend_index;
//			Log.d("<==TeacherAttendInfo==>", "table_name="+table_name);
			db2.FC_queryForAttendInfoList(table_name);
			String result2=null;
			int times=0;
			try {
					Thread.sleep(1000);
					result2=db2.s_return_result;
					while(times<=10&&result2==null){
					   Thread.sleep(1000);
					   result2=db2.s_return_result;
					   Log.d("<==TeacherAttendInfo==>", "downloadAttendinfo_runnable多休息了1秒");	
					   times++;
				    }
					if(times<=10){
//						Log.d("<==TeacherAttendInfo==>", "成功抓取點名INFO清單"+result2);
						JSONArray jsonArray;
						try {
								jsonArray = new JSONArray(result2);
								
								info_account_ID = new String[jsonArray.length()];
								info_attend_tag = new String[jsonArray.length()];
								info_attend_time = new String[jsonArray.length()];
								notcheck_count=0;
								
								for(int i = 0 ; i<jsonArray.length() ; i++)
								{
									JSONObject jsonData = jsonArray.getJSONObject(i);
									info_account_ID[i] = jsonData.getString("account_ID");
									info_attend_tag[i] = jsonData.getString("attend_tag");
									info_attend_time[i] = jsonData.getString("attend_time");
									
									if(info_attend_tag[i].equals("0"))
										notcheck_count++;
//									Log.d("=====>", "GroupFragment 找到的Json = "+course_member[i]);
								}
								
								info_uncheck_account_ID=new String[notcheck_count];
								info_uncheck_attend_tag=new String[notcheck_count];
								info_uncheck_attend_time=new String[notcheck_count];
								int j=0;
								for(int i = 0 ; i<jsonArray.length() ; i++)
								{
									if(info_attend_tag[i].equals("0"))
									{
										info_uncheck_account_ID[j]=info_account_ID[i];
										info_uncheck_attend_tag[j]=info_attend_tag[i];
										info_uncheck_attend_time[j]=info_attend_time[i];
										j++;
									}
//									Log.d("=====>", "GroupFragment 找到的Json = "+course_member[i]);
								}
								
								try {
									
								if(isRefreshThread)
								mhandler.post(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
//										Toast.makeText(getActivity(), "測試是"+info_account_ID[1]+info_attend_tag[1],Toast.LENGTH_LONG).show();
										initGridView();
									}

									
								});} catch (Exception e) {
									// TODO: handle exception
								}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
					else
						Log.d("<==TeacherAttendInfo==>", "抓取點名INFO清單 失敗..."+result2);
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}
	};
	private void initGridView() {
		// TODO Auto-generated method stub
		pgb.setVisibility(View.GONE);
		cb.setVisibility(View.VISIBLE);
		gv_title.setNumColumns(3);
		gv_title.setAdapter(new AttendGridListTitleAdapter(getActivity(),3));
		gv_info.setNumColumns(3);
		if(!cb.isChecked())
		{
			gv_info.setAdapter(new AttendGridInfoAdapter(getActivity(),3,info_account_ID,info_attend_tag,info_attend_time));
			gv_info.setVisibility(View.VISIBLE);
		}
		else
		{	gv_info.setAdapter(new AttendGridInfoAdapter(getActivity(),3,info_uncheck_account_ID,info_uncheck_attend_tag,info_uncheck_attend_time));
			gv_info.setVisibility(View.VISIBLE);
//			Toast.makeText(getActivity(), "測試未簽到人數 = "+notcheck_count,Toast.LENGTH_LONG).show();
		}
		gv_info.setOnItemClickListener(gv_info_clicklistener);
		
	}
	private OnItemClickListener gv_info_clicklistener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			TextView tv=(TextView)view;
			final String click_acc=tv.getText().toString();
			final int myposition=position;
			if(position%3==0)
			{
				final int click_row_count=position/3;
				dialog = new AlertDialog.Builder(getActivity());
				Toast.makeText(getActivity(), "你成功點擊物件 : "+click_acc, Toast.LENGTH_LONG).show();
				dialog.setTitle("點名修改");
				dialog.setMessage("切換"+click_acc+"的出席狀況");
				dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
		            public void onClick(DialogInterface dialog, int which) {  
		                // 按下PositiveButton要做的事  
//		                Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
		            }  
		        }); 
				dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() {  
		            public void onClick(DialogInterface dialog, int which) {  
		                // 按下PositiveButton要做的事
		            	DataFromDatabase db=new DataFromDatabase();
		            	String table_name="attend_"+now_course_ID+"_"+now_attend_index;
		               if(cb.isChecked())
		               {
		            	   db.FC_updateTotrueForAttendInfo(table_name, click_acc);
		            	   gv_info.getChildAt(myposition+1).setBackgroundColor(Color.WHITE);
		            	   TextView mtv=(TextView)gv_info.getChildAt(myposition+1);
		            	   mtv.setText("出席");
		            	   mtv=(TextView)gv_info.getChildAt(myposition+2);
		            	   Calendar cl=Calendar.getInstance();
   		           		   String nowtime=String.valueOf(cl.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(cl.get(Calendar.MINUTE))+":"+String.valueOf(cl.get(Calendar.SECOND));
   		           		   mtv.setText(nowtime);
		               }
		               else if(info_attend_tag[click_row_count].contains("0"))
		               {
		            	   db.FC_updateTotrueForAttendInfo(table_name, click_acc);
		            	   gv_info.getChildAt(myposition+1).setBackgroundColor(Color.WHITE);
		            	   TextView mtv=(TextView)gv_info.getChildAt(myposition+1);
		            	   mtv.setText("出席");
		            	   mtv=(TextView)gv_info.getChildAt(myposition+2);
		            	   Calendar cl=Calendar.getInstance();
   		           		   String nowtime=String.valueOf(cl.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(cl.get(Calendar.MINUTE))+":"+String.valueOf(cl.get(Calendar.SECOND));
   		           		   mtv.setText(nowtime);
		               }
		               else
		               {
		            	   db.FC_updateTofalseAttendInfo(table_name, click_acc);
		            	   gv_info.getChildAt(myposition+1).setBackgroundColor(Color.parseColor("#FFA488"));
		            	   TextView mtv=(TextView)gv_info.getChildAt(myposition+1);
		            	   mtv.setText("未簽到");
		            	   mtv=(TextView)gv_info.getChildAt(myposition+2);
		            	   mtv.setText("00:00:00");
		               }
		            		   
		            }
		        }); 
				
				
				dialog.show();
				
				
			}
		}
	};
	private Runnable setSpinnerRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ArrayAdapter<String> ad = new ArrayAdapter<String>
	           (getActivity(), android.R.layout.simple_spinner_item, attendlist_date);
			spinner.setAdapter(ad);
			Log.d("<==TeacherAttendInfo==>", "setAdapter Done!");
		}
	};
	public Boolean isLoading(){
		if(pgb.getVisibility()==View.VISIBLE)
			return true;
		else
			return false;
	}
	public void endAllThread(){
		
		 mhandler.removeCallbacks(downloadAttenList_runnable);
		 isRefreshThread=false;
		 Log.d("<==AttendTeacherAttendinfo==>", "setSpinnerThread  DIE");
		 mhandler.removeCallbacks(downloadAttendinfo_runnable);
		 isRefreshThread=false;
		 Log.d("<==AttendTeacherAttendinfo==>", "setGridViewThread  DIE");
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.endAllThread();
	}
	
	
}
