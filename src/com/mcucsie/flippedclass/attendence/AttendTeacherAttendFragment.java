package com.mcucsie.flippedclass.attendence;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
public class AttendTeacherAttendFragment extends Fragment {
	private TextView tv_start_time;
	private TextView tv_end_time;
	private Button btn_send_attend;
	private AnalogClock clock;
	private TimePickerDialog startTimePickerDialog;
	private GetNowCourseInfo gnci;
	private AlertDialog.Builder dialog;
	private ProgressBar pgb_receive;
	private ProgressBar pgb_wait;
	private static Handler mhandler;
	private static Handler mhandler2;
	private Thread downloadAttendListThread;
	@SuppressWarnings("unused")
	private Thread downloadAttendInfoThread;
	private Thread refresh_receivepage_Thread;
	private String[] attendlist_date;
	private String[] index;
	private String[] start_time;
	private String[] end_time;
	private String[] info_account_ID;
	private String[] info_attend_tag;
	private String[] info_attend_time;
	private String now_course_ID="null";
	private String now_attend_index;
	private int start_hour;
	private int start_min;
	private int start_sec;
	private int offset_delay_hours=0;
	private int offset_delay_min=0;
	private int delay_hours=0;
	private int delay_min=0;
	private int delay_sec=0;
	private int page_attend_tag=0;
	private int already_student_attend_count;
	private int attendtimerange=0;
	private Boolean download_info_isRunning=false;
	private String delay_sec_str;
	
	public String SecurityCode;
	public String now_Code;
	//RandomString random = new RandomString();
	
	//SecurityCode = random.randomString(4);
	//�ǿ����ҽX�ϥ�
	/*public String Get_Now_code(String now_Code){
		this.now_Code = now_Code;
		return now_Code;
	}*/
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		return inflater.inflate(R.layout.fragment_attend_teacherattend, container, false); 
	}
    
	@SuppressWarnings("static-access")
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			SQLiteDatabase SQLite_DB=getActivity().openOrCreateDatabase("flippedclass_database", getActivity().MODE_PRIVATE, null);
			Cursor cursor=SQLite_DB.rawQuery("SELECT * FROM attend", null);
			cursor.moveToFirst();
			page_attend_tag=cursor.getInt(0);
			Log.d("<==TeacherAttendFragment==>", "���\���oSQLite 'attend'��'tag' = "+page_attend_tag);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(page_attend_tag==1){
			initReceiveAttendPage();
			clock.setClickable(false);
		}
		
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.endAllThread();
	}
	@SuppressWarnings("static-access")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		dialog = new AlertDialog.Builder(getActivity());
		
		tv_start_time=(TextView)getView().findViewById(R.id.tv_attend_starttime);
		tv_end_time=(TextView)getView().findViewById(R.id.tv_attend_endtime);
		clock=(AnalogClock)getView().findViewById(R.id.analogClock1);
		btn_send_attend=(Button)getView().findViewById(R.id.btn_attend_startattend);
		pgb_receive=(ProgressBar)getView().findViewById(R.id.pgb_attend_teacherreceive);
		pgb_wait=(ProgressBar)getView().findViewById(R.id.pgb_attend_teacherwait);
		pgb_receive.setVisibility(View.GONE);
		pgb_wait.setVisibility(View.GONE);
		btn_send_attend.setOnClickListener(btn_attendstart_click);
		clock.setOnClickListener(clockclickListener);
		already_student_attend_count=0;
		mhandler=new Handler();
		mhandler2=new Handler();
