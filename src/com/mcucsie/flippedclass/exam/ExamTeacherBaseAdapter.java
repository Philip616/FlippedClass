package com.mcucsie.flippedclass.exam;

import java.util.ArrayList;
import java.util.HashMap;











import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
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

public class ExamTeacherBaseAdapter extends BaseAdapter{

	ExamMainTeacherFragment frag_exam;
	private LayoutInflater myInflater;
	private ArrayList<HashMap<String, String>> list = null;
	private ViewTag viewTag;
	private Context context;
	
	GetNowCourseInfo course_info;
	String  course_ID;
	
	static Bundle bundle;
	 
	public ExamTeacherBaseAdapter(Context context, ArrayList<HashMap<String, String>> list, ExamMainTeacherFragment frag_exam) {
	    //���o MainActivity ����
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
	 
		course_info = new GetNowCourseInfo(context);		
		course_ID = course_info.getNowCourseID();
		
		if (convertView == null) {
	        // ���olistItem�e�� view
	        convertView = myInflater.inflate(R.layout.listview_exam_tch_item_4, null);
	 
	        // �غclistItem���eview
	        viewTag = new ViewTag(
	                (TextView) convertView.findViewById(R.id.txt_tch_exam_name),
	                (TextView) convertView.findViewById(R.id.txt_tch_exam_state),
	                (Button) convertView.findViewById(R.id.btn_tch_open_exam),
	        		(Button) convertView.findViewById(R.id.btn_tch_edit_exam));
	 
	        // �]�m�e�����e
	        convertView.setTag(viewTag);
	 
	    } else {
	        viewTag = (ViewTag) convertView.getTag();
	    }
	    
	    viewTag.text1.setText(list.get(position).get("exam_name"));
	    
	    if(list.get(position).get("exam_state").equals("open"))
	    {
	    	viewTag.btn1.setText("����");
	    	viewTag.text2.setText("�}�񤤡G" + list.get(position).get("count_for_done") + "��" + list.get(position).get("allmembercount"));	
	    }
	    else
	    {
	    	viewTag.btn1.setText("�}��");
	    	viewTag.text2.setText("���}��");	
	    }
  	
	    //viewTag.btn2.setText("�s��");
	    
	    //�]�w���s��ť�ƥ�ζǤJ MainActivity ����
	    viewTag.btn1.setOnClickListener(new btn1_ItemButton_Click(context, position));
	    viewTag.btn2.setOnClickListener(new btn2_ItemButton_Click(context, position));
	     
	    return convertView;
	}
	 
	public class ViewTag {
	    TextView text1,text2;
	    TextView btn1,btn2;
	     
	    public ViewTag(TextView textview1, TextView textview2, Button button1, Button button2) {
	        this.text1 = textview1;
	        this.text2 = textview2;
	        this.btn1 = button1;
	        this.btn2 = button2;
	    }
	}
	 
	//btn1�ۭq���s��ť�ƥ�
	class btn1_ItemButton_Click implements OnClickListener {
	    private int position;
	    private Context context;
	     
	    btn1_ItemButton_Click(Context context, int pos) {
	        this.context = context;
	        position = pos;
	    }
	 
	    public void onClick(View v) {
	        Log.d("allenj", "ItemButton = " + list.get(position).get("exam_code"));
	 
	        //myDialog(Integer.valueOf(list.get(position).get("id")));
	        //Toast.makeText(context, "ItemButton = " + list.get(position).get("exam_code") + " �ڬObtn1" , Toast.LENGTH_LONG).show();
	        
	        //frag_exam.RefreshTeacherBaseAdapter();
	        
 String exam_state = list.get(position).get("exam_state");
	        
	        DataFromDatabase MySQL_DB = new DataFromDatabase();
	        
	        switch(exam_state)
			{								
				case "open":
					MySQL_DB.FC_SetExamOpenorClose("0", course_ID,list.get(position).get("exam_code"));
					Toast.makeText(context, "�w��������", Toast.LENGTH_LONG).show();
				break;
					
				case "close":
					MySQL_DB.FC_SetExamOpenorClose("1", course_ID,list.get(position).get("exam_code"));

					
					DataFromDatabase GCM_DB = new DataFromDatabase();
					GCM_DB.GCM_sendMessageToCourseMember(course_ID, "����G" + course_ID + "�}��@���s������");
					
					Toast.makeText(context, "�w�}�Ҵ���", Toast.LENGTH_LONG).show();
				break;
			}
	       
	    }
	}
	
	//btn1�ۭq���s��ť�ƥ�
		class btn2_ItemButton_Click implements OnClickListener {
		    private int position;
		    private Context context;
		     
		    btn2_ItemButton_Click(Context context, int pos) {
		        this.context = context;
		        position = pos;
		    }
		 
		    public void onClick(View v) {
		        Log.d("allenj", "ItemButton = " + list.get(position).get("exam_code"));
		 
		        //myDialog(Integer.valueOf(list.get(position).get("id")));
		        //Toast.makeText(context, "ItemButton = " + list.get(position).get("exam_code"), Toast.LENGTH_LONG).show();
		        
		        //Toast.makeText(context, "ItemButton = " + list.get(position).get("exam_code") + " �ڬObtn2", Toast.LENGTH_LONG).show();
		        
		        if(list.get(position).get("exam_state").equals("open"))
			        Toast.makeText(context, "����}�񤤡A�Х���������A�i��s��" , Toast.LENGTH_LONG).show();
		        
		        else
		        {
			        bundle = new Bundle();
		    		bundle.putString("exam_name",list.get(position).get("exam_name"));
		    		bundle.putString("exam_code", list.get(position).get("exam_code"));
		    		bundle.putString("exam_state", list.get(position).get("exam_state"));
			        
			        frag_exam.CreateEditFragment();
		        }   
		    }
		}	
}
