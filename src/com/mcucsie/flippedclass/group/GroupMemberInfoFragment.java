package com.mcucsie.flippedclass.group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class GroupMemberInfoFragment extends Fragment {
	private String[] groupmember_data;
	private String Account;
	private String Course_ID;
	private Spinner spinner;
	private GetNowAccountInfo gnai;
	private GetNowCourseInfo gnci;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_group_groupmemberinfo, container, false);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		gnai=new GetNowAccountInfo(getActivity());
		gnci=new GetNowCourseInfo(getActivity());
		Account=gnai.getNowAccountID();
		Course_ID=gnci.getNowCourseID();
		spinner=(Spinner)getView().findViewById(R.id.spinner_group_memberinfo);
				
					getGroupmember_data();
					setSpinner();
	
	}
	
	

	@SuppressWarnings("resource")
	public void getGroupmember_data() {
		// TODO Auto-generated method stub
		int time=0;
		
		SQLiteDatabase db=getActivity().openOrCreateDatabase("FlippedClass_DataBase", getActivity().MODE_PRIVATE, null);
		Cursor cursor=db.rawQuery("SELECT groupnum FROM GroupMember_"+Course_ID + " WHERE member='"+Account+"'", null);
			
			try{
				while(time<=10 && cursor.getCount()==0){
					Thread.sleep(1000);
					db=getActivity().openOrCreateDatabase("FlippedClass_DataBase", getActivity().MODE_PRIVATE, null);
					cursor=db.rawQuery("SELECT groupnum FROM GroupMember_"+Course_ID + " WHERE member='"+Account+"'", null);
					time++;
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
		cursor.moveToFirst();
		Log.d("Count","result="+cursor.getCount());
		String groupnum=cursor.getString(0);
		
		cursor=db.rawQuery("SELECT member FROM GroupMember_"+Course_ID+" WHERE groupnum='"+groupnum+"'", null);
		int membercount=cursor.getCount();
        Log.d("<==GroupMemberInfoFragment==>", "取得此人的分組組別人數="+membercount);
		groupmember_data=new String[membercount];
		cursor.moveToFirst();
		for(int i=0;i<membercount;i++){
			groupmember_data[i]=cursor.getString(0);
			if(i<membercount-1)
				cursor.moveToNext();
		}
	}

	private void setSpinner() {
		// TODO Auto-generated method stub
		ArrayAdapter<String> ad = new ArrayAdapter<String>
        (getActivity(), android.R.layout.simple_spinner_item, groupmember_data);
		spinner.setAdapter(ad);
		spinner.setOnItemSelectedListener(spinner_listener);
	}
	
	private OnItemSelectedListener spinner_listener=new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			TextView tv=(TextView)view;
//			Log.d("<==GroupMemberInfoFragment==>", "spinner正選擇的人ID="+tv.getText());
			PersonInfoPopupWindow mpw=new PersonInfoPopupWindow(getActivity(), tv.getText().toString());
			mpw.showAtDownDrop(spinner);
		
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
  
}
