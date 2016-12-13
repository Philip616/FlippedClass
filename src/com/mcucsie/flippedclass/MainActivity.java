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
	private String Type="";       //�ǥ�=student  �Ѯv=teacher
	private Bundle bundle;
	private int now_page=0;      //0=����  1=�I�W  2=����  3=�t�K  4=����  5=�]�w
	private int group_page_tag=0; //1=�Юv���� 2=�d�ݦW�� 3=�ǥͬd�ݦP�խ���T
	private int exam_page_tag=0; //1=��ܴ��� 2=�d�ݦ��Z
	private int attend_page_tag=0;//1=�Юv�I�W //2=�Юv�d�ݯZ�W�X�u�v //3=�ǥͬd�ݦۤv�X�u�v //4=�ǥ�ñ�쭶��
	public static int quickpick_page_tag =0; // 0=�ǥ� 1=�Юv

	
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
		//�Ұ�Alarm�A��
        wakeAlarm();
		
        //��l��ActionBar
        initActionBar();
        //��l�ƥ����PFunction
        iniNavigationDrawer();
        //��l�ƼȦs��Ʈw(flipped_temporal)
        //initTemporalDatabase();
        //��l�ƭӤH��Ʈw(flipped_database)
        //initPersonalDatabase();
        
        
        if(bundle.getInt("LauncherTag")==0)      //���s�n�J���ϥΪ�
        {
        	//��s�Ȧs��Ʈw
        	updateTemporalDatabase();
        	//��s�ӤH��Ʈw
        	updatePersonalDatabase();
        	//��sGCM
        	updateGCM();
        }
		mTitle = getTitle();
	
		frag_main = new MainFragment();  
        getFragmentManager().beginTransaction().replace(R.id.container, frag_main).commit();
        getSupportActionBar().setTitle("����");
        mDrawerList.setItemChecked(0, true);
		mDrawerList.setSelection(0);
		
        now_page=0;                                       //�ثe�����]�m������
        
        
        mpw = new MyPopupWindow(R.layout.popupwindow_choosecourse_layout,getApplicationContext(),Account);
		mpw.setOnDismissListener(dismiss_listener);
		 
        try {
        	
        	if(bundle.getInt("GCM_TAG")==1)
        	{
        	   //�p�G�z�L�����q���i�J�A�{���۰ʸ����I�W�e��
    		   attendence_entrance();
    		   //�p�G�z�L�����q���i�J�A�{���۰ʸ�����յe��
    		   group_entrance();
    		   //�p�G�z�L�����q���i�J�A�{���۰ʸ��ണ���e��(�ǥ�)
    		   //remind_entrance();
    		 //�p�G�z�L�����q���i�J�A�{���۰ʸ���Юv�T�{���ɵe��(�Юv)
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
			Log.d("=====>", "MainActivity �Ȧs��Ʈw����Cursor�ƶq="+cursor.getCount());
			if(cursor.getCount()!=0)
			{
				tDB.delete("account", null, null);
				tDB.delete("now_course", null, null);
				Log.d("=====>", "MainActivity �Ȧs��Ʈw����DELETE�¸��");
			}
			ContentValues insertCV_accounttable=new ContentValues();
			insertCV_accounttable.put("account_ID", Account);
			insertCV_accounttable.put("password", Password);
			insertCV_accounttable.put("type", Type);
			tDB.insert("account", null, insertCV_accounttable);
			Log.d("=====>", "MainActivity �Ȧs��ƮwInsert account="+Account+" password="+Password+" type="+Type);
			
			tDB.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected void refreshNowPage() {
		// TODO Auto-generated method stub
		switch(now_page){
		case 0:      //����
			frag_main.initTextView();     //��popupwindow��Ҥ���A��s������TextView�C
			break;
		case 1:      //�I�W
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
		case 2:      //����	
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
		case 3:      //�t�K
			if(quickpick_page_tag==0)
				frag_quickpick.init_quicktext();    
			if(quickpick_page_tag==1)
				//if(frag_T.quicktext_mode()==0)  // 0=�R�A  1=�ʺA
					frag_T.init_quicktext();
			break;
		case 4:      //����
			frag_group.initGroupRootTextView();//��popupwindow��Ҥ���A��s����RootFragment��TextView�C
			
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
			
			
		case 5:      //�]�w
			
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
				Log.d("=====>", "MainActivity �ӤH��Ʈw����Cursor�ƶq="+cursor.getCount());
				if(cursor.getCount()!=0)
				{
					SQLite_DB.delete("user_profile", null, null);
					SQLite_DB.delete("personal_courselist", null, null);
					SQLite_DB.delete("course_information", null, null);
					SQLite_DB.delete("gcm", null, null);
					SQLite_DB.delete("attend", null, null);
					Log.d("=====>", "MainActivity �ӤH��Ʈw����DELETE�¸��");
				}
		
				try {
					ContentValues cv=new ContentValues();
					cv.put("attend_tag", 0);
					SQLite_DB.insert("attend", null, cv);
					Log.d("<==LauncherActivity==>", "���\��JSQLite 'attend'��'tag' = 0(int)");
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
						Log.d("=====>", "MainActivity Waitfodata �h���F1��");
						result = MySQL_DB.return_result;
						times++;
					}
					Log.d("=====>", "MainActivity �ӤH��Ʈw����result="+result);
					
					
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
										
										Log.d("=====>", "MainActivity �ӤH��Ʈw����course_ID="+course_ID);
	
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
									
									Log.d("=====>", "MainActivity �ӤH��Ʈw����str_where="+str_where);
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
								
										
										Log.d("=====>", "MainActivity �ӤH��Ʈw����course_ID="+course_ID);
	
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
		spirit1 = message.split("�i");
		shareGroup = spirit1[1].split("�j");
		openTo = spirit1[2].split("�j");
		
		course_ID = gnci.getNowCourseID();
		
		checkDialog.setTitle("���ɰQ�װ�");
		checkDialog.setNegativeButton("�ڵ�", new DialogInterface.OnClickListener() {				
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		checkDialog.setPositiveButton("�T�{", new DialogInterface.OnClickListener() {
			
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
	//�k���P
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
				Log.d("=====>", "MainActivity �Ȧs��Ʈw����DELETE�¸��");
				Intent intent=new Intent();
		        intent.setClass(MainActivity.this, LoginActivity.class);
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        if(GCMRegistrar.isRegisteredOnServer(this))
		        {
		        	GCMRegistrar.unregister(this);        //�p�G�n�X���ɭԡA���g���U�LGCM�A�b���B�|����GCM redID
		        	Log.d("<==MainActivity==>", "�n�X�P��unRegister Red ID");
		        	
		        	DataFromDatabase del_redID_db =new DataFromDatabase();
		        	del_redID_db.GCM_deleteRegID(Account);
		        	Log.d("<==MainActivity==>", "�n�X�P�ɧR���ۥ�Database�� Red ID");
		        }
		        startActivity(intent);
		        this.finish();
				return true;
				
			case R.id.item_showinfo:
				//showinfo();
	            return true;
	            //�ק�K�X
			case R.id.item_change_pw:
				final View chpw = LayoutInflater.from(MainActivity.this).inflate(R.layout.dilog_main_changepw, null);
				dialog = new AlertDialog.Builder(this);
				dialog.setIcon(R.drawable.changepw1);	
	        	dialog.setTitle("�ܧ�K�X");
	        	dialog.setView(chpw);
	    		dialog.setPositiveButton("����", new DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {  
	                    // ���UPositiveButton�n������       
	                }  
	            }); 
	    		dialog.setNegativeButton("�T�{", new DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {  
	                    // ���UNegativeButton�n������ 
	                	get_pw = (EditText)chpw.findViewById(R.id.ed_input_pw);
	                	String input_change_pw = get_pw.getText().toString();
	                	CheckPW(Account, input_change_pw);
	                	                
	                }
	            }); 
	    		dialog.show();
				return true;   
			case R.id.action_choosecourse:
				
				//�H�U ���ݮe4.0�H�UAndroid����(�䤣��ActionBar)���O�@�C
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
				//�H�W ���ݮe4.0�H�UAndroid����(�䤣��ActionBar)���O�@�C
			case R.id.action_edit_info:
				refreshNowPage();
				
				return true;
				
	        default:
	        	return super.onOptionsItemSelected(item);
		}
		
		
	}
	//�ˬd��J���±K�X
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
							Log.d("=====>", "LoginActivity Waitfodata �h���F1��");
							result = MySQL_DB.return_result;
							times++;
						}
						
						Log.d("=====>", "LoginActivity Waitfodata �@���F" + times + "��");
						
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
					// ��ܺ����W��������
					case REFRESH_DATA:
						String result = null;
						if (msg.obj instanceof String)
						result = (String) msg.obj;
						if (result != null)
							// result����NULL�Ұ����B�z
						{			
							StringBuilder showstr=new StringBuilder();					
							//�T�{�b���K�X���T����K�X
							if(!result.equals("0"))
					        {		
								final View newpw = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_main_newpw, null);
								//dialog = new AlertDialog.Builder(this);
					        	dialog.setTitle("�ܧ�K�X");
					        	dialog.setView(newpw);
					    		dialog.setPositiveButton("����", new DialogInterface.OnClickListener() {  
					                public void onClick(DialogInterface dialog, int which) {  
					                    // ���UPositiveButton�n������       
					                }  
					            }); 
					    		dialog.setNegativeButton("�T�{", new DialogInterface.OnClickListener() {  
					                public void onClick(DialogInterface dialog, int which) {  
					                    // ���UNegativeButton�n������ 
					                	get_newpw = (EditText)newpw.findViewById(R.id.ed_newpw);
					                	get_again_newpw = (EditText)newpw.findViewById(R.id.ed_again_newpw);
					                	String input_new_pw = get_newpw.getText().toString();
					                	String inputagain_new_pw = get_again_newpw.getText().toString();
					                	
					                	if(input_new_pw.equals(inputagain_new_pw)){
					                		DataFromDatabase db = new DataFromDatabase();
					                		db.updatePassword(Account,input_new_pw);
					                		
					                		Toast.makeText(getApplicationContext(),  "�ק�K�X���\", Toast.LENGTH_SHORT).show();
					                	}
					                	else{
					                		Toast.makeText(getApplicationContext(),  "�⦸��J�K�X���P", Toast.LENGTH_SHORT).show();
					                	}
					                	
					        }
					    		 });
					    		dialog.show();
					        }
					        
							else
					        {
								Toast.makeText(getApplicationContext(), "�d�L���b��", Toast.LENGTH_SHORT).show();
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
					// ��ܺ����W��������
					case REFRESH_DATA:			
							Toast.makeText(MainActivity.this, "�s�u����", Toast.LENGTH_LONG).show();				
					break;
				}
			}
		};
    private void initActionBar() {
        // TODO Auto-generated method stub
        getSupportActionBar().setTitle("����");
        //��� Up Button (��b Logo �����䪺���s�ϥ�)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //���} Up Button ���I���\��
        getSupportActionBar().setHomeButtonEnabled(true);
        actionbar=getSupportActionBar();

       
	    
        Log.d("=====>", "StartActivity��ActionBar�l�Ƨ���");
    }
	
	private void iniNavigationDrawer() {
		
		//��l�ƥ����P
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
	        	map.put("ItemTitle", "����");
	        	break;
	        case 1:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "�I�W");
	        	break;
	        case 2:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "����");
	        	break;
	        case 3:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "�t�K");
	        	break;
	        case 4:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "����");
	        	break;
	        case 5:
	        	map.put("ItemImage", images[i]);  
	        	map.put("ItemTitle", "�]�w");
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
        
     // �B��ActionBarDrawerToggle�i�H�ݮeActionbar�P��P�A�åB���������O�]�w��ť�C
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* �ǤJ�DActivity */
                mDrawerLayout,         /* �ǤJNavigation Drawer��View */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.open_left_drawer,  /* "open drawer" �y�z */
                R.string.close_left_drawer  /* "close drawer" �y�z */
                ) {

			public void onDrawerClosed(View view) {
            
            	//getSupportActionBar().setTitle(mTitle);
                Log.d("=====>", "StartActivity��P�����F");
                //Toast.makeText(getApplicationContext(), "�����F", Toast.LENGTH_LONG).show();
              
                if(now_page==2 || now_page==4|| now_page==1)
                	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
                if(now_page==5)
                	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
                
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
 
            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle(mDrawerTitle);
                Log.d("=====>", "StartActivity��P���}�F");
                
                
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
         
        //����]�w���W�����Ϯ׬��T���u(�i���I��)�����A�C
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        Log.d("=====>", "StartActivity��P��l�Ƨ���");
	}
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// �����P(Navigation Drawer)�̭���ListView�I����ť
			onSectionAttached(position);
		}
    }
    
    public void onSectionAttached(int number) {
		//ListItemĲ�o�ƥ�
		
    	Log.d("=====>", "Now User Type = " + Type);
    	Type=gnai.getNowAccountType();
    	
    	switch (number) {
		case 0:
			frag_main = new MainFragment();  
            getFragmentManager().beginTransaction().replace(R.id.container, frag_main).commit();
            getSupportActionBar().setTitle("����");
            now_page=0;                                       //�ثe�����]�m������
            break;
		case 1:
			replace_AttendRoot();
			now_page=1;                                       //�ثe�����]�m���I�W
            break;
		case 2:
			
			while(getSupportActionBar().getTabCount()>0)
				getSupportActionBar().removeAllTabs();
					
			replace_ExamUserMainRoot();
			                                       //�ثe�����]�m������
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
			getSupportActionBar().setTitle("�t�K�G"+ gnai.getNowAccountName());

			
			/*frag_quickpick=new QuickpickFragment();
			getFragmentManager().beginTransaction().replace(R.id.container, frag_quickpick).commit();
			getSupportActionBar().setTitle("�t�K");*/
			now_page=3;                                       //�ثe�����]�m���t�K                                    //�ثe�����]�m���t�K
			break;			
		case 4:
			
			replace_GrouptoRoot();
			now_page=4;    
			//�ثe�����]�m������
			break;
			
		case 5:
			while(getSupportActionBar().getTabCount()>0)
				getSupportActionBar().removeAllTabs();			
			replace_setting();
			//remind_page_tage=0;
			now_page=5;                                       //�ثe�����]�m���]�w
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
		
		int rows_num = cursor.getCount();//���o��ƪ�C��
		int columns_num = cursor.getColumnCount();
		
		if(rows_num != 0) {
			 cursor.moveToFirst();   //�N���в��ܲĤ@�����

			 for(int i=0;i<columns_num;i++)
			 {
				 user_data[i] = cursor.getString(i);
			 }
			
			 //cursor.moveToNext();
			 cursor.close();
		}
		
		if(user_data[4].equals("1"))
			user_data[4] = "�k";
		
		if(user_data[4].equals("0"))
			user_data[4] = "�k";
		
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
	            //���UBack��A�{������
	        	dialog = new AlertDialog.Builder(this);
	        	dialog.setTitle("�T�w���}FlippedClass?");
	    		dialog.setPositiveButton("����", new DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {  
	                    // ���UPositiveButton�n������       
	                }  
	            }); 
	    		dialog.setNegativeButton("���}", new DialogInterface.OnClickListener() {  
	                public void onClick(DialogInterface dialog, int which) {  
	                    // ���UNegativeButton�n������  
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
			if(arg0.getText().equals("���Z�W��"))
				frag_groupmemberlist.exceptDismiss();
			
			else if(arg0.getText().equals("�ҵ{�Q�װ�")){
				
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
			if(arg0.getText().equals("����"))
				replace_GrouptoSort();
			
			else if(arg0.getText().equals("���Z�W��"))
				replace_GrouptoMemberlist();
			
			else if(arg0.getText().equals("�խ���T"))
					replace_GrouptoMemberInfo();
		
			else if(arg0.getText().equals("�ҵ{�Q�װ�"))
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
			getSupportActionBar().setTitle("���աG" + gnai.getNowAccountName());
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	
		if(Type.equals("teacher"))
		{
			getSupportActionBar().addTab(actionbar.newTab().setText("����").setTabListener(Group_tablistener));
			getSupportActionBar().addTab(actionbar.newTab().setText("���Z�W��").setTabListener(Group_tablistener));
			getSupportActionBar().addTab(actionbar.newTab().setText("�ҵ{�Q�װ�").setTabListener(Group_tablistener));
		}
		else
		{
			getSupportActionBar().addTab(actionbar.newTab().setText("�ҵ{�Q�װ�").setTabListener(Group_tablistener));
			getSupportActionBar().addTab(actionbar.newTab().setText("���Z�W��").setTabListener(Group_tablistener));
			getSupportActionBar().addTab(actionbar.newTab().setText("�խ���T").setTabListener(Group_tablistener));
		}
        now_page=4;                                       //�ثe�����]�m������
	}
	private void replace_GrouptoSort() {
		// TODO Auto-generated method stub
		GroupSortFragment frag_groupsort=new GroupSortFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_group_content, frag_groupsort).commit();
		group_page_tag=1;   //�]�m���Юv����
	}
	private void replace_GrouptoMemberlist() {
		frag_groupmemberlist=new GroupMemberListFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_group_content, frag_groupmemberlist).commit();
		group_page_tag=2;	//�]�m���Юv+�ǥͪ��d�ݦW��
	}
	private void replace_GrouptoMemberInfo(){
		GroupMemberInfoFragment frag_groupmemberinfo =new GroupMemberInfoFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_group_content, frag_groupmemberinfo).commit();
		group_page_tag=3;	//�]�m���ǥͬd�ݲխ���T
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

		group_page_tag=4;	//�]�m���d�ݰQ�װ�
		
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
//			Log.d("<==MainActivity==>", "Tab�Q����:"+arg0.getText());
			if(arg0.getText().equals("�˵��Z�ťX�u"))
				frag_teacherattendinfo.endAllThread();
			if(arg0.getText().equals("�Ұ��I�W"))
				frag_teacherattend.endAllThread();	
		}
		
		@Override
		public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub
//			Log.d("<==MainActivity==>", "Tab�Q���:"+arg0.getText());
			if(arg0.getText().equals("�Ұ��I�W"))
				replace_AttendTeacherAttend();
			else if(arg0.getText().equals("�˵��Z�ťX�u"))
				replace_AttendTeacherAttendInfo();
			else if(arg0.getText().equals("�ӤH�X�u"))
				replace_AttendStudentAttendInfo();
			else if(arg0.getText().equals("ñ��"))
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
			
			if(arg0.getText().equals("����C��"))
			{
				if(Type.equals("student"))	
				frag_main_std.endAllThread();
				if(Type.equals("teacher"))
				frag_main_tch.endAllThread();
			}
			
			if(arg0.getText().equals("�ǥͦ��Z"))
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
			if(arg0.getText().equals("����C��"))
				replace_ExamUserMain();/*			
			else if(arg0.getText().equals("�d�ݦ��Z"))
				replace_ExamStudentGrade();*/
			else if(arg0.getText().equals("�ǥͦ��Z"))
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
            getSupportActionBar().setTitle("����G" + gnai.getNowAccountName());
			
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
            
            if(Type.equals("teacher"))
			{
            	getSupportActionBar().addTab(actionbar.newTab().setText("����C��").setTabListener(Exam_tablistener));
            	getSupportActionBar().addTab(actionbar.newTab().setText("�ǥͦ��Z").setTabListener(Exam_tablistener));
            	getSupportActionBar().addTab(actionbar.newTab().setText("IRS").setTabListener(Exam_tablistener));
			}
			else
			{
				getSupportActionBar().addTab(actionbar.newTab().setText("����C��").setTabListener(Exam_tablistener));
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
        getSupportActionBar().setTitle("�I�W�G" + gnai.getNowAccountName());
        Type=gnai.getNowAccountType();
        
        if(Type.equals("teacher"))
		{
        	getSupportActionBar().addTab(actionbar.newTab().setText("�Ұ��I�W").setTabListener(Attend_tablistener));
        	getSupportActionBar().addTab(actionbar.newTab().setText("�˵��Z�ťX�u").setTabListener(Attend_tablistener));
		}
		else
		{
			getSupportActionBar().addTab(actionbar.newTab().setText("ñ��").setTabListener(Attend_tablistener));
        	getSupportActionBar().addTab(actionbar.newTab().setText("�˵��Z�ťX�u").setTabListener(Attend_tablistener));
		}
        
        now_page=1;                                       //�ثe�����]�m���I�W
	}
	private void replace_AttendTeacherAttend(){
		frag_teacherattend=new AttendTeacherAttendFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_attend_content, frag_teacherattend).commit();
		attend_page_tag=1;	//�]�m���Юv�I�W�e��
	}
    private void replace_AttendTeacherAttendInfo(){
		frag_teacherattendinfo=new AttendTeacherAttendInfoFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_attend_content, frag_teacherattendinfo).commit();
		attend_page_tag=2;	//�]�m���Юv�d�ݥX�u�e��
	}
    private void replace_AttendStudentAttendInfo(){
		AttendStudentInfoFragment frag_studentinfo=new AttendStudentInfoFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_attend_content, frag_studentinfo).commit();
		attend_page_tag=3;	//�]�m���ǥͬd�ݥX�u�e��
	}
	private void replace_AttendStudentAttend(){
		AttendStudentAttendFragment frag_studentattend=new AttendStudentAttendFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_attend_content, frag_studentattend).commit();
		attend_page_tag=4;	//�]�m���ǥ�ñ��e��
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
		exam_page_tag=1;	//�]�m�����筺��
	}
	
	private void replace_ExamTeacherGrade() {
		frag_examgrade=new ExamGradeTeacherFragment();
		getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_examgrade).commit();
		exam_page_tag=2;	//�]�m���Юv�d�ݦ��Z
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
	
		exam_page_tag=3;	//�]�m��IRS����
	}
	
	private void replace_setting(){
			
		frag_set = new SettingMainFragment();  
        getFragmentManager().beginTransaction().replace(R.id.container, frag_set).commit();
        getSupportActionBar().setTitle("�]�w�G" + gnai.getNowAccountName());
        
        Type=gnai.getNowAccountType();
		while(getSupportActionBar().getTabCount()>0)
			getSupportActionBar().removeAllTabs();
		
        if(Type.equals("teacher"))
		{
        	getSupportActionBar().addTab(actionbar.newTab().setText("�]�w").setTabListener(Setting_TabListener));
        	getSupportActionBar().addTab(actionbar.newTab().setText("�פJ").setTabListener(Setting_TabListener));
		}
        if(Type.equals("student"))
		{
        	getSupportActionBar().addTab(actionbar.newTab().setText("�]�w").setTabListener(Setting_TabListener));
        	getSupportActionBar().addTab(actionbar.newTab().setText("�򥻸��").setTabListener(Setting_TabListener));
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
			if(arg0.getText().equals("�]�w"))
			{
				frag_set = new SettingMainFragment();  
				getFragmentManager().beginTransaction().replace(R.id.container, frag_set).commit();
			}
			else if(arg0.getText().equals("�פJ"))
			{
				SettingUploadDatabase upload = new SettingUploadDatabase();
				getFragmentManager().beginTransaction().replace(R.id.frame_setting_content, upload).commit();
			}
			else if(arg0.getText().equals("�򥻸��"))
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
			�C * depending upon your app requirement
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
