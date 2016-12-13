package com.mcucsie.flippedclass.exam;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ExamGridIRSInfoAdapter extends BaseAdapter{

	private Context context;
	private int count=0;
	private int column_num=0;
    private String[] info_account_ID;
    private String[] info_ans; 
	
	public ExamGridIRSInfoAdapter(Context context,int count,int column_num) {
		// TODO Auto-generated constructor stub
		this.count=count*2;
	}
	
	
	public ExamGridIRSInfoAdapter(Context context, int column_num,
			String[] info_account_ID, String[] info_ans) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.column_num=column_num;
		this.count=info_account_ID.length*2;
		this.info_account_ID=info_account_ID;
		this.info_ans = info_ans;
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
       
        if(position%column_num == 0)
        	result.setText(info_account_ID[position/2]);
        else if(position%column_num == 1)
        	{
        		if(info_ans[position/2].equals("Z"))
        			{
        				result.setBackgroundColor(Color.RED);
        			   	result.setText("¥¼§@µª");
        			}
   			   	else
        			result.setText(info_ans[position/2]);
        	}
        return result;  
	}
}
