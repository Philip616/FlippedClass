package com.mcucsie.flippedclass.remind;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.MainActivity;
import com.mcucsie.flippedclass.R;

public class Remind_courseInfo {
	String course_ID;	
	String[][] courselist = new String[1][5];
	String[] courseInfo = new String[1];
	int count=0;
	Context context;
	
	public Remind_courseInfo(Context context){
		this.context = context;
		
		GetNowCourseInfo course_info = new GetNowCourseInfo(context);
		course_ID = course_info.getNowCourseID();
		GetNowAccountInfo gnai = new GetNowAccountInfo(context);
		String Type = gnai.getNowAccountType();
		
		if(course_ID.equals("null")){
			
		}	
		else{
			DataFromDatabase MySQL_DB = new DataFromDatabase();
			MySQL_DB.Remind_getCourseInfo(course_ID);
			Thread waitfordata = new Thread(new getdata(MySQL_DB));
			waitfordata.start();
		}
		Log.d("=====================>", "okokokokookok");	
	}
	
	class getdata implements Runnable
	{

    	private DataFromDatabase MySQL_DB;
		// 建構子，設定要傳的字串
		public getdata(DataFromDatabase MySQL_DB_1)
		{
			this.MySQL_DB=MySQL_DB_1;
		}

		@Override
		public void run()
		{

			Log.d("=====>", "Runable Download成員名單的課程ID="+course_ID);
			// TODO Auto-generated method stub
			String result=null;
			int times=0;
			try {
					//Thread.sleep(1000);
					result=MySQL_DB.return_result;
					while(times<=100&&result==null){
					   Thread.sleep(100);
					   result=MySQL_DB.return_result;
					   Log.d("=====>", "ThreadDownload的多休息了1秒");	
					   times++;
					}
					if(times<=10){
						Log.d("=====>", "成功抓取ID的課程列表"+result);
						JSONArray jsonArray;
						try {
								jsonArray = new JSONArray(result);							
								courselist = new String[jsonArray.length()][5];
								for(int i = 0 ; i<jsonArray.length() ; i++)
								{
									JSONObject jsonData = jsonArray.getJSONObject(i);
									courselist[i][0] = jsonData.getString("course_ID");
									courselist[i][1] = jsonData.getString("title");
									courselist[i][2] = jsonData.getString("data");
									courselist[i][3] = jsonData.getString("resources");
									courselist[i][4] = jsonData.getString("note");
									Log.d("=====>", "RemindFragment 找到的Json = "+courselist[i][1]);
								
								}
								count = jsonArray.length();
								Log.d("=====>", Integer.toString(count));	
								mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						Log.d("=====>", "ThreadDownload下載太多次，下載失敗..."+times);
					}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	protected static final int REFRESH_DATA = 0x00000001;
	
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// 顯示網路上抓取的資料
				case REFRESH_DATA:
					courseInfo = new String[count];				
					if(count>0){			
						for(int i=0;i<count;i++)
						{
							courseInfo[i] = courselist[i][1];
						}		
					}
					else{
						courseInfo[0]="暫無相關資料";
					}

						//Toast.makeText(context, "2"+courseInfo[0], Toast.LENGTH_LONG).show();
					//getCourseInfo();
				break;
			}
		}
	};
	
	public String[] getCourseInfo(){		
		return courseInfo;
	}
	
	public String[][] getCourselist(){
		return courselist; 
	} 
}
