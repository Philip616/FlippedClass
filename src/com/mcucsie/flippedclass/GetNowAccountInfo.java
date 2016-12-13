package com.mcucsie.flippedclass;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GetNowAccountInfo {
	Context context;
	Cursor cursor;
	SQLiteDatabase db;
	public GetNowAccountInfo(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	
	@SuppressWarnings("static-access")
	public String getNowAccountID(){
		String now_account_ID="null";
		db=context.openOrCreateDatabase("FlippedClass_Temporal", context.MODE_PRIVATE, null);
    	try {
    		cursor=db.rawQuery("Select account_ID FROM account", null);
    		cursor.moveToFirst();
    		now_account_ID=cursor.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("=====>", "Error在GetNowAccountInfo!!!"+e.getMessage());
		}
    	db.close();
		return now_account_ID;
	}
	
	@SuppressWarnings("static-access")
	public String getNowAccountPassword(){
		String now_password="null";
		db=context.openOrCreateDatabase("FlippedClass_Temporal", context.MODE_PRIVATE, null);
    	try {
    		cursor=db.rawQuery("Select password FROM account", null);
    		cursor.moveToFirst();
    		now_password=cursor.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("=====>", "Error在GetNowAccountInfo!!!"+e.getMessage());
		}
    	db.close();
		return now_password;
	}
	
	@SuppressWarnings("static-access")
	public String getNowAccountType(){
		int now_type=-1;
		db=context.openOrCreateDatabase("FlippedClass_Temporal", context.MODE_PRIVATE, null);
    	try {
    		cursor=db.rawQuery("Select type FROM account", null);
    		cursor.moveToFirst();
    		now_type=cursor.getInt(0);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("=====>", "Error在GetNowAccountInfo!!!"+e.getMessage());
		}
    	db.close();
    	if(now_type==0)
    		return "teacher";
    	else if(now_type==1)
    		return "student";
    	else
    	{
    		return "null";
    	}
    }
	
	
	@SuppressWarnings("static-access")
	public String getNowAccountName(){
		String name="null";
		db=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
    	try {
    		cursor=db.rawQuery("Select name FROM user_profile", null);
    		cursor.moveToFirst();
    		name=cursor.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("=====>", "Error在GetNowAccountInfo!!!"+e.getMessage());
		}
    	db.close();
    	return name;
    	
    }
}
	
