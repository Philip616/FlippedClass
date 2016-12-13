package com.mcucsie.flippedclass.setting;

import java.util.Calendar;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;











import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;



public class ServiceRemind extends Service {

	DataFromDatabase db = new DataFromDatabase();
	GetNowCourseInfo gnci;
	String course_ID;
	String list[];
	String class_Date;
 	String[] dateInformation;
	Boolean check = false;
	int cur_year,cur_month,cur_day,year,month,day,remind_day,week,check_day; 
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 @Override
	 public void onCreate() {
	 // TODO Auto-generated method stub
	 super.onCreate();
		Log.i("服務", "建立");
	 }
	  
	 @Override
	 public int onStartCommand(Intent intent, int flags, int startId) {
	
		 	gnci = new GetNowCourseInfo(getApplication());
		 	course_ID = gnci.getNowCourseID();
		
		 	class_Date = gnci.getNowCourseDate();
			week = Integer.valueOf(gnci.getNowCourseWeek());
		
			remind_day = gnci.getNowCourseRemindDay();	
			
		 	Calendar c = Calendar.getInstance(); 
			cur_year = c.get(Calendar.YEAR);
			cur_month = c.get(Calendar.MONTH);
			cur_day = c.get(Calendar.DAY_OF_MONTH);
			
			//Log.d("intent","resuult="+intent);
			
			dateInformation = class_Date.split("-");
			
			year = Integer.valueOf(dateInformation[0]);
			month = Integer.valueOf(dateInformation[1]);
			day = Integer.valueOf(dateInformation[2]);
			
		
			DataFromDatabase MySQL = new DataFromDatabase();
			MySQL.Bulletin_Board_getcourseInfo(course_ID, String.valueOf(week));
			Thread waitData = new Thread(new getData(MySQL));
			waitData.start();
						
		 	Log.d("Service Activity", "Service start"); 
		    
		 	//stopService(intent);
		 	
		 	
		 		return START_REDELIVER_INTENT;
	 
	 }
	 

	 class getData implements Runnable{
		 DataFromDatabase MySQL; 
		 
		 private getData (DataFromDatabase MySQL){
			 this.MySQL = MySQL;
		 }
		 
		 
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				String result = null;
				int time=0;
						
				result = MySQL.return_result;
				
				while(result==null && time<10){
					result = MySQL.return_result;
					Thread.sleep(500);
					time++;
				}
				
				
				result = result.trim();		
				
				if(!result.equals("null")){
						check = true;
						Log.d("result","result="+cur_month);
				}
				
				//該提醒的日期
				check_day = day - remind_day;
				
				if(check_day <= 0 && cur_month+1 != month){
					int lastmonth = month - 1;
					cur_month = cur_month+1;
					
					if(lastmonth == 1 || lastmonth == 3 || lastmonth == 5 || lastmonth == 7 || lastmonth == 8 || lastmonth == 10)
						check_day = 31 + day - remind_day;
					
					else if(lastmonth == 4 || lastmonth == 6 || lastmonth == 9 || lastmonth == 11)
						check_day = 30 + day - remind_day;
					
					else if(lastmonth == 2)
					   {
					    if ((cur_year % 4 == 0 && cur_year % 100 != 0) || cur_year % 400 == 0 )	
					    	check_day = 29 + day - remind_day;
					    
					    else 
					    	check_day = 28 + day - remind_day;
					   }
				}
				else if (check_day < 0) 
					check_day = day;
			
				Log.d("check_day","result="+check_day+cur_day);
			
		    if(cur_day >= check_day){	    	
		    if(cur_year == year && (cur_month+1) == month){
		    	
		    	if(!check)
		    		db.GCM_sendMessageToCourseTeacher(course_ID, course_ID+"記得上傳資料喔!!");
		    	
		    	else
		    		db.GCM_sendMessageToCourseMember(course_ID, course_ID+"記得課前預習喔!!");
		    	
		    	if(cur_day == day){
		    		class_Date = cur_year + "-" + (cur_month+1) + "-" + (day+7);
		    		
		    if((month == 12) && (cur_day+7)>31)
		    	class_Date = cur_year+1 + "-" + 1 + "-" + (day+7-31);
		    		
		    else if(month == 2)
		   {
		    if ((cur_year % 4 == 0 && cur_year % 100 != 0) || cur_year % 400 == 0 )
		    	if((day+7)>29)		
		    			class_Date = cur_year + "-" + (cur_month+2) + "-" + (day+7-29);
		    	else if((day+7)>28) 
	            	   	class_Date = cur_year + "-" + (cur_month+2) + "-" + (day+7-28);
		   }
		   
		    else if((day+7)>31)		
		    	if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10)
			    	class_Date = cur_year + "-" + (cur_month+2) + "-" + (day+7-31);
		    
		    else if((day+7)>30)
		    	if (month == 4 || month == 6 || month == 9 || month == 11)
		    		class_Date = cur_year + "-" + (cur_month+2) + "-" + (day+7-30);   	
		    		
			    	db.FC_UpdateClassDate(class_Date,String.valueOf(week+1),course_ID);
		    		}
		    	
		    	}
		    	
		    }
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 
	 }
	 
	 public void onDestroy(){
	  super.onDestroy();
	  Log.d("Service Activity", "Service stop");  
	 }

	

}
