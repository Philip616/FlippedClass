package com.mcucsie.flippedclass;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;
import com.mcucsie.flippedclass.exam.ExamMainStudentFragment;
import com.mcucsie.flippedclass.exam.ExamQuestionContentFragment;

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

public class BulletinBoardDoPreExamFragment extends Fragment{

	ExamQuestionContentFragment[] frag_quescontent;
	
	TextView txt_exam_name,txt_show_ans;
	ListView list_queslist;
	Button btn_send_ans;
	ProgressBar pgb;
	
	static Handler mHandler_setexam;
	setViewForExamCodeListRunnable setViewForExamCodeListRunnable;
	boolean setViewToNormalRunnable_isRunning = false;
	
	static Handler mHandler_updategrade;
	static Handler mHandler_checkexamopen;
	setViewForCheckExamOpenRunnable setViewForCheckExamOpenRunnable;
	boolean setViewForCheckExamOpenRunnable_isRunning = false;
	
	String exam_name,exam_code,exam_state;
	
	GetNowCourseInfo course_info;
	GetNowAccountInfo account_info;
	String  course_ID,course_name;
	String account_ID; 
	
	int jsonArray_length = 0,pre_position = 0;
	int[] user_ans,ans_code,point;
	String[] ques_code,ans,type;
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
		btn_send_ans = (Button)getView().findViewById(R.id.btn_send_ans);
		txt_show_ans = (TextView)getView().findViewById(R.id.txt_show_ans);
		pgb=(ProgressBar)getView().findViewById(R.id.pgb_exam_ques);
		
		list_queslist.setOnItemClickListener(new list_queslist_click());
		btn_send_ans.setOnClickListener(btn_send_ans_click);
		

