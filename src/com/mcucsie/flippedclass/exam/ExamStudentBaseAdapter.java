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
	 
	    if (convertView == null) {
	        // ���olistItem�e�� view
	        convertView = myInflater.inflate(R.layout.listview_exam_std_item_3, null);
	 
	        // �غclistItem���eview
	        viewTag = new ViewTag(
	                (TextView) convertView.findViewById(R.id.txt_std_exam_name),
	                (TextView) convertView.findViewById(R.id.txt_std_exam_state),
	                (Button) convertView.findViewById(R.id.btn_std_open_exam));
	 
	        // �]�m�e�����e
	        convertView.setTag(viewTag);
	 
	    } else {
	        viewTag = (ViewTag) convertView.getTag();
	    }
	     
	    viewTag.text1.setText(list.get(position).get("exam_name"));
	    
	    if(!list.get(position).get("exam_grade").equals("���@��"))
	    viewTag.text2.setText(list.get(position).get("exam_grade")+"��");
	    
	    else
	    viewTag.text2.setText(list.get(position).get("exam_grade")); 
	    
	    if(list.get(position).get("exam_grade").equals("���@��") && list.get(position).get("exam_state").equals("open"))
	    viewTag.text2.setText("�}��");	    	

	    if(list.get(position).get("exam_grade").equals("���@��"))
	    viewTag.btn1.setText("�}�l����");
	    
	    else
	    viewTag.btn1.setText("�˵�����");
	    
	    //�]�w���s��ť�ƥ�ζǤJ MainActivity ����
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
	 
	//�ۭq���s��ť�ƥ�
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
	        
	        
	        if(!list.get(position).get("exam_grade").equals("���@��") && list.get(position).get("exam_state").equals("open"))
	        {
	        	Toast.makeText(context, "����}�񤤡A�L�k�d��", Toast.LENGTH_LONG).show();
	        }
	        
	        else if(!list.get(position).get("exam_grade").equals("���@��") && list.get(position).get("exam_state").equals("close"))
	        {
	        	//Toast.makeText(context, "�w�������i�ݪ�����", Toast.LENGTH_LONG).show();
	        	bundle = new Bundle();
	    		bundle.putString("exam_name",list.get(position).get("exam_name"));
	    		bundle.putString("exam_code", list.get(position).get("exam_code"));
	    		bundle.putString("exam_state", list.get(position).get("exam_state"));
	    		
	    		frag_exam.CreateQuestionFragment();
	        }
	        
	        else if(list.get(position).get("exam_grade").equals("���@��") && list.get(position).get("exam_state").equals("close"))
	        {
	        	Toast.makeText(context, "����|���}��", Toast.LENGTH_LONG).show();
	        }
	        
	        else
	        {
	        	//Toast.makeText(context, "�}�Ҵ���", Toast.LENGTH_LONG).show();
	        	
	        	bundle = new Bundle();
	    		bundle.putString("exam_name",list.get(position).get("exam_name"));
	    		bundle.putString("exam_code", list.get(position).get("exam_code"));
	    		bundle.putString("exam_state", list.get(position).get("exam_state"));
	        	
	        	frag_exam.initDialog();
	        	frag_exam.dialog.setMessage("�T�w�}�l����H");
	        	frag_exam.dialog.show();
	        }
	    }
	}
	
	


	
}
