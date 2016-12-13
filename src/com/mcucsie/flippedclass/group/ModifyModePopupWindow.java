package com.mcucsie.flippedclass.group;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.res.Resources;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.mcucsie.flippedclass.R;

public class ModifyModePopupWindow {
//	private Cursor cursor;
//	private SQLiteDatabase db_group;
	private PopupWindow myPopUpWindow;
	private int scaleWidth,scaleHeight;
//	private String Account;
	private LayoutInflater localinflater;
	private View popupView;
	private Context context;
	private int changetag=0;
	OnDismissListener listener;

	
	public ModifyModePopupWindow(Context context){
		this.context=context;
		this.localinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.scaleWidth=getScaleWidth();
		this.scaleHeight=getScaleHeight();
		this.popupView = localinflater.inflate(R.layout.popupwindow_modifymode, null);
		this.myPopUpWindow = new PopupWindow(popupView,LayoutParams.MATCH_PARENT, scaleHeight, true);
		this.myPopUpWindow.setTouchable(true);
		this.myPopUpWindow.setFocusable(false);
		this.myPopUpWindow.setOutsideTouchable(true);
//		this.myPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
//		this.Account=Account;
		findallview();
	}
	private void findallview() {
		// TODO Auto-generated method stub
     	Button btn_save =(Button)this.myPopUpWindow.getContentView().findViewById(R.id.btn_modify_save);
     	Button btn_cancel =(Button)this.myPopUpWindow.getContentView().findViewById(R.id.btn_modify_cancel);
     	
     	btn_save.setText("¥æ´«");
     	btn_cancel.setText("¦sÀÉ");
     	btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changetag=1;
				myPopUpWindow.dismiss();
			}
		});
     	btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			   changetag=0;
			   myPopUpWindow.dismiss();
			}
		});
     	this.changetag=99;
	}
	public int getChangetag(){
		
		return this.changetag;
	}
	public interface onDismissListener{

	    public void onDismiss();

	}
	public void setChangetag(int i){
		this.changetag=i;
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
		myPopUpWindow.update();
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
	public  void showAtBottom(View v){
		//myPopUpWindow.showAsDropDown(v,0,0);
		myPopUpWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		myPopUpWindow.update();
	}
	public void dismiss(){
		this.myPopUpWindow.dismiss();
	}
	public int getScaleWidth() {
		// TODO Auto-generated method stub
        double dScaleWidth=0.55*this.getScreenWidthPixels();
        scaleWidth=(int) dScaleWidth;
		return scaleWidth;
	}
	public int getScaleHeight() {
		// TODO Auto-generated method stub
        double dScaleHeight=0.13*this.getScreenHeightPixels();
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
}