//		Calendar cl=Calendar.getInstance();
		gnci=new GetNowCourseInfo(getActivity());
		now_course_ID=gnci.getNowCourseID();
		
		tv_start_time.setText("�ثe�ɶ�");
		offset_delay_hours=0;
		offset_delay_min=5;
		tv_end_time.setText("�ɶ�����i5�����j");
		try {
			SQLiteDatabase SQLite_DB=getActivity().openOrCreateDatabase("flippedclass_database", getActivity().MODE_PRIVATE, null);
			Cursor cursor=SQLite_DB.rawQuery("SELECT * FROM attend", null);
			cursor.moveToFirst();
			page_attend_tag=cursor.getInt(0);
			Log.d("<==TeacherAttendFragment==>", "���\���oSQLite 'attend'��'tag' = "+page_attend_tag);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(page_attend_tag==1){
			initReceiveAttendPage();
			clock.setClickable(false);
		}
		
		
	}
	OnClickListener clockclickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			startTimePickerDialog=new TimePickerDialog(getActivity(), startTimeSetListener ,0, 5, true);
			startTimePickerDialog.setTitle("�]�w�I�W�ɶ�����");
			//endTimePickerDialog=new  TimePickerDialog(getActivity(), endTimeSetListener , cl.get(Calendar.HOUR_OF_DAY), cl.get(Calendar.MINUTE), true);
			//endTimePickerDialog.setTitle("�]�w�����ɶ�");
			startTimePickerDialog.show();
			
			
		}
		
		private TimePickerDialog.OnTimeSetListener startTimeSetListener =  
                 new TimePickerDialog.OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						// TODO Auto-generated method stub
						offset_delay_hours=hourOfDay;
						offset_delay_min=minute;
						if(hourOfDay==0)
							tv_end_time.setText("�ɶ�����i"+minute+"�����j");
						else
							tv_end_time.setText("�ɶ�����i"+hourOfDay+"�p��"+minute+"�����j");
