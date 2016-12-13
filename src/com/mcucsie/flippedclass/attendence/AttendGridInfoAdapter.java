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

public class AttendGridInfoAdapter extends BaseAdapter {
	private Context context;
	private int count=0;
	private int column_num=0;
    private String[] info_account_ID;
    private String[] info_attend_tag;
	private String[] info_attend_time;  
	
	public AttendGridInfoAdapter(Context context,int count,int column_num) {
		// TODO Auto-generated constructor stub
		this.count=count*3;
	}
	
	
	public AttendGridInfoAdapter(Context context, int column_num,
			String[] info_account_ID, String[] info_attend_tag,
			String[] info_attend_time) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.column_num=column_num;
		this.count=info_account_ID.length*3;
		this.info_account_ID=info_account_ID;
		this.info_attend_tag=info_attend_tag;
		this.info_attend_time=info_attend_time;
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
		
        result.setTextColor(Color.BLACK);  
        result.setTextSize(12);
        result.setLines(2);
        result.setLayoutParams(new AbsListView.LayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)));  
        result.setGravity(Gravity.CENTER);  
        result.setBackgroundColor(Color.WHITE);
//        result.setTag("other");
        if(position%column_num==0){
        	result.setText(info_account_ID[position/3]);
        }
        else if(position%column_num==1){
        	if(info_attend_tag[position/3].equals("0"))
        	{
        		result.setBackgroundColor(Color.parseColor("#FFA488"));
        		result.setText("¥¼Ã±¨ì");
        		result.getBackground().setAlpha(100);
        	}
        	else
        	    result.setText("¥X®u");
        }
        else if(position%column_num==2){
        	result.setText(info_attend_time[position/3]);
        }
        
        
        return result;  
	}
}
