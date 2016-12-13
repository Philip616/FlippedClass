package com.mcucsie.flippedclass;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;






import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class LauncherActivity extends Activity {
	
	private static Handler mHandler;
	private GetNowAccountInfo gnai=new GetNowAccountInfo(this);
	private int isExistsAccount=0;
	private Bundle bundle;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		TODO Auto-generated method stub
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mHandler=new Handler();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		bundle=new Bundle();
		
		new Thread(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                	    //初始化暫存資料庫
                		initTemporalDatabase();
                		//初始化個人資料庫
                		initPersonalDatabase();
                		//更新暫存資料庫
                		updateTemporalDatabase();
                		
                		Thread.sleep(1000);//這邊可以做你想預先載入的資料
                		
//                      Toast.makeText(getApplicationContext(), "Loading結束", Toast.LENGTH_LONG).show();
                        //接下來轉跳到app的主畫面
                		if(isExistsAccount==0)
                		{
                			Log.d("<==LauncherActivity==>", "此手機尚未登入");
                			mHandler.post(LoginActivity);
                		}
                		else
                		{
                			Log.d("<==LauncherActivity==>", "此手機已經登入過了");
                			mHandler.post(updatePersonalDatabase);
                			mHandler.post(MainActivity);
                		}
                    
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        }).start();
}
	
	private void initTemporalDatabase() {
		// TODO Auto-generated method stub
		SQLiteDatabase tDB=openOrCreateDatabase("FlippedClass_Temporal", MODE_PRIVATE, null);
		String str_init_table="CREATE TABLE if not exists `account` (`account_ID` TEXT NOT NULL,"
							  +"`password` TEXT NOT NULL,`type` INTEGER,"
							  +"PRIMARY KEY(account_ID));";
		tDB.execSQL(str_init_table);
		str_init_table="CREATE TABLE if not exists `now_course` (`course_ID` TEXT NOT NULL,"
				  +"`course_name` TEXT NOT NULL,"
				  +"PRIMARY KEY(course_ID));";
		tDB.execSQL(str_init_table);
		tDB.close();
		Log.d("<==LauncherActivity==>", "初始化暫存資料庫完成");
	}
	private void initPersonalDatabase() {
		// TODO Auto-generated method stub
		SQLiteDatabase FC_DB=openOrCreateDatabase("flippedclass_database", MODE_PRIVATE, null);
		String str_init_table="CREATE TABLE if not exists `user_profile` (`account_ID` TEXT NOT NULL,"
				  +"`name` TEXT NOT NULL,"
				  + "`department` TEXT NOT NULL,"
				  + "`grade` INTEGER,"
				  + "`gender` INTEGER,"
				  + "`mail_address` TEXT,"
				  + "`LINE_ID` TEXT,"
				  + "`photo` TEXT,"
				  + "PRIMARY KEY(account_ID));";
		FC_DB.execSQL(str_init_table);
		
		str_init_table="CREATE TABLE if not exists 'personal_courselist' (`course_ID` TEXT NOT NULL,"
				  + "PRIMARY KEY(course_ID));";
		FC_DB.execSQL(str_init_table);
		
		str_init_table="CREATE TABLE if not exists `course_information` (`course_ID` TEXT NOT NULL,"
				  + "`course_name` TEXT NOT NULL,"
				  + "`teacher_account` TEXT NOT NULL,"
				  + "`classroom` TEXT NOT NULL,"
				  + "`week` TEXT,"
				  + "`number_of_students` INTEGER,"
				  + "`remind_day` INTEGER,"
				  + "`start_date` TEXT NOT NULL,"
				  + "`end_date` TEXT NOT NULL,"
				  + "PRIMARY KEY(course_ID));";
		FC_DB.execSQL(str_init_table);
		
		str_init_table="CREATE TABLE if not exists `gcm` (`message` TEXT NOT NULL,"
				  + "`tag` INTEGER,"
				  + "'time' TEXT NOT NULL);";
		FC_DB.execSQL(str_init_table);
		str_init_table="CREATE TABLE if not exists `attend` (`attend_tag` TEXT NOT NULL);";
		FC_DB.execSQL(str_init_table);
		
		FC_DB.close();
		Log.d("<==LauncherActivity==>", "初始化個人資料庫完成");
	}
	private void updateTemporalDatabase() {
		// TODO Auto-generated method stub
		try {
			String Account =gnai.getNowAccountID();
			String Password = gnai.getNowAccountPassword();
			String Str_Type = gnai.getNowAccountType();
			int Type;
			if(Str_Type.equals("teacher"))
				Type=0;
			else
				Type=1;
			
			SQLiteDatabase tDB=openOrCreateDatabase("FlippedClass_Temporal", MODE_PRIVATE, null);
			Cursor cursor=tDB.rawQuery("SELECT account_ID FROM account", null);
			Log.d("<==LauncherActivity==>", "LauncherActivity 暫存資料庫測試Cursor數量="+cursor.getCount());
			if(cursor.getCount()!=0)
			{
				isExistsAccount=1;
				bundle.putString("Account", Account);
				bundle.putString("Password", Password);
				bundle.putInt("Type", Type);
				bundle.putInt("LauncherTag", 1);
			}
			/*ContentValues insertCV_accounttable=new ContentValues();
			insertCV_accounttable.put("account_ID", Account);
			insertCV_accounttable.put("password", Password);
			insertCV_accounttable.put("type", Type);
			tDB.insert("account", null, insertCV_accounttable);
			Log.d("=====>", "MainActivity 暫存資料庫Insert account="+Account+" password="+Password+" type="+Type);*/
			
			tDB.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private void updatePersonalDatabase() {
		// TODO Auto-generated method stub
		
		
		try {
				String Account = gnai.getNowAccountID();

				SQLiteDatabase SQLite_DB=openOrCreateDatabase("flippedclass_database", MODE_PRIVATE, null);
				Cursor cursor=SQLite_DB.rawQuery("SELECT account_ID FROM user_profile", null);
				Log.d("<==LauncherActivity==>", "LauncherActivity 個人資料庫測試Cursor數量="+cursor.getCount());
				if(cursor.getCount()!=0)
				{
					SQLite_DB.delete("user_profile", null, null);
					SQLite_DB.delete("personal_courselist", null, null);
					SQLite_DB.delete("course_information", null, null);
					SQLite_DB.delete("gcm", null, null);
//					SQLite_DB.delete("attend", null, null);
					Log.d("<==LauncherActivity==>", "LauncherActivity 個人資料庫測試DELETE舊資料");
				}
				/*
				try {
					ContentValues cv=new ContentValues();
					cv.put("attend_tag", 0);
					SQLite_DB.insert("attend", null, cv);
					Log.d("<==LauncherActivity==>", "成功輸入SQLite 'attend'的'tag' = 0(int)");
				} catch (Exception e) {
					// TODO: handle exception
				}*/
				DataFromDatabase MySQL_DB = new DataFromDatabase();
				MySQL_DB.FC_getStdInfo(Account);
		
				Thread waitfordata = new Thread(new WaitForData(MySQL_DB,SQLite_DB,"user_profile"));
				waitfordata.start();			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	class WaitForData implements Runnable
	{
		DataFromDatabase MySQL_DB;
		SQLiteDatabase SQLite_DB;
		String table_name;
		Thread waitfordata;
		JSONArray jsonArray;
		int times = 0;
		
		public WaitForData(DataFromDatabase MySQL_DB,SQLiteDatabase SQLite_DB,String table_name)
		{
			this.MySQL_DB = MySQL_DB;
			this.SQLite_DB = SQLite_DB;
			this.table_name = table_name;
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
						Log.d("<==LauncherActivity==>", "LauncherActivity Waitfodata 多等了1秒");
						result = MySQL_DB.return_result;
						times++;
					}
					Log.d("<==LauncherActivity==>", "LauncherActivity 個人資料庫測試result="+result);
					
					
					switch(table_name)
					{
						case "user_profile":
							String[] data = result.split(":");
							
							ContentValues insertCV_userprofiletable=new ContentValues();
							insertCV_userprofiletable.put("account_ID", data[0]);
							insertCV_userprofiletable.put("name", data[1]);
							insertCV_userprofiletable.put("department", data[2]);
							insertCV_userprofiletable.put("grade", Integer.valueOf(data[3]).intValue());
							insertCV_userprofiletable.put("gender", Integer.valueOf(data[4]).intValue());
							insertCV_userprofiletable.put("mail_address", data[5]);
							insertCV_userprofiletable.put("Line_ID", data[6]);
							insertCV_userprofiletable.put("photo", data[7]);

							SQLite_DB.insert("user_profile", null, insertCV_userprofiletable);
							String Account = gnai.getNowAccountID();
							MySQL_DB.clearResult();
							MySQL_DB.FC_getCourseList(Account);
							waitfordata = new Thread(new WaitForData(MySQL_DB,SQLite_DB,"personal_courselist"));
							waitfordata.start();
						break;
						
						case "personal_courselist":
							
							
							try {
									String str_where = null;
									
									jsonArray = new JSONArray(result);
																							
									ContentValues insertCV_personalcourselisttable=new ContentValues();
									
									for(int i = 0 ; i<jsonArray.length() ; i++)
									{
										JSONObject jsonData = jsonArray.getJSONObject(i);
									
										String course_ID = jsonData.getString("course_ID");
										
										Log.d("<==LauncherActivity==>", "LauncherActivity 個人資料庫測試course_ID="+course_ID);
	
										insertCV_personalcourselisttable.put("course_ID", course_ID);
										SQLite_DB.insert("personal_courselist", null, insertCV_personalcourselisttable);
										
										
										
										switch(i)
										{
											case 0:
												str_where = "course_ID = " + course_ID;
												break;
											
											default:
												str_where = str_where + " OR course_ID = " + course_ID;												
										}
									}
									
									Log.d("<==LauncherActivity==>", "LauncherActivity 個人資料庫測試str_where="+str_where);
									MySQL_DB.clearResult();
									MySQL_DB.FC_getCourseInfo(str_where);									
									

									waitfordata = new Thread(new WaitForData(MySQL_DB,SQLite_DB,"course_information"));
									waitfordata.start();
							}
							
							catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
							
						break;
						
						case "course_information":
							
							try {									
									jsonArray = new JSONArray(result);
																							
									ContentValues insertCV_personalcourseinformationtable=new ContentValues();
									
									for(int i = 0 ; i<jsonArray.length() ; i++)
									{
										JSONObject jsonData = jsonArray.getJSONObject(i);
									
										String course_ID = jsonData.getString("course_ID");
										String course_name = jsonData.getString("course_name");
										String teacher_account = jsonData.getString("teacher_account");
										String classroom = jsonData.getString("classroom");
										String week = jsonData.getString("week");
										String number_of_students = jsonData.getString("number_of_students");
										String remind_day = jsonData.getString("remind_day");
										String start_date = jsonData.getString("start_date");
										String end_date = jsonData.getString("end_date");
						
										
										Log.d("<==LauncherActivity==>", "LauncherActivity 個人資料庫測試course_ID="+course_ID);
	
										insertCV_personalcourseinformationtable.put("course_ID", course_ID);
										insertCV_personalcourseinformationtable.put("course_name", course_name);
										insertCV_personalcourseinformationtable.put("teacher_account", teacher_account);
										insertCV_personalcourseinformationtable.put("classroom", classroom);
										insertCV_personalcourseinformationtable.put("week", week);
										insertCV_personalcourseinformationtable.put("number_of_students", Integer.valueOf(number_of_students).intValue());
										insertCV_personalcourseinformationtable.put("remind_day", Integer.valueOf(remind_day).intValue());
										insertCV_personalcourseinformationtable.put("start_date", start_date);
										insertCV_personalcourseinformationtable.put("end_date", end_date);
										insertCV_personalcourseinformationtable.put("course_ID", course_ID);
										
										SQLite_DB.insert("course_information", null, insertCV_personalcourseinformationtable);
									}
							}
							
							catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
							
							SQLite_DB.close();
							
							break;
					}					
					
				} 
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	/*private void wakeAlarm()
	{
			c = Calendar.getInstance(); 
			c.setTimeInMillis(System.currentTimeMillis());
			
	    	Intent intent =new Intent(this, ServiceRemindTch.class);
		    intent.setAction("short");
		    
	    	am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		    pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);	
		    am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi); 
		
	}*/
	
	Runnable LoginActivity=new Runnable() {
		public void run() {
			Intent intent=new Intent();
    		intent.setClass(LauncherActivity.this, LoginActivity.class);
    		//Bundle bundle=new Bundle();
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startActivity(intent);
    		LauncherActivity.this.finish();
		}
	};
	Runnable MainActivity=new Runnable() {
		public void run() {
			Intent intent=new Intent();
    		intent.setClass(LauncherActivity.this, MainActivity.class);
    		//Bundle bundle=new Bundle();
    		intent.putExtras(bundle);
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startActivity(intent);
    		LauncherActivity.this.finish();
		}
	};
	Runnable updatePersonalDatabase=new Runnable() {
		public void run() {
			updatePersonalDatabase();
		}
	};

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
}
