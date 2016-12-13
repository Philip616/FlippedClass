package com.mcucsie.flippedclass.exam;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ExamEditExamFragment extends Fragment{

	ExamEditExamContentFragment[] frag_editcontent;
	
	TextView txt_exam_name;
	ListView list_queslist;
	Button btn_save_edit;
	ProgressBar pgb;
	
	static Handler mHandler_setexam;
	setViewToNormalRunnable setViewToNormalRunnable;
	boolean WaitForExamCodeList_isRunning = false;
	boolean setViewToNormalRunnable_isRunning = false;
	
	String exam_name,exam_code,exam_state;
	
	String[] edit_ques,edit_point,edit_ans,edit_chooseA,edit_chooseB,edit_chooseC,edit_chooseD;
	String[] type,ques_code;
	
	GetNowCourseInfo course_info;
	GetNowAccountInfo account_info;
	String  course_ID,course_name;
	String account_ID; 
	
	int jsonArray_length = 0,pre_position = 0;
	int [] img_dividers;
	int grade = 0;
	
	View click_view; //保存點選的View
    int select_item=-1; //一開始未選擇任何一個item所以為-1
    
    AlertDialog.Builder dialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_question, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		txt_exam_name = (TextView)getView().findViewById(R.id.txt_exam_name);
		list_queslist = (ListView)getView().findViewById(R.id.list_questionlist);
		btn_save_edit = (Button)getView().findViewById(R.id.btn_send_ans);
		pgb=(ProgressBar)getView().findViewById(R.id.pgb_exam_ques);
		
		list_queslist.setOnItemClickListener(new list_queslist_click());
		btn_save_edit.setOnClickListener(btn_save_edit_click);
		

		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);
		
		Bundle bundle = getArguments();
        if (bundle != null) {
            exam_name = bundle.getString("exam_name");
            exam_code = bundle.getString("exam_code");
            exam_state = bundle.getString("exam_state");
        }
        
        course_info = new GetNowCourseInfo(getActivity());
		
		course_ID = course_info.getNowCourseID();
		course_name = course_info.getNowCourseName();
		
		account_info = new GetNowAccountInfo(getActivity());
		
		account_ID = account_info.getNowAccountID();
        
        txt_exam_name.setText(exam_name);

        btn_save_edit.setText("完成編輯");
        
        dialog = new AlertDialog.Builder(getActivity());
        
        mHandler_setexam = new Handler();
        
        DataFromDatabase MySQL_DB = new DataFromDatabase();
        MySQL_DB.FC_GetExam(course_ID,exam_code);
        
        WaitForExamCodeList_isRunning = true;
        setViewToNormalRunnable_isRunning = true;
        
        Thread waitdata = new Thread(new WaitForExamCodeList(MySQL_DB));
		waitdata.start();
	}
	
	class list_queslist_click implements OnItemClickListener
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
				
			Log.d("=====>", "QuesList 的AdapterView:" + parent);
			
			Log.d("=====>", "QuesList 按下第" + position +"項");
			Log.d("=====>", "QuesList 前一次為第" + pre_position +"項");
	                                 
			pgb.setVisibility(View.VISIBLE);
			pgb.setVisibility(View.FOCUS_FORWARD);
			
			//點選某個item並呈現被選取的狀態
	        if ((select_item == -1) || (select_item==position))
	        {
	        	view.setBackgroundColor(Color.parseColor("#FF8C00")); //為View加上選取效果
	        }
	        else
	        {            
	        	click_view.setBackgroundDrawable(null); //將上一次點選的View保存在click_view
	            view.setBackgroundColor(Color.parseColor("#FF8C00")); //為View加上選取效果
	        }
	        
	        click_view=view; //保存點選的View
	        select_item=position;//保存目前的View位置

	        edit_ques[pre_position] = frag_editcontent[pre_position].edit_ques.getText().toString();
        	edit_point[pre_position] = frag_editcontent[pre_position].edit_point.getText().toString();
        	edit_ans[pre_position] = frag_editcontent[pre_position].edit_ans;
        	
        	if(type[pre_position].equals("1"))
        	{
        		edit_chooseA[pre_position] = frag_editcontent[pre_position].edit_chooseA.getText().toString();
        		edit_chooseB[pre_position] = frag_editcontent[pre_position].edit_chooseB.getText().toString();
        		edit_chooseC[pre_position] = frag_editcontent[pre_position].edit_chooseC.getText().toString();
        		edit_chooseD[pre_position] = frag_editcontent[pre_position].edit_chooseD.getText().toString();
        	}
        	
        	if(type[pre_position].equals("2"))
        	{
        		edit_chooseA[pre_position] = frag_editcontent[pre_position].edit_chooseA.getText().toString();
        		edit_chooseB[pre_position] = frag_editcontent[pre_position].edit_chooseB.getText().toString();
        		edit_chooseC[pre_position] = "";
        		edit_chooseD[pre_position] = "";
        	}
				
        	pgb.setVisibility(View.GONE);
        	
        	if(pre_position != position)
			getFragmentManager().beginTransaction().replace(R.id.fram_exam_ques_content, frag_editcontent[position]).commit();	
			
			pre_position = position;
			
		}
	}
	
	private OnClickListener btn_save_edit_click= new OnClickListener(){
		
    	@Override
		public void onClick(View v) {			
    		
    		initDialog();
	    	dialog.setMessage("確定離開並儲存編輯？");
			dialog.show();	
		}
	};
	
	private void initDialog() {
		// TODO Auto-generated method stub
		//		dialog.setMessage("Hi this is test dialog");

			dialog.setTitle("警告！將離開此次測驗");
			dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {  
	                // 按下PositiveButton要做的事  
	                Toast.makeText(getActivity(), "請繼續編輯", Toast.LENGTH_SHORT).show();
	            }  
	        }); 
			dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {  
	                // 按下PositiveButton要做的事  	        	
		        	
	            	edit_ques[pre_position] = frag_editcontent[pre_position].edit_ques.getText().toString();
	            	edit_point[pre_position] = frag_editcontent[pre_position].edit_point.getText().toString();
	            	edit_ans[pre_position] = frag_editcontent[pre_position].edit_ans;
	            	
	            	if(type[pre_position].equals("1"))
	            	{
	            		edit_chooseA[pre_position] = frag_editcontent[pre_position].edit_chooseA.getText().toString();
	            		edit_chooseB[pre_position] = frag_editcontent[pre_position].edit_chooseB.getText().toString();
	            		edit_chooseC[pre_position] = frag_editcontent[pre_position].edit_chooseC.getText().toString();
	            		edit_chooseD[pre_position] = frag_editcontent[pre_position].edit_chooseD.getText().toString();
	            	}
	            	
	            	if(type[pre_position].equals("2"))
	            	{
	            		edit_chooseA[pre_position] = frag_editcontent[pre_position].edit_chooseA.getText().toString();
	            		edit_chooseB[pre_position] = frag_editcontent[pre_position].edit_chooseB.getText().toString();
	            		edit_chooseC[pre_position] = "";
	            		edit_chooseD[pre_position] = "";
	            	}
	            	
	            	for(int i = 0; i<jsonArray_length; i++)
		        	{
			        	DataFromDatabase MySQL_DB = new DataFromDatabase();
			            MySQL_DB.FC_UpDateExam(edit_ques[i], edit_chooseA[i], edit_chooseB[i], edit_chooseC[i], edit_chooseD[i], edit_point[i], edit_ans[i], ques_code[i], course_ID, exam_code);
		        	}
	            	
	            	ExamMainTeacherFragment frag_exam_tch_main=new ExamMainTeacherFragment();
	    			getFragmentManager().beginTransaction().replace(R.id.frame_exam_content, frag_exam_tch_main).commit();
	    			
	            }
			});
	}
	
	
	
	
	class WaitForExamCodeList implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		
		public WaitForExamCodeList(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					
				if(WaitForExamCodeList_isRunning)
				{
					Thread.sleep(500);
						
						String result = MySQL_DB.return_result;
						Log.d("=====>", "ExamQuestionFragment Waitfodata get result" + result);
						
						while(result==null && times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "ExamQuestionFragment Waitfodata 多等了1秒");
							result = MySQL_DB.return_result;
							times++;
						}
						
						Log.d("=====>", "ExamQuestionFragment Waitfodata get result" + result);
						
						JSONArray jsonArray;
						
						try {
								jsonArray = new JSONArray(result);
							
								jsonArray_length = jsonArray.length();
	
								frag_editcontent = new ExamEditExamContentFragment[jsonArray_length];
								
								ques_code = new String[jsonArray_length];
								type = new String[jsonArray_length];
								
								edit_ques = new String[jsonArray_length];
								edit_point = new String[jsonArray_length];
								edit_ans = new String[jsonArray_length];
								edit_chooseA = new String[jsonArray_length];
								edit_chooseB = new String[jsonArray_length];
								edit_chooseC = new String[jsonArray_length];
								edit_chooseD = new String[jsonArray_length];
							
								for(int i = 0 ; i<jsonArray_length ; i++)
								{													
									Bundle bundle = new Bundle();
									bundle.putString("result",result);
									bundle.putInt("ques_code",i);
									
									frag_editcontent[i] = new ExamEditExamContentFragment();
									frag_editcontent[i].setArguments(bundle);
								}
						}
						
						catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
						setViewToNormalRunnable = new setViewToNormalRunnable(result);
						
						mHandler_setexam.post(setViewToNormalRunnable);					
					}
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	class setViewToNormalRunnable implements Runnable
	{
		String result;
		int times = 0;
		
		public setViewToNormalRunnable(String result)
		{
			this.result = result;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			if(setViewToNormalRunnable_isRunning)
			{
				JSONArray jsonArray;
				try {
						jsonArray = new JSONArray(result);
						
						ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
														
						img_dividers = new int[jsonArray_length];
						
						for(int i = 0 ; i<jsonArray_length ; i++)
						{
							JSONObject jsonData = jsonArray.getJSONObject(i);
							
							ques_code[i] = jsonData.getString("question_num");
							type[i] = jsonData.getString("type");
							
							edit_ques[i] = jsonData.getString("question_content");
							edit_point[i] = jsonData.getString("point");
							edit_ans[i] = jsonData.getString("answer");
							edit_chooseA[i] = jsonData.getString("choose_A");
							edit_chooseB[i] = jsonData.getString("choose_B");
							edit_chooseC[i] = jsonData.getString("choose_C");
							edit_chooseD[i] = jsonData.getString("choose_D");
							
							img_dividers[i] = R.drawable.exam_ques_dividers;
							//img_dividers[i] = R.drawable.drawer_shadow;
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("QuestionCode", ques_code[i]);
				        	map.put("ImgDividers", img_dividers[i]);
				        	listItem.add(map);
						}
						
						SimpleAdapter listItemAdapter = new SimpleAdapter(getActivity(),listItem, //套入動態資訊 
					        	R.layout.listview_quescode_item_2,//套用自訂的XML
					        	new String[] {"QuestionCode","ImgDividers"}, //動態資訊取出順序
					        	new int[] {R.id.Item_quescode,R.id.Item_quescode_dividers} //將動態資訊對應到元件ID
					        );
					
						list_queslist.setAdapter(listItemAdapter);//執行SimpleAdapter
						
						pre_position = 0;
						pgb.setVisibility(View.GONE);
						getFragmentManager().beginTransaction().replace(R.id.fram_exam_ques_content, frag_editcontent[0]).commit();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		endAllThread();
	}

	public void endAllThread(){
		
		 mHandler_setexam.removeCallbacks(setViewToNormalRunnable);
		 Log.d("<==ExamEdit==>", "handler and thread have kill");
		 
		 WaitForExamCodeList_isRunning = false;
		 setViewToNormalRunnable_isRunning = false;
//		 refresh_receivepage_Thread.interrupt();
//		 downloadAttendListThread.interrupt();
	}

}
