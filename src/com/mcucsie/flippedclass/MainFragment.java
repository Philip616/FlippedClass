package com.mcucsie.flippedclass;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_mainlayout, container,false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		BulletinBoardMain board = new BulletinBoardMain();
		getFragmentManager().beginTransaction().replace(R.id.frame_bulletinboard_content, board).commit();
		
		
		
	}
	
	public void initTextView(){
    	/*SQLiteDatabase db=getActivity().openOrCreateDatabase("FlippedClass_Temporal", getActivity().MODE_PRIVATE, null);
    	Cursor cursor=db.rawQuery("Select * FROM account", null);
    	cursor.moveToFirst();
    	String Account=cursor.getString(0);
    	String Type=gnai.getNowAccountType();
    	
    	
    	String now_course="null";
    	String now_course_ID="null";
    
    	now_course=gnci.getNowCourseName();
		now_course_ID=gnci.getNowCourseID();
    	
    	
		user_tv.setText("�ϥΪ� "+Account+" �z�n!\n"+"�{�b���Ҧ��� "+Type+"\n�{�b�ҿ諸�ҵ{�� : "+now_course+"\n�{�b�ҿ諸�ҵ{�N�X�� : "+now_course_ID);
    	
    	db.close();*/
		BulletinBoardMain board = new BulletinBoardMain();
		getFragmentManager().beginTransaction().replace(R.id.frame_bulletinboard_content, board).commit();


    }
	
		}