//						endTimePickerDialog.show();
					}
		};
		@SuppressWarnings("unused")
		private TimePickerDialog.OnTimeSetListener endTimeSetListener =  
                new TimePickerDialog.OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						// TODO Auto-generated method stub
						tv_end_time.setText("�����ɶ� : "+hourOfDay+":"+minute);
						
					}
		};
	};
	//���U�I�W
	OnClickListener btn_attendstart_click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			
			// TODO Auto-generated method stub
			if(!gnci.getNowCourseID().equals("null"))
			{
				initDialog();
				dialog.show();
			}
			else
				Toast.makeText(getActivity(), "�A�٨S����ܽҵ{...", Toast.LENGTH_LONG).show();
		}
	};
	//�ק�
	protected void initDialog() {
		// TODO Auto-generated method stub
		Calendar cl=Calendar.getInstance();
		start_hour=cl.get(Calendar.HOUR_OF_DAY);
		start_min=cl.get(Calendar.MINUTE);
		start_sec=cl.get(Calendar.SECOND);
		delay_hours=cl.get(Calendar.HOUR_OF_DAY)+offset_delay_hours;
		delay_min=cl.get(Calendar.MINUTE)+offset_delay_min;
		delay_sec=cl.get(Calendar.SECOND);
		
		//new
		RandomString random = new RandomString();
		SecurityCode = random.randomString(4);
		//now_Code = SecurityCode;
		//now_Code = SecurityCode;
		//Get_Now_code(now_Code);
		
		//new
		AttendStudentAttendFragment asaf = new AttendStudentAttendFragment();
		now_Code = SecurityCode;
		asaf.Get_Now_code(now_Code);
		
		
		delay_sec_str=String.valueOf(delay_sec);
		if(delay_min>=60)
		{
			delay_min-=60;
			delay_hours++;
		}
		if(delay_sec<10)
		{
			delay_sec_str="0"+delay_sec_str;
		}
		dialog.setTitle("�I�W��������");
		dialog.setMessage("�I�W�ɶ���"+cl.get(Calendar.HOUR_OF_DAY)+":"+cl.get(Calendar.MINUTE)+" �� "+delay_hours+":"+delay_min+"\n�o�e���Z�ši"+gnci.getNowCourseID()+"�j"+"\n�{�ҽX:�i"+now_Code+"�j");
		dialog.setPositiveButton("����", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                // ���UPositiveButton�n������  
                Toast.makeText(getActivity(), "�I�W����...", Toast.LENGTH_SHORT).show();
            }  
        }); 
		dialog.setNegativeButton("�T�w", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                // ���UPositiveButton�n������  
             DataFromDatabase db=new DataFromDatabase();
			 String course_ID=gnci.getNowCourseID();
			 String endtime=delay_hours+":"+delay_min+":"+delay_sec_str;
			 String starttime=start_hour+":"+start_min+":"+start_sec;
			 db.GCM_sendMessageToCourseMember(course_ID, "TAG1 : �Z�ši"+course_ID+"�j�}�l�I�W�F!");
			 db.FC_insertAttendList("attendlist_"+course_ID,course_ID,starttime,endtime);
             Toast.makeText(getActivity(), "�I�W�T���������\!!", Toast.LENGTH_SHORT).show();
             btn_send_attend.setEnabled(false);
             clock.setClickable(false);
             btn_send_attend.setText("�I�W��...");
             updateSQLite_AttendToReceive();  
             initReceiveAttendPage();
            }
        }); 
	}
	
	@SuppressWarnings("static-access")
	private void updateSQLite_AttendToReceive(){
		SQLiteDatabase SQLite_DB=getActivity().openOrCreateDatabase("flippedclass_database", getActivity().MODE_PRIVATE, null);
		ContentValues cv=new ContentValues();
		try {
			SQLite_DB.delete("attend", null, null);
			cv.put("attend_tag", 1);
			SQLite_DB.insert("attend", null, cv);
			Log.d("<==TeacherAttendFragment==>", "���\��JSQLite 'attend'��'tag' = 1(int)");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	@SuppressWarnings("static-access")
	private void updateSQLite_AttendToNormal(){
		SQLiteDatabase SQLite_DB=getActivity().openOrCreateDatabase("flippedclass_database", getActivity().MODE_PRIVATE, null);
		ContentValues cv=new ContentValues();
		try {
			SQLite_DB.delete("attend", null, null);
			cv.put("attend_tag", 0);
			SQLite_DB.insert("attend", null, cv);
			Log.d("<==TeacherAttendFragment==>", "���\��JSQLite 'attend'��'tag' = 0(int)");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private void initReceiveAttendPage(){
		tv_end_time.setText("�ثe���\ñ�쪺�H�� = Ū����...");
		btn_send_attend.setText("�I�W��...");
		btn_send_attend.setEnabled(false);
		pgb_wait.setVisibility(View.VISIBLE);
		downloadAttendInfoThread=new Thread(downloadAttendinfo_runnable);
		downloadAttendListThread=new Thread(downloadAttenList_runnable);
		downloadAttendListThread.start();
	}
	private Runnable downloadAttenList_runnable=new Runnable() {
		DataFromDatabase db=new DataFromDatabase();
		String table_name;
		@Override
		public void run() {
			now_course_ID=gnci.getNowCourseID();
			// TODO Auto-generated method stub
			table_name="attendlist_"+now_course_ID;
//			Log.d("<==TeacherAttendInfo==>", "table_name="+table_name);
			db.FC_getAttendList(table_name);
			String result=null;
			int times=0;
			try {
					Thread.sleep(1000);
					result=db.return_result;
					while(times<=10&&result==null){
					   Thread.sleep(1000);
					   result=db.return_result;
					   Log.d("<==TeacherAttendInfo==>", "downloadAttenList_runnable�h�𮧤F1��");	
					   times++;
				   }
					if(times<=10){
						Log.d("<==TeacherAttendInfo==>", "���\����I�W�M��"+result);
						JSONArray jsonArray;
						try {
								jsonArray = new JSONArray(result);
								
								index = new String[jsonArray.length()];
								attendlist_date = new String[jsonArray.length()];
								start_time = new String[jsonArray.length()];
								end_time = new String[jsonArray.length()];
								
								for(int i = 0 ; i<jsonArray.length() ; i++)
								{
									JSONObject jsonData = jsonArray.getJSONObject(i);
									index[i] = jsonData.getString("indax");
									attendlist_date[i] = jsonData.getString("date");
									start_time[i] = jsonData.getString("start_time");
									end_time[i] = jsonData.getString("end_time");
									//Log.d("=====>", "GroupFragment ��쪺Json = "+course_member[i]);
								}
								now_attend_index=index[0];
								try {
									updateSQLite_AttendToReceive();
								   mhandler.post(setViewRunnable);
				  				} catch (Exception e) {
				  					// TODO: handle exception
				  				}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}
	};
	private Runnable setViewRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			Log.d("<==TeacherAttendFragment==>", "�i�JsetViewRunnable");
			Log.d("<==TeacherAttendFragment==>", "�i�JsetViewRunnable");
			if(isOnLastAttendTime())
			{
				pgb_wait.setVisibility(View.GONE);
				pgb_receive.setMax(gnci.getAllMemberCount(now_course_ID));
				pgb_receive.setVisibility(View.VISIBLE);
				//tv_end_time.setText("�ثe���\ñ�쪺�H�� = 0/"+gnci.getAllMemberCount(now_course_ID));
				pgb_receive.setProgress(0);
				
				refresh_receivepage_Thread=new Thread(refresh_receivepage_runnable);
				download_info_isRunning=true;
				refresh_receivepage_Thread.start();
			}
			else
			{
				try {
					updateSQLite_AttendToNormal();
					pgb_receive.setVisibility(View.GONE);
					pgb_wait.setVisibility(View.GONE);
					offset_delay_hours=0;
					offset_delay_min=5;
					tv_end_time.setText("�ɶ�����i5�����j");
					btn_send_attend.setEnabled(true);
					btn_send_attend.setText("�ߧY�I�W");
					clock.setClickable(true);
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
		}
	};
	private Runnable downloadAttendinfo_runnable=new Runnable() {
		@Override
		public void run() {
			now_course_ID=gnci.getNowCourseID();
			// TODO Auto-generated method stub
//			Log.d("<==TeacherAttendInfo==>", "table_name="+table_name);
			
			
			
		}
	};
	private Runnable refresh_receivepage_runnable=new Runnable() {
		DataFromDatabase db2=new DataFromDatabase();
		String table_name;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			now_course_ID=gnci.getNowCourseID();
			table_name="attend_"+now_course_ID+"_"+now_attend_index;
			
			String result2=null;
			int times=0;
			try {
				 Thread.sleep(1500); //��1.5��
				   while(isOnLastAttendTime()&&download_info_isRunning==true){
					db2.FC_queryForAttendInfoList(table_name);
						try {
							Thread.sleep(1000);
							result2=db2.s_return_result;
							while(times<=10&&result2==null){
							   Thread.sleep(1000);
							   result2=db2.s_return_result;
							   Log.d("<==TeacherAttendInfo==>", "downloadAttendinfo_runnable�h�𮧤F1��");	
							   times++;
						    }
							if(times<=10){
//								Log.d("<==TeacherAttendInfo==>", "���\����I�WINFO�M��"+result2);
								already_student_attend_count=0;
								JSONArray jsonArray;
								try {
										jsonArray = new JSONArray(result2);
										
										info_account_ID = new String[jsonArray.length()];
										info_attend_tag = new String[jsonArray.length()];
										info_attend_time = new String[jsonArray.length()];
										
										for(int i = 0 ; i<jsonArray.length() ; i++)
										{
											JSONObject jsonData = jsonArray.getJSONObject(i);
											info_account_ID[i] = jsonData.getString("account_ID");
											info_attend_tag[i] = jsonData.getString("attend_tag");
											info_attend_time[i] = jsonData.getString("attend_time");
											
											if(info_attend_tag[i].equals("1"))
												already_student_attend_count++;
//											Log.d("=====>", "GroupFragment ��쪺Json = "+course_member[i]);
										}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								mhandler.post(refreshProgressRunnable);
								Thread.sleep(1500); //��3.5��
								}
							else
								Log.d("<==TeacherAttendInfo==>", "����I�WINFO�M�� ����..."+result2);
					}catch (Exception e) {
						// TODO: handle exception
					}
					}
					
					
				   
				
				//�I�W��������A�۰ʨ�^���`����
				if(download_info_isRunning){
				mhandler.removeCallbacks(refreshProgressRunnable);
				mhandler.removeCallbacks(setViewRunnable);
				mhandler2.post(setViewToNormalRunnable);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	private Runnable refreshProgressRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d("<==TeacherAttendFragment==>", "�i�JrefreshProgressRunnable");
			  tv_end_time.setText("�{�ҽX:�i"+now_Code+"�j"+"\n�Ѿl��� : "+attendtimerange+"\n�ثe���\ñ�쪺�H�� = "+already_student_attend_count+"/"+gnci.getAllMemberCount(now_course_ID));
			  pgb_receive.setProgress(already_student_attend_count);
		}
	};
	private Runnable setViewToNormalRunnable= new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			downloadAttendInfoThread.interrupt();
//			downloadAttendListThread.interrupt();
			Log.d("<==TeacherAttendFragment==>", "�i�JsetViewToNormalRunnable");
			Toast.makeText(getActivity(), "�I�W����!!!", Toast.LENGTH_LONG).show();
			updateSQLite_AttendToNormal();
			pgb_receive.setVisibility(View.GONE);
			pgb_wait.setVisibility(View.GONE);
			offset_delay_hours=0;
			offset_delay_min=5;
			tv_end_time.setText("�ɶ�����i5�����j");
			btn_send_attend.setEnabled(true);
			btn_send_attend.setText("�ߧY�I�W");
			clock.setClickable(true);
			dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle("�I�W���G");
			dialog.setNegativeButton("����", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			dialog.setMessage("ñ�챡�p :�@"+already_student_attend_count+"/"+gnci.getAllMemberCount(now_course_ID));
			dialog.show();
		}
	};
	private Boolean isOnLastAttendTime()
	{
//		Calendar cl=Calendar.getInstance();
		String[] start_time_arrray=start_time[0].split(":");
		String[] end_time_arrray=end_time[0].split(":");
//		Log.d("=====>", "isOnLastAttendTime��ARRAY = "+start_time_arrray[0]+"="+start_time_arrray[1]);
		int start_hours=Integer.valueOf(start_time_arrray[0]);
		int start_min=Integer.valueOf(start_time_arrray[1]);
		int start_sec=Integer.valueOf(start_time_arrray[2]);
		int starttimecount=start_hours*3600+start_min*60+start_sec;
		
		int end_hours=Integer.valueOf(end_time_arrray[0]);
		int end_min=Integer.valueOf(end_time_arrray[1]);
		int end_sec=Integer.valueOf(end_time_arrray[2]);
		int endtimecount=end_hours*3600+end_min*60+end_sec;
		Calendar cl=Calendar.getInstance();
		int now_hours=cl.get(Calendar.HOUR_OF_DAY);
		int now_min=cl.get(Calendar.MINUTE);
		int now_sec=cl.get(Calendar.SECOND);
		int nowtimecount=now_hours*3600+now_min*60+now_sec;
		
		attendtimerange=endtimecount-nowtimecount;
//		attend_time=now_hours+":"+now_min+":"+now_sec;
			
		
		if((nowtimecount>endtimecount)||(nowtimecount<starttimecount))
			return false;
		else
			return true;
	}
	public void endAllThread(){
		
		 mhandler.removeCallbacks(downloadAttenList_runnable);
		 mhandler.removeCallbacks(downloadAttendinfo_runnable);
		 mhandler.removeCallbacks(refreshProgressRunnable);
		 mhandler.removeCallbacks(refresh_receivepage_runnable);
		 mhandler.removeCallbacks(setViewToNormalRunnable);
		 Log.d("<==AttendTeacherAttend==>", "handler and thread have kill");
		 download_info_isRunning=false;
//		 refresh_receivepage_Thread.interrupt();
//		 downloadAttendListThread.interrupt();
	}
}