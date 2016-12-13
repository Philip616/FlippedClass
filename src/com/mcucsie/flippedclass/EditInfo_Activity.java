package com.mcucsie.flippedclass;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditInfo_Activity extends Activity {
	/*MainActivity ma;
	StringBuilder sb=new StringBuilder();
	
	private Button  btn_ok;
	private EditText ed_ID,ed_name,ed_department,ed_email,ed_gender,ed_lineID;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editinfo);
		
		TextView tv_check = (TextView)findViewById(R.id.tv_checking);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		ed_ID = (EditText)findViewById(R.id.ed_ID);
		ed_name = (EditText)findViewById(R.id.ed_name);
		ed_department = (EditText)findViewById(R.id.ed_department);
		ed_email = (EditText)findViewById(R.id.ed_email);
		ed_gender = (EditText)findViewById(R.id.ed_gender);
		ed_lineID = (EditText)findViewById(R.id.ed_lineID);		
		
		
		Bundle bundle = this.getIntent().getExtras();		
		String Account = bundle.getString("Account");		
		DataFromDatabase_old FC_Data = new DataFromDatabase_old("ID = '"+Account+"'", "FlippedClass_DataBase", "User_Profile", this);
		Log.d("=====>", "Mainlayout call DataFromDataBase success!");		
		String str = FC_Data.ShowInfo();
		sb.append(FC_Data.getRows());		
		tv_check.setText(str);
		
		btn_ok.setOnClickListener(btn_ok_click);

	}
	
	private OnClickListener btn_ok_click = new OnClickListener(){
		
		private Context context;
		private SQLiteDatabase Database;
		private String Database_name="FlippedClass_DataBase";
		//private String Database_name="Information_DataBase";
		
		@Override
		public void onClick(View v) {
		
		// TODO Auto-generated method stub
			Log.d("btn_ok", "click");
			
			this.Database = openOrCreateDatabase(this.Database_name, MODE_PRIVATE, null);
			
			ContentValues insertCV_StdInfo=new ContentValues();
	    	insertCV_StdInfo.put("id", ed_ID.getText().toString());
	    	insertCV_StdInfo.put("name", ed_name.getText().toString());
	    	insertCV_StdInfo.put("department", ed_department.getText().toString());
	    	insertCV_StdInfo.put("mail_address", ed_email.getText().toString());
	    	insertCV_StdInfo.put("gender", ed_gender.getText().toString());
	    	insertCV_StdInfo.put("LineID", ed_lineID.getText().toString());
	    	
	    	//Database.insert("User_Profile", null, insertCV_StdInfo);
	    	Database.update("User_Profile", insertCV_StdInfo, "ID = '"+ed_ID.getText().toString()+"'", null);
	    	//Database.delete("User_Profile", "id = '"+ed_ID.getText().toString()+"'", null);
	    	Log.d("=====>", "Data From Database User Information update!");
	    	
	    	Database.close();
			Log.d("=====>", "Data From Database Read User Information DataBase Close!");
			
		}
	};*/

}
