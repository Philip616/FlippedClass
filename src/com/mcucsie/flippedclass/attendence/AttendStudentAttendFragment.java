package com.mcucsie.flippedclass.attendence;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

public class AttendStudentAttendFragment extends Fragment {
	private TextView tv_center,tv;
	private Button btn_checkin;
	private ProgressBar pgb;
	private static Handler mhandler;
	private Thread downloadThread;
	private GetNowCourseInfo gnci;
	private GetNowAccountInfo gnai;
	private String[] attendlist_date;
	private String[] index;
	private String[] start_time;
	private String[] end_time;
	
	
	//new
	public AlertDialog.Builder dialog;
	private EditText input_security_code;
	//
	
	private Boolean isRefreshThread=true;
	private String now_course_ID="null";
	private String account_ID="";
	private String attend_time="";
	@SuppressWarnings("unused")
	private int attend_time_range=0;
	
	public String NowCode;
	static String now_Code;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		//LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_attend_dialog, null);
		return inflater.inflate(R.layout.fragment_attend_studentattend, container, false); 
	}
	//new
	public void Get_Now_code(String now_Code){
		//now_Code = NowCode;
		this.now_Code = now_Code;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mhandler=new Handler();
		gnci=new GetNowCourseInfo(getActivity());
		gnai=new GetNowAccountInfo(getActivity());
		now_course_ID=gnci.getNowCourseID();
		account_ID=gnai.getNowAccountID();
		//new
		
		
		AttendTeacherAttendFragment ataf = new AttendTeacherAttendFragment();
		NowCode = ataf.now_Code;
		
		
		
		
		//LayoutInflater inflater = LayoutInflater.from(AttendStudentAttendFragment.this);
	    //final View v = inflater.inflate(R.layout, null);
		//認證碼輸入
		//input_security_code =(EditText)getView().findViewById(R.id.ed_attent_dialog);
		
		  
		tv_center=(TextView)getView().findViewById(R.id.tv_attend_studentcheckin_center);
		tv=(TextView)getView().findViewById(R.id.tv_attend_ttt);
		btn_checkin=(Button)getView().findViewById(R.id.btn_attend_studentcheckin);
		pgb=(ProgressBar)getView().findViewById(R.id.pgb_attend_studentcheckin);
		btn_checkin.setOnClickListener(btn_checkin_click);
		
		 
		if(now_course_ID.equals("null"))
		{
			Toast.makeText(getActivity(), "你還沒選課",Toast.LENGTH_LONG).show();
			btn_checkin.setEnabled(false);
			pgb.setVisibility(View.GONE);
		}
		else 
		{
			tv_center.setText("");
			tv.setText("");
			downloadThread=new Thread(downloadAttenList_runnable);
			btn_checkin.setEnabled(false);
			downloadThread.start();
		}
	}
	
	public Dialog SecurityCodeDialog() {
		
		// TODO Auto-generated method stub
		//new
		dialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View dialog_ed =inflater.inflate(R.layout.fragment_attend_dialog, null);
		input_security_code =(EditText)dialog_ed.findViewById(R.id.ed_attent_dialog);
		dialog.setView(dialog_ed);
		dialog.setTitle("驗證碼輸入");
		dialog.setMessage("驗證碼:"+ now_Code);
		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
		
			public void onClick(DialogInterface dialog, int which) {  
				// 按下PositiveButton要做的事 
                Toast.makeText(getActivity(), "請盡快簽到", Toast.LENGTH_SHORT).show();
            }  
        }); 
		dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) {  
                // 按下PositiveButton要做的事  
            
            String input_code =input_security_code.getText().toString();
           if(input_code.equals("")){
            	Toast.makeText(getActivity(), "尚未輸入驗證碼", Toast.LENGTH_SHORT).show();
           }
           if(input_code.equals(now_Code)){
    		//"輸入的驗證碼為"+ input_security_code.getText().toString()
            Log.d("輸入的驗證碼為", input_code);
            String table_name="attend_"+now_course_ID+"_"+index[0];			
			DataFromDatabase db=new DataFromDatabase();
			account_ID=gnai.getNowAccountID();
			Log.d("TTTTT", "table="+table_name+"acc="+account_ID+"Time="+attend_time);
			db.FC_updateForAttendInfo(table_name, account_ID, attend_time);
			db.GCM_sendMessageToCourseTeacher(now_course_ID, account_ID+"簽到了");
			 Toast.makeText(getActivity(), "驗證成功", Toast.LENGTH_SHORT).show();
			 Toast.makeText(getActivity(), "已簽到!!!", Toast.LENGTH_LONG).show();
				tv.setText("成功簽到!!!\n簽入的時間 : "+attend_time);
				tv.setTextColor(Color.BLUE);
          }
           else{
        	   Toast.makeText(getActivity(), "驗證碼輸入錯誤", Toast.LENGTH_SHORT).show();
           }
            
            	
				
			}
            
        }); 
		return dialog.create();
	}		
	
	//按下簽到鍵後
	private OnClickListener btn_checkin_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(isOnLastAttendTime())
			{
				SecurityCodeDialog();
				dialog.show();
				/*String table_name="attend_"+now_course_ID+"_"+index[0];			
				DataFromDatabase db=new DataFromDatabase();
				account_ID=gnai.getNowAccountID();
				Log.d("TTTTT", "table="+table_name+"acc="+account_ID+"Time="+attend_time);
				db.FC_updateForAttendInfo(table_name, account_ID, attend_time);
				db.GCM_sendMessageToCourseTeacher(now_course_ID, account_ID+"簽到了");*/
				
				/*Toast.makeText(getActivity(), "已簽到!!!", Toast.LENGTH_LONG).show();
				tv.setText("成功簽到!!!\n簽入的時間 : "+attend_time);
				tv.setTextColor(Color.BLUE);*/
			}
			else
			{
				Toast.makeText(getActivity(), "已經超出時間囉...", Toast.LENGTH_LONG).show();
			}
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
								    mhandler.post(setViewRunnable);
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
	private Runnable setViewRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(isOnLastAttendTime())
			{
				tv_center.setText("最近一次的點名日期 : "+attendlist_date[0]+"\n開始時間 : "+start_time[0]+"\n結束時間 : "+end_time[0]);
				tv.setText("請盡快按下簽到鍵!!!");
				tv.setTextColor(Color.BLUE);
				btn_checkin.setEnabled(true);
				
			}
			else
			{
				tv_center.setText("最近一次的點名日期 : "+attendlist_date[0]+"\n開始時間 : "+start_time[0]+"\n結束時間 : "+end_time[0]);
				tv.setText("最近的點名已經結束...");
				tv.setTextColor(Color.RED);
			}
			pgb.setVisibility(View.GONE);
			
		}
	};
	
	private Boolean isOnLastAttendTime()
	{
//		Calendar cl=Calendar.getInstance();
		String[] start_time_arrray=start_time[0].split(":");
		String[] end_time_arrray=end_time[0].split(":");
//		Log.d("=====>", "isOnLastAttendTime的ARRAY = "+start_time_arrray[0]+"="+start_time_arrray[1]);
		int start_hours=Integer.valueOf(start_time_arrray[0]);
		int start_min=Integer.valueOf(start_time_arrray[1]);
		int start_sec=Integer.valueOf(start_time_arrray[2]);
		int starttimecount=start_hours*3600+start_min*60+start_sec;
		
		int end_hours=Integer.valueOf(end_time_arrray[0]);
		int end_min=Integer.valueOf(end_time_arrray[1]);
		int end_sec=Integer.valueOf(end_time_arrray[2]);
		int endtimecount=end_hours*3600+end_min*60+end_sec;
		Calendar cl=Calendar.getInstance();
		int now_hours=cl.get(Calendar.HOUR_OF_DAY);
		int now_min=cl.get(Calendar.MINUTE);
		int now_sec=cl.get(Calendar.SECOND);
		int nowtimecount=now_hours*3600+now_min*60+now_sec;
		
		attend_time_range=endtimecount-starttimecount;
		attend_time=now_hours+":"+now_min+":"+now_sec;
			
		
		if((nowtimecount<=endtimecount)&&(nowtimecount>=starttimecount))
			return true;
		else
			return false;
	}
	public void endAllThread(){
		 mhandler.removeCallbacks(setViewRunnable);
		 isRefreshThread=false;
		 Log.d("<==AttendStudentAttendFragment==>", "DownloadThread  DIE");
	}
}
