package com.mcucsie.flippedclass.group;

import java.util.ArrayList;
import java.util.HashMap;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class GroupDiscussAdapter extends BaseAdapter {
	
	private LayoutInflater myInflater = null;
	private ArrayList<HashMap<String, String>> list = null;
	private ViewTag viewTag;
	private Context context;
	private AlertDialog.Builder Dialog;
	FragmentManager fragmentManager;
	
	GetNowCourseInfo course_info;
	String  course_ID;
	
	static Bundle bundle;
	
	
	
	public GroupDiscussAdapter(Context context, ArrayList<HashMap<String, String>> list,Bundle bundle) {
	    //取得 MainActivity 實體
		Log.i("msg","MainActivity");
	    this.context = context;
	    
	    if(context != null)
	    this.myInflater = LayoutInflater.from(context);
	    this.list = list;
	    this.bundle = bundle;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return Long.valueOf(list.get(position).get("discuss_number"));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		course_info = new GetNowCourseInfo(context);		
		course_ID = course_info.getNowCourseID();
		Dialog = new AlertDialog.Builder(context);
		
		if (convertView == null) {
	        // 取得listItem容器 view
	        convertView = myInflater.inflate(R.layout.listview_group_dicuss_item, null);
	 
	        // 建構listItem內容view
	        viewTag = new ViewTag(
	                (TextView) convertView.findViewById(R.id.dicuss_text),
	                (Button) convertView.findViewById(R.id.enter_dicuss),
	        		(Button) convertView.findViewById(R.id.delete_discuss));

	        
	        // 設置容器內容
	        convertView.setTag(viewTag);
	 
	    } else {
	        viewTag = (ViewTag) convertView.getTag();
	    }
		
		viewTag.btn1.setOnClickListener(new enter_board(position));
		viewTag.btn2.setOnClickListener(new delete_board(list.get(position).get("discuss_name")));
		
		viewTag.text1.setText(list.get(position).get("discuss_name"));
		
			
		
		return convertView;
	}
	
	public class ViewTag {
	    TextView text1;
	    Button btn1;
	    Button btn2;
	     
	    public ViewTag(TextView textview1, Button button1, Button button2) {
	        this.text1 = textview1;
	        this.btn1 = button1;
	        this.btn2 = button2;
	    }
	}
	
class enter_board implements OnClickListener {
		
		private int position;
		
		
			enter_board(int pos) {
		       position = pos;
		       
		   }

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i("msg","group disscuss adapter --->click:");
			GroupDiscussBoard groupDiscussBoard = new GroupDiscussBoard();
			fragmentManager = ((Activity)context).getFragmentManager();
		
			groupDiscussBoard.setArguments(bundle);
			bundle.putString("board_name", list.get(position).get("discuss_name"));

			fragmentManager.beginTransaction().replace(R.id.frame_group_content, groupDiscussBoard).addToBackStack(null).commitAllowingStateLoss();
		}
		
	};

class delete_board implements OnClickListener{
	
	String BoardName,Type,groupNum,tableName;

		delete_board(String name) {		
			BoardName = name;
		}
	
	@Override
	public void onClick(View v) {	
		Dialog.setCancelable(false);
		Dialog.setTitle("刪除討論區");
		Dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Type = bundle.getString("DiscussType");
				groupNum = bundle.getString("groupNum");
				
				tableName = "discusslist_" + course_ID + "_" + Type;
				DataFromDatabase MySQL_DB = new DataFromDatabase();
				MySQL_DB.FC_deleteBoard(tableName, groupNum,Type,BoardName,course_ID);
				
			}
		});
		
		Dialog.show();	
	}
	
};


}
