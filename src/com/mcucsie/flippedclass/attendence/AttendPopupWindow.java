package com.mcucsie.flippedclass.attendence;
import com.mcucsie.flippedclass.R;

import android.app.ActionBar.LayoutParams;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;

public class AttendPopupWindow{
	
	private PopupWindow myPopUpWindow;
	private int scaleWidth;
	private int layoutID;
	private String Account;
	private LayoutInflater localinflater;
	private CountDownTimer c;
	private Boolean attendence=true;
	private View popupView;
	public Context context;
	protected String[] function_str = {"點名", "考試", "速貼", "分組", "課後"};
	OnDismissListener listener;
	@SuppressWarnings("deprecation")
	public AttendPopupWindow(int layoutID,final Context context,String Account){
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
		if(this.layoutID==R.layout.popupwindow_attendpop_layout)
		setAttendView();
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
//				SQL_insertAttendence();
				myPopUpWindow.dismiss();
			}
           }.start();
	   }
	   private OnClickListener btn_check_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setAttendence(true);
//			SQL_insertAttendence();
			Toast.makeText(context, "你在時間內簽到成功!\n輸入進資料庫!", Toast.LENGTH_LONG).show();
		    myPopUpWindow.dismiss();
		}
	};
	private OnClickListener btn_cancel_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setAttendence(false);
//		    SQL_insertAttendence();
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
	   @SuppressWarnings({ "unused", "static-access" })
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
}

