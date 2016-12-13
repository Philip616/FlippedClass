package com.mcucsie.flippedclass.quickpick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuickpickPictureCount extends Fragment {

	private String quicktext_tablename,course_ID;
	private String[][] list;
	private int count_a=0,lastSerial=0;
	private int[][] count_picture;
	private boolean TeacherWaitForQuickCount_isRunning = false;
	JSONObject jsonData;
	
	TextView tv_shock_count,tv_yes_count,tv_no_count,tv_question_count;
	Button btn_clear_piccount,btn_start_countpic;
	
	GetNowCourseInfo gnci;
	Handler mHandler;
	Bundle bundle;
	
	getdata getdata;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_quickpick_picturecount,container,false);
	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		bundle = getArguments();
		lastSerial = bundle.getInt("lastSerial");
		
		//Log.d("lastSerial","result="+lastSerial);
		
		
		gnci = new GetNowCourseInfo(getActivity());
		course_ID = gnci.getNowCourseID();
		
		tv_shock_count = (TextView)getView().findViewById(R.id.tv_shock_count);
		tv_yes_count = (TextView)getView().findViewById(R.id.tv_yes_count);
		tv_no_count = (TextView)getView().findViewById(R.id.tv_no_count);
		tv_question_count = (TextView)getView().findViewById(R.id.tv_question_count);
		btn_clear_piccount = (Button)getView().findViewById(R.id.btn_close_piccount);
		btn_start_countpic = (Button)getView().findViewById(R.id.btn_start_countpic);
		
		
		btn_clear_piccount.setOnClickListener(click_btn_close_piccount);
		btn_start_countpic.setOnClickListener(click_btn_start_countpic);
		
		mHandler = new Handler();
		
		
	}
	
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		endAllThread();
	}
	
	OnClickListener click_btn_start_countpic = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			quicktext_tablename = "quicktext_" + course_ID;
			DataFromDatabase MySQL_DB = new DataFromDatabase();
			MySQL_DB.Quickpick_getCourseListT(quicktext_tablename,String.valueOf(lastSerial));
			
			TeacherWaitForQuickCount_isRunning = true;
			
			getdata = new getdata(MySQL_DB);
			Thread waitfordata = new Thread(getdata);
			waitfordata.start();
			
			Toast.makeText(getActivity(), "統計開啟!", Toast.LENGTH_SHORT).show();
			
		}
		
	};



	class getdata implements Runnable{
		DataFromDatabase MySQL_DB;
		
		
		
		
		public getdata(DataFromDatabase MySQL_DB){
			this.MySQL_DB = MySQL_DB;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		try{
			int time=0;
			Thread.sleep(500);

			while(TeacherWaitForQuickCount_isRunning){
				
				String result = MySQL_DB.return_result;
				
			while(TeacherWaitForQuickCount_isRunning && result == null && time<=10){
				
				Thread.sleep(1000);
				Log.d("=====>", "ThreadDownload的多休息了1秒");
				result = MySQL_DB.return_result;
				time++;
			}
			
			
				Log.d("=====>", "成功抓取ID的課程" + result);
				
				Thread.sleep(500);
				
				result = result.trim();
				JSONArray jsonArray;
				
			if(!result.equals("null")){
				try {
					Log.d("error", "result=" + result);
					
					jsonArray = new JSONArray(result);
					list = new String[jsonArray.length()][2];
					
					count_picture = new int[4][1];
					
					for(int i=0;i<4;i++)
					count_picture[i][0] = 0;
					
						for (int i = 0; i < jsonArray.length(); i++) {
							
							jsonData = jsonArray.getJSONObject(i);
							
							list[i][0] = jsonData.getString("msg");
							list[i][1] = jsonData.getString("serial");
							
							if(list[i][0].equals("#000")) 
								count_picture[0][0]++;
						
							if(list[i][0].equals("#001"))
								count_picture[1][0]++;
				
								
							if(list[i][0].equals("#002"))
								count_picture[2][0]++;
					
								
							if(list[i][0].equals("#003")) 
								count_picture[3][0]++;
					


							//Log.d("=====>", "Quickpick_T 找到的Json = "+ list[i][0]);

						}
						
						count_a = jsonArray.length();
						mHandler.post(setViewToNormalRunnable);
						
					} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
				}
	
						if(TeacherWaitForQuickCount_isRunning){
							
							Thread.sleep(3000);
							Log.d("lastSerial","result="+lastSerial);
							
							quicktext_tablename = "quicktext_" + course_ID;
							MySQL_DB.Quickpick_getCourseListT(quicktext_tablename,String.valueOf(lastSerial));
						}
					
				
			
				
			}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		

	}
	
	protected static final int REFRESH_DATA = 0x00000001;
	
	private Runnable setViewToNormalRunnable= new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

							tv_shock_count.setText(String.valueOf(count_picture[0][0]));
							tv_question_count.setText(String.valueOf(count_picture[1][0]));
							tv_yes_count.setText(String.valueOf(count_picture[2][0]));
							tv_no_count.setText(String.valueOf(count_picture[3][0]));				
		}
	
	};
	
	OnClickListener click_btn_close_piccount = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(count_a>0){
			lastSerial = Integer.valueOf(list[count_a-1][1]);
			
			for(int i=0;i<4;i++)
				count_picture[i][0] = 0;
			
			tv_question_count.setText("0");
			tv_shock_count.setText("0");
			tv_yes_count.setText("0");
			tv_no_count.setText("0");
			
			endAllThread();

			Toast.makeText(getActivity(), "統計關閉!", Toast.LENGTH_SHORT).show();
			}
			
			
			
			
			
		}

		
		
	};
	
	public void endAllThread(){
		mHandler.removeCallbacks(getdata);
		TeacherWaitForQuickCount_isRunning = false;
	}
	
}
