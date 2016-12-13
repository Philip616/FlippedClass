package com.mcucsie.flippedclass.setting;

import java.util.ArrayList;
import java.util.HashMap;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.MainActivity;
import com.mcucsie.flippedclass.R;
import com.mcucsie.flippedclass.group.GroupDiscussAdapter.ViewTag;
import com.mcucsie.flippedclass.setting.SettingTeacher.WaitForInsertCourse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SettingStdProfileAdapter extends BaseAdapter {

	private LayoutInflater myInflater = null;
	private ArrayList<HashMap<String, String>> list = null;
	private ViewTag viewTag;
	private Context context;
	private AlertDialog.Builder Dialog;
	private String[] profile_title = {"姓名","性別","系級","年級","信箱","LINE"};
	private String[] list_name = {"name","gender","department","grade","mail_address","LINE_ID"};
	AlertDialog.Builder dialog;
	
	Fragment getFragmentManager;
	GetNowAccountInfo gnai;
	String  account_ID;

	
	public SettingStdProfileAdapter(Context context, ArrayList<HashMap<String, String>> list) {
	    this.context = context;
	    gnai = new GetNowAccountInfo(context);
	    account_ID = gnai.getNowAccountID();
	    if(context != null)
	    this.myInflater = LayoutInflater.from(context);
	    this.list = list;
	    dialog = new AlertDialog.Builder(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if (convertView == null) {
	        // 取得listItem容器 view
	        convertView = myInflater.inflate(R.layout.listview_setting, null);
	 
	        // 建構listItem內容view
	        viewTag = new ViewTag(
	                (TextView) convertView.findViewById(R.id.profile_title),
	                (TextView) convertView.findViewById(R.id.profile_content),
	        		(Button) convertView.findViewById(R.id.modify_profile));

	        
	        // 設置容器內容
	        convertView.setTag(viewTag);
	 
	    } else {
	        viewTag = (ViewTag) convertView.getTag();
	    }
	
		
		viewTag.text1.setText(profile_title[position]);
		viewTag.text2.setText(list.get(position).get(list_name[position]));
		viewTag.btn1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initDialog(position);
				dialog.show();
			}
			
		});

		
		return convertView;
	}
	
	public class ViewTag {
	    TextView text1;
	    TextView text2;
	    Button btn1;
	     
	    public ViewTag(TextView textview1, TextView textview2, Button button1) {
	        this.text1 = textview1;
	        this.text2 = textview2;
	        this.btn1 = button1;
	    }
	}
	
	@SuppressLint("InflateParams")
	private void initDialog (final int position){
		
		LayoutInflater inflater = LayoutInflater.from(context);
		final View v = inflater.inflate(R.layout.dialog_setting_modify_profile, null);
		
		dialog.setView(v);
		
		TextView text1 = (TextView)v.findViewById(R.id.modify_title);
		final EditText edit1 = (EditText)v.findViewById(R.id.modify_content);
		
		text1.setText(profile_title[position]);
		edit1.setText(list.get(position).get(list_name[position]));
		
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                // 按下PositiveButton要做的事  
                //Toast.makeText(getActivity(), "請繼續作答", Toast.LENGTH_SHORT).show();
            }  
        }); 
		
		dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String content;
				content = String.valueOf(edit1.getText());
				
				if(content.equals("男"))
					content = "1";
				else if (content.equals("女"))
					content = "0";
				
				DataFromDatabase MySQL = new DataFromDatabase();
				MySQL.UpdataStdProfile(content, list_name[position], account_ID);
				
			}
	
		});
		
		
	}

}
