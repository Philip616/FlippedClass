package com.mcucsie.flippedclass.group;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.R;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.drawable.BitmapDrawable;

public class PersonInfoPopupWindow{
	
	private PopupWindow myPopUpWindow;
	private int scaleWidth,scaleHeight;
	private String Account;
	private LayoutInflater localinflater;
	private View popupView;
	private Context context;
	private String[] data;
    public Handler mHandler;

	
	@SuppressWarnings("deprecation")
	public PersonInfoPopupWindow(Context context,String Account){
		this.context=context;
		this.localinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.scaleWidth=getScaleWidth();
		this.scaleHeight=getScaleHeight();
		this.popupView = localinflater.inflate(R.layout.popupwindow_personinfo, null);
		this.myPopUpWindow = new PopupWindow(popupView,scaleWidth, scaleHeight, true);
		this.myPopUpWindow.setTouchable(true);
//		this.myPopUpWindow.setFocusable(false);
		this.myPopUpWindow.setOutsideTouchable(true);
		this.myPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
		this.Account=Account;
		this.mHandler=new Handler();
		findallview();
	}
	private void findallview() {
		// TODO Auto-generated method stub
		Button btn_close=(Button)this.myPopUpWindow.getContentView().findViewById(R.id.btn_personinfo_close);
	
		btn_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myPopUpWindow.dismiss();
			}
		});
		Thread mThread=new Thread(QueryAccountRunnable);
		mThread.start();
		/*db_group = context.openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
		String str_selectTable="SELECT * FROM GroupMemberInfo WHERE name='"+Account+"'";
		cursor=db_group.rawQuery(str_selectTable, null);
		cursor.moveToFirst();
		tv_name.setText("Name : "+cursor.getString(0));
		tv_grade.setText("Grade : "+cursor.getString(1));
		tv_otherinfo.setText("OtherInfo");
		db_group.close();*/
		
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
		myPopUpWindow.update();
		//myPopUpWindow.dismiss();
	}
	public  void showAtCenter(View v){
		//myPopUpWindow.showAsDropDown(v,0,0);
		int Yoffset=0;
		Yoffset=v.getHeight()/5+0;
		myPopUpWindow.showAtLocation(v, Gravity.TOP, 0, Yoffset);
		myPopUpWindow.update();
	}
	public  void showAtDownDrop(View v){
		//myPopUpWindow.showAsDropDown(v,0,0);
		myPopUpWindow.showAsDropDown(v);
		myPopUpWindow.update();
	}
	public int getScaleWidth() {
		// TODO Auto-generated method stub
        double dScaleWidth=0.55*this.getScreenWidthPixels();
        scaleWidth=(int) dScaleWidth;
		return scaleWidth;
	}
	public int getScaleHeight() {
		// TODO Auto-generated method stub
        double dScaleHeight=0.45*this.getScreenHeightPixels();
        scaleHeight=(int) dScaleHeight;
		return scaleHeight;
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
    Runnable QueryAccountRunnable=new Runnable() {
    	private DataFromDatabase testdd=new DataFromDatabase();
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			testdd.FC_getStdInfo(Account);
//			Log.d("=====>", "測試CLASS ARRAY 執行開始ˋ!");
			Log.d("=====>", "Runable Download成員個人資訊ID="+Account);
			// TODO Auto-generated method stub
			String result=null;
			int times=0;
			try {
					Thread.sleep(1000);
					result=testdd.return_result;
					while(times<=10&&result==null){
					   Thread.sleep(1000);
					   result=testdd.return_result;
					   Log.d("=====>", "ThreadDownload的多休息了1秒");	
					   times++;
				   }
					if(times<=10){
						Log.d("=====>", "成功抓取課程成員資訊"+result);
						data = result.split(":");
						
						mHandler.post(initAllview);
					}
					else
					{
						Log.d("=====>", "ThreadDownload下載太多次，下載失敗..."+times);
					}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
	 Runnable initAllview=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			TextView tv_name=(TextView)myPopUpWindow.getContentView().findViewById(R.id.tv_personinfo_name);
			TextView tv_grade=(TextView)myPopUpWindow.getContentView().findViewById(R.id.tv_personinfo_grade);
			TextView tv_otherinfo=(TextView)myPopUpWindow.getContentView().findViewById(R.id.tv_personinfo_other);
			ProgressBar pgb=(ProgressBar)myPopUpWindow.getContentView().findViewById(R.id.pgb_groupinfo_mypopup);
			ImageView head = (ImageView)myPopUpWindow.getContentView().findViewById(R.id.imageView2);
			head.setImageResource(R.drawable.headexample);
			
			pgb.setVisibility(View.GONE);
			tv_name.setText("姓名 : "+data[1]);
			tv_grade.setText("系所 : "+data[2]);
			tv_otherinfo.setText("Line : "+data[6]);
		}
	};
}
