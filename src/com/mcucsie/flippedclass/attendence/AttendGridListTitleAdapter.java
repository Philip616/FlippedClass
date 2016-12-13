package com.mcucsie.flippedclass.attendence;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AttendGridListTitleAdapter extends BaseAdapter{

	private Context context;
	private int column_num=0;
//	private int real_position=0;
	
	public AttendGridListTitleAdapter(Context context,int column_num) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.column_num=column_num;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return column_num;
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
	
        result.setTextColor(Color.BLACK);  
        result.setTextSize(12);
        result.setLines(2);
        result.setLayoutParams(new AbsListView.LayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)));  
        result.setGravity(Gravity.CENTER);  
        result.setBackgroundColor(Color.GRAY);
        result.setText("組員");
        result.setTag("title");
        if(position==0)
        	result.setText("學號");
        else if(position==1)
        	result.setText("出席狀況");
        else if(position==2)
        	result.setText("簽到時間");
        
        return result;  
	}
}
