package com.mcucsie.flippedclass.remind;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.MainActivity;
import com.mcucsie.flippedclass.R;
import com.mcucsie.flippedclass.R.id;
import com.mcucsie.flippedclass.R.layout;
import com.mcucsie.flippedclass.R.menu;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Remind_teacherupload extends Fragment {

	private Button mButton,btn_clear,btn_cts,btn_file;
	private TextView tv_title;
	private TextView mText1;
	private TextView mText2;
	private EditText mText3;
	private EditText et_title; 
	
	private String course_ID;

	private String uploadFile = "/mnt/sdcard/testimg.jpg";
	private String srcPath = "/mnt/sdcard/testimg.jpg";
	//private String actionUrl = "http://10.0.2.2/rec.php";
	private String actionUrl = "";
	private String filename="";
	String course_name;
	
	private String[] name;
	private ListView listView;
	private ListView popfile_lv;
	private List<String> items = null;
	private List<String> paths = null;
	private String RootPath = "/mnt/sdcard/FlippedClass_download/";
	private String curPath = "/";
	private TextView mPath;
	private File file;
	private String selectPath;
	private File[] files;
	private PopupWindow popupWindow;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		

		return inflater.inflate(R.layout.activity_remind_teacherupload, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		DataFromDatabase getPath = new DataFromDatabase();
		actionUrl = getPath.actionUrl;
		
		tv_title = (TextView) getView().findViewById(R.id.tv_t2_course);
		mText1 = (TextView) getView().findViewById(R.id.myText1);
		mText2 = (TextView)getView(). findViewById(R.id.myText2);
		mText1.setText("來源:"+uploadFile);
		mText2.setText("目的:"+actionUrl);
		
		mButton = (Button) getView().findViewById(R.id.myButton);
		mButton.setOnClickListener(mButton_Click);		
		btn_clear = (Button) getView().findViewById(R.id.btn_clear);
		btn_clear.setOnClickListener(btn_clear_Click);
		btn_file=(Button)getView().findViewById(R.id.download_btn_1_1);
		btn_file.setOnClickListener(btn_file_Click);
	
		GetNowCourseInfo course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
 
		course_name = course_info.getNowCourseName();

		init_Remind_Teacherupload();
	
		//----------------
	}
	private OnClickListener btn_file_Click= new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			
			initPopWindow();      
		}};
		
		
		private void initPopWindow(){  
	         
	        //View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popfile, null);  
			View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_remind_teacherupload, null);  
	        contentView.setBackgroundColor(Color.WHITE);  
	          
	        popupWindow = new PopupWindow(getView().findViewById(R.id.filepath), 200, 200);  
	        popupWindow.setContentView(contentView);  
	        popfile_lv = (ListView)contentView.findViewById(R.id.popfile_lv);
	 		listView = (ListView) contentView.findViewById(R.id.popfile_lv);  
	        DirOpen(RootPath);  
	        
	        // 點外面消失
	        popupWindow.setBackgroundDrawable(new BitmapDrawable());  
			// 點窗外消失  
	        popupWindow.setOutsideTouchable(true);  
			// 設為可點擊
	        popupWindow.setFocusable(true);  
	        popupWindow.showAsDropDown(btn_file);  
	 }  
	    
	 private void DirOpen(String Path )  
	 {  

	 		File file  = new File(Path);  		
			// 如果SD卡目錄不存在創建
			if (!file.exists()) {
				file.mkdir();
			}

	 		files = file.listFiles();  
	 		name = new String[files.length];  
	 		if(!Path.equals(RootPath)){
	 			name = new String[files.length+1];
	 			name[files.length] = "返回根目錄";
	 			//name[files.length+1] = "返回根目錄";
	 		}
	 		for(int i=0;i<files.length;i++){  
	 			name[i]=files[i].getName();  
	 			System.out.println(name[i]);  
	 		}

	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, name);  
	        listView.setAdapter(adapter);  
	        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					//Toast.makeText(MainActivity.this,"test", Toast.LENGTH_SHORT).show();
					if(position == files.length){
							DirOpen(RootPath);
					}
					else{
						if(files[position].isDirectory()){					
							DirOpen(files[position].getPath());
						}
						else{
							//TextView tv;
							//tv = (TextView)findViewById(R.id.fileText);
							//tv.setText(files[position].getPath());
							filename = files[position].getName();
							uploadFile = files[position].getPath();
							mText1.setText("來源:"+files[position].getPath());
							popupWindow.dismiss();
						}
					}	
				}
			});
	 }
	private OnClickListener mButton_Click= new OnClickListener(){
		@Override
		public void onClick(View v) {
			//myThread th = new myThread( actionUrl, srcPath);
			//th.start();
			myAsyncTask task = new myAsyncTask(getActivity(),uploadFile);
			task.execute();
			
			//SQLiteDatabase db =openOrCreateDatabase("FlippedClass_DataBase",android.content.Context.MODE_PRIVATE ,null);
			String note,title;
			mText3 =(EditText)getView().findViewById(R.id.ed_tq);
            note = mText3.getText().toString();
            et_title = (EditText)getView().findViewById(R.id.et_title);
            title = et_title.getText().toString();

            //先行定義時間格式
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            //取得現在時間
            Date dt=new Date();
            //透過SimpleDateFormat的format方法將Date轉為字串
            String dts=sdf.format(dt);
            
			DataFromDatabase MySQL_DB_mButton = new DataFromDatabase();
			MySQL_DB_mButton.Remind_StdInput(course_ID,title,dts,filename,note);
	    	Toast.makeText(v.getContext(), "上傳完畢", Toast.LENGTH_LONG).show();
	    	MySQL_DB_mButton.GCM_sendMessageToCourseMember(course_ID,"TAG3: "+course_ID+"新增了提醒!!");
		}}; 
  
		private OnClickListener btn_clear_Click= new OnClickListener(){
		@Override
		public void onClick(View v) {
				mText3 = (EditText) getView().findViewById(R.id.ed_tq);
				mText3.setText("");
				//Toast.makeText(v.getContext(), course_ID, Toast.LENGTH_LONG).show();
		}};
		
		
		public void init_Remind_Teacherupload(){
			//tv_title.setText("─ "+"請選擇課程"+"的訊息列表 ─\n");
			GetNowCourseInfo course_info;
			course_info = new GetNowCourseInfo(getActivity());
			String course_ID_1 = course_info.getNowCourseID();		
			course_name = course_info.getNowCourseName();
			if(!course_ID_1.equals("null")){
				tv_title.setText(course_ID_1+"  "+course_name);	
			}
			
			course_ID = course_ID_1;

		}  
	    
		
		
}
