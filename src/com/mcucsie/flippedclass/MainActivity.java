package com.mcucsie.flippedclass;

import static com.mcucsie.flippedclass.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.mcucsie.flippedclass.CommonUtilities.EXTRA_MESSAGE;
import static com.mcucsie.flippedclass.CommonUtilities.SENDER_ID;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.mcucsie.flippedclass.attendence.AttendFragment;
import com.mcucsie.flippedclass.attendence.AttendStudentAttendFragment;
import com.mcucsie.flippedclass.attendence.AttendStudentInfoFragment;
import com.mcucsie.flippedclass.attendence.AttendTeacherAttendFragment;
import com.mcucsie.flippedclass.attendence.AttendTeacherAttendInfoFragment;
import com.mcucsie.flippedclass.exam.ExamFragment;
import com.mcucsie.flippedclass.exam.ExamGradeTeacherFragment;
import com.mcucsie.flippedclass.exam.ExamIRSMainStudentFragment;
import com.mcucsie.flippedclass.exam.ExamIRSMainTeacherFragment;
import com.mcucsie.flippedclass.exam.ExamMainStudentFragment;
import com.mcucsie.flippedclass.exam.ExamMainTeacherFragment;
import com.mcucsie.flippedclass.group.GroupFragment;
import com.mcucsie.flippedclass.group.GroupMainDiscuss;
import com.mcucsie.flippedclass.group.GroupMemberInfoFragment;
import com.mcucsie.flippedclass.group.GroupMemberListFragment;
import com.mcucsie.flippedclass.group.GroupSortFragment;
import com.mcucsie.flippedclass.group.GroupStdPublicDiscuss;
import com.mcucsie.flippedclass.group.GroupTchPublicDiscuss;
import com.mcucsie.flippedclass.quickpick.Quickpick_T;
import com.mcucsie.flippedclass.quickpick.QuicktextFragment;
import com.mcucsie.flippedclass.remind.Remind_S;
import com.mcucsie.flippedclass.setting.ServiceRemind;
import com.mcucsie.flippedclass.setting.SettingMainFragment;
import com.mcucsie.flippedclass.setting.SettingStudentProfile;
import com.mcucsie.flippedclass.setting.SettingUploadDatabase;
//import com.mcucsie.flippedclass.remind.RemindFragment;
 
public class MainActivity extends ActionBarActivity {

	private AlertDialog.Builder dialog;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mLeftDrawer;
    private ActionBar actionbar;
    private AsyncTask<Void, Void, Void> mRegisterTask;
    private AlertDialogManager alert = new AlertDialogManager();
    private AlertDialog.Builder checkDialog;
    private ConnectionDetector cd;
    private GroupStdPublicDiscuss frag_StdDiscuss = new GroupStdPublicDiscuss();
    private GroupTchPublicDiscuss frag_TchDiscuss = new GroupTchPublicDiscuss();
    
	private CharSequence mTitle;
	private String[] mListTitles;
	
	private TextView tv_test;
	private MyPopupWindow mpw;
	private String Account;
	private String AttendenceTag="";
	private String GroupTag="";
	private String RemindTag="";
	private String DiscussTag="";
	private String Type="";       //學生=student  老師=teacher
	private Bundle bundle;
	private int now_page=0;      //0=首頁  1=點名  2=測驗  3=速貼  4=分組  5=設定
	private int group_page_tag=0; //1=教師分組 2=查看名單 3=學生查看同組員資訊
	private int exam_page_tag=0; //1=選擇測驗 2=查看成績
	private int attend_page_tag=0;//1=教師點名 //2=教師查看班上出席率 //3=學生查看自己出席率 //4=學生簽到頁面
	public static int quickpick_page_tag =0; // 0=學生 1=教師

	
	private StringBuilder sb=new StringBuilder();
	private MainFragment frag_main;
	private AttendFragment frag_atten;
	private ExamFragment frag_exam;
	private ExamMainTeacherFragment frag_main_tch;
	private ExamMainStudentFragment frag_main_std;
	private ExamGradeTeacherFragment frag_examgrade;
	private ExamIRSMainTeacherFragment frag_irs_tch;
	private GroupMemberListFragment frag_groupmemberlist;
	private SettingMainFragment frag_set;
	private GroupFragment frag_group;
	private Quickpick_T frag_T;
	private QuicktextFragment frag_quickpick;
	private AttendTeacherAttendInfoFragment frag_teacherattendinfo;
	private AttendTeacherAttendFragment frag_teacherattend;
	//private RemindFragment frag_remind;
	
	private GetNowCourseInfo gnci;
	private GetNowAccountInfo gnai;
	
	
	public static String name="name";
	public static String email="email";
	
	private AlarmManager am;
	private PendingIntent pi;
	private Calendar c;
	
	public EditText get_pw,get_again_newpw,get_newpw;
	static String check_result;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bundle = this.getIntent().getExtras();	
		gnci=new GetNowCourseInfo(this);
		gnai=new GetNowAccountInfo(this);	
		Log.d("=====>", "Change to MainActivity!");		
		Account = bundle.getString("Account");
		Type = gnai.getNowAccountType();
		checkDialog = new AlertDialog.Builder(this);
		//啟動Alarm服務
        wakeAlarm();
		
