package com.mcucsie.flippedclass;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
/*
	private Button btn_register_submit;
	private EditText et_register_acc,et_register_pwd,et_register_pwdcheck;
	private TextView tv_test;
	private SQLiteDatabase FC_Database;
	private SQLiteDatabase SI_Database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		Log.d("=====>", "RegisterActivity setContentView Done!");
		
		
		//Findview所有元件
		btn_register_submit = (Button)findViewById(R.id.btn_register_submit);
		et_register_acc=(EditText)findViewById(R.id.et_register_acc);
		et_register_pwd=(EditText)findViewById(R.id.et_register_pwd);
		et_register_pwdcheck=(EditText)findViewById(R.id.et_register_pwdcheck);
		tv_test=(TextView)findViewById(R.id.tv_register_test);
		Log.d("=====>", "RegisterActivity FindViewByID Done!");
		
		
		btn_register_submit.setOnClickListener(btn_register_submit_Click);
		
	}
	
	private OnClickListener btn_register_submit_Click = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// Get input
			String Account = et_register_acc.getText().toString();
			String Password = et_register_pwd.getText().toString();
			String Passwordcheck = et_register_pwdcheck.getText().toString();
			Log.d("=====>", "RegisterActivity get Input Message Done!");
			
			
			if(inputIsAvaliable(Account,Password,Passwordcheck))
				registerAccount(Account,Password,v);
			else
				Toast.makeText(v.getContext(), "請重新確認輸入的密碼是否正確\n請重新確認是否有遺漏內容", Toast.LENGTH_LONG).show();
		}
	};
 
	
	private void registerAccount(String Account,String Password,View v){
		
		FC_Database = openOrCreateDatabase("FlippedClass_DataBase", MODE_PRIVATE, null);
		SI_Database = openOrCreateDatabase("StudentInformation_DataBase",MODE_PRIVATE,null);
		Log.d("=====>", "RegisterActivity FC_Database & SI_DataBase Open!");
		Cursor cursor;
		if(isCheckAccount(Account))
		{			
			Toast.makeText(v.getContext(), "開始建立帳號", Toast.LENGTH_LONG).show();

			/*AccountManagement AM = new AccountManagement(Account, Password, this, "FlippedClass_DataBase", "Account");
			
			AM.RegisterAccount();//創建帳號
			
			insert_StdInfo(Account);//輸入該帳號基本資料
			
			Toast.makeText(v.getContext(), "帳號建立完成 : "+Account, Toast.LENGTH_LONG).show();
			super.finish();
			Log.d("=====>", "RegisterActivity Finished!");			 
		}		
			 
		else
		{
			Toast.makeText(v.getContext(), "帳號已經註冊或不在學生資訊系統內", Toast.LENGTH_LONG).show();
		}
		
		FC_Database.close();
		Log.d("=====>", "RegisterActivity FC_DataBase Close!");
		SI_Database.close();
		Log.d("=====>", "RegisterActivity SI_DataBase Close!");
	}
    
	private Boolean isCheckAccount(String Account){
    	Log.d("=====>", "RegisterActivity Enter into existAccount function!");
    	Cursor cursor_FC,cursor_SI;
    	cursor_FC=FC_Database.query("Account", null, "ID= '"+Account+"' ", null, null, null, null);
    	cursor_SI=SI_Database.query("Student_Information", null, "ID= '"+Account+"' ", null, null, null, null);
    	
    	if(cursor_FC.getCount()==0 && cursor_SI.getCount()==1)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    
    }
    
    private Boolean inputIsAvaliable(String Account,String Password,String Passwordcheck)
    {
    	if(!Password.equals(Passwordcheck)||Account.equals("")||Password.equals("")||Passwordcheck.equals(""))
    	{
    		return false;
    	}
    	else
    		return true;
    }
    
    private void insert_StdInfo(String Account)
    { 	    	
        DataFromDatabase_old SI_Data = new DataFromDatabase_old("ID = '"+Account+"'", "StudentInformation_DataBase", "Student_Information", this);
		Log.d("=====>", "RegisterActivity call DataFromDataBase success!");
		
		String[][] SI_User_Data;
		
		SI_User_Data = SI_Data.getData();
		
		TextView TV_test = (TextView) findViewById(R.id.tv_register_test);
		
		StringBuilder sb = new StringBuilder();
		
		DataFromDatabase_old FC_Data = new DataFromDatabase_old(null, "FlippedClass_DataBase", "User_Profile", this);
		Log.d("=====>", "RegisterActivity call DataFromDataBase success!");
		
		FC_Data.insertData(SI_User_Data);
		Log.d("=====>", "RegisterActivity SaveDatabase success!");
    }	*/
    
}
