package com.mcucsie.flippedclass;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GetNowCourseInfo {
	Context context;
	Cursor cursor;
	SQLiteDatabase db;
	public GetNowCourseInfo(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	
	@SuppressWarnings("static-access")
	public String getNowCourseID(){
		String now_course_ID="null";
		db=context.openOrCreateDatabase("FlippedClass_Temporal", context.MODE_PRIVATE, null);
    	try {
    		cursor=db.rawQuery("Select course_ID FROM now_course", null);
    		cursor.moveToFirst();
    		now_course_ID=cursor.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("=====>", "Error在GetNowCourseInfo!!!"+e.getMessage());
		}
    	db.close();
		return now_course_ID;
	}
	
	@SuppressWarnings("static-access")
	public String getNowCourseName(){
		String now_course_Name="null";
		db=context.openOrCreateDatabase("FlippedClass_Temporal", context.MODE_PRIVATE, null);
    	try {
    		cursor=db.rawQuery("Select course_name FROM now_course", null);
    		cursor.moveToFirst();
    		now_course_Name=cursor.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("=====>", "Error在GetNowCourseInfo!!!"+e.getMessage());
		}
    	db.close();
		return now_course_Name;
	}
	
	@SuppressWarnings("static-access")
	public String getNowCourseDate(){
		
		String start_date="null";
		db=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
    	try {
    		cursor=db.rawQuery("Select start_date FROM course_information", null);
    		cursor.moveToFirst();
    		start_date=cursor.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("=====>", "Error在GetNowCourseInfo!!!"+e.getMessage());
		}
    	db.close();
		return start_date;
	}
	
	@SuppressWarnings("static-access")
	public String getNowCourseWeek(){
		
		String week="null";
		db=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
    	try {
    		cursor=db.rawQuery("Select week FROM course_information", null);
    		cursor.moveToFirst();
    		week=cursor.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("=====>", "Error在GetNowCourseInfo!!!"+e.getMessage());
		}
    	db.close();
		return week;
	}
	
	@SuppressWarnings("static-access")	
	public int getNowCourseRemindDay(){
		
		int remind_day=0;
		db=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
    	try {
    		cursor=db.rawQuery("Select remind_day FROM course_information", null);
    		cursor.moveToFirst();
    		remind_day=cursor.getInt(0);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("=====>", "Error在GetNowCourseInfo!!!"+e.getMessage());
		}
    	db.close();
		return remind_day;
	}
	
	public String getSettingCourseInfo(String course_ID){
		String courseInfo = null;
		SQLiteDatabase db=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
		try {
		Cursor cursor=db.rawQuery("SELECT remind_day,start_date,end_date FROM course_information WHERE course_ID='"+course_ID+"'", null);
		cursor.moveToFirst();
		courseInfo = cursor.getString(0);
		courseInfo += ":" + cursor.getString(1) + ":" + cursor.getString(2);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("=====>", "Error在GetSettingCourseInfo!!!"+e.getMessage());
		}
		
		return courseInfo;
	}
	
	@SuppressWarnings("static-access")	
	public int getAllCourseCount(){
		SQLiteDatabase db=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
		Cursor cursor=db.rawQuery("SELECT course_ID FROM course_information", null);
		
		if(cursor.getCount()!=0)
			db.close();
		
		return cursor.getCount();
	}
	
	@SuppressWarnings("static-access")	
	public int getAllMemberCount(String course_ID){
		int count=-1;
		SQLiteDatabase db=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
		Cursor cursor=db.rawQuery("SELECT number_of_students FROM course_information WHERE course_ID='"+course_ID+"'", null);
		cursor.moveToFirst();
		if(cursor.getCount()!=0)
		{
			db.close();
			count=cursor.getInt(0);
		}
		return count;
	}
	
	
}
