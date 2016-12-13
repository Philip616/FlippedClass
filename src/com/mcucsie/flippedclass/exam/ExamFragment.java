package com.mcucsie.flippedclass.exam;

import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ExamFragment extends Fragment{
	
	TextView txt_examtile;	

	String  course_ID,course_name,account_Type,account_ID;
	
	GetNowCourseInfo course_info;
	GetNowAccountInfo account_info;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_main, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		txt_examtile = (TextView)getView().findViewById(R.id.txt_examtitle);
		
		course_info = new GetNowCourseInfo(getActivity());
		account_info = new GetNowAccountInfo(getActivity());
		
		course_ID = course_info.getNowCourseID();
		course_name = course_info.getNowCourseName();
		
		account_Type = account_info.getNowAccountType();
		account_ID = account_info.getNowAccountID();
		
		if(course_ID.equals("null"))
			txt_examtile.setText("請選擇課程");
		
		else
		{
			txt_examtile.setText(course_ID + "  " + course_name);
		}
	}
	
	public void initExamRootView()
	{
		course_ID = course_info.getNowCourseID();
		course_name = course_info.getNowCourseName();
		
		if(course_ID.equals("null"))
			txt_examtile.setText("請選擇課程");
		
		else
		{
			txt_examtile.setText(course_ID + "  " +course_name);
		}
	}
	
	
	
}
