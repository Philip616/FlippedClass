package com.mcucsie.flippedclass.setting;




import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingMainFragment extends Fragment {

	
	TextView txt_settingtitle;	

	String  course_ID,course_name,account_Type,account_ID,Type;
	Bundle bundle;

	
	GetNowCourseInfo course_info;
	GetNowAccountInfo gnai;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_setting_main, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		gnai = new GetNowAccountInfo(getActivity());
			
		Type = gnai.getNowAccountType();
		
		txt_settingtitle = (TextView)getView().findViewById(R.id.txt_settingtitle);

		course_info = new GetNowCourseInfo(getActivity());
		
		initSettingRootView();
		
	}
	
	public void initSettingRootView()
	{	
		course_ID = course_info.getNowCourseID();
		course_name = course_info.getNowCourseName();
		
		if(course_ID.equals("null"))
			txt_settingtitle.setText("½Ð¿ï¾Ü½Òµ{");
		
		else
		{
			txt_settingtitle.setText(course_ID + "  " + course_name);
			if(Type.equals("teacher")){
				SettingTeacher frag_set_tch = new SettingTeacher();
				getFragmentManager().beginTransaction().replace(R.id.frame_setting_content, frag_set_tch).commit();
			}
			
			else{
				SettingStudent frag_set_std = new SettingStudent();
				getFragmentManager().beginTransaction().replace(R.id.frame_setting_content, frag_set_std).commit();
			}
		}
	}

}