        //初始化ActionBar
        initActionBar();
        //初始化左邊抽屜Function
        iniNavigationDrawer();
        //初始化暫存資料庫(flipped_temporal)
        //initTemporalDatabase();
        //初始化個人資料庫(flipped_database)
        //initPersonalDatabase();
        
        
        if(bundle.getInt("LauncherTag")==0)      //重新登入的使用者
        {
        	//更新暫存資料庫
        	updateTemporalDatabase();
        	//更新個人資料庫
        	updatePersonalDatabase();
        	//更新GCM
        	updateGCM();
        }
		mTitle = getTitle();
	
		frag_main = new MainFragment();  
        getFragmentManager().beginTransaction().replace(R.id.container, frag_main).commit();
        getSupportActionBar().setTitle("首頁");
        mDrawerList.setItemChecked(0, true);
		mDrawerList.setSelection(0);
		
        now_page=0;                                       //目前頁面設置為首頁
        
        
        mpw = new MyPopupWindow(R.layout.popupwindow_choosecourse_layout,getApplicationContext(),Account);
		mpw.setOnDismissListener(dismiss_listener);
		 
        try {
        	
        	if(bundle.getInt("GCM_TAG")==1)
        	{
        	   //如果透過推播通知進入，程式自動跳轉點名畫面
    		   attendence_entrance();
    		   //如果透過推播通知進入，程式自動跳轉分組畫面
    		   group_entrance();
    		   //如果透過推播通知進入，程式自動跳轉提醒畫面(學生)
    		   //remind_entrance();
    		 //如果透過推播通知進入，程式自動跳轉教師確認分享畫面(教師)
    		   discuss_entrance();
        	}
        	
		} catch (Exception e) {
			// TODO: handle exception
		}
	
	}
	
	
	
	 /*@Override
	    protected void onDestroy() {
	        super .onDestroy();
	        this .stopService( new Intent( this , ServiceRemindTch. class ));
	    }*/

	

	private void updateGCM() {
    	cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		
		// Getting name, email from intent

		
		name = bundle.getString("Account");
		email = bundle.getString("Account")+"@mail.com";
		
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM			
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.				
				Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, name, email, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
    	
    	
    	
    	
//    	Intent i=new Intent();
//		i.setClass(MainActivity.this, GCMMainActivity.class);
////		Bundle bundle=new Bundle();
//		i.putExtra("name", Account);
//		i.putExtra("email", Account+"@email");
//		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(i);
////		LauncherActivity.this.finish();
    	
	}
	private OnDismissListener dismiss_listener=new OnDismissListener() {
		
		@Override
		public void onDismiss() {
			// TODO Auto-generated method stub
			refreshNowPage();
			Log.d("=====>", "MypopupWindow  DISMISS!!");
		}
	};
	

	/*private void initTemporalDatabase() {
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
	}*/
	private void updateTemporalDatabase() {
		// TODO Auto-generated method stub
		try {
			String Account = bundle.getString("Account");
			String Password = bundle.getString("Password");
			int Type = bundle.getInt("Type");
			SQLiteDatabase tDB=openOrCreateDatabase("FlippedClass_Temporal", MODE_PRIVATE, null);
			Cursor cursor=tDB.rawQuery("SELECT account_ID FROM account", null);
			Log.d("=====>", "MainActivity 暫存資料庫測試Cursor數量="+cursor.getCount());
			if(cursor.getCount()!=0)
			{
				tDB.delete("account", null, null);
				tDB.delete("now_course", null, null);
				Log.d("=====>", "MainActivity 暫存資料庫測試DELETE舊資料");
			}
			ContentValues insertCV_accounttable=new ContentValues();
			insertCV_accounttable.put("account_ID", Account);
			insertCV_accounttable.put("password", Password);
			insertCV_accounttable.put("type", Type);
			tDB.insert("account", null, insertCV_accounttable);
			Log.d("=====>", "MainActivity 暫存資料庫Insert account="+Account+" password="+Password+" type="+Type);
			
			tDB.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected void refreshNowPage() {
		// TODO Auto-generated method stub
		switch(now_page){
		case 0:      //首頁
			frag_main.initTextView();     //由popupwindow選課之後，刷新首頁的TextView。
			break;
		case 1:      //點名
			frag_atten.initAttendRootTextView();
			if(attend_page_tag==1)
			{
				frag_teacherattend.endAllThread();
				replace_AttendTeacherAttend();			
			}
			else if(attend_page_tag==2)
				replace_AttendTeacherAttendInfo();
			else if(attend_page_tag==3)
				replace_AttendStudentAttendInfo();
			else if(attend_page_tag==4)
				replace_AttendStudentAttend();
			break;
		case 2:      //測驗	
			frag_exam.initExamRootView();
			if(exam_page_tag==1)
			{
				replace_ExamUserMain();
				
				if(Type.equals("student"))
					frag_main_std.endAllThread();
				else
					frag_main_tch.endAllThread();
			}
			else if(exam_page_tag==2)
			{
				replace_ExamTeacherGrade();
				frag_examgrade.endAllThread();
			}
			else if(exam_page_tag==3)
			{
				if(Type.equals("teacher"))
					frag_irs_tch.endAllThread();
				
				replace_ExamIRSMain();				
			}
			break;
		case 3:      //速貼
			if(quickpick_page_tag==0)
				frag_quickpick.init_quicktext();    
			if(quickpick_page_tag==1)
				//if(frag_T.quicktext_mode()==0)  // 0=靜態  1=動態
					frag_T.init_quicktext();
			break;
		case 4:      //分組
			frag_group.initGroupRootTextView();//由popupwindow選課之後，刷新分組RootFragment的TextView。
			
			if(group_page_tag==1)
				replace_GrouptoSort();
			else if(group_page_tag==2)
			{
				frag_groupmemberlist.exceptDismiss();
				replace_GrouptoMemberlist();
			}
			else if(group_page_tag==3)
				replace_GrouptoMemberInfo();
			
			else if(group_page_tag==4)
			{
					int backStackCount = getFragmentManager().getBackStackEntryCount();
					for (int i = 0; i < backStackCount; i++) {
				 
				    // Get the back stack fragment id.
				    int backStackId = getFragmentManager().getBackStackEntryAt(i).getId();		 
				    getFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);

				}
				replace_GrouptoDicuss();
			}
			
			break;
			
			
		case 5:      //設定
			
			frag_set.initSettingRootView();
				replace_setting();



			break;
		
		}
		
	}

	
	private void updatePersonalDatabase() {
		// TODO Auto-generated method stub
		
		
		try {
				String Account = bundle.getString("Account");

				SQLiteDatabase SQLite_DB=openOrCreateDatabase("flippedclass_database", MODE_PRIVATE, null);
				Cursor cursor=SQLite_DB.rawQuery("SELECT account_ID FROM user_profile", null);
				Log.d("=====>", "MainActivity 個人資料庫測試Cursor數量="+cursor.getCount());
				if(cursor.getCount()!=0)
				{
					SQLite_DB.delete("user_profile", null, null);
					SQLite_DB.delete("personal_courselist", null, null);
					SQLite_DB.delete("course_information", null, null);
					SQLite_DB.delete("gcm", null, null);
					SQLite_DB.delete("attend", null, null);
					Log.d("=====>", "MainActivity 個人資料庫測試DELETE舊資料");
				}
		
				try {
					ContentValues cv=new ContentValues();
					cv.put("attend_tag", 0);
					SQLite_DB.insert("attend", null, cv);
					Log.d("<==LauncherActivity==>", "成功輸入SQLite 'attend'的'tag' = 0(int)");
				} catch (Exception e) {
					// TODO: handle exception
				}
				
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
						Log.d("=====>", "MainActivity Waitfodata 多等了1秒");
						result = MySQL_DB.return_result;
						times++;
					}
					Log.d("=====>", "MainActivity 個人資料庫測試result="+result);
					
					
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
										
										Log.d("=====>", "MainActivity 個人資料庫測試course_ID="+course_ID);
	
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
									
									Log.d("=====>", "MainActivity 個人資料庫測試str_where="+str_where);
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
										//String classtime = jsonData.getString("classtime");
										String number_of_students = jsonData.getString("number_of_students");
										String remind_day = jsonData.getString("remind_day");
										String start_date = jsonData.getString("start_date");
										String end_date = jsonData.getString("end_date");
								
										
										Log.d("=====>", "MainActivity 個人資料庫測試course_ID="+course_ID);
	
										insertCV_personalcourseinformationtable.put("course_ID", course_ID);
										insertCV_personalcourseinformationtable.put("course_name", course_name);
										insertCV_personalcourseinformationtable.put("teacher_account", teacher_account);
										insertCV_personalcourseinformationtable.put("classroom", classroom);
										//insertCV_personalcourseinformationtable.put("classtime", classtime);
										insertCV_personalcourseinformationtable.put("number_of_students", Integer.valueOf(number_of_students).intValue());
										insertCV_personalcourseinformationtable.put("remind_day", Integer.valueOf(remind_day).intValue());
										insertCV_personalcourseinformationtable.put("start_date", start_date);
										insertCV_personalcourseinformationtable.put("end_date", end_date);
										//insertCV_personalcourseinformationtable.put("week", Integer.valueOf(week).intValue());
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
	
	

	private void attendence_entrance(){
		try {
			AttendenceTag=bundle.getString("AttendenceTag");
			if(AttendenceTag.equals("attend_now"))
			{
				replace_AttendRoot();
				AttendenceTag="";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void group_entrance(){
		try {
			GroupTag=bundle.getString("GroupTag");
			if(GroupTag.equals("group_change"))
			{
				replace_GrouptoRoot();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void discuss_entrance(){
		try {
			DiscussTag=bundle.getString("DiscussTag");
			if(DiscussTag.equals("ask_share"))
			{
				initCheckDialog();
				checkDialog.show();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void initCheckDialog(){
		String message;
		final String course_ID;
		String[] spirit1;
		final String[] openTo,shareGroup;
		
		checkDialog.setCancelable(false);	
		
		message = bundle.getString("DiscussMessage");
		spirit1 = message.split("【");
		shareGroup = spirit1[1].split("】");
		openTo = spirit1[2].split("】");
		
		course_ID = gnci.getNowCourseID();
		
		checkDialog.setTitle("分享討論區");
		checkDialog.setNegativeButton("拒絕", new DialogInterface.OnClickListener() {				
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		checkDialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				DataFromDatabase MySQL_DB = new DataFromDatabase();
				MySQL_DB.FC_ShareOtherGroupDiscuss("discusslist_privateshare"+course_ID+"_team", shareGroup[0], openTo[0], "open");
				//Log.d("message","result="+shareGroup[0]+":"+openTo[0]);
			}
		});
	}
	
	private void remind_entrance(){
		try {
			RemindTag=bundle.getString("RemindTag");
			if(RemindTag.equals("RemindStu"))
			{
				replace_RemindStu();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	//右邊抽屜
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.


		if (mDrawerToggle.onOptionsItemSelected(item)) {
						
			if(now_page==2 || now_page==4 || now_page==1)
				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            return true;
        }
	 
		switch(item.getItemId())
		{
			case R.id.item_logout:
				SQLiteDatabase tDB=openOrCreateDatabase("FlippedClass_Temporal", MODE_PRIVATE, null);
				tDB.delete("account", null, null);
				tDB.delete("now_course", null, null);
				tDB.close();
				Log.d("=====>", "MainActivity 暫存資料庫測試DELETE舊資料");
				Intent intent=new Intent();
		        intent.setClass(MainActivity.this, LoginActivity.class);
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        if(GCMRegistrar.isRegisteredOnServer(this))
		        {
		        	GCMRegistrar.unregister(this);        //如果登出的時候，曾經註冊過GCM，在此處會移除GCM redID
		        	Log.d("<==MainActivity==>", "登出同時unRegister Red ID");
		        	
		        	DataFromDatabase del_redID_db =new DataFromDatabase();
		        	del_redID_db.GCM_deleteRegID(Account);
		        	Log.d("<==MainActivity==>", "登出同時刪除自用Database的 Red ID");
		        }
		        startActivity(intent);
		        this.finish();
				return true;
				
			case R.id.item_showinfo:
				//showinfo();
	            return true;
	            //修改密碼
			case R.id.item_change_pw:
				final View chpw = LayoutInflater.from(MainActivity.this).inflate(R.layout.dilog_main_changepw, null);
				dialog = new AlertDialog.Builder(this);
				dialog.setIcon(R.drawable.changepw1);	
	        	dialog.setTitle("變更密碼");
	        	dialog.setView(chpw);
	    		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {  
	                    // 按下PositiveButton要做的事       
	                }  
	            }); 
	    		dialog.setNegativeButton("確認", new DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {  
	                    // 按下NegativeButton要做的事 
	                	get_pw = (EditText)chpw.findViewById(R.id.ed_input_pw);
	                	String input_change_pw = get_pw.getText().toString();
	                	CheckPW(Account, input_change_pw);
	                	                
	                }
	            }); 
	    		dialog.show();
				return true;   
			case R.id.action_choosecourse:
				
				//以下 為兼容4.0以下Android版本(找不到ActionBar)的保護。
				if(!mpw.isListdata_exsits()){
					mpw = new MyPopupWindow(R.layout.popupwindow_choosecourse_layout,getApplicationContext(),bundle.getString("Account"));
					mpw.setOnDismissListener(dismiss_listener);
				}
				View v=findViewById(mpw.getActionBarViewID());
				if(mpw.getActionBarViewID()==0)
				 {
					v=findViewById(R.id.drawer_layout);
					mpw.showAtCenter(v);
		         }
				else
				{
					v=findViewById(mpw.getActionBarViewID());
				    mpw.show(v);
				}
				return true;
				//以上 為兼容4.0以下Android版本(找不到ActionBar)的保護。
			case R.id.action_edit_info:
				refreshNowPage();
				
				return true;
				
	        default:
	        	return super.onOptionsItemSelected(item);
		}
		
		
	}
	//檢查輸入的舊密碼
		private void checkPassword(){
			String input_change_pw = get_pw.getText().toString();
			DataFromDatabase my_ac = new DataFromDatabase();
			my_ac.FC_checkhAccountExist(Account, input_change_pw);
			
			Toast.makeText(getApplicationContext(),  "Result="+check_result, Toast.LENGTH_SHORT).show();	
			//String result = my_ac.return_result;
			
//			if(!result.equals("0"))
//	        {
//				Toast.makeText(getApplicationContext(),  "Result"+result, Toast.LENGTH_SHORT).show();
//	        }
//			else{
//				Toast.makeText(getApplicationContext(),  "Result"+result, Toast.LENGTH_SHORT).show();
//			}
//			//Thread waitfordata = new Thread(new WaitForData2(MySQL_DB));
//			//waitfordata.start();
		}
		public void CheckPW(String account_ID,String password){
			DataFromDatabase db = new DataFromDatabase();
			db.FC_checkhAccountExist(account_ID, password);
			
			Thread waitfordata = new Thread(new WaitForNowData(db));
			waitfordata.start();
		}
		//copy from login
		class WaitForNowData implements Runnable
		{
			DataFromDatabase MySQL_DB;
			int times = 0;
			
			public WaitForNowData(DataFromDatabase MySQL_DB)
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
							StringBuilder showstr=new StringBuilder();					
							//確認帳號密碼正確後更改密碼
							if(!result.equals("0"))
					        {		
								final View newpw = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_main_newpw, null);
								//dialog = new AlertDialog.Builder(this);
					        	dialog.setTitle("變更密碼");
					        	dialog.setView(newpw);
					    		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
					                public void onClick(DialogInterface dialog, int which) {  
					                    // 按下PositiveButton要做的事       
					                }  
					            }); 
					    		dialog.setNegativeButton("確認", new DialogInterface.OnClickListener() {  
					                public void onClick(DialogInterface dialog, int which) {  
					                    // 按下NegativeButton要做的事 
					                	get_newpw = (EditText)newpw.findViewById(R.id.ed_newpw);
					                	get_again_newpw = (EditText)newpw.findViewById(R.id.ed_again_newpw);
					                	String input_new_pw = get_newpw.getText().toString();
					                	String inputagain_new_pw = get_again_newpw.getText().toString();
					                	
					                	if(input_new_pw.equals(inputagain_new_pw)){
					                		DataFromDatabase db = new DataFromDatabase();
					                		db.updatePassword(Account,input_new_pw);
					                		
					                		Toast.makeText(getApplicationContext(),  "修改密碼成功", Toast.LENGTH_SHORT).show();
					                	}
					                	else{
					                		Toast.makeText(getApplicationContext(),  "兩次輸入密碼不同", Toast.LENGTH_SHORT).show();
					                	}
					                	
					        }
					    		 });
					    		dialog.show();
					        }
					        
							else
					        {
								Toast.makeText(getApplicationContext(), "查無此帳號", Toast.LENGTH_SHORT).show();
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
							Toast.makeText(MainActivity.this, "連線失敗", Toast.LENGTH_LONG).show();				
					break;
				}
			}
		};
    private void initActionBar() {
        // TODO Auto-generated method stub
        getSupportActionBar().setTitle("首頁");
        //顯示 Up Button (位在 Logo 左手邊的按鈕圖示)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //打開 Up Button 的點擊功能
        getSupportActionBar().setHomeButtonEnabled(true);
        actionbar=getSupportActionBar();

       
	    
        Log.d("=====>", "StartActivity的ActionBar始化完成");
    }
	
	private void iniNavigationDrawer() {
		
		//初始化左邊抽屜
		mTitle = getTitle();
        mListTitles = getResources().getStringArray(R.array.list_title);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mLeftDrawer = (LinearLayout)findViewById(R.id.drawer_view);
     
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        
        int[] images = new int[] { R.drawable.icon_home,  
	            R.drawable.icon_attend, R.drawable.icon_exam,  
	            R.drawable.icon_quick, R.drawable.icon_group,R.drawable.icon_remind };  
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();  
	    for (int i = 0; i < 6; i++) {  
	        HashMap<String, Object> map = new HashMap<String, Object>();
	        switch(i){
	        case 0:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "首頁");
	        	break;
	        case 1:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "點名");
	        	break;
	        case 2:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "測驗");
	        	break;
	        case 3:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "速貼");
	        	break;
	        case 4:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "分組");
	        	break;
	        case 5:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "設定");
	        	break;
	        default:
	        	break;
	        }
	        data.add(map);  
	    }  
	    mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    
	    
	    String[] from = new String[] { "ItemImage", "ItemTitle"};  
	    int[] to = new int[] { R.id.drawer_ItemImage, R.id.drawer_Itemtext}; 
	    SimpleAdapter adapter = new SimpleAdapter(this, data,  
	            R.layout.listview_drawer_item_1, from, to);  
        
	    mDrawerList.setAdapter(adapter);
        /*mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.listview_drawer_item_1, mListTitles));*/
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
     // 運用ActionBarDrawerToggle可以兼容Actionbar與抽屜，並且直接於此類別設定監聽。
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* 傳入主Activity */
                mDrawerLayout,         /* 傳入Navigation Drawer的View */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.open_left_drawer,  /* "open drawer" 描述 */
                R.string.close_left_drawer  /* "close drawer" 描述 */
                ) {

			public void onDrawerClosed(View view) {
            
            	//getSupportActionBar().setTitle(mTitle);
                Log.d("=====>", "StartActivity抽屜關閉了");
                //Toast.makeText(getApplicationContext(), "關閉了", Toast.LENGTH_LONG).show();
              
                if(now_page==2 || now_page==4|| now_page==1)
                	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
                if(now_page==5)
                	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
                
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
 
            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle(mDrawerTitle);
                Log.d("=====>", "StartActivity抽屜打開了");
                
                
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
         
        //此行設定左上角的圖案為三條線(可供點選)的狀態。
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        Log.d("=====>", "StartActivity抽屜初始化完成");
	}
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 左邊抽屜(Navigation Drawer)裡面的ListView點擊監聽
			onSectionAttached(position);
		}
    }
    
    public void onSectionAttached(int number) {
		//ListItem觸發事件
		
    	Log.d("=====>", "Now User Type = " + Type);
    	Type=gnai.getNowAccountType();
    	
    	switch (number) {
		case 0:
			frag_main = new MainFragment();  
            getFragmentManager().beginTransaction().replace(R.id.container, frag_main).commit();
            getSupportActionBar().setTitle("首頁");
            now_page=0;                                       //目前頁面設置為首頁
            break;
		case 1:
			replace_AttendRoot();
			now_page=1;                                       //目前頁面設置為點名
            break;
		case 2:
			
			while(getSupportActionBar().getTabCount()>0)
				getSupportActionBar().removeAllTabs();
					
			replace_ExamUserMainRoot();
			                                       //目前頁面設置為測驗
			break;
		case 3:
			bundle = this.getIntent().getExtras();
			Account = bundle.getString("Account");
			
			if(Type.equals("teacher")){
				//Quickpick_T frag_T = new Quickpick_T();
				//getFragmentManager().beginTransaction().replace(R.id.container, frag_T).commit();
				quickpick_page_tag =1;
				
				frag_T = new Quickpick_T();
				getFragmentManager().beginTransaction().replace(R.id.container, frag_T).commit();
			
			}
			else if(Type.equals("student")){
				quickpick_page_tag =0;
				
				Byte check_ID =0;
				bundle.putByte("check_ID", check_ID);
				//QuickpickFragment frag_quickpick=new QuickpickFragment();
				frag_quickpick=new QuicktextFragment();
				frag_quickpick.setArguments(bundle);
				getFragmentManager().beginTransaction().replace(R.id.container, frag_quickpick).commit();
			}
			getSupportActionBar().setTitle("速貼："+ gnai.getNowAccountName());

			
			/*frag_quickpick=new QuickpickFragment();
			getFragmentManager().beginTransaction().replace(R.id.container, frag_quickpick).commit();
			getSupportActionBar().setTitle("速貼");*/
			now_page=3;                                       //目前頁面設置為速貼                                    //目前頁面設置為速貼
			break;			
		case 4:
			
			replace_GrouptoRoot();
			now_page=4;    
			//目前頁面設置為分組
			break;
			
		case 5:
			while(getSupportActionBar().getTabCount()>0)
				getSupportActionBar().removeAllTabs();			
			replace_setting();
			//remind_page_tage=0;
			now_page=5;                                       //目前頁面設置為設定
			break;
		}
		mDrawerList.setItemChecked(number, true);
		mDrawerList.setSelection(number);
        mDrawerLayout.closeDrawer(mLeftDrawer);
	}
	
	private void showinfo()
	{	
		/*SQLiteDatabase SQLite_DB= openOrCreateDatabase("flippedclass_database", MODE_PRIVATE, null);
		Cursor cursor;
		cursor=SQLite_DB.query("user_profile", null, "account_ID = '"+Account+"'", null, null, null, null);
		
		String[] user_data = new String[8];
		
		int rows_num = cursor.getCount();//取得資料表列數
		int columns_num = cursor.getColumnCount();
		
		if(rows_num != 0) {
			 cursor.moveToFirst();   //將指標移至第一筆資料

			 for(int i=0;i<columns_num;i++)
			 {
				 user_data[i] = cursor.getString(i);
			 }
			
			 //cursor.moveToNext();
			 cursor.close();
		}
		
		if(user_data[4].equals("1"))
			user_data[4] = "男";
		
		if(user_data[4].equals("0"))
			user_data[4] = "女";
		
		String str = "ID : " + user_data[0] + "\n" +"Name : " + user_data[1] + "\n" + "Department : " + user_data[2] + "\n" + "grade : " + user_data[3] + "\n" + "gender : " + user_data[4] + "\n"  + "E-Mail : " + user_data[5] + "\n"  + "LINE ID : " + user_data[6];
		
		TextView user_tv = (TextView) findViewById(R.id.tv_main_1);
		user_tv.setText(str);*/
		Intent intent=new Intent();
		intent.setClass(MainActivity.this, FirstLoginActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
		
	}
	
	@Override
	public void onBackPressed() {
	    FragmentManager fm = this.getFragmentManager();
	    if (fm.getBackStackEntryCount() == 0) {
	            //按下Back鍵，程式關閉
	        	dialog = new AlertDialog.Builder(this);
	        	dialog.setTitle("確定離開FlippedClass?");
	    		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {  
	                    // 按下PositiveButton要做的事       
	                }  
	            }); 
	    		dialog.setNegativeButton("離開", new DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {  
	                    // 按下NegativeButton要做的事  
	                	MainActivity.this.finish();                 
	                }
	            }); 
	    		dialog.show();
	    } else {
	        fm.popBackStack();
	    }
	}
	
	
	
	private TabListener Group_tablistener=new TabListener() {
		
		@Override
		public void onTabUnselected(Tab arg0,
				android.support.v4.app.FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			if(arg0.getText().equals("全班名單"))
				frag_groupmemberlist.exceptDismiss();
			
			else if(arg0.getText().equals("課程討論區")){
				
				int backStackCount = getFragmentManager().getBackStackEntryCount();
				for (int i = 0; i < backStackCount; i++) {
				 
				    // Get the back stack fragment id.
				    int backStackId = getFragmentManager().getBackStackEntryAt(i).getId();			 
				    getFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				 
				} 
			}
				
	
		}
		
		@Override
		public void onTabSelected(Tab arg0,
				android.support.v4.app.FragmentTransaction arg1) {
			// TODO Auto-generated method 
			if(arg0.getText().equals("分組"))
				replace_GrouptoSort();
			
			else if(arg0.getText().equals("全班名單"))
				replace_GrouptoMemberlist();
			
			else if(arg0.getText().equals("組員資訊"))
					replace_GrouptoMemberInfo();
		
			else if(arg0.getText().equals("課程討論區"))
				replace_GrouptoDicuss();
		}
		
		@Override
		public void onTabReselected(Tab arg0,
				android.support.v4.app.FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}
	};
	private void replace_GrouptoRoot(){
		
		while(getSupportActionBar().getTabCount()>0)
			getSupportActionBar().removeAllTabs();
		
		Type=gnai.getNowAccountType();
		frag_group=new GroupFragment();
		
			getFragmentManager().beginTransaction().replace(R.id.container, frag_group).commit();
			getSupportActionBar().setTitle("分組：" + gnai.getNowAccountName());
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	
		if(Type.equals("teacher"))
		{
			getSupportActionBar().addTab(actionbar.newTab().setText("分組").setTabListener(Group_tablistener));
			getSupportActionBar().addTab(actionbar.newTab().setText("全班名單").setTabListener(Group_tablistener));
			getSupportActionBar().addTab(actionbar.newTab().setText("課程討論區").setTabListener(Group_tablistener));
		}
		else
		{
			getSupportActionBar().addTab(actionbar.newTab().setText("課程討論區").setTabListener(Group_tablistener));
			getSupportActionBar().addTab(actionbar.newTab().setText("全班名單").setTabListener(Group_tablistener));
			getSupportActionBar().addTab(actionbar.newTab().setText("組員資訊").setTabListener(Group_tablistener));
		}
        now_page=4;                                       //目前頁面設置為分組
	}
	private void replace_GrouptoSort() {
		// TODO Auto-generated method stub
		GroupSortFragment frag_groupsort=new GroupSortFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_group_content, frag_groupsort).commit();
		group_page_tag=1;   //設置成教師分組
	}
	private void replace_GrouptoMemberlist() {
		frag_groupmemberlist=new GroupMemberListFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_group_content, frag_groupmemberlist).commit();
		group_page_tag=2;	//設置成教師+學生的查看名單
	}
	private void replace_GrouptoMemberInfo(){
		GroupMemberInfoFragment frag_groupmemberinfo =new GroupMemberInfoFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_group_content, frag_groupmemberinfo).commit();
		group_page_tag=3;	//設置成學生查看組員資訊
	}
	
	private void replace_GrouptoDicuss(){
		bundle = this.getIntent().getExtras();
		Account = bundle.getString("Account");
		Byte check_ID;
		
		if(Type.equals("teacher"))
			check_ID =1;
		else
			check_ID =0;
		
			bundle.putByte("check_ID", check_ID);
			
			GroupMainDiscuss frag_groupDiscuss =new GroupMainDiscuss();
			
			frag_groupDiscuss.setArguments(bundle);
		
			getFragmentManager().beginTransaction().replace(R.id.frame_group_content, frag_groupDiscuss).commit();

		group_page_tag=4;	//設置成查看討論區
		
		//Log.d("dicuss","2");
	}
	
	private void replace_RemindStu(){
  	
    	Bundle bundle = new Bundle();
    	bundle.putString("title", "a");
    	bundle.putString("course_ID", "36101");
    	bundle.putString("Account", Account);
    	
    	Remind_S frag = new Remind_S();
		frag.setArguments(bundle);
		
		getFragmentManager().beginTransaction().replace(R.id.container, frag).commit();	
	}
	
	