		pgb.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.FOCUS_FORWARD);

		
		Bundle bundle = getArguments();
        if (bundle != null) {
            exam_name = bundle.getString("exam_name");//x
            exam_code = bundle.getString("exam_code");//O
            exam_state = bundle.getString("exam_state");//x
        }
        
        course_info = new GetNowCourseInfo(getActivity());
		
		course_ID = course_info.getNowCourseID();
		course_name = course_info.getNowCourseName();
		
		account_info = new GetNowAccountInfo(getActivity());
		
		account_ID = account_info.getNowAccountID();
        
        txt_exam_name.setText(exam_name);
        
        if(exam_state.equals("close"))
        {
        	btn_send_ans.setText("完成檢視");
        }
        
        dialog = new AlertDialog.Builder(getActivity());
        
        setViewToNormalRunnable_isRunning = true;
        
        mHandler_setexam = new Handler();
        mHandler_updategrade = new Handler();
        mHandler_checkexamopen = new Handler();
        
        DataFromDatabase MySQL_DB = new DataFromDatabase();
        MySQL_DB.FC_GetPreExam(course_ID,exam_code);
        
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

	        user_ans[pre_position] = frag_quescontent[pre_position].user_ans;
				
	        pgb.setVisibility(View.GONE);
	        
	        if(pre_position != position)
			getFragmentManager().beginTransaction().replace(R.id.fram_exam_ques_content, frag_quescontent[position]).commit();	
			
			if(exam_state.equals("close"))
	        {
	        	txt_show_ans.setText("本題答案為：" + ans[pre_position]);
	        }
			
			pre_position = position;
			
		}
	}
	
	private OnClickListener btn_send_ans_click= new OnClickListener(){
		
    	@Override
		public void onClick(View v) {
			
    		if(exam_state.equals("open"))
    		{
    			user_ans[pre_position] = frag_quescontent[pre_position].user_ans;
	    		initDialog();
	    		dialog.setMessage("離開此測驗後將無法繼續作答，確定送出答案並離開？");
				dialog.show();
    		}
    		
    		if(exam_state.equals("close"))
            {
    			MainFragment frag_main = new MainFragment();
    			getFragmentManager().beginTransaction().replace(R.id.container, frag_main).commit();
            }
		}
	};
	
	private void initDialog() {
		// TODO Auto-generated method stub
		//		dialog.setMessage("Hi this is test dialog");
			
		dialog.setTitle("警告！將離開此次測驗");
		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		    // 按下PositiveButton要做的事  
		    Toast.makeText(getActivity(), "請繼續作答", Toast.LENGTH_SHORT).show();
		    }  
	    }); 
			
		dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		    	// 按下PositiveButton要做的事  	            	
		        grade = 0;
		    		
		    	for(int i=0 ; i<jsonArray_length ; i++)
		    	{				
		    		Log.d("=====>", "第" + i +"題答案：" + user_ans[i]);
		    		Log.d("=====>", "第" + i +"題正確答案：" + ans[i]);
														
		    		ans_code[i] = ansToInt(ans[i]);
		    		Log.d("=====>", "答案轉換為數字" + ans_code[i]);
						
		    		if(user_ans[i] == ans_code[i])
		    		{
		    			grade = grade + point[i];
		    		}			
		    	}
		    						
		    	setViewForCheckExamOpenRunnable_isRunning = true;
		    	//000000000000000000000000000000000000000000000000000000000000000000
		    	DataFromDatabase MySQL_DB = new DataFromDatabase();
		    	MySQL_DB.FC_CheckPreExamOpen(exam_code,course_ID);
		    	        
		    	Thread waitdata = new Thread(new WaitForCheckExamOpen(MySQL_DB));
		    	waitdata.start();	  
		    	//000000000000000000000000000000000000000000000000000000000000000000
		    }
		});	
	}
	
	private int ansToInt(String ans)
	{		
		int ans_code = -1;
		
		switch(ans)
		{
			case "A":
				ans_code = 0;
				break;
			
			case "B":
				ans_code = 1;
				break;
				
			case "C":
				ans_code = 2;
				break;
					
			case "D":
				ans_code = 3;
				break;
			
			default:
				break;
		}
		
		return ans_code;
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
					Thread.sleep(500);
					
					if(setViewToNormalRunnable_isRunning)
					{
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
	
								frag_quescontent = new ExamQuestionContentFragment[jsonArray_length];
								
								user_ans = new int[jsonArray_length];
							
								for(int i = 0 ; i<jsonArray_length ; i++)
								{								
									Bundle bundle = new Bundle();
									bundle.putString("result",result);
									bundle.putInt("ques_code",i);
									
									frag_quescontent[i] = new ExamQuestionContentFragment();
									frag_quescontent[i].setArguments(bundle);
									user_ans[i] = -1;
								}
						}
						
						catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						setViewForExamCodeListRunnable = new setViewForExamCodeListRunnable(result);
						
						mHandler_setexam.post(setViewForExamCodeListRunnable);
					}
				} 
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	class setViewForExamCodeListRunnable implements Runnable
	{
		String result;
		int times = 0;
		
		public setViewForExamCodeListRunnable(String result)
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
						
						ques_code = new String[jsonArray_length];
						ans = new String[jsonArray_length];
						ans_code = new int[jsonArray_length];
						point = new int[jsonArray_length];
						type = new String[jsonArray_length];
						
						img_dividers = new int[jsonArray_length];
						
						for(int i = 0 ; i<jsonArray_length ; i++)
						{
							JSONObject jsonData = jsonArray.getJSONObject(i);
							
							ques_code[i] = jsonData.getString("question_num");
							
							ans[i] = jsonData.getString("answer");
							
							point[i] = Integer.valueOf(jsonData.getString("point")).intValue();
							
							type[i] = jsonData.getString("type");
							
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
						
						if(setViewToNormalRunnable_isRunning)
						{
							getFragmentManager().beginTransaction().replace(R.id.fram_exam_ques_content, frag_quescontent[0]).commit();
							if(exam_state.equals("close"))
							{
								txt_show_ans.setText("本題答案為：" + ans[0]);
							}
						}
			
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	class WaitForCheckExamOpen implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		
		public WaitForCheckExamOpen(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					Thread.sleep(500);
					
					String result = MySQL_DB.return_result;
					
					while(result==null && times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "ExamQuestionFragment Waitfodata 多等了1秒");
						result = MySQL_DB.return_result;
						times++;
					}			
					
					Log.d("=====>", "ExamQuestionFragment WaitForCheckExamOpen get result" + result);
					
					setViewForCheckExamOpenRunnable = new setViewForCheckExamOpenRunnable(result);
					
					mHandler_checkexamopen.post(setViewForCheckExamOpenRunnable);
					
					
				} 
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	class setViewForCheckExamOpenRunnable implements Runnable
	{
		String result;
		int times = 0;
		
		public setViewForCheckExamOpenRunnable(String result)
		{
			this.result = result;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			if(setViewForCheckExamOpenRunnable_isRunning)
			{
				if(result.equals("1"))
				{	            	
	    			DataFromDatabase MySQL_DB = new DataFromDatabase();
	    	        MySQL_DB.FC_InsertPreExamGrade(account_ID,course_ID,grade,exam_code);
	    			
	    			Thread waitdata = new Thread(new WaitForUpdateGrade(MySQL_DB));
	    			waitdata.start();
				}
				
				if(result.equals("0"))
				{
					Toast.makeText(getActivity(), "測驗已關閉，無法上傳成績", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	
	
	class WaitForUpdateGrade implements Runnable
	{
		DataFromDatabase MySQL_DB;
		int times = 0;
		
		public WaitForUpdateGrade(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					Thread.sleep(500);
					
					String result = MySQL_DB.return_result;
					Log.d("=====>", "ExamQuestionFragment WaitForUpdateGrade get result" + result);
					
					while(result==null && times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "ExamQuestionFragment Waitfodata 多等了1秒");
						result = MySQL_DB.return_result;
						times++;
					}			
					
					Log.d("=====>", "ExamQuestionFragment WaitForUpdateGrade get result" + result);
					
					mHandler_updategrade.post(SetViewForUpdateGrade);					
				} 
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	private Runnable SetViewForUpdateGrade = new Runnable() {
		public void run() {
			Toast.makeText(getActivity(), "你獲得了" + grade + "分", Toast.LENGTH_LONG).show();
			Log.d("=====>", "共得" + grade +"分");
			MainFragment frag_main = new MainFragment();
			getFragmentManager().beginTransaction().replace(R.id.container, frag_main).commit();
		}
	};
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		endAllThread();
	}

	public void endAllThread(){
		
		mHandler_setexam.removeCallbacks(setViewForExamCodeListRunnable);
		mHandler_checkexamopen.removeCallbacks(setViewForCheckExamOpenRunnable);
		mHandler_updategrade.removeCallbacks(SetViewForUpdateGrade);
		setViewToNormalRunnable_isRunning = false;
		setViewForCheckExamOpenRunnable_isRunning = false;
		 Log.d("<==ExamQuestion==>", "handler and thread have kill");
//		 refresh_receivepage_Thread.interrupt();
//		 downloadAttendListThread.interrupt();
	}
	
}
