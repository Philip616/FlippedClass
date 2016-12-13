package com.mcucsie.flippedclass.group;

import com.mcucsie.flippedclass.GetNowCourseInfo;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GroupGridListAdapter extends BaseAdapter {

	private Context context;
	private int count=0;
	private int column_num=0;
//	private int real_position=0;
	private SQLiteDatabase db_group;
	private Cursor cursor;
	private String now_course_ID="null";
	private String group_tablename="null";
	private String str_selectTable="null";
	private GetNowCourseInfo gnci;
	
	public GroupGridListAdapter(Context context,int count,int column_num) {
		// TODO Auto-generated constructor stub
		this.count=count+(count/(column_num-1));
		this.context=context;
		this.column_num=column_num;
		gnci=new GetNowCourseInfo(context);
		db_group=context.openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
		now_course_ID=gnci.getNowCourseID();
		group_tablename="GroupMember_"+now_course_ID;
		str_selectTable="SELECT * FROM "+group_tablename;
		cursor=db_group.rawQuery(str_selectTable, null);
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView result = new TextView(context);
		String nulltag="";
		if(now_course_ID.equals("null"))
		{
			result.setTextColor(Color.BLACK);  
	        result.setTextSize(12);
	        result.setLines(2);
	        result.setLayoutParams(new AbsListView.LayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)));  
	        result.setGravity(Gravity.CENTER);  
	        result.setBackgroundColor(Color.WHITE);
	        result.setTag("other");
	        result.setText("null");
		}
		else{
        result.setTextColor(Color.BLACK);  
        result.setTextSize(12);
        result.setLines(2);
        result.setLayoutParams(new AbsListView.LayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)));  
        result.setGravity(Gravity.CENTER);  
        result.setBackgroundColor(Color.WHITE);
        result.setTag("other");
        if(position%column_num!=0){
        	cursor.moveToPosition(position-((position/column_num)+1));
//          if(real_position==1)real_position=0;
        	nulltag=cursor.getString(1);
        	if(cursor.getString(1).equals("NULL")||cursor.getString(1).equals("null")||nulltag.contains("null"))
        	{
        		result.setText(cursor.getString(1));
        		result.setTextColor(color.transparent);
        		result.setTag("false");
        	}
        	else
        		result.setText(cursor.getString(1));
        }
//        Log.d("=====>", "position="+position+"column="+column_num);
        if(position%column_num==0){
        	result.setTag("false");
        	result.setText("²Ä"+((position/column_num)+1)+"²Õ");
        }
//        Log.d("=====>", "POSITION"+position);
        
		}
        
        
        return result;  
	}

}
