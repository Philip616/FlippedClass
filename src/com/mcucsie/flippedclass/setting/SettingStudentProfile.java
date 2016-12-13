package com.mcucsie.flippedclass.setting;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.R;
import com.mcucsie.flippedclass.group.GroupDiscussAdapter;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SettingStudentProfile extends Fragment{

	GetNowAccountInfo gnai;
	private String account_ID;
	private String[] profile_List,profile_title = {"name","gender","department","grade","mail_address","LINE_ID"};
	private ArrayList<HashMap<String, String>> List = null;
	private Handler mHandler;
	private ListView mListView;
	
	SettingStdProfileAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_setting_std_profile, container,false);
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		gnai = new GetNowAccountInfo(getActivity());
		account_ID = gnai.getNowAccountID();
		
		mHandler = new Handler();
		mListView = (ListView)getActivity().findViewById(R.id.std_profile);
		
		DataFromDatabase MySQL = new DataFromDatabase();
		MySQL.getStdProfile(account_ID);
		Thread waitData = new Thread(new getData(MySQL));
		waitData.start();
		
	}
	
	private class getData implements Runnable{
		DataFromDatabase MySQL;
		
		private getData(DataFromDatabase MySQL){
			this.MySQL = MySQL;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			try {
				String result=null;
				int times=0;
				
				result = MySQL.return_result;
				
				while(result==null && times<10){
					result = MySQL.return_result;
					Thread.sleep(500);
					times++;
				}
				
				if(times<=10){
					JSONArray jsonArray;
					result = result.trim();
					
					if(!result.equals("null")){
						List = new ArrayList<HashMap<String, String>>();
						jsonArray = new JSONArray(result);	
						profile_List = new String[6];
	
							
							JSONObject jsonData = jsonArray.getJSONObject(0);
							profile_List[0] = jsonData.getString("name");
							profile_List[1] = jsonData.getString("gender");
							profile_List[2] = jsonData.getString("department");
							profile_List[3] = jsonData.getString("grade");
							profile_List[4] = jsonData.getString("mail_address");
							profile_List[5] = jsonData.getString("LINE_ID");
							
							if(profile_List[1].equals("1"))
									profile_List[1] = "¨k";
								else
									profile_List[1] = "¤k";
							
							for (int i=0;i<6;i++){
							
							HashMap<String, String> item = new HashMap<String, String>();
							item.put(profile_title[i], profile_List[i]);
							List.add(item);
							}
						
						}
						
						mHandler.post(setViewToAdapter);
					
				}
				
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	private Runnable setViewToAdapter = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
		
			mAdapter = new SettingStdProfileAdapter(getActivity(), List);
			mListView.setAdapter(mAdapter);

		}
	};

	

}
