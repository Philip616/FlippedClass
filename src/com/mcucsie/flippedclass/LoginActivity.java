package com.mcucsie.flippedclass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private Button btn_login,btn_register;
	private EditText et_inputacc,et_inputpwd;
	private TextView tv_acc,tv_pwd,tv_test,tv_test2;
	private String Account;
	
	private String[] account_ID ;
	private String[] name ;
	private String[] department ;
	private String[] grade ;
	private String[] gender ;
	private String[] mail_address ;
	private String[] LINE_ID ;
	private String[] photo ;
	private String[] account;
	private String[] password;
	private String[] type;
	

	String pw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Log.d("=====>", "LogActivity setContentView Done!");
		
		//Findview所有元件
		findAllLoginView();
		
		//設立點擊監聽事件1
		setAllLoginListener();
		
	}
	/*public void aboutApp(View view) {
        Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show();
	}*/
	//點擊忘記密碼textview
		// public void Forgot_pw(View v1){
		 private OnClickListener btn_register_Click= new OnClickListener(){
				@Override
				public void onClick(View v) {
			 //Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show();
			 final View item = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_login_forgot_pw, null);
			    new AlertDialog.Builder(LoginActivity.this)
			    	.setIcon(R.drawable.sendmail)
			        .setTitle("傳送密碼至您的e-mail")
			        .setView(item)
			        .setPositiveButton("取消", new DialogInterface.OnClickListener() {  
			
			        	public void onClick(DialogInterface dialog, int which) {  
					// 按下PositiveButton要做的事 
			        		Toast.makeText(getApplicationContext(),"繼續登入", Toast.LENGTH_SHORT).show();
			        	}  
			        })
			        .setNegativeButton("傳送e-mail", new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			            	//輸入帳號已找回密碼的EditText
			                EditText tv_findpw= (EditText) item.findViewById(R.id.tv_ac_for_pw);
			                String ac_mail = tv_findpw.getText().toString();
			                
			                if(ac_mail.equals("")){
			                	Toast.makeText(getApplicationContext(), "尚未輸入", Toast.LENGTH_SHORT).show();
			                }
			               // if(ac_mail.equals("00360006")){
			                	String send_mail = find_mail(ac_mail);
			                	Log.d("<==forgot passwd==>", "成功抓取學生信箱"+send_mail);
			                	//透過帳號找密碼
			                	pw=justAccount(ac_mail);
			                	if(pw != null){
			                	 //mail
			                	  /* 加入StrictMode避免發生 android.os.NetworkOnMainThreadException */  
			                	 StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
			                     .detectDiskReads().detectDiskWrites().detectNetwork()  
			                     .penaltyLog().build());  
			                   try {     
			                       GMailSender sender = new GMailSender(" mcuflippedclassroom@gmail.com", "00360036"); //寄件者(開發方)帳號與密碼  
			                       sender.sendMail("系統自動發送",   //信件標題  
			                               "您的密碼為:"+pw+"\n"+"此信件由系統自動發送，請勿直接回覆！",  //信件內容  
			                               "mcuflippedclassroom@gmail.com",   //寄件者  
			                               send_mail,
			                               "mcuflippedclassroom@gmail.com","Flipped Classroom系統郵件");   //收件者  
			                   } catch (Exception e) {     
			                       Log.e("SendMail", e.getMessage(), e);     
			                   } 
			                	 //mail
			                     Toast.makeText(getApplicationContext(),  "已傳送密碼至"+ac_mail+"使用者信箱", Toast.LENGTH_SHORT).show();
			                }//end of if
			                else{
			                	Toast.makeText(getApplicationContext(), "查無此帳號", Toast.LENGTH_SHORT).show();
			               }
			                
			                
			               
			            }
			        })
			    .show();
				}};
		 //}
		//透過帳號找尋信箱
			private String find_mail(String account_for_mail){
				DataFromDatabase db_find_mail = new DataFromDatabase();
				db_find_mail.FC_getmail (account_for_mail);
				String results=null;
				String mail=null;
				int times=0;
				try {
						Thread.sleep(1000);
						results=db_find_mail.s_return_result;
						Log.d("<==forgot passwd==>", "result:"+results);
						while(times<=10 && results==null){
						   Thread.sleep(1000);
						   results=db_find_mail.return_result;
						  // b_result =db_find_mail.return_result;
						   Log.d("<==forgot passwd==>", "downloadAttenList_runnable多休息了1秒");	
						   times++;
					   }
						if(times<=10){
							Log.d("<==forgot passwd==>", "成功抓取學生資訊"+results);
							
							try {
								
								JSONArray jsonArray=new JSONArray(results);
							
								 
									account_ID   = new String[jsonArray.length()];
									name         = new String[jsonArray.length()];
									department   = new String[jsonArray.length()];
									grade        = new String[jsonArray.length()];
									gender       = new String[jsonArray.length()];
									mail_address = new String[jsonArray.length()];
									LINE_ID      = new String[jsonArray.length()];
									photo        = new String[jsonArray.length()];
									
									
									for(int i = 0 ; i < jsonArray.length() ; i++)
									{
										//JSONObject jsonData = new JSONObject(results);
										JSONObject jsonData = jsonArray.getJSONObject(i);
										account_ID[i] = jsonData.getString("account_ID");
										name[i] = jsonData.getString("name");
										department[i] = jsonData.getString("department");
										grade[i] = jsonData.getString("grade");
										gender[i] = jsonData.getString("gender");
										mail_address[i] = jsonData.getString("mail_address");
										LINE_ID[i] = jsonData.getString("LINE_ID");
										photo[i] = jsonData.getString("photo");
										Log.d("=====>", "抓取學生資訊 找到的Json = "+mail_address[i]);
										mail = mail_address[i];
										
									}
									
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								
							}
						}
				}catch (Exception e) {
					// TODO: handle exception
				}
				return mail;
			}
			
		 //透過帳號回傳密碼
			private String justAccount(String account_ID){
				DataFromDatabase db_find_pw = new DataFromDatabase();
				db_find_pw.FC_getPassword(account_ID);
				String password_result = null;//取得該帳號PW結果
				String findpw_result = null;//DB回傳結果
				int times=0;
				
				
				Log.d("<==find password==>", " findpw_result:"+findpw_result);
				try {
					Thread.sleep(1000);
					findpw_result=db_find_pw.s_return_result;
					Log.d("<==find password==>", "result:"+findpw_result);
					while(times<=10 && findpw_result==null){
					   Thread.sleep(1000);
					   Log.d("<==find password==>", "downloadAttenList_runnable多休息了1秒");	
					   times++;
					   
				   }
			   
				if(times<=10){
					Log.d("<==find password==>", "成功抓取學生密碼"+findpw_result);
					
					try {
						
						JSONArray jsonArray=new JSONArray(findpw_result);
					
						 
						account  = new String[jsonArray.length()];
						password = new String[jsonArray.length()];
						type     = new String[jsonArray.length()];
							
							for(int i = 0 ; i < jsonArray.length() ; i++)
							{
								//JSONObject jsonData = new JSONObject(results);
								JSONObject jsonData2 = jsonArray.getJSONObject(i);
								account[i]  = jsonData2.getString("account_ID");
								password[i] = jsonData2.getString("password");
								type[i]     =jsonData2.getString("type");
								
								password_result =password[i] ;
								Log.d("<==find password==>", "學生密碼為"+password_result);
							}
							
							
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
						}
					}
				}catch (Exception e) {
			// TODO: handle exception
					}
					return password_result;
			}
	
			
			protected void onResume(){
				super.onResume();
				ConnectivityManager  connectivityMgr = (ConnectivityManager)
				this.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = connectivityMgr.getActiveNetworkInfo();
				if(netInfo ==null){//若沒開啟
					new AlertDialog.Builder(LoginActivity.this)//顯示對話框
					.setTitle("No connection")
					.setMessage("請確認網路狀態\n即將開啟網路連線設定")
					.setNeutralButton(android.R.string.ok,new DialogInterface.OnClickListener(){//按下ok後跳入設定網路畫面
						public void onClick (DialogInterface dialog,int whichButton){
							startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
						}//end of OnClick對話框
					})//end of OnClickListener
					.show();//顯示連線訊息
				}//end of 網路未連線
			}//end of onResume
			
	private void setAllLoginListener(){
		btn_login.setOnClickListener(btn_login_Click);
		btn_register.setOnClickListener(btn_register_Click);
		Log.d("=====>", "LogActivity setAllLoginListener Done!");
	}
	private void findAllLoginView(){		
		
	btn_login=(Button)findViewById(R.id.btn_login);
	btn_register=(Button)findViewById(R.id.btn_register);
	et_inputacc=(EditText)findViewById(R.id.et_inputacc);
	et_inputpwd=(EditText)findViewById(R.id.et_inputpwd);
	tv_acc=(TextView)findViewById(R.id.tv_acc);
	tv_pwd=(TextView)findViewById(R.id.tv_pwd);
	tv_test=(TextView)findViewById(R.id.tv_testtext);
	et_inputacc.requestFocus();
	//btn_register.setEnabled(false);
	Log.d("=====>", "LogActivity findViewByID Done!");	
	
	}


	private OnClickListener btn_login_Click= new OnClickListener(){
		@Override
		public void onClick(View v) {
			// Get Input (Account & Password)
			String /*Account,*/Password;
			Account  = et_inputacc.getText().toString();
			Password = et_inputpwd.getText().toString();
			if(Account.equals("")||Password.equals(""))
				tv_test.setText("請輸入正確的帳號密碼");
			else{
				Log.d("=====>", "LogActivity get Input Message Done!");
				
				queryAccount(Account,Password);
			}
		}};
		
		
		
		
   /* private OnClickListener btn_register_Click= new OnClickListener(){
		@Override
		public void onClick(View v) {
			// Call register Activity
           Intent register_intent=new Intent();
           register_intent.setClass(LoginActivity.this, RegisterActivity.class);
           startActivity(register_intent);
		}};*/

		
	private void queryAccount(String account_ID,String password){
		DataFromDatabase MySQL_DB = new DataFromDatabase();
		MySQL_DB.FC_checkhAccountExist(account_ID, password);
		
		Thread waitfordata = new Thread(new WaitForData(MySQL_DB));
		waitfordata.start();
	}
	
	class WaitForData implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		
		public WaitForData(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
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
						Log.d("=====>", "LoginActivity Waitfodata 多等了1秒");
						result = MySQL_DB.return_result;
						times++;
					}
					
					Log.d("=====>", "LoginActivity Waitfodata 共等了" + times + "秒");
					
					if(times < 11)
					{
						mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();	
					}
					
					else
					{
						NetErrorHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
					}
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
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
					String result = null;
					if (msg.obj instanceof String)
					result = (String) msg.obj;
					if (result != null)
						// result不為NULL所做的處理
					{			
						StringBuilder sb=new StringBuilder();					
						
						if(!result.equals("0"))
				        {								
							String[] str = result.split(":");
							
							String account_ID = str[0];
							String password = str[1];
							int type =  Integer.valueOf(str[2]).intValue();
							
							sb.append("登入成功!");
				    		tv_test.setText(sb);	
				    		//--------------------------------------------
				    		
				    		Intent intent=new Intent();
				            
				    		Bundle bundle = new Bundle();
							
							bundle.putString("Account",account_ID);
							bundle.putString("Password",password);
							bundle.putInt("Type", type);
							bundle.putInt("LauncherTag", 0);
							intent.putExtras(bundle);
				    		
				    		intent.setClass(LoginActivity.this, FirstLoginActivity.class);
				            startActivity(intent);
				            LoginActivity.this.finish();
				    		
				    		//-----------------------------------------------
				           /*
				            Intent intent=new Intent();
				            
				    		Bundle bundle = new Bundle();
							
							bundle.putString("Account",account_ID);
							bundle.putString("Password",password);
							bundle.putInt("Type", type);
							bundle.putInt("LauncherTag", 0);
							intent.putExtras(bundle);
				    		
				    		intent.setClass(LoginActivity.this, MainActivity.class);
				            startActivity(intent);
				            LoginActivity.this.finish();
				             */
				    		tv_test2=(TextView)findViewById(R.id.tv_attendpop_cooldowntime);		
				        }
				        
						else
				        {
				        	sb.append("帳號或密碼錯誤");
				        	tv_test.setText(sb);
				        }
					}
				break;
			}
		}
	};
	
	Handler NetErrorHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// 顯示網路上抓取的資料
				case REFRESH_DATA:			
						Toast.makeText(LoginActivity.this, "連線失敗", Toast.LENGTH_LONG).show();				
				break;
			}
		}
	};
	

}
