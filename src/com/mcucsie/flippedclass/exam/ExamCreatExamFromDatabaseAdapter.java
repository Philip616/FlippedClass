package com.mcucsie.flippedclass.exam;

import java.util.ArrayList;
import java.util.HashMap;

import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExamCreatExamFromDatabaseAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<HashMap<String, String>> List,List_2;
	private ArrayList<Boolean> Check;
	private LayoutInflater myInflater;
	private ViewTag ViewTag;
	private AlertDialog.Builder dialog,dialog2;
	static Bundle bundle;
	int code;
	GetNowCourseInfo course_info;
	String  course_ID;
	
    public ExamCreatExamFromDatabaseAdapter(Context context,ArrayList<HashMap<String, String>> list,Bundle bundle)
	{
		// TODO Auto-generated constructor stub
		this.context=context;
		this.myInflater=LayoutInflater.from(context);
		this.List=list;
		this.bundle = bundle;
		List_2=new ArrayList<HashMap<String,String>>();
		Check = new ArrayList<Boolean>();
		HashMap<String, String> itemforlist_2 = new HashMap<String, String>();

		for(int i=0;i<List.size();i++){
			Check.add(false);
			itemforlist_2.put("point","尚未設定分數");
			//List_2.add(itemforlist_2);
		}
		code=bundle.getInt("code");
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return List.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return List.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// TODO Auto-generated method stub
		course_info = new GetNowCourseInfo(context);		
		course_ID = course_info.getNowCourseID();
		dialog = new AlertDialog.Builder(context);
		dialog2 = new AlertDialog.Builder(context);
		
		if(convertView==null){
		ViewTag = new ViewTag();
		convertView=myInflater.inflate(R.layout.listview_exam_examdata, null);
			
		ViewTag.text1 = (TextView) convertView.findViewById(R.id.exam_list_database_examinfo);
		ViewTag.btn1 = (Button) convertView.findViewById(R.id.exam_list_database_btn_choose);
		ViewTag.text2 = (TextView) convertView.findViewById(R.id.exam_list_database_grade);		
		ViewTag.btn2 = (Button) convertView.findViewById(R.id.exam_list_database_btn_grade); 		
		ViewTag.layout1 = (RelativeLayout) convertView.findViewById(R.id.exam_list_database_layout);		            

		convertView.setTag(ViewTag);
		
		}else{
			ViewTag = (ViewTag) convertView.getTag();
		}
		if(code==1){
			ViewTag.text2.setVisibility(View.GONE);
			ViewTag.btn2.setVisibility(View.GONE);
			ViewTag.btn1.setOnClickListener(new btn_choose_ques(context,position));
	
		}
		
		if(code==2){
			ViewTag.btn1.setVisibility(View.GONE);
			ViewTag.btn2.setVisibility(View.VISIBLE);
			ViewTag.text2.setVisibility(View.VISIBLE);
			ViewTag.btn2.setOnClickListener(new btn_set_grade(context,position));

		}
		ViewTag.text1.setText(List.get(position).get("question_content"));
		//ViewTag.btn1.setOnClickListener(new btn_choose_ques(context,position));
		//ViewTag.btn2.setOnClickListener(null);
		ViewTag.layout1.setOnClickListener(new btn_exam_info(context,position));

		if(code==2 && Check.get(position))
		{
		ViewTag.text2.setText(List_2.get(position).get("point"));
		}
		
		if(code==1 && Check.get(position)){
			ViewTag.btn1.setEnabled(false);		
		}
		else 
			ViewTag.btn1.setEnabled(true);	
		
		return convertView;
	}
	
	public class ViewTag {
	    TextView text1,text2;
	    Button btn1,btn2;
	    RelativeLayout layout1;
	}
	
	class btn_choose_ques implements android.view.View.OnClickListener{
		private int position;
	    private Context context;
	    private View textset;
		TextView setlist;
		public btn_choose_ques(Context context, int pos) {
		this.context = context;
		this.position = pos;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			HashMap<String, String> itemforlist_2 = new HashMap<String, String>();
			itemforlist_2.put("question_content", List.get(position).get("question_content"));
			itemforlist_2.put("choose_A", List.get(position).get("choose_A"));
			itemforlist_2.put("choose_B", List.get(position).get("choose_B"));
			itemforlist_2.put("choose_C", List.get(position).get("choose_C"));
			itemforlist_2.put("choose_D", List.get(position).get("choose_D"));
			itemforlist_2.put("type", List.get(position).get("type"));
			itemforlist_2.put("answer", List.get(position).get("answer"));
			List_2.add(itemforlist_2);
			Log.d("onclick", List_2.toString());
			
	        Toast.makeText(context, "已選擇"+List_2.size()+"題" , Toast.LENGTH_SHORT).show();
	        
	        notifyDataSetChanged();
			Check.set(position, true);
	        
		}
	}
	
	class btn_set_grade implements android.view.View.OnClickListener{
		private int position;
	    private Context context;
		private View grade;
		public btn_set_grade(Context context, int pos) {
		this.context = context;
		position = pos;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			grade = LayoutInflater.from(context).inflate(R.layout.dialog_database_grade, null);
			dialog.setTitle("輸入配分");
			dialog.setView(grade);
			dialog.setPositiveButton("確認", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(code==2 && Check.get(position))
					{
					EditText text = (EditText)grade.findViewById(R.id.edit_exam_grade);
					HashMap<String, String> resetgrade = new HashMap<String, String>();
					resetgrade.put("question_content", List.get(position).get("question_content"));
					resetgrade.put("choose_A", List.get(position).get("choose_A"));
					resetgrade.put("choose_B", List.get(position).get("choose_B"));
					resetgrade.put("choose_C", List.get(position).get("choose_C"));
					resetgrade.put("choose_D", List.get(position).get("choose_D"));
					resetgrade.put("type", List.get(position).get("type"));
					resetgrade.put("answer", List.get(position).get("answer"));
					resetgrade.put("point",text.getText().toString());
					List_2.set(position,resetgrade);
					Log.d("onclick", List_2.toString());
					notifyDataSetChanged();
					}
					else{
					EditText text = (EditText)grade.findViewById(R.id.edit_exam_grade);
					HashMap<String, String> itemforlist_2 = new HashMap<String, String>();
					itemforlist_2.put("question_content", List.get(position).get("question_content"));
					itemforlist_2.put("choose_A", List.get(position).get("choose_A"));
					itemforlist_2.put("choose_B", List.get(position).get("choose_B"));
					itemforlist_2.put("choose_C", List.get(position).get("choose_C"));
					itemforlist_2.put("choose_D", List.get(position).get("choose_D"));
					itemforlist_2.put("type", List.get(position).get("type"));
					itemforlist_2.put("answer", List.get(position).get("answer"));
					itemforlist_2.put("point",text.getText().toString());
					HashMap<String, String> setgrade = new HashMap<String, String>();
					List_2.add(itemforlist_2);
					Check.set(position, true);
					Log.d("onclick", List_2.toString());
					notifyDataSetChanged();
					}
				}
			});
			dialog.show();
		}
		
	}
	class btn_exam_info implements android.view.View.OnClickListener{
		private int position;
	    private Context context;
		public btn_exam_info(Context context, int pos) {
		this.context = context;
		position = pos;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog2.setTitle("題目內容");
			if(List.get(position).get("type")=="1"){
			dialog2.setMessage("題目:\n"+List.get(position).get("question_content")
					+"\nA選項: "+List.get(position).get("choose_A")
					+"\nB選項: "+List.get(position).get("choose_B")
					+"\nC選項: "+List.get(position).get("choose_C")
					+"\nD選項: "+List.get(position).get("choose_D")
					+"\n題型: "+"單選題"
					+"\n答案: "+List.get(position).get("answer"));
			dialog2.show();

			}
			if(List.get(position).get("type")=="2"){
			dialog2.setMessage("題目:\n"+List.get(position).get("question_content")
					+"\nA選項: "+List.get(position).get("choose_A")
					+"\nB選項: "+List.get(position).get("choose_B")
					+"\nC選項: "+List.get(position).get("choose_C")
					+"\nD選項: "+List.get(position).get("choose_D")
					+"\n題型: "+"是非題"
					+"\n答案: "+List.get(position).get("answer"));
			dialog2.show();

			}
		}
		}
	public ArrayList<HashMap<String, String>> getList2(){
		return List_2;
	}
	
	public int getList2count()
	{
		return List_2.size();
	}
	
	public boolean getlistpoint()
	{
		if(List_2.size()==List.size())
		{
		return true;	
		}
		else
		{
		return false;
		}
	}
	
}
