package com.mcucsie.flippedclass.exam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ExamIRSMainTeacherFragment extends Fragment{

	Button btn_irs_start,btn_irs_end,btn_irs_detail;
	
	GetNowCourseInfo course_info;
	String course_ID;
	
	ExamIRSActivityShowFragment frag_activityshow;
	ExamIRSStudentAnswer frag_irsans;
	
	int IRS_now_page = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_irs_tch, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		btn_irs_start = (Button)getView().findViewById(R.id.btn_irs_start);
		btn_irs_end = (Button)getView().findViewById(R.id.btn_irs_end);
		btn_irs_detail = (Button)getView().findViewById(R.id.btn_irs_detail);
		
		btn_irs_start.setOnClickListener(btn_irs_start_click);
		btn_irs_end.setOnClickListener(btn_irs_end_click);
		btn_irs_detail.setOnClickListener(btn_irs_detail_click);
		
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
		
		frag_activityshow = new ExamIRSActivityShowFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_exam_irs_content, frag_activityshow).commit();
		
		IRS_now_page = 1;
	}
	
	private OnClickListener btn_irs_start_click= new OnClickListener(){
		
    	@Override
		public void onClick(View v) {
			
    		DataFromDatabase MySQL_DB = new DataFromDatabase();
    		MySQL_DB.FC_IRSSetState(course_ID,"1","1");
    		
    		DataFromDatabase GCM_DB = new DataFromDatabase();
			GCM_DB.GCM_sendMessageToCourseMember(course_ID, "測驗："+ course_ID + "的IRS開放中");
    		
    		DataFromDatabase MySQL_DB_Reset = new DataFromDatabase();
    		MySQL_DB_Reset.queryForCourseMemberList("course_memberlist_" + course_ID);

    		Thread wiatfordata = new Thread(new TeacherWaitForResetIRS(MySQL_DB_Reset));
    		wiatfordata.start();
    		
    		Toast.makeText(getActivity(), "IRS開放中", Toast.LENGTH_SHORT).show();
		}
	};
	
	private OnClickListener btn_irs_end_click= new OnClickListener(){
		
    	@Override
		public void onClick(View v) {
			
    		DataFromDatabase MySQL_DB = new DataFromDatabase();
    		MySQL_DB.FC_IRSSetState(course_ID,"1","0");
    		
    		DataFromDatabase MySQL_DB_Reset = new DataFromDatabase();
    		MySQL_DB_Reset.queryForCourseMemberList("course_memberlist_" + course_ID);
    		
    		Thread wiatfordata = new Thread(new TeacherWaitForResetIRS(MySQL_DB_Reset));
    		wiatfordata.start();
    		
    		Toast.makeText(getActivity(), "IRS關閉", Toast.LENGTH_SHORT).show();
		}
	};
	
	private OnClickListener btn_irs_detail_click= new OnClickListener(){
		
    	@Override
		public void onClick(View v) {
			
    		Log.d("=====>", "IRS Teacher Now Page = " + IRS_now_page);
    		
    		if(IRS_now_page == 1)
    		{
    			Log.d("=====>", "Get IN IRS Teacher Now Page = 1");
    			
    			btn_irs_detail.setText("即時答題情況");
    			
    			endAllThread();
    			
    			frag_irsans = new ExamIRSStudentAnswer();
    			getFragmentManager().beginTransaction().replace(R.id.frame_exam_irs_content, frag_irsans).commit();
    			
    			IRS_now_page = 2;
    		}
    		
    		else if(IRS_now_page == 2)
    		{
    			Log.d("=====>", "Get IN IRS Teacher Now Page = " + IRS_now_page);
    			
    			btn_irs_detail.setText("詳細答題情況");
    			
    			endAllThread();
    			
    			frag_activityshow = new ExamIRSActivityShowFragment();
    			getFragmentManager().beginTransaction().replace(R.id.frame_exam_irs_content, frag_activityshow).commit();
    			
    			IRS_now_page = 1;
    		}
		}
	};
	
	class TeacherWaitForResetIRS implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		
		public TeacherWaitForResetIRS(DataFromDatabase MySQL_DB)
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
						Log.d("=====>", "ExamFragment Waitfodata 多等了1秒");
						result = MySQL_DB.return_result;
						times++;
					}
					try {
							JSONArray jsonArray;
						
							jsonArray = new JSONArray(result);
							
							String[] account_ID = new String[jsonArray.length()];
							
							for(int i = 0 ; i<jsonArray.length() ; i++)
							{
								JSONObject jsonData = jsonArray.getJSONObject(i);
								
								account_ID[i] = jsonData.getString("account_ID");
								
				    	        MySQL_DB.FC_UpdateIRSAns(account_ID[i],course_ID,"Z");							
							}
								
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} 	

	
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public void endAllThread(){
		
		if(IRS_now_page == 1)
		{
			frag_activityshow.endAllThread();
		}
		if(IRS_now_page == 2)
		{
			frag_irsans.endAllThread();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		endAllThread();
		
	}
	
	
	
}
