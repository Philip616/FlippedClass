package com.mcucsie.flippedclass.exam;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;


public class ExamIRSMainStudentFragment extends Fragment{

	RadioGroup rg_irs_ans;
	Button btn_sent;
	
	String ans = "Z";
	
	GetNowCourseInfo course_info;
	String course_ID;
	
	GetNowAccountInfo account_info;
	String account_ID;
	
	WaitForCheck WaitForCheck;
	setViewForCheckIRSOpenRunnable setViewForCheckIRSOpenRunnable;
	static Handler mHandler_checkIRSopen;
	
	boolean setViewForCheckIRSOpenRunnable_isRunning = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_irs_std, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		rg_irs_ans = (RadioGroup)getView().findViewById(R.id.radiogroup_irs_ans);
		btn_sent = (Button)getView().findViewById(R.id.btn_irs_sent);
		
		rg_irs_ans.setOnCheckedChangeListener(rg_irs_ans_click);
		
		btn_sent.setOnClickListener(btn_sent_click);
		
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
		
		account_info = new GetNowAccountInfo(getActivity());
		account_ID = account_info.getNowAccountID();
		
		mHandler_checkIRSopen = new Handler(); 
	}
	
	private RadioGroup.OnCheckedChangeListener rg_irs_ans_click = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			
			switch(checkedId)
			{
				case R.id.rb_irs_chooseA:
					ans = "A";
					break;
				case R.id.rb_irs_chooseB:
					ans = "B";
					break;
				case R.id.rb_irs_chooseC:
					ans = "C";
					break;
				case R.id.rb_irs_chooseD:
					ans = "D";
					break;
			}
			
		}
		
	};
	
	private OnClickListener btn_sent_click= new OnClickListener(){
		
    	@Override
		public void onClick(View v) {
			
    		DataFromDatabase MySQL_DB = new DataFromDatabase();
    		MySQL_DB.FC_CheckIRSOpen(course_ID);
    		
    		setViewForCheckIRSOpenRunnable_isRunning = true;
    		
    		WaitForCheck = new WaitForCheck(MySQL_DB);
    		Thread waitforcheck = new Thread(WaitForCheck);
    		waitforcheck.start();
		}
	};
	
	class WaitForCheck implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		String allmembercount;
		
		public WaitForCheck(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					Thread.sleep(500);
					
					String result = MySQL_DB.return_result;
					
					while(result==null && times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "ExamQuestionFragment Waitfodata 多等了1秒");
						result = MySQL_DB.return_result;
						times++;
					}
					
					setViewForCheckIRSOpenRunnable = new setViewForCheckIRSOpenRunnable(result);
					
					mHandler_checkIRSopen.post(setViewForCheckIRSOpenRunnable);
					
			} 	

	
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	class setViewForCheckIRSOpenRunnable implements Runnable
	{
		String result;
		int times = 0;
		
		public setViewForCheckIRSOpenRunnable(String result)
		{
			this.result = result;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			if(setViewForCheckIRSOpenRunnable_isRunning)
			{
				if(result.equals("1") && !ans.equals("Z"))
				{	            	
	    			DataFromDatabase MySQL_DB = new DataFromDatabase();
	    	        MySQL_DB.FC_UpdateIRSAns(account_ID,course_ID,ans);
	    	        Toast.makeText(getActivity(), "發送答案：" + ans, Toast.LENGTH_LONG).show();
				}
				
				if(result.equals("1") && ans.equals("Z"))
				{	            	
					Toast.makeText(getActivity(), "請選擇答案", Toast.LENGTH_LONG).show();
				}
				
				if(result.equals("0"))
				{
					Toast.makeText(getActivity(), "無開啟中的IRS，無法上傳成績", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
