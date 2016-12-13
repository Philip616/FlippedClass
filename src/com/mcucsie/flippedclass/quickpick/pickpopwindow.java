package com.mcucsie.flippedclass.quickpick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;
import com.mcucsie.flippedclass.quickpick.QuicktextFragment.getdata;

public class pickpopwindow {
	private PopupWindow pw;
	private int scaleheight;
	private int scaleWidth;
	private String Account;
	private String picID;
	private Context context;
	private View view;
	private Button btn_pic1,btn_pic2,btn_pic3,btn_pic4;
	private byte check_ID;
	private int count_a;
	
	OnDismissListener listener;
	
	public pickpopwindow(Context context,String Account,View v,byte check_ID){
		this.context=context;
		this.Account = Account;
		this.check_ID = check_ID;
		this.count_a = count_a;
		picID=null;
		
		scaleheight=getHeight();
		scaleWidth=getWidth();
		
		
		LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pw = new PopupWindow(inflater.inflate(R.layout.fragment_quickpick_quickpicture,null)
			,scaleWidth,scaleheight, true);
		
		// 點外面消失
		pw.setBackgroundDrawable(new BitmapDrawable());  
		// 點窗外消失  
		pw.setOutsideTouchable(true);  
		// 設為可點擊
		pw.setFocusable(true);  
		
		setListener();
		
		// 顯示窗戶
		pw.showAsDropDown(v);
		
		
		//btn_pic2.setOnClickListener(btn_pic2_click);
		//btn_pic3.setOnClickListener(btn_pic3_click);
		//btn_pic4.setOnClickListener(btn_pic4_click);
		
	}
	public void setListener(){
		btn_pic1=(Button)this.pw.getContentView().findViewById(R.id.btn_quickpicture1);
		btn_pic2=(Button)this.pw.getContentView().findViewById(R.id.btn_quickpicture2);
		btn_pic3=(Button)this.pw.getContentView().findViewById(R.id.btn_quickpicture3);
		btn_pic4=(Button)this.pw.getContentView().findViewById(R.id.btn_quickpicture4);
		
		btn_pic1.setOnClickListener(btn_pic1_click);
		btn_pic2.setOnClickListener(btn_pic2_click);
		btn_pic3.setOnClickListener(btn_pic3_click);
		btn_pic4.setOnClickListener(btn_pic4_click);
	}
			
	private int getHeight() {
		// TODO Auto-generated method stub
	    double dScaleWidth=0.5*this.getWidth();
		return (int) dScaleWidth/2;
	}

	private int getWidth(){
		int Pixels=123;
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager=(WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		Pixels=dm.widthPixels;
		return Pixels;
	}
	
	
	private OnClickListener btn_pic1_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Toast.makeText(context, "1", Toast.LENGTH_LONG).show();
			send("#000");
		}
	};
		

	private OnClickListener btn_pic2_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Toast.makeText(v.getContext(), "你傳送了速貼圖片 : 2", Toast.LENGTH_LONG).show();
			send("#001");
		}
	};
	private OnClickListener btn_pic3_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Toast.makeText(v.getContext(), "你傳送了速貼圖片 : 3", Toast.LENGTH_LONG).show();
			send("#002");
		}
	};
	private OnClickListener btn_pic4_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Toast.makeText(v.getContext(), "你傳送了速貼圖片 : 4", Toast.LENGTH_LONG).show();
			send("#003");
		}
	};
	private void send(String picID){

		GetNowCourseInfo course_info = new GetNowCourseInfo(context);
		String course_ID = course_info.getNowCourseID();
		GetNowAccountInfo gnai = new GetNowAccountInfo(context);
		String Type = gnai.getNowAccountType();
		
		if(course_ID.equals("null")){
			Toast.makeText(context, "尚未選課", Toast.LENGTH_LONG).show();
			picID=null;
		}
		else
		{
			Toast.makeText(context, "傳送成功  ", Toast.LENGTH_LONG).show();
			//getCourseInfo(course_ID);
			if(picID.equals(null)){
				this.picID = null;			
			}
			else{
				this.picID = picID;
			}
		}		
		Log.d("===>", "pickpopwindow ok!!");	
		pw.dismiss();
	}
	
	public String sendback(){	
		return picID;
	}
	
	public void setOnDismissListener(OnDismissListener listener){
	    this.listener = listener;
	    this.pw.setOnDismissListener(listener);
	}
	
}
