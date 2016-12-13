package com.mcucsie.flippedclass.remind;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.R;

public class Remind_titleInfo {
	int count=0;
	int check=0;
	String[][] courselist;
	String[][] str;
	public Remind_titleInfo(String title){
		DataFromDatabase MySQL_DB = new DataFromDatabase();
		MySQL_DB.Remind_getStuRemindInfo(title);
		Log.d("�H�U��"+title+"����T", "�H�U��"+title+"����T");
		Thread waitfordata = new Thread(new getdata(MySQL_DB));
		waitfordata.start();	
	}
	
	class getdata implements Runnable
	{
    	private String course_ID;
    	private DataFromDatabase MySQL_DB;
		// �غc�l�A�]�w�n�Ǫ��r��
		public getdata(DataFromDatabase MySQL_DB_1)
		{
			this.MySQL_DB=MySQL_DB_1;
		}

		@Override
		public void run()
		{
		
			Log.d("=====>", "Runable Download�����W�檺�ҵ{ID="+course_ID);
			// TODO Auto-generated method stub
			String result=null;
			int times=0;
			try {
					Thread.sleep(1000);
					result=MySQL_DB.return_result;
					while(times<=10&&result==null){
					   Thread.sleep(1000);
					   result=MySQL_DB.return_result;
					   Log.d("=====>", "ThreadDownload���h�𮧤F1��");	
					   times++;
					}
					if(times<=10){
						Log.d("=====>", "���\����ҵ{����1111111111111111"+result);
						JSONArray jsonArray;
						try {
								jsonArray = new JSONArray(result);							
								courselist = new String[jsonArray.length()][5];
								for(int i = 0 ; i<jsonArray.length() ; i++)
								{
									JSONObject jsonData = jsonArray.getJSONObject(i);
									courselist[i][0] = jsonData.getString("course_ID");
									courselist[i][1] = jsonData.getString("title");
									courselist[i][2] = jsonData.getString("student_ID");
									courselist[i][3] = jsonData.getString("date");
									courselist[i][4] = jsonData.getString("note");
									Log.d("=====>", "RemindFragment ��쪺Json = "+courselist[i][2]);
													 		
								}
								count = jsonArray.length();
								
								if(count>0){
									str = new String[count][3];
									for(int i=0 ; i<count ; i++)
									{
										str[i][0] = courselist[i][2] ;
										str[i][1] = courselist[i][3] ; 
										str[i][2] = courselist[i][4] ;
									}
								}
								else{
									str = new String[1][3];
									str[0][0]=" ";
									str[0][1]="�ȵL���";
									str[0][2]=" ";
									check=1;
								}
								
								Log.d("=====>", Integer.toString(count));	
								mHandler1.obtainMessage(REFRESH_DATA, result).sendToTarget();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						Log.d("=====>", "ThreadDownload�U���Ӧh���A�U������..."+times);
					}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	protected static final int REFRESH_DATA = 0x00000001;
	
	Handler mHandler1 = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// ��ܺ����W��������
				case REFRESH_DATA:

					if(count>0||check==1){
						check=1;
					}
					
					
				break;
			}
		}
	};
	
	public String[][] getTitleInfo(){
		return str;
	}
	public int getCheck(){
		return check;
	}
	public int getCount(){
		return count;
	}
}
