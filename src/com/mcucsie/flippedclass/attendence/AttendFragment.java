package com.mcucsie.flippedclass.attendence;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AttendFragment extends Fragment{

	private String Account;
	private Bundle bundle;
	private Button btn_attend,btn_2; 
	private TextView tv_attend_root;
	private String tag="";
	private Handler handler=new Handler();
	private DataFromDatabase ddd;
	private GetNowCourseInfo gnci;
	public AttendFragment() {
		// TODO Auto-generated constructor stub
	}
	public AttendFragment(String tag) {
		// TODO Auto-generated constructor stub
		this.tag=tag;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		gnci=new GetNowCourseInfo(getActivity());
		
		bundle = getActivity().getIntent().getExtras();	
		Account = bundle.getString("Account");
		
		tv_attend_root=(TextView)getView().findViewById(R.id.tv_attend);
		
		btn_attend=(Button)getView().findViewById(R.id.btn_attendance);
		btn_attend.setOnClickListener(btn_attend_click);
		
		btn_2=(Button)getView().findViewById(R.id.btn_attend_2);
		btn_2.setOnClickListener(btn_attend_2_click);
		
		
		btn_attend.setVisibility(View.GONE);
		btn_2.setVisibility(View.GONE);
		
		initAttendRootTextView();
		
		if(tag.equals("attend_now"))
		{
			Thread_Attend_now thread=new Thread_Attend_now();
			thread.start();
		}
	}
	
	private OnClickListener btn_attend_click=new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
				Toast.makeText(getActivity(), "click test", Toast.LENGTH_LONG).show();
				ddd=new DataFromDatabase();
				ddd.GCM_sendMessageToALL("HI every one!!! This Message Send To ALL From " + Account);
				new Thread(sendMessageToALLRunable).start();
				
				
		}
	};
	private OnClickListener btn_attend_2_click=new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
				Toast.makeText(getActivity(), "click 2 test", Toast.LENGTH_LONG).show();
				ddd=new DataFromDatabase();
				ddd.GCM_sendMessageToALL("TAG1 : 這是標籤1(點名視窗跳轉)測試 來自" + Account);
				new Thread(sendMessageToALLRunable).start();
				
				
		}
	};
 
	Runnable sendMessageToALLRunable=new Runnable() {
		int times=0;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(500);
				String result = ddd.return_result;
				while(result==null && times<=10)
				{
					Thread.sleep(1000);
					result = ddd.return_result;
					times++;
				}
				Log.d("<==AttendenceFragment==>", "PHP測試result="+result);
				getActivity().finish();
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
					}
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	};
	
	@Override  
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
	    return inflater.inflate(R.layout.fragment_attend, container, false);  
	 }
	
	public void initAttendRootTextView() {
		// TODO Auto-generated method stub
		
		if(gnci.getNowCourseID().equals("null"))
			tv_attend_root.setText("請選擇課程");
		
		else
		tv_attend_root.setText(gnci.getNowCourseID()+"  "+gnci.getNowCourseName());
		
	}
	
	private class Thread_Attend_now extends Thread{
		 public void run() {
	           // TODO Auto-generated method stub
	           super.run();
	           try {
	        	    Thread.sleep(1500);
	        	    Log.d("=====>", "點名自動啟動執行緒，已經等待1.5秒");
	   				handler.post(new Runnable() {
						@Override
						public void run() {
							AttendPopupWindow attendpopwindow=new AttendPopupWindow(R.layout.popupwindow_attendpop_layout, getActivity().getApplicationContext(), bundle.getString("Account"));
					    	attendpopwindow.showAtCenter(getView());
						}
					});
	               
	           } catch (Exception e) {
	                e.printStackTrace();
	           }
	       }
		
		
	}
}
