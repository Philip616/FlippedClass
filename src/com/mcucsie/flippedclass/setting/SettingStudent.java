package com.mcucsie.flippedclass.setting;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import android.widget.TextView;

import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

public class SettingStudent extends Fragment {

	String  course_ID,course_name,account_Type,account_ID;
	
	
	GetNowCourseInfo course_info;
	GetNowAccountInfo account_info;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_setting_std, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
			
		course_info = new GetNowCourseInfo(getActivity());
		account_info = new GetNowAccountInfo(getActivity());
		
		course_ID = course_info.getNowCourseID();

		
		initView();

	}
	
	private void initView(){
		TextView start_time,end_time;
		String classinfo;
		String[] data; 
		
		start_time = (TextView)getView().findViewById(R.id.start_time);
		end_time = (TextView)getView().findViewById(R.id.end_time);
		
		
		classinfo = course_info.getSettingCourseInfo(course_ID);
		data = classinfo.split(":");
		
		start_time.setText(data[1]);
		end_time.setText(data[2]);
	}
}
