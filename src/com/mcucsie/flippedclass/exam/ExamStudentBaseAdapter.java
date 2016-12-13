package com.mcucsie.flippedclass.exam;

import java.util.ArrayList;
import java.util.HashMap;









import com.mcucsie.flippedclass.R;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ExamStudentBaseAdapter extends BaseAdapter{

	ExamMainStudentFragment frag_exam;
	private LayoutInflater myInflater;
	private ArrayList<HashMap<String, String>> list = null;
	private ViewTag viewTag;
	private Context context;
	
	static Bundle bundle;
	 
	public ExamStudentBaseAdapter(Context context, ArrayList<HashMap<String, String>> list, ExamMainStudentFragment frag_exam) {
	    //取得 MainActivity 實體
	    this.context = context;
	    this.myInflater = LayoutInflater.from(context);
	    this.list = list;
	    this.frag_exam = frag_exam;
	}
	 
	public int getCount() {
	    return list.size();
	}
	 
	public Object getItem(int position) {
	    return list.get(position);
	}
	 
	public long getItemId(int position) {
	    return Long.valueOf(list.get(position).get("exam_code"));
	}
	 
	public View getView(int position, View convertView, ViewGroup parent) {
	 
	    if (convertView == null) {
	        // 取得listItem容器 view
	        convertView = myInflater.inflate(R.layout.listview_exam_std_item_3, null);
	 
	        // 建構listItem內容view
	        viewTag = new ViewTag(
	                (TextView) convertView.findViewById(R.id.txt_std_exam_name),
	                (TextView) convertView.findViewById(R.id.txt_std_exam_state),
	                (Button) convertView.findViewById(R.id.btn_std_open_exam));
	 
	        // 設置容器內容
	        convertView.setTag(viewTag);
	 
	    } else {
	        viewTag = (ViewTag) convertView.getTag();
	    }
	     
	    viewTag.text1.setText(list.get(position).get("exam_name"));
	    
	    if(!list.get(position).get("exam_grade").equals("未作答"))
	    viewTag.text2.setText(list.get(position).get("exam_grade")+"分");
	    
	    else
	    viewTag.text2.setText(list.get(position).get("exam_grade")); 
	    
	    if(list.get(position).get("exam_grade").equals("未作答") && list.get(position).get("exam_state").equals("open"))
	    viewTag.text2.setText("開放中");	    	

	    if(list.get(position).get("exam_grade").equals("未作答"))
	    viewTag.btn1.setText("開始測驗");
	    
	    else
	    viewTag.btn1.setText("檢視測驗");
	    
	    //設定按鈕監聽事件及傳入 MainActivity 實體
	    viewTag.btn1.setOnClickListener(new ItemButton_Click(context, position));
	     
	    return convertView;
	}
	 
	public class ViewTag {
	    TextView text1,text2;
	    TextView btn1;
	     
	    public ViewTag(TextView textview1, TextView textview2, Button button1) {
	        this.text1 = textview1;
	        this.text2 = textview2;
	        this.btn1 = button1;
	    }
	}
	 
	//自訂按鈕監聽事件
	class ItemButton_Click implements OnClickListener {
	    private int position;
	    private Context context;
	     
	    ItemButton_Click(Context context, int pos) {
	        this.context = context;
	        position = pos;
	    }
	 
	    public void onClick(View v) {
	        Log.d("allenj", "ItemButton = " + list.get(position).get("exam_code"));
	 
	        //myDialog(Integer.valueOf(list.get(position).get("id")));
	        //Toast.makeText(context, "ItemButton = " + list.get(position).get("exam_code"), Toast.LENGTH_LONG).show();
	        
	        
	        if(!list.get(position).get("exam_grade").equals("未作答") && list.get(position).get("exam_state").equals("open"))
	        {
	        	Toast.makeText(context, "測驗開放中，無法查看", Toast.LENGTH_LONG).show();
	        }
	        
	        else if(!list.get(position).get("exam_grade").equals("未作答") && list.get(position).get("exam_state").equals("close"))
	        {
	        	//Toast.makeText(context, "已完成但可看的測驗", Toast.LENGTH_LONG).show();
	        	bundle = new Bundle();
	    		bundle.putString("exam_name",list.get(position).get("exam_name"));
	    		bundle.putString("exam_code", list.get(position).get("exam_code"));
	    		bundle.putString("exam_state", list.get(position).get("exam_state"));
	    		
	    		frag_exam.CreateQuestionFragment();
	        }
	        
	        else if(list.get(position).get("exam_grade").equals("未作答") && list.get(position).get("exam_state").equals("close"))
	        {
	        	Toast.makeText(context, "測驗尚未開放", Toast.LENGTH_LONG).show();
	        }
	        
	        else
	        {
	        	//Toast.makeText(context, "開啟測驗", Toast.LENGTH_LONG).show();
	        	
	        	bundle = new Bundle();
	    		bundle.putString("exam_name",list.get(position).get("exam_name"));
	    		bundle.putString("exam_code", list.get(position).get("exam_code"));
	    		bundle.putString("exam_state", list.get(position).get("exam_state"));
	        	
	        	frag_exam.initDialog();
	        	frag_exam.dialog.setMessage("確定開始測驗？");
	        	frag_exam.dialog.show();
	        }
	    }
	}
	
	


	
}