private TabListener Attend_tablistener=new TabListener() {
		
		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
//			Log.d("<==MainActivity==>", "Tab被取消:"+arg0.getText());
			if(arg0.getText().equals("檢視班級出席"))
				frag_teacherattendinfo.endAllThread();
			if(arg0.getText().equals("課堂點名"))
				frag_teacherattend.endAllThread();	
		}
		
		@Override
		public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
//			Log.d("<==MainActivity==>", "Tab被選擇:"+arg0.getText());
			if(arg0.getText().equals("課堂點名"))
				replace_AttendTeacherAttend();
			else if(arg0.getText().equals("檢視班級出席"))
				replace_AttendTeacherAttendInfo();
			else if(arg0.getText().equals("個人出席"))
				replace_AttendStudentAttendInfo();
			else if(arg0.getText().equals("簽到"))
				replace_AttendStudentAttend();
		}
		
		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	
private TabListener Exam_tablistener=new TabListener() {
		
		@Override
		public void onTabUnselected(Tab arg0,
				android.support.v4.app.FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
			if(arg0.getText().equals("測驗列表"))
			{
				if(Type.equals("student"))	
				frag_main_std.endAllThread();
				if(Type.equals("teacher"))
				frag_main_tch.endAllThread();
			}
			
			if(arg0.getText().equals("學生成績"))
				frag_examgrade.endAllThread();
			
			if(arg0.getText().equals("IRS"))
			{
				if(Type.equals("teacher"))
				frag_irs_tch.endAllThread();
			}
		}
		
		@Override
		public void onTabSelected(Tab arg0,
				android.support.v4.app.FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			if(arg0.getText().equals("測驗列表"))
				replace_ExamUserMain();/*			
			else if(arg0.getText().equals("查看成績"))
				replace_ExamStudentGrade();*/
			else if(arg0.getText().equals("學生成績"))
				replace_ExamTeacherGrade();

			else if(arg0.getText().equals("IRS"))
				replace_ExamIRSMain();
		}
		
		@Override
		public void onTabReselected(Tab arg0,
				android.support.v4.app.FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void replace_ExamUserMainRoot() {
		// TODO Auto-generated method stub
		
			frag_exam = new ExamFragment();  
            getFragmentManager().beginTransaction().replace(R.id.container, frag_exam).commit();
            getSupportActionBar().setTitle("測驗：" + gnai.getNowAccountName());
			
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
            
            if(Type.equals("teacher"))
			{
            	getSupportActionBar().addTab(actionbar.newTab().setText("測驗列表").setTabListener(Exam_tablistener));
            	getSupportActionBar().addTab(actionbar.newTab().setText("學生成績").setTabListener(Exam_tablistener));
            	getSupportActionBar().addTab(actionbar.newTab().setText("IRS").setTabListener(Exam_tablistener));
			}
			else
			{
				getSupportActionBar().addTab(actionbar.newTab().setText("測驗列表").setTabListener(Exam_tablistener));
				getSupportActionBar().addTab(actionbar.newTab().setText("IRS").setTabListener(Exam_tablistener));
			}
            
            exam_page_tag=1;
            
            now_page=2;
	}
	private void replace_AttendRoot(){
		while(getSupportActionBar().getTabCount()>0)
			getSupportActionBar().removeAllTabs();
		if(AttendenceTag.equals("attend_now"))
		  frag_atten = new AttendFragment(AttendenceTag);  
		else
		  frag_atten = new AttendFragment();  
        getFragmentManager().beginTransaction().replace(R.id.container, frag_atten).commit();
        getSupportActionBar().setTitle("點名：" + gnai.getNowAccountName());
        Type=gnai.getNowAccountType();
        
        if(Type.equals("teacher"))
		{
        	getSupportActionBar().addTab(actionbar.newTab().setText("課堂點名").setTabListener(Attend_tablistener));
        	getSupportActionBar().addTab(actionbar.newTab().setText("檢視班級出席").setTabListener(Attend_tablistener));
		}
		else
		{
			getSupportActionBar().addTab(actionbar.newTab().setText("簽到").setTabListener(Attend_tablistener));
        	getSupportActionBar().addTab(actionbar.newTab().setText("檢視班級出席").setTabListener(Attend_tablistener));
		}
        
        now_page=1;                                       //目前頁面設置為點名
	}
	private void replace_AttendTeacherAttend(){
		frag_teacherattend=new AttendTeacherAttendFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_attend_content, frag_teacherattend).commit();
		attend_page_tag=1;	//設置成教師點名畫面
	}
    private void replace_AttendTeacherAttendInfo(){
		frag_teacherattendinfo=new AttendTeacherAttendInfoFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_attend_content, frag_teacherattendinfo).commit();
		attend_page_tag=2;	//設置成教師查看出席畫面
	}
    private void replace_AttendStudentAttendInfo(){
		AttendStudentInfoFragment frag_studentinfo=new AttendStudentInfoFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_attend_content, frag_studentinfo).commit();
		attend_page_tag=3;	//設置成學生查看出席畫面
	}
	private void replace_AttendStudentAttend(){
		AttendStudentAttendFragment frag_studentattend=new AttendStudentAttendFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_attend_content, frag_studentattend).commit();
		attend_page_tag=4;	//設置成學生簽到畫面
	}

	
	private void replace_ExamUserMain() {
		if(Type.equals("student"))
		{	
			frag_main_std = new ExamMainStudentFragment();
			getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_main_std).commit();
		}
		
		if(Type.equals("teacher"))
		{	
			frag_main_tch = new ExamMainTeacherFragment();
			getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_main_tch).commit();
		}
		exam_page_tag=1;	//設置成測驗首頁
	}
	
	private void replace_ExamTeacherGrade() {
		frag_examgrade=new ExamGradeTeacherFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_examgrade).commit();
		exam_page_tag=2;	//設置成教師查看成績
	}
	
	private void replace_ExamIRSMain() {
		if(Type.equals("student"))
		{	
			ExamIRSMainStudentFragment frag_irs_std = new ExamIRSMainStudentFragment();
			getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_irs_std).commit();
		}
		
		if(Type.equals("teacher"))
		{	
			frag_irs_tch = new ExamIRSMainTeacherFragment();
			getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_irs_tch).commit();
		}
	
		exam_page_tag=3;	//設置成IRS首頁
	}
	
	private void replace_setting(){
			
		frag_set = new SettingMainFragment();  
        getFragmentManager().beginTransaction().replace(R.id.container, frag_set).commit();
        getSupportActionBar().setTitle("設定：" + gnai.getNowAccountName());
        
        Type=gnai.getNowAccountType();
		while(getSupportActionBar().getTabCount()>0)
			getSupportActionBar().removeAllTabs();
		
        if(Type.equals("teacher"))
		{
        	getSupportActionBar().addTab(actionbar.newTab().setText("設定").setTabListener(Setting_TabListener));
        	getSupportActionBar().addTab(actionbar.newTab().setText("匯入").setTabListener(Setting_TabListener));
		}
        if(Type.equals("student"))
		{
        	getSupportActionBar().addTab(actionbar.newTab().setText("設定").setTabListener(Setting_TabListener));
        	getSupportActionBar().addTab(actionbar.newTab().setText("基本資料").setTabListener(Setting_TabListener));
		}
	}

	private TabListener Setting_TabListener = new TabListener() {
		
		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			if(arg0.getText().equals("設定"))
			{
				frag_set = new SettingMainFragment();  
				getFragmentManager().beginTransaction().replace(R.id.container, frag_set).commit();
			}
			else if(arg0.getText().equals("匯入"))
			{
				SettingUploadDatabase upload = new SettingUploadDatabase();
				getFragmentManager().beginTransaction().replace(R.id.frame_setting_content, upload).commit();
			}
			else if(arg0.getText().equals("基本資料"))
			{
				SettingStudentProfile stdProfile = new SettingStudentProfile();
				getFragmentManager().beginTransaction().replace(R.id.frame_setting_content, stdProfile).commit();
			}

		}
		
		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
			
		}
	};

	

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());
			
			/**
			 * Take appropriate action on this message
			。 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */
			
			// Showing received message
					
//			Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
			
			// Releasing wake lock
			WakeLocker.release();
		}
	};
	
	private void wakeAlarm()
	{
		
		Intent intent =new Intent(this, ServiceRemind.class);
		intent.setAction("short");
	    
		c = Calendar.getInstance(); 
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.AM_PM, 0);
		c.set(Calendar.HOUR_OF_DAY, 8);
		c.set(Calendar.MINUTE, 30);
		c.set(Calendar.SECOND, 0); 	
		    
	    am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		pi = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);	
		
		am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi); 
		
	}
	
	
    
	//public static void setTag(int tag){
	//	remind_page_tage = tag;
	//}
	
	
}
