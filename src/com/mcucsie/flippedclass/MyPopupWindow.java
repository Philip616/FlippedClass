package com.mcucsie.flippedclass;
import android.R.color;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;

public class MyPopupWindow{
	
	private PopupWindow myPopUpWindow;
	private int scaleWidth;
	private int layoutID;
	private String Account;
	private LayoutInflater localinflater;
	private View v;
	private CountDownTimer c;
	private Boolean attendence=true;
	private View popupView;
	public Context context;
	private ListView lv;
	private ProgressBar pgb;
	private int nowcoursecount=0;
	final private  Handler mhandler;
	protected String[] function_str = {"點名", "考試", "速貼", "分組", "課後"};
	OnDismissListener listener;
	public MyPopupWindow(int layoutID,final Context context,String Account){
		this.layoutID=layoutID;
		this.context=context;
		this.localinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.scaleWidth=getScaleWidth();
		this.popupView = localinflater.inflate(this.layoutID, null);
		this.myPopUpWindow = new PopupWindow(popupView,scaleWidth, LayoutParams.WRAP_CONTENT, true);
		this.myPopUpWindow.setTouchable(true);
		this.myPopUpWindow.setOutsideTouchable(true);
		this.myPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
		this.Account=Account;
		this.mhandler=new Handler();
		if(this.layoutID==R.layout.popupwindow_choosecourse_layout)
			setListViewAdapter();
		if(this.layoutID==R.layout.popupwindow_attendpop_layout)
			setAttendView();
		
		new Thread(new Runnable(){
			SQLiteDatabase flipDB=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
			Cursor cursor=flipDB.rawQuery("SELECT course_name FROM course_information", null);
			int times=0;
            @Override
            public void run() {
                // TODO Auto-generated method stub
            	
                try {
                	   while(cursor.getCount()==0&&times<=10){
                		Thread.sleep(1000);//這邊可以做你想預先載入的資料
                		Log.d("=====>", "MypopupWindow Progress 多等了1秒");
                		cursor=flipDB.rawQuery("SELECT course_name FROM course_information", null);
                		times++;
                	   }
                	   flipDB.close();
                	   
                	   mhandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							setListViewAdapter();
							pgb.setVisibility(View.GONE);
						}
					});
                	   
                		
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        }).start();
		
	}
	public interface onDismissListener{

	    public void onDismiss();

	}
	
	public void setOnDismissListener(OnDismissListener listener){
	    this.listener = listener;
	    this.myPopUpWindow.setOnDismissListener(listener);
	}
	public View getPopupView() {
		// TODO Auto-generated method stub
        return this.popupView;
	}
	public  void show(View v){
		//myPopUpWindow.showAsDropDown(v,0,0);
		
		int Yoffset=0;
		Yoffset=v.getHeight()+v.getHeight()/2+5;
		myPopUpWindow.showAtLocation(v, Gravity.TOP, 0, Yoffset);
	}
	public  void showAtCenter(View v){
		//myPopUpWindow.showAsDropDown(v,0,0);
		int Yoffset=0;
		Yoffset=v.getHeight()/5+0;
		myPopUpWindow.showAtLocation(v, Gravity.TOP, 0, Yoffset);
	}
	public int getScaleWidth() {
		// TODO Auto-generated method stub
        double dScaleWidth=0.75*this.getScreenWidthPixels();
        scaleWidth=(int) dScaleWidth;
		return scaleWidth;
	}
	/*public void catchActionBarView(){
		this.v=getActionBarView();
	}*/
	public int getScreenWidthPixels(){
		int Pixels=123;
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager=(WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		Pixels=dm.widthPixels;
		return Pixels;
	}
	public int getScreenHeightPixels(){
		int Pixels=123;
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager=(WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		Pixels=dm.heightPixels;
		return Pixels;
	}
	public int getActionBarViewID() {
		
	    int actionViewResId = 0;
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
	        actionViewResId = this.context.getResources().getIdentifier(
	                "abs__action_bar_container", "id", this.context.getPackageName());
	    } else {
	        actionViewResId = Resources.getSystem().getIdentifier(
	                "action_bar_container", "id", "android");
	    }
	    if (actionViewResId > 0) {
	        /*return ((Activity) this.context).findViewById(actionViewResId);*/
	    	return actionViewResId;
	    }

	    return actionViewResId;
	}
	public Boolean isListdata_exsits(){
//		   SQLiteDatabase flipDB=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
//		   Cursor cursor=flipDB.rawQuery("SELECT course_name FROM course_information", null);
		   if(nowcoursecount==0)
			   return false;
		   else
			   return true;
	}
	public int getListViewCount(){
			   return lv.getCount();
	}
	   public String[] getStudentCourseList(){

//		   String str_selectcoursename="SELECT CI.name FROM 'Course_Information' AS CI,'User_CourseList' AS UC WHERE CI.ID = UC.course_ID AND UC.ID="+"'"+this.Account+"'"; 
//		   SQLiteDatabase FC_database=this.context.openOrCreateDatabase("FlippedClass_DataBase", this.context.MODE_PRIVATE, null);
		   SQLiteDatabase flipDB=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
		   Cursor cursor=flipDB.rawQuery("SELECT course_name FROM course_information", null);
		   
		   Log.d("=====>", "MypopupWindow getCourseCount = "+ cursor.getCount());
		   nowcoursecount=cursor.getCount();
		   String [] user_data = new String[cursor.getCount()];
		   cursor.moveToFirst();
		   for(int j=0;j<cursor.getCount();j++)
			 {
				 user_data[j] = cursor.getString(0);	
				 cursor.moveToNext();
			 }
		   flipDB.close();
		   return user_data;
	   }
	   public void setListViewAdapter(){
		   pgb=(ProgressBar)this.myPopUpWindow.getContentView().findViewById(R.id.pgb_popupwindow_loading);
		   lv=(ListView) this.myPopUpWindow.getContentView().findViewById(R.id.lv_popupwindow);
			Log.d("=====>", "MypopupWindow get ListView success");
	        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.context, R.layout.listview_popupwindow_item_1, getStudentCourseList());
	        Log.d("=====>", "MypopupWindow ArrayAdapter define success");
	        lv.setAdapter(adapter);
			Log.d("=====>", "MypopupWindow ListView Set String[] success");
			lv.setOnItemClickListener(listitem_click);
	        View lv=this.myPopUpWindow.getContentView().findViewById(R.id.popupwindow_linearlayout);
	       // lv.getBackground().setAlpha(150);
	       // Log.d("=====>", "MypopupWindow ListView Set BackGround Alpha success");
	   }
	   public void setAttendView(){
		   
		   Button btn_check=(Button)this.myPopUpWindow.getContentView().findViewById(R.id.btn_attendpop_check);
		   Button btn_cancel=(Button)this.myPopUpWindow.getContentView().findViewById(R.id.btn_attendpop_cancel);
		   final TextView tv_timer=(TextView)this.myPopUpWindow.getContentView().findViewById(R.id.tv_attendpop_cooldowntime);
		   final ProgressBar pgb_timerbar=(ProgressBar)this.myPopUpWindow.getContentView().findViewById(R.id.pgb_attendpop_timerbar);
		   Log.d("=====>", "MypopupWindow View Set success");
           tv_timer.setText("我抓到了!");
           pgb_timerbar.setMax(100);
           pgb_timerbar.setProgress(0);
           btn_check.setOnClickListener(btn_check_click);
           btn_cancel.setOnClickListener(btn_cancel_click);
           c=new CountDownTimer(10000,100){
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
                 tv_timer.setText("seconds remaining:"+millisUntilFinished/1000);
                 pgb_timerbar.setProgress(100-(int) (millisUntilFinished/100));
			}
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				tv_timer.setText("Done...");
				pgb_timerbar.setProgress(100);
				Toast.makeText(context, "你沒有在時間內簽到...", Toast.LENGTH_LONG).show();
				setAttendence(false);
				SQL_insertAttendence();
				myPopUpWindow.dismiss();
			}
           }.start();
	   }
	   private OnClickListener btn_check_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setAttendence(true);
			SQL_insertAttendence();
			Toast.makeText(context, "你在時間內簽到成功!\n輸入進資料庫!", Toast.LENGTH_LONG).show();
		    myPopUpWindow.dismiss();
		}
	};
	private OnClickListener btn_cancel_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setAttendence(false);
		    SQL_insertAttendence();
		    Toast.makeText(context, "你取消了簽到...", Toast.LENGTH_LONG).show();
		    myPopUpWindow.dismiss();
		}
	};
	   public void setAttendence(Boolean tag){
		   this.attendence=tag;
	   }
	   public Boolean getAttendence(){
		   return this.attendence;
	   }
	   private void SQL_insertAttendence() {
			// TODO Auto-generated method stub
		   SQLiteDatabase FC_database=this.context.openOrCreateDatabase("FlippedClass_DataBase", this.context.MODE_PRIVATE, null);
		  
		   ContentValues insertCV_StdAttend=new ContentValues();
		   insertCV_StdAttend.put("course_ID", 36101);
		   insertCV_StdAttend.put("student_ID", this.Account);
		   insertCV_StdAttend.put("attendence",this.getAttendence());
		   insertCV_StdAttend.put("week", 2);
		   insertCV_StdAttend.put("time", "2014-07-21");
			
	    	FC_database.insert("Course_Attendence", null, insertCV_StdAttend);
	    	FC_database.close();
	    	c.cancel();
		}
	private OnItemClickListener listitem_click=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			try {
				TextView tt=(TextView)view;
				String course_name=tt.getText().toString();
//				String data[][]=new String[1][5];
				
//				DataFromDatabase mydb=new DataFromDatabase("name='"+course_name+"'", "FlippedClass_DataBase", "Course_Information", context);
//				data=mydb.getData();
				SQLiteDatabase flipDB=context.openOrCreateDatabase("flippedclass_database", context.MODE_PRIVATE, null);
				Cursor my_cursor=flipDB.rawQuery("SELECT course_ID FROM course_information WHERE course_name='"+course_name+"'", null);
				my_cursor.moveToFirst();
				
				SQLiteDatabase tDB=context.openOrCreateDatabase("FlippedClass_Temporal", context.MODE_PRIVATE, null);
				Cursor cursor=tDB.rawQuery("SELECT * FROM now_course", null);
				if(cursor.getCount()!=0)
				{
					tDB.delete("now_course", null, null);
					Log.d("=====>", "MypopupWindow 暫存資料庫測試DELETE舊資料(now_course)");
				}
				ContentValues insertCV_nowcoursetable=new ContentValues();
				insertCV_nowcoursetable.put("course_ID", my_cursor.getString(0));
				insertCV_nowcoursetable.put("course_name", course_name);
				tDB.insert("now_course", null, insertCV_nowcoursetable);
				tDB.close();
				flipDB.close();
				Log.d("=====>", "MypopupWindow 點擊的課程是 = "+tt.getText().toString()+" 代碼="+my_cursor.getString(0));
	
			} catch (Exception e) {
				// TODO: handle exception
				Log.d("=====>", "Error在MypopupWindow的ItemClick"+e.getMessage().toString());
			}
			myPopUpWindow.dismiss();
		}
	};  
}

