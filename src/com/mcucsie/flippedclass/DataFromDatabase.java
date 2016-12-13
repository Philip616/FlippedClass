package com.mcucsie.flippedclass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class DataFromDatabase {


	private String SI_uriAPI = "http://1.34.20.54/FlippedClass/StudentInformation.php";
	private String FC_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass.php";
	private String FC_personal_courselsit_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_Personal_CourseLsit.php";
	private String FC_exam_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_Exam.php";
	private String FC_exam_grade_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_Exam_Grade.php";
	private String FC_list_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_List.php";
	private String FC_course_memberlist_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_Course_MemberList.php";
	private String FC_attend_info_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_Attend_Info.php";
	private String Remind_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_remind.php";
	private String Quickpick_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_quickpick.php";
	private String Discuss_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_Discuss.php";
	private String GCM_uriAPI = "http://1.34.20.54/gcm_server_php/GCM_Control.php";
	public String actionUrl = "http://1.34.20.54/FlippedClass_remind_Upload.php";
	public String downloadUrl = "http://1.34.20.54/FlippedClass_Upload/";
	public String Bulletin_Board_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_Bulletin_Board.php";
	public String FC_pre_exam_uriAPI = "http://1.34.20.54/FlippedClass/FlippedClass_Pre_Exam.php";
	public String test = "http://1.34.20.54/FlippedClass/test.php";
	public String DatabaseUrl = "http://1.34.20.54/FlippedClass/FlippedClass_Database.php";

	public String return_result = null;
	public String s_return_result = null;

	/** 「要更新版面」的訊息代碼 */
	protected static final int REFRESH_DATA = 0x00000001;

	/** 建立UI Thread使用的Handler，來接收其他Thread來的訊息 */
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// 顯示網路上抓取的資料
				case REFRESH_DATA:
					String result = null;
					if (msg.obj instanceof String)
					result = (String) msg.obj;
					if (result != null)
						// result不為NULL所做的處理
					{
						//String[] name = result.split(":");
					
						//Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
						
						return_result = result; 
					}
				break;
			}
		}
	};
	
	public void clearResult(){
		this.return_result=null;
	}
	
	private void startThread(String data[],String data_name[],int count_for_data,String case_code,String JsonorStr,String uriAPI)
	{
		Thread t = new Thread(new sendPostRunnable(data,data_name,count_for_data,case_code,JsonorStr,uriAPI));
		t.start();
	}
	
	class sendPostRunnable implements Runnable
	{
		String data[],data_name[],case_code,JsonorStr,uriAPI;
		int count_for_data;

		// 建構子，設定要傳的字串
		public sendPostRunnable(String data[],String data_name[],int count_for_data,String case_code,String JsonorStr,String uriAPI)
		{
			this.data = data;
			this.data_name = data_name;
			this.count_for_data = count_for_data;
			this.case_code = case_code;
			this.JsonorStr = JsonorStr;
			this.uriAPI = uriAPI;
		}

		@Override
		public void run()
		{
			String result = sendPostDataToInternet(data,data_name,count_for_data,case_code,JsonorStr,uriAPI);
			Log.d("=====>", "DataFromDatabase 的 result = " + result);
			s_return_result=result;
			mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
		}
	}
	
	private String sendPostDataToInternet(String data[],String data_name[],int count_for_data,String case_code,String JsonorStr,String uriAPI)
	{

		/* 建立HTTP Post連線 */

		HttpPost httpRequest = new HttpPost(uriAPI);
		/*
		 * Post運作傳送變數必須用NameValuePair[]陣列儲存
		 */
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for(int i=0;i<count_for_data;i++)
		{	
			params.add(new BasicNameValuePair(data_name[i], data[i]));
		}
		
		params.add(new BasicNameValuePair("case", case_code));
		
		try

		{

			// 發出HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 取得HTTP response
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			

			

				// 若狀態碼為200 ok
				if (httpResponse.getStatusLine().getStatusCode() == 200)
				{
					if(JsonorStr.equals("0"))
					{
						HttpEntity httpEntity = httpResponse.getEntity();
						InputStream inputStream = httpEntity.getContent();
						
						BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
						StringBuilder builder = new StringBuilder();
						String line = null;
						while((line = bufReader.readLine()) != null) {
							builder.append(line + "\n");
						}
						Log.d("=====>", "Get result for JSON!");
						inputStream.close();
						String result = builder.toString();
						return result;
					}
					
					
					else
					{
						// 取出回應字串 
						String strResult = EntityUtils.toString(httpResponse.getEntity());
						// 回傳回應字串
						Log.d("=====>", "Get result for String!");
						return strResult;
					}
				}
			
		} 
	
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
	
	void SI_SearchAccount(String ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "1";
		
		data[0] = ID;
		data_name[0] = "ID";
		
		Log.d("=====>", "SI_DB Insert ID set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,SI_uriAPI);
	}
	
	void SI_SearchAccountInfo(String ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "1";
		
		data[0] = ID;
		data_name[0] = "ID";
		
		Log.d("=====>", "SI_DB Insert ID set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,SI_uriAPI);
	}
	
	void FC_checkhAccountExist(String ID,String password)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "1";
		
		data[0] = ID;
		data[1] = password;
		data_name[0] = "ID";
		data_name[1] = "password";
		Log.d("=====>", "FC_DB Insert ID & password set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
	}
	//取得密碼
		public void FC_getPassword(String ID){
			int count_for_data = 1;
			String data[] = new String[count_for_data];
			String data_name[] = new String[count_for_data];
			String case_code = "13";
			String JsonorStr = "1";
			
			data[0] = ID;
			data_name[0] = "ID";
			Log.d("=====>", "FC_DB get password from data in array Done!");
			
			startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
		}
		
		//更新密碼
		public void updatePassword(String ID,String password){
			int count_for_data = 2;
			String data[] = new String[count_for_data];
			String data_name[] = new String[count_for_data];
			String case_code = "10";
			String JsonorStr = "1";
			
			data[0] = ID;
			data[1] = password;
			data_name[0] = "ID";
			data_name[1] = "password";
			Log.d("=====>", "FC_DB update passward set Done!");
			startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
		}
		//取得學生資訊
		public void FC_getmail(String ID)
		{
			int count_for_data = 1;
			String data[] = new String[count_for_data];
			String data_name[] = new String[count_for_data];
			String case_code = "12";
			String JsonorStr = "1";

			data[0] = ID;
			data_name[0] = "ID";

			Log.d("=====>", "FC_DB Get ID info set data in array Done!");
			
			startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
		}
	void FC_InsertStdAccount(String ID,String password,String type)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "1";
		
		data[0] = ID;
		data[1] = password;
		data[2] = type;
		data_name[0] = "ID";
		data_name[1] = "password";
		data_name[2] = "type";
		Log.d("=====>", "FC_DB Insert ID & password & type set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
	}
	
	public void insertStdInfo(String ID,String Name,String Department,String Grade,String Gender,String email,String Line)
	{
		int count_for_data = 8;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
		
		String table_name = "user_profile";
		
		data[0] = ID;
		data[1] = Name;
		data[2] = Department;
		data[3] = Grade;
		data[4] = Gender;
		data[5] = email;
		data[6] = Line;
		data[7] = table_name;
		
		data_name[0] = "id";
		data_name[1] = "name";
		data_name[2] = "department";
		data_name[3] = "grade";
		data_name[4] = "gender";
		data_name[5] = "email";
		data_name[6] = "line";
		data_name[7] = "table_name";
		
		
		Log.d("=====>", "FC_DB Insert user_profile set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,DatabaseUrl);
	}
	
	public void insertExamInfo(String course_ID,String Ques_content,String choose_A,String choose_B,String choose_C,String choose_D,String Ans,String type)
	{
		int count_for_data = 8;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "13";
		String JsonorStr = "1";
		
		String table_name = "examtable_"+course_ID;
		
		data[0] = Ques_content;
		data[1] = choose_A;
		data[2] = choose_B;
		data[3] = choose_C;
		data[4] = choose_D;
		data[5] = Ans;
		data[6] = type;
		data[7] = table_name;
		
		data_name[0] = "question_content";
		data_name[1] = "choose_A";
		data_name[2] = "choose_B";
		data_name[3] = "choose_C";
		data_name[4] = "choose_D";
		data_name[5] = "answer";
		data_name[6] = "type";
		data_name[7] = "table_name";
		
		
		Log.d("=====>", "FC_DB Insert Exam_info set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}
//暫時改
	public void FC_getStdInfo(String ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "4";
		String JsonorStr = "1";

		data[0] = ID;
	
		data_name[0] = "ID";

		Log.d("=====>", "FC_DB Get ID info set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
	}

	public void FC_getCourseList(String ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
		
		String table_name = "courselist_" + ID;
		
		data[0] = table_name;
	
		data_name[0] = "table_name";

		Log.d("=====>", "FC_DB Get Coursr List set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_personal_courselsit_uriAPI);
	}
	
	void FC_getCourseInfo(String str_where)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "5";
		String JsonorStr = "0";
		
		data[0] = str_where;
	
		data_name[0] = "str_where";

		Log.d("=====>", "FC_DB Get Coursr Info set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
	}

	
	//---------------------以下為測驗function---------------------//	
	public void FC_GetList(String course_ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
		
		String table_name = "examlist_" + course_ID;
		
		data[0] = table_name;
		data_name[0] = "table_name";
		
		Log.d("=====>", "FC_DB Get List set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_GetGrade(String account_ID,String course_ID)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
		
		String table_name = "gradelist_" + account_ID;
		
		data[0] = table_name;
		data[1] = course_ID;
		
		data_name[0] = "table_name";
		data_name[1] = "course_ID";
		
		Log.d("=====>", "FC_DB Get Exam setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_grade_uriAPI);
	}
	
	public void FC_GetExam(String course_ID,String exam_code)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
				
		String table_name = "exam_" + course_ID + "_" + exam_code;
		
		data[0] = table_name;
		data_name[0] = "table_name";
		
		Log.d("=====>", "FC_DB Get Exam setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}
	
	public void FC_CreateExam(String course_ID,int exam_code,String exam_name)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "1";

		Log.d("=====>", "GET IN Create!");
		
		data[0] = "exam_" + course_ID + "_" + exam_code;
		data_name[0] = "exam_table";
		data[1] = "grade_" + course_ID + "_" + exam_code;
		data_name[1] = "grade_table";
		
		Log.d("=====>", "FC_DB Create table set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
		
		count_for_data = 4;
		String data2[] = new String[count_for_data];
		String data_name2[] = new String[count_for_data];
		case_code = "2";
		JsonorStr = "1";
		
		data2[0] = "examlist_" + course_ID;
		data_name2[0] = "exam_table";
		
		data2[1] = "gradelist_" + course_ID;
		data_name2[1] = "grade_table";
		
		data2[2] = "" + exam_code;
		data_name2[2] = "exam_code";
		
		data2[3] = "" + exam_name;
		data_name2[3] = "exam_name";
		
		Log.d("=====>", "FC_DB Create table set Done!");
		
		startThread(data2,data_name2,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}

	public void FC_InputExam(String ques,String chooseA,String chooseB,String chooseC,String chooseD,String point,String ans,String type,String course_ID,String exam_code)
	{
		int count_for_data = 9;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "3";
		String JsonorStr = "1";
		
		String table_name = "exam_" + course_ID + "_" + exam_code;
		
		data[0] = ques;
		data[1] = chooseA;
		data[2] = chooseB;
		data[3] = chooseC;
		data[4] = chooseD;
		data[5] = point;
		data[6] = ans;
		data[7] = type;
		data[8] = table_name;
		
		data_name[0] = "question_content";
		data_name[1] = "choose_A";
		data_name[2] = "choose_B";
		data_name[3] = "choose_C";
		data_name[4] = "choose_D";
		data_name[5] = "point";
		data_name[6] = "answer";
		data_name[7] = "type";
		data_name[8] = "table_name";
		
		Log.d("=====>", "FC_DB Insert Exam set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}
	
	public void FC_InputGradeMember(String course_ID,String exam_code,String account_ID)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "9";
		String JsonorStr = "1";
		
		String table_name = "grade_" + course_ID + "_" + exam_code;
		
		data[0] = account_ID;
		data[1] = "-1";
		data[2] = table_name;
		
		data_name[0] = "account_ID";
		data_name[1] = "grade";
		data_name[2] = "table_name";
		
		Log.d("=====>", "FC_DB Insert Exam set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}
/*	
	public void FC_DeleteExam(String course_ID,int exam_code)
	{		
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "4";
		String JsonorStr = "1";

		Log.d("=====>", "GET IN Create!");
		
		data[0] = "exam_" + course_ID + "_" + exam_code;
		data_name[0] = "exam_table";
		data[1] = "grade_" + course_ID + "_" + exam_code;
		data_name[1] = "grade_table";
		
		Log.d("=====>", "FC_DB Create table set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
		
		count_for_data = 3;
		String data2[] = new String[count_for_data];
		String data_name2[] = new String[count_for_data];
		case_code = "3";
		JsonorStr = "1";
		
		data2[0] = "examlist_" + course_ID;
		data_name2[0] = "exam_table";
		
		data2[1] = "gradelist_" + course_ID;
		data_name2[1] = "grade_table";
		
		data2[2] = "" + exam_code;
		data_name2[2] = "exam_code";
		
		Log.d("=====>", "FC_DB Create table set Done!");
		
		startThread(data2,data_name2,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}*/
	
	public void FC_GetLatestExamCode(String course_ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "4";
		String JsonorStr = "1";
		
		String table_name = "examlist_" + course_ID;
		
		data[0] = table_name;
		
		data_name[0] = "table_name";
		
		Log.d("=====>", "FC_DB Get Lates Exam setDone!");

		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
/*	
	public void FC_UpdateExamName(String exam_name,int exam_code_for_update,String course_ID)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "7";
		String JsonorStr = "1";
		
		String exam_table_name = "examlist_" + course_ID;
		
		data[0] = exam_name;
		data[1] = Integer.toString(exam_code_for_update);
		data[2] = exam_table_name;
		
		data_name[0] = "exam_name";
		data_name[1] = "exam_code_for_update";
		data_name[2] = "exam_table_name";
		
		Log.d("=====>", "FC_DB Get Lates Exam setDone!");

		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}*/
	
	public void FC_SetExamOpenorClose(String open_or_close,String course_ID,String exam_code)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "5";
		String JsonorStr = "1";
		
		String table_name = "examlist_" + course_ID;
		
		data[0] = open_or_close;
		data[1] = table_name;
		data[2] =exam_code;
		
		data_name[0] = "open_or_close";
		data_name[1] = "table_name";
		data_name[2] = "exam_code";
		
		Log.d("=====>", "FC_DB Get Lates Exam setDone!");

		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_GetAllStdGrade(String course_ID, String exam_code)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "5";
		String JsonorStr = "0";
		
		String table_name = "grade_" + course_ID + "_" + exam_code;
		
		data[0] = table_name;
		
		data_name[0] = "table_name";
		
		Log.d("=====>", "FC_DB Get Exam setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}

	public void FC_UpDateExam(String ques,String chooseA,String chooseB,String chooseC,String chooseD,String point,String ans,String ques_code,String course_ID,String exam_code)
	{

		int count_for_data = 9;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "6";
		String JsonorStr = "1";
		
		String table_name = "exam_" + course_ID + "_" +exam_code;
		
		data[0] = ques_code;
		data[1] = ques;
		data[2] = chooseA;
		data[3] = chooseB;
		data[4] = chooseC;
		data[5] = chooseD;
		data[6] = point;
		data[7] = ans;
		data[8] = table_name;
			
		data_name[0] = "question_code";
		data_name[1] = "question_content";
		data_name[2] = "choose_A";
		data_name[3] = "choose_B";
		data_name[4] = "choose_C";
		data_name[5] = "choose_D";
		data_name[6] = "point";
		data_name[7] = "answer";
		data_name[8] = "table_name";
		
		Log.d("=====>", "FC_DB Insert UpDateExam set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);		
	}
	
	public void FC_CheckExamOpen(String exam_code,String course_ID)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "9";
		String JsonorStr = "1";
				
		String table_name = "examlist_" + course_ID;
		
		data[0] = exam_code;
		data[1] = table_name;
		
		data_name[0] = "exam_code";
		data_name[1] = "table_name";
		
		Log.d("=====>", "FC_DB CheckExamOpen setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_InsertGrade(String account_ID,String course_ID,int grade,String exam_code)
	{
		int count_for_data = 4;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "1";
				
		String table_name = "gradelist_" + account_ID;
		
		data[0] = course_ID;
		data[1] = exam_code;
		data[2] = table_name;
		data[3] = Integer.toString(grade);
		
		data_name[0] = "course_ID";
		data_name[1] = "exam_code";
		data_name[2] = "table_name";
		data_name[3] = "grade";
		
		Log.d("=====>", "FC_DB InsertGrade setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_grade_uriAPI);
		
		count_for_data = 3;
		data = new String[count_for_data];
		data_name = new String[count_for_data];
		case_code = "7";
		
		table_name = "grade_" + course_ID + "_" + exam_code;
		
		data[0] = account_ID;
		data[1] = Integer.toString(grade);
		data[2] = table_name;
		
		data_name[0] = "account_ID";
		data_name[1] = "grade";
		data_name[2] = "table_name";
		
		Log.d("=====>", "FC_DB InsertGrade setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}
	
	public void FC_ExamCountForDone(String course_ID,String exam_code)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "10";
		String JsonorStr = "1";
				
		String table_name = "grade_" + course_ID + "_" + exam_code;
		
		data[0] = exam_code;
		data[1] = table_name;
		
		data_name[0] = "exam_code";
		data_name[1] = "table_name";
		
		Log.d("=====>", "FC_DB CheckExamOpen setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}
	
/*	public void FC_CheckUnDoExam(String account_ID,String course_ID,int exam_code)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "8";
		String JsonorStr = "1";
				
		String table_name = "grade_" + course_ID + "_" + exam_code;
		
		data[0] = account_ID;
		data[1] = table_name;
		
		data_name[0] = "account_ID";
		data_name[1] = "table_name";
		
		Log.d("=====>", "FC_DB CheckExamOpen setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}
*/
	
	public void FC_CheckIRSOpen(String course_ID)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "10";
		String JsonorStr = "1";
				
		String table_name = "irslist_" + course_ID;
		
		data[0] = "1";
		data[1] = table_name;
		
		data_name[0] = "IRS_code";
		data_name[1] = "table_name";
		
		Log.d("=====>", "FC_DB CheckIRSOpen setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_UpdateIRSAns(String account_ID,String course_ID,String ans)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "11";
		String JsonorStr = "1";
				
		String table_name = "irs_" + course_ID;
		
		data[0] = ans;
		data[1] = table_name;
		data[2] = account_ID;
		
		data_name[0] = "answer";
		data_name[1] = "table_name";
		data_name[2] = "account_ID";
		
		Log.d("=====>", "FC_DB UpdateIRSAns setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}
	
	public void FC_IRSSetState(String course_ID,String IRS_code,String IRS_state)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "11";
		String JsonorStr = "1";
				
		String table_name = "irslist_" + course_ID;
		
		data[0] = IRS_code;
		data[1] = table_name;
		data[2] = IRS_state;
		
		data_name[0] = "IRS_code";
		data_name[1] = "table_name";
		data_name[2] = "IRS_state";
		
		Log.d("=====>", "FC_DB UpdateIRSAns setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_GetStdIRSAns(String course_ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "12";
		String JsonorStr = "0";
				
		String table_name = "irs_" + course_ID;
		
		data[0] = table_name;
		
		data_name[0] = "table_name";
		
		Log.d("=====>", "FC_DB UpdateIRSAns setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}
	
	//---------------------以上為測驗function---------------------//	
	public void queryForCourseMemberList(String table_name){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
		
		data[0]=table_name;
		
		data_name[0]="table_name";
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_course_memberlist_uriAPI);
	}
	
	public void FC_queryForCourseMemberList(String table_name){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
		
		data[0]=table_name;
		
		data_name[0]="table_name";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_course_memberlist_uriAPI);
	}
	public void FC_queryForAttendInfoList(String table_name){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";      //查詢所有學生的出席狀況
		String JsonorStr = "0";
		
		data[0]=table_name;
		
		data_name[0]="table_name";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_attend_info_uriAPI);
	}
	public void FC_updateForAttendInfo(String table_name,String account_ID,String attend_time){
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";      //更新某位學生的出席狀況
		String JsonorStr = "1";
		
		data[0]=table_name;
		data[1]=account_ID;
		data[2]=attend_time;
		
		data_name[0]="table_name";
		data_name[1]="account_ID";
		data_name[2]="attend_time";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_attend_info_uriAPI);
	}
	public void FC_updateTotrueForAttendInfo(String table_name,String account_ID){
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "3";      //更新某位學生的出席狀況成為"出席"
		String JsonorStr = "1";
		
		data[0]=table_name;
		data[1]=account_ID;

		
		data_name[0]="table_name";
		data_name[1]="account_ID";

		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_attend_info_uriAPI);
	}
	public void FC_updateTofalseAttendInfo(String table_name,String account_ID){
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "4";      //更新某位學生的出席狀況成為"未出席"
		String JsonorStr = "1";
		
		data[0]=table_name;
		data[1]=account_ID;

		
		data_name[0]="table_name";
		data_name[1]="account_ID";

		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_attend_info_uriAPI);
	}
	public void FC_queryForAccountInfo(String table_name){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";   
		String JsonorStr = "0";
		
		data[0]=table_name;
		
		data_name[0]="table_name";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_course_memberlist_uriAPI);
	}
	
	//---------------------以下為分組function-------------------------//
	public void FC_deleteGroupMemberList(String table_name){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "1";
		
		data[0]=table_name;
		
		data_name[0]="del_tablename";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_course_memberlist_uriAPI);
	}
	public void FC_insertGroupMemberList(String table_name,String account_ID,String groupnum){
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "3";   //insert某個分組成員進入MySQL
		String JsonorStr = "1";
		
		data[0]=table_name;
		data[1]=account_ID;
		data[2]=groupnum;
		
		data_name[0]="insert_tablename";
		data_name[1]="account_ID";
		data_name[2]="groupnum";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_course_memberlist_uriAPI);
	}
	public void FC_checkGroupMemberList(String table_name){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "4";   //確認某個table是否為空值
		String JsonorStr = "0";
		
		data[0]=table_name;
		
		//Log.d("table_name",table_name);
		data_name[0]="table_name";
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_course_memberlist_uriAPI);
	}
	
	public void FC_createGroupList(String courseID){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "5";   //創建分組名單Table
		String JsonorStr = "1";
	
		String group_name = "course_grouplist_" + courseID;
		
		data[0] = group_name;
		
		data_name[0] = "group_table";
		
		Log.d("=====>", "Create Group List is Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_course_memberlist_uriAPI);
	}
	
	public void FC_createDiscuss(String courseID,String type,String groupNum){
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "12";   //確認某個table是否為空值
		String JsonorStr = "1";
		
		String dicuss_table;
		
			dicuss_table = "discusslist_" + courseID + "_" + type;	
			
		data[0] = dicuss_table;
		data[1] = groupNum;
		
		data_name[0] = "discuss_table";
		data_name[1] = "groupNum";
		

		Log.d("=====>", "Create Dicuss is Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_insertDiscuss(String courseID,String discuss_name,String date,String discuss_number,String type,String groupNum){
		int count_for_data = 5;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "15";   
		String JsonorStr = "1";
		
		String discuss_table;
		
		
			discuss_table = "discusslist_" + courseID + "_" + type;
		
		
		data[0] = discuss_table;
		data[1] = discuss_number;
		data[2] = date;
		data[3] = discuss_name;
		data[4] = groupNum;

		data_name[0] = "discuss_table";
		data_name[1] = "discuss_number";
		data_name[2] = "date";
		data_name[3] = "discuss_name";
		data_name[4] = "groupNum";

		
			startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);	
	}
	
	public void FC_GetLatestDiscussCode(String course_ID,String type,String groupNum)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "13";
		String JsonorStr = "1";
		String table_name;
		
			table_name = "discusslist_" + course_ID + "_" + type;

		
		data[0] = table_name;
		data[1] = groupNum;
		
		data_name[0] = "table_name";
		data_name[1] = "groupNum";
		
		
		Log.d("=====>", "FC_DB Get Lates Discuss setDone!");

		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_GetDiscussList(String course_ID,String type,String groupNum)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "14";
		String JsonorStr = "0";
		String table_name;
		
			table_name = "discusslist_" + course_ID + "_" + type;
		
		
		data[0] = table_name;
		data[1] = groupNum;
		
		data_name[0] = "table_name";
		data_name[1] = "groupNum";
		
		Log.d("=====>", "FC_DB Get List set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_GetGroupCount(String course_ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "6";
		String JsonorStr = "0";
		
		String table_name = "course_grouplist_" + course_ID;
		
		data[0] = table_name;
		
		data_name[0] = "table_name";
		
		Log.d("=====>", "FC_DB Get GroupCount set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_course_memberlist_uriAPI);
	}
	
	public void FC_BoardCourseList(String ID,String board_name,String groupNumber)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
		
		String table_name = ID;
		
		data[0] = table_name;
		data[1] = board_name;
		data[2] = groupNumber;
	
		data_name[0] = "table_name";
		data_name[1] = "board_name";
		data_name[2] = "groupNumber";

		Log.d("=====>", "Board Get Coursr List set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Discuss_uriAPI);
	}
	
	//多寫的
	/*public void FC_getBoardName(String tableName,String board_name,String groupNum){
		int count_for_data = 3;		
		
		String case_code = "16";
		String JsonorStr = "0";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = tableName;
		data[1] = board_name;
		data[2] = groupNum;
		
		data_name[0] = "tableName";
		data_name[1] = "boardName";
		data_name[2] = "groupNum";
		
		Log.d("=====>", "BoardName get is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}*/
	
	public void FC_BoardInsertText(String ID,String tage,String date,String time,String dialogue,String Account,String board_name,String groupNumber)
	{
		int count_for_data = 8;		
		
		String case_code = "2";
		String JsonorStr = "0";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		String table_name = ID;
		
		data[0] = table_name;
		data[1] = date;
		data[2] = time;
		data[3] = dialogue;
		data[4] = tage;
		data[5] = Account;
		data[6] = board_name;
		data[7] = groupNumber;
		
		data_name[0] = "table_name";
		data_name[1] = "date";
		data_name[2] = "time";
		data_name[3] = "dialogue";
		data_name[4] = "tage";
		data_name[5] = "ID";
		data_name[6] = "board_name";
		data_name[7] = "groupNumber";
		
		Log.d("=====>", "BoardText Insert student set Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Discuss_uriAPI);
	}
	
	public void FC_ShareThisGroupDiscuss(String tableName,String groupNum,String type){
		int count_for_data = 3;		
		
		String case_code = "17";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = tableName;
		data[1] = groupNum;
		data[2] = type;
		
		data_name[0] = "tableName";
		data_name[1] = "groupNum";
		data_name[2] = "type";
		
		Log.d("=====>", "FC_shareGroupDiscuss is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_ShareOtherGroupDiscuss(String tableName,String groupNum,String openTo,String type){
		int count_for_data = 4;		
		
		String case_code = "24";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = tableName;
		data[1] = groupNum;
		data[2] = openTo;
		data[3] = type;
		
		data_name[0] = "tableName";
		data_name[1] = "groupNumber";
		data_name[2] = "openTo";
		data_name[3] = "type";
		
		Log.d("=====>", "FC_shareGroupDiscuss is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_GetPrivateShareList(String tableName){
		int count_for_data = 1;		
		
		String case_code = "25";
		String JsonorStr = "0";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = tableName;
			
		data_name[0] = "tableName";
		
		
		Log.d("=====>", "FC_shareGroupDiscuss is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_deleteGroupDiscussList(String tableName,String now_course_ID){
		int count_for_data = 4;		
		
		String case_code = "18";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = tableName;
		data[1] = "discusslist_publicshare_"+now_course_ID+"_team";
		data[2] = "discusslist_privateshare_"+now_course_ID+"_team";
		data[3] = "discuss_"+now_course_ID+"_team";
		
		data_name[0] = "tableName";
		data_name[1] = "public";
		data_name[2] = "private";
		data_name[3] = "discussTable";
		
		Log.d("=====>", "deleteGroupList get is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
		startThread(data,data_name,count_for_data,"6",JsonorStr,Discuss_uriAPI);
	}
	
	public void FC_insertGroupDiscussList(String tableName,int groupNum){
		int count_for_data = 2;		
		
		String case_code = "20";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = tableName;
		data[1] = String.valueOf(groupNum);
		
		data_name[0] = "tableName";
		data_name[1] = "groupNum";
		
		Log.d("=====>", "insertGroupList get is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_createGroupDiscussList(String tableName){
		int count_for_data = 1;		
		
		String case_code = "19";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = tableName;
		
		data_name[0] = "tableName";
		
		Log.d("=====>", "createGroupList get is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_CheckGroupDiscussOpen(String tableName,String groupNum){
		int count_for_data = 2;		
		
		String case_code = "21";
		String JsonorStr = "0";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = tableName;
		data[1] = groupNum;
		
		data_name[0] = "tableName";
		data_name[1] = "groupNum";
		
		Log.d("=====>", "CheckGroupList is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_deleteBoard(String tableName,String groupNum,String Type,String BoardName,String course_ID){
		int count_for_data = 3;		
		
		String case_code = "22";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		
			data[0] = tableName;
			data[1] = "discuss_"+course_ID+"_"+Type;
			data[2] = BoardName;
		
			data_name[0] = "tableName";
			data_name[1] = "discussName";
			data_name[2] = "BoardName";

		
		Log.d("=====>", "DeleteBoard is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
		startThread(data,data_name,count_for_data,"3",JsonorStr,Discuss_uriAPI);
	}
	
	public void FC_getIamgeName(String tableName,String groupNum,String board_name){
		int count_for_data = 3;		
		
		String case_code = "4";
		String JsonorStr = "0";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = tableName;
		data[1] = groupNum;
		data[2] = board_name;
		
		data_name[0] = "tableName";
		data_name[1] = "group_number";
		data_name[2] = "board_name";
		
		
		Log.d("=====>", "CheckGroupList is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Discuss_uriAPI);
	}
	
	public void FC_UploadImage(String table_name,String groupNum,String imageName,String board_name){
		int count_for_data = 4;		
		
		String case_code = "7";
		String JsonorStr = "0";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		
			data[0] = table_name;
			data[1] = groupNum;
			data[2] = imageName;
			data[3] = board_name;
			
		
			data_name[0] = "table_name";
			data_name[1] = "groupNum";
			data_name[2] = "imageName";
			data_name[3] = "board_name";
			
		
		Log.d("=====>", "DeleteBoard is Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Discuss_uriAPI);
	}
	
	
	//---------------------以上為分組function-----------------------//
	public void FC_getAttendList(String table_name){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "6";   //取得點名清單
		String JsonorStr = "0";
		
		data[0]=table_name;
		
		
		data_name[0]="table_name";
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	public void FC_insertAttendList(String table_name,String course_ID,String starttime,String endtime){
		int count_for_data = 4;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "8";   //insert一筆新的資料進入table
		String JsonorStr = "0";
		
		data[0]=table_name;
		data[1]=course_ID;
		data[2]=starttime;
		data[3]=endtime;
		
		
		data_name[0]="table_name";
		data_name[1]="course_ID";
		data_name[2]="starttime";
		data_name[3]="endtime";
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	public void Remind_getCourseInfo(String ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";

		data[0] = ID;
	
		data_name[0] = "course_ID";

		Log.d("=====>", "Remind_DB Get ID info set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Remind_uriAPI);
	}
	
	public void Remind_getRemindInfo(String ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "0";

		data[0] = ID;
	
		data_name[0] = "title";

		Log.d("=====>", "Remind_DB Get ID info set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Remind_uriAPI);
	}
	
	public void Remind_Input(String course_ID,String title,String student_ID,String date,String note)
	{
		int count_for_data = 5;		
		String case_code = "3";
		String JsonorStr = "1";
		String data[]=new String [5];
		String data_name[]=new String [5];
		
		data[0] = course_ID;
		data[1] = title;
		data[2] = student_ID;
		data[3] = date;
		data[4] = note;
		
		data_name[0] = "course_ID";
		data_name[1] = "title";
		data_name[2] = "student_ID";
		data_name[3] = "date";
		data_name[4] = "note";
		Log.d("=====>", "Remind_DB Insert student set Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Remind_uriAPI);
	}

	public void Remind_getStuRemindInfo(String title)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "4";
		String JsonorStr = "0";

		data[0] = title;
	
		data_name[0] = "title";

		Log.d("=====>", "Remind_DB Get ID info set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Remind_uriAPI);
	}
	
	public void Remind_StdInput(String course_ID,String title,String date,String resources,String note)
	{
		int count_for_data = 5;		
		String case_code = "5";
		String JsonorStr = "1";
		String data[]=new String [5];
		String data_name[]=new String [5];
		
		data[0] = course_ID;
		data[1] = title;
		data[2] = date;
		data[3] = resources;
		data[4] = note;
		
		data_name[0] = "course_ID";
		data_name[1] = "title";
		data_name[2] = "date";
		data_name[3] = "resources";
		data_name[4] = "note";
		Log.d("=====>", "Remind_DB Insert student set Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Remind_uriAPI);
	}
	
	public void Quickpick_getCourseList(String ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
		
		String table_name = ID;
		
		data[0] = table_name;
	
		data_name[0] = "table_name";

		Log.d("=====>", "Quickpick Get Coursr List set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Quickpick_uriAPI);
	}
	
	public void Quickpick_updateCourseList(String ID,String serial,String tage_check)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "0";
		
		String table_name = "quicktext_" + ID;
		
		data[0] = table_name;
		data[1] = serial;
		data[2] = tage_check;
		data_name[0] = "table_name";
		data_name[1] = "serial";
		data_name[2] = "tage_check";
		Log.d("=====>", "Quickpick Get Coursr List set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Quickpick_uriAPI);
	}
	
	public void Quickpick_Insert(String ID,String tage,String date,String time,String dialogue)
	{
		int count_for_data = 5;		
		String case_code = "3";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		String table_name = "qp_quicktext_" + ID;
		
		data[0] = table_name;
		data[1] = tage;
		data[2] = date;
		data[3] = time;
		data[4] = dialogue;
		
		data_name[0] = "table_name";
		data_name[1] = "tage";
		data_name[2] = "date";
		data_name[3] = "time";
		data_name[4] = "dialogue";
		Log.d("=====>", "Remind_DB Insert student set Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Quickpick_uriAPI);
	}
	public void Quickpick_Insert_T(String class_ID,String stu_ID,String msg,String	time,String	date,String	tage_check,String serial)
	{
		int count_for_data = 7;		
		String case_code = "4";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		String table_name = "quicktext_" + class_ID;
		
		data[0] = table_name;
		data[1] = stu_ID;
		data[2] = msg;
		data[3] = time;
		data[4] = date;
		data[5] = tage_check;
		data[6] = serial;
		
		data_name[0] = "table_name";
		data_name[1] = "stu_ID";
		data_name[2] = "msg";
		data_name[3] = "time";
		data_name[4] = "date";
		data_name[5] = "tage_check";
		data_name[6] = "serial";
		Log.d("=====>", "Remind_DB Insert student set Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Quickpick_uriAPI);
	}

	
	public void Quickpick_getCourseListT(String ID,String lastSerial)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "5";
		String JsonorStr = "0";
		
		String table_name = ID;
		
		data[0] = table_name;
		data[1] = lastSerial;
	
		data_name[0] = "table_name";
		data_name[1] = "lastSerial";

		Log.d("=====>", "Quickpick Get Coursr List set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Quickpick_uriAPI);
	}
	
	//--------------以下為GCM Function------------------//
	public void GCM_sendMessageToALL(String message){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";      // 1 是推播某個訊息給所有使用者
		String JsonorStr = "1";
		
		data[0]=message;
		
		data_name[0]="message";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,GCM_uriAPI);
	}
	public void GCM_deleteRegID(String Account){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "99";        //99是刪除某個Account的RegID
		String JsonorStr = "1";
		
		data[0]=Account;
		
		data_name[0]="delAccount";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,GCM_uriAPI);
	}
	public void GCM_sendMessageToCourseMember(String course_ID,String message){
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";        //2是傳送訊息給某個班級
		String JsonorStr = "1";
		
		data[0]=course_ID;
		data[1]=message;
		
		data_name[0]="course_ID";
		data_name[1]="message";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,GCM_uriAPI);
	}
	public void GCM_sendMessageToCourseTeacher(String course_ID,String message){
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "3";        //3是傳送訊息給某個班級的老師
		String JsonorStr = "1";
		
		data[0]=course_ID;
		data[1]=message;
		
		data_name[0]="course_ID";
		data_name[1]="message";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,GCM_uriAPI);
	}
	public void GCM_sendMessageToOneUser(String account_ID,String message){
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "4";        //4是傳送訊息給某個帳號
		String JsonorStr = "1";
		
		data[0]=account_ID;
		data[1]=message;
		
		data_name[0]="account_ID";
		data_name[1]="message";
		startThread(data,data_name,count_for_data,case_code,JsonorStr,GCM_uriAPI);
	}
	
	//-------------------以上為GCM function-------------------------------//
	//-------------------以下為設定function--------------------------------//
	
	public void FC_insertCourse(String courseID,String courseName,String start_Date,String end_date,String Account_ID,String remind_day)
	{
		int count_for_data = 7;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "6";
		String JsonorStr = "1";
		
		String number_of_students = "0";

		data[0] = courseID;
		data[1] = courseName;
		data[2] = start_Date;
		data[3] = end_date;
		data[4] = Account_ID;
		data[5] = number_of_students;
		data[6] = remind_day;
		
	
		data_name[0] = "course_ID";
		data_name[1] = "course_name";
		data_name[2] = "start_date";
		data_name[3] = "end_date";
		data_name[4] = "teacher_account";
		data_name[5] = "number_of_students";
		data_name[6] = "remind_day";

		Log.d("=====>", "FC_DB Insert course info set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
	}
	
	public void insertaccount(String ID)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "0";
		
		String table_name = "account";
		
		data[0] = ID;
		data[1] = ID;
		data[2] = table_name;	
		
		data_name[0] = "id";
		data_name[1] = "code";
		data_name[2] = "table_name";
	
		
		Log.d("=====>", "FC_DB Insert Exam_info set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,DatabaseUrl);
	}
	
	public void Exam_getExamFromDatabase(String course_ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "14";
		String JsonorStr = "0";
		String table_name ="examtable_"+course_ID;
		
		data[0] = table_name;
		
	
		data_name[0] = "table_name";
		
		
		Log.d("=====>", "Remind_DB Get ID info set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_uriAPI);
	}
	
	public void FC_GetAllStdPreExamGrade(String course_ID, String exam_code)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "6";
		String JsonorStr = "0";
		
		String table_name = "pre_exam_grade_" + course_ID + "_" + exam_code;
		
		data[0] = table_name;
		
		data_name[0] = "table_name";
		
		Log.d("=====>", "FC_DB Get Exam setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_pre_exam_uriAPI);
	}
	
	public void FC_UpdataCourse(String next_Date,String end_date,String course_ID,String remind_day)
	{
		int count_for_data = 4;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "8";
		String JsonorStr = "1";

		data[0] = next_Date;
		data[1] = end_date;
		data[2] = course_ID;
		data[3] = remind_day;
		
	
		data_name[0] = "start_date";
		data_name[1] = "end_date";
		data_name[2] = "course_ID";
		data_name[3] = "remind_day";

		Log.d("=====>", "FC_DB Updata course info set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
	}
	
	public void FC_DeleteCourse(String course_ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "9";
		String JsonorStr = "1";

		data[0] = course_ID;
		
		data_name[0] = "course_ID";

		Log.d("=====>", "FC_DB Delete course in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
	}
	
	public void FC_insertPersonalCourse(String course_ID,String Account_ID)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "1";

		String table_name = "courselist_" + Account_ID;
		
		data[0] = table_name;
		data[1] = course_ID;
					
		data_name[0] = "table_name";
		data_name[1] = "course_ID";
	

		Log.d("=====>", "FC_DB Insert Personalcourse info set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_personal_courselsit_uriAPI);

	}
	
	public void FC_UpdateClassDate(String next_date,String Week,String course_ID)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "7";
		String JsonorStr = "0";
		
		data[0] = next_date;
		data[1] = course_ID;
		data[2] = Week;
					
		data_name[0] = "start_date";
		data_name[1] = "course_ID";
		data_name[2] = "Week";
	
		Log.d("=====>", "FC_DB Update ClassDate set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_uriAPI);
	}
	
	public void insertTest(String num,String ID,String Name,String Midtern,String Change)
	{
		int count_for_data = 6;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "1";
		
		String table_name = "test";
		
		data[0] = num;
		data[1] = ID;
		data[2] = Name;
		data[3] = Midtern;
		data[4] = Change;
		data[5] = table_name;
		
		data_name[0] = "num";
		data_name[1] = "ID";
		data_name[2] = "Name";
		data_name[3] = "Midtern";
		data_name[4] = "Change";
		data_name[5] = "table_name";
		
		
		Log.d("=====>", "FC_DB Insert Exam set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,test);
	}
	
	public void getStdProfile(String account_ID){
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "3";
		String JsonorStr = "0";
		
		data[0] = account_ID;
						
		data_name[0] = "account_ID";
	
		Log.d("=====>", "getStdProfile is Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,DatabaseUrl);
	}
	
	public void UpdataStdProfile(String content,String title,String account_ID){
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "4";
		String JsonorStr = "0";
		
		data[0] = account_ID;
		data[1] = content;
		data[2] = title;
			
		data_name[0] = "account_ID";
		data_name[1] = "content";
		data_name[2] = "title";
	
		Log.d("=====>", "UpdataStdProfile is Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,DatabaseUrl);
	}
	
	public void insertcoursememberlist(String ID,String course_ID)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "7";
		String JsonorStr = "1";
		
		String table_name = "course_memberlist_"+course_ID;
		
		data[0] = ID;
		data[1] = table_name;

		
		
		data_name[0] = "id";
		data_name[1] = "table_name";

		
		
		
		Log.d("=====>", "FC_DB Insert Exam_info set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_course_memberlist_uriAPI);
	}
	public void CreatPersonalCourselist(String ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "3";
		String JsonorStr = "1";
		
		String courselist_table = "courselist_"+ID;
		
		data[0] = courselist_table;

		
		
		data_name[0] = "courselist_table";

		
		
		
		Log.d("=====>", "FC_DB Insert Exam_info set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_personal_courselsit_uriAPI);
	}
	public void InsertPersonalCourselist(String ID,String course_ID)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "1";
		
		String table_name = "courselist_"+ID;
		
		data[0] = course_ID;
		data[1] = table_name;

		
		data_name[0] = "course_ID";
		data_name[1] = "table_name";

		
		
		
		Log.d("=====>", "FC_DB Insert Exam_info set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_personal_courselsit_uriAPI);
	}
	
	//--------------------以上為設定function---------------------//
	//--------------------以下為佈告欄function--------------------//	
	public void Bulletin_BoardInput(String course_ID,String title,String week,String date,String file,String note)
	{
		int count_for_data = 6;		
		String case_code = "5";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = course_ID;
		data[1] = title;
		data[2] = week;
		data[3] = date;
		data[4] = file;
		data[5] = note;
		
		
		data_name[0] = "course_ID";
		data_name[1] = "title";
		data_name[2] = "week";
		data_name[3] = "date";
		data_name[4] = "file";
		data_name[5] = "note";
		Log.d("=====>", "BBDB Insert  set Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Bulletin_Board_uriAPI);
	}
	
	public void Bulletin_Board_getcourseInfo(String course_ID,String week)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";

		data[0] = course_ID;
		data[1] = week;
	
		data_name[0] = "course_ID";
		data_name[1] = "week";
		
		Log.d("=====>", "Remind_DB Get ID info set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Bulletin_Board_uriAPI);
	}
	
	
	public void Bulletin_Board_InputChat(String course_ID,String title,String week,String date,String student_ID,String note)
	{
		int count_for_data = 6;		
		String case_code = "3";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = course_ID;
		data[1] = title;
		data[2] = week;
		data[3] = date;
		data[4] = student_ID;
		data[5] = note;
		
		data_name[0] = "course_ID";
		data_name[1] = "title";
		data_name[2] = "week";
		data_name[3] = "date";
		data_name[4] = "student_ID";
		data_name[5] = "note";

		Log.d("=====>", "Remind_DB Insert student set Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Bulletin_Board_uriAPI);
	}

	public void Bulletin_BoardgetChatInfo(String title,String week)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "4";
		String JsonorStr = "0";

		data[0] = title;
		data[1] = week;
	
		data_name[0] = "title";
		data_name[1] = "week";
		
		Log.d("=====>", "Remind_DB Get ID info set data in array Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Bulletin_Board_uriAPI);
	}
	
	public void Bulletin_BoardInput(String course_ID,String title,String week,String date,String file,String note,int exam_code,String exam_name)
	{
		int count_for_data = 8;		
		String case_code = "5";
		String JsonorStr = "1";
		String data[]=new String [count_for_data];
		String data_name[]=new String [count_for_data];
		
		data[0] = course_ID;
		data[1] = title;
		data[2] = week;
		data[3] = date;
		data[4] = file;
		data[5] = note;
		data[6] = String.valueOf(exam_code);
		data[7] = exam_name;
		
		
		data_name[0] = "course_ID";
		data_name[1] = "title";
		data_name[2] = "week";
		data_name[3] = "date";
		data_name[4] = "file";
		data_name[5] = "note";
		data_name[6] = "exam_times";
		data_name[7] = "exam_name";
		Log.d("=====>", "BBDB Insert  set Done!");		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,Bulletin_Board_uriAPI);
	}
	
	public void FC_CreatePreExam(String course_ID,int exam_code,String exam_name)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "1";

		Log.d("=====>", "GET IN Create!");
		
		data[0] = "pre_exam_" + course_ID + "_" + exam_code;
		data_name[0] = "exam_table";
		data[1] = "pre_exam_grade_" + course_ID + "_" + exam_code;
		data_name[1] = "grade_table";
		
		Log.d("=====>", "FC_DB Create table set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_pre_exam_uriAPI);
		
		count_for_data = 4;
		String data2[] = new String[count_for_data];
		String data_name2[] = new String[count_for_data];
		case_code = "23";
		JsonorStr = "1";
		
		data2[0] = "pre_examlist_" + course_ID;
		data_name2[0] = "exam_table";
		
		data2[1] = "pre_exam_gradelist_" + course_ID;
		data_name2[1] = "grade_table";
		
		data2[2] = "" + exam_code;
		data_name2[2] = "exam_code";
		
		data2[3] = "" + exam_name;
		data_name2[3] = "exam_name";
		
		Log.d("=====>", "FC_DB Create table set Done!");
		
		startThread(data2,data_name2,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}

	public void FC_InputPreExam(String ques,String chooseA,String chooseB,String chooseC,String chooseD,String point,String ans,String type,String course_ID,String exam_code)
	{
		int count_for_data = 9;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "1";
		
		String table_name = "pre_exam_" + course_ID + "_" + exam_code;
		
		data[0] = ques;
		data[1] = chooseA;
		data[2] = chooseB;
		data[3] = chooseC;
		data[4] = chooseD;
		data[5] = point;
		data[6] = ans;
		data[7] = type;
		data[8] = table_name;
		
		data_name[0] = "question_content";
		data_name[1] = "choose_A";
		data_name[2] = "choose_B";
		data_name[3] = "choose_C";
		data_name[4] = "choose_D";
		data_name[5] = "point";
		data_name[6] = "answer";
		data_name[7] = "type";
		data_name[8] = "table_name";
		
		Log.d("=====>", "FC_DB Insert Exam set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_pre_exam_uriAPI);
	}
	
	public void FC_InputPreExamGradeMember(String course_ID,String exam_code,String account_ID)
	{
		int count_for_data = 3;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "3";
		String JsonorStr = "1";
		
		String table_name = "pre_exam_grade_" + course_ID + "_" + exam_code;
		
		data[0] = account_ID;
		data[1] = "-1";
		data[2] = table_name;
		
		data_name[0] = "account_ID";
		data_name[1] = "grade";
		data_name[2] = "table_name";
		
		Log.d("=====>", "FC_DB Insert Exam set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_pre_exam_uriAPI);
	}
	public void FC_GetLatestPreExamCode(String course_ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "4";
		String JsonorStr = "1";
		
		String table_name = "pre_examlist_" + course_ID;
		
		data[0] = table_name;
		
		data_name[0] = "table_name";
		
		Log.d("=====>", "FC_DB Get Lates Exam setDone!");

		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	
	public void FC_GetPreExamList(String course_ID)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
		
		String table_name = "pre_examlist_" + course_ID;
		
		data[0] = table_name;
		data_name[0] = "table_name";
		
		Log.d("=====>", "FC_DB Get List set Done!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	public void FC_GetPreExamGrade(String account_ID,String course_ID)
	{
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "1";
		String JsonorStr = "0";
		
		String table_name = "pre_exam_gradelist_" + account_ID;
		
		data[0] = table_name;
		data[1] = course_ID;
		
		data_name[0] = "table_name";
		data_name[1] = "course_ID";
		
		Log.d("=====>", "FC_DB Get Exam setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_grade_uriAPI);
	}
	
	public void FC_GetPreExam(String course_ID,String exam_code)
	{
		int count_for_data = 1;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "5";
		String JsonorStr = "0";
				
		String table_name = "pre_exam_" + course_ID + "_" + exam_code;
		
		data[0] = table_name;
		data_name[0] = "table_name";
		
		Log.d("=====>", "FC_DB Get Exam setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_pre_exam_uriAPI);
	}
	public void FC_CheckPreExamOpen(String exam_code,String course_ID)
	{///完成
		int count_for_data = 2;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "9";
		String JsonorStr = "1";
				
		String table_name = "pre_examlist_" + course_ID;
		
		data[0] = exam_code;
		data[1] = table_name;
		
		data_name[0] = "exam_code";
		data_name[1] = "table_name";
		
		Log.d("=====>", "FC_DB CheckExamOpen setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_list_uriAPI);
	}
	public void FC_InsertPreExamGrade(String account_ID,String course_ID,int grade,String exam_code)
	{
		int count_for_data = 4;
		String data[] = new String[count_for_data];
		String data_name[] = new String[count_for_data];
		String case_code = "2";
		String JsonorStr = "1";
				
		String table_name = "pre_exam_gradelist_" + account_ID;
		
		data[0] = course_ID;
		data[1] = exam_code;
		data[2] = table_name;
		data[3] = Integer.toString(grade);
		
		data_name[0] = "course_ID";
		data_name[1] = "exam_code";
		data_name[2] = "table_name";
		data_name[3] = "grade";
		
		Log.d("=====>", "FC_DB InsertGrade setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_exam_grade_uriAPI);
		
		count_for_data = 3;
		data = new String[count_for_data];
		data_name = new String[count_for_data];
		case_code = "4";
		
		table_name = "pre_exam_grade_" + course_ID + "_" + exam_code;
		
		data[0] = account_ID;
		data[1] = Integer.toString(grade);
		data[2] = table_name;
		
		data_name[0] = "account_ID";
		data_name[1] = "grade";
		data_name[2] = "table_name";
		
		Log.d("=====>", "FC_DB InsertGrade setDone!");
		
		startThread(data,data_name,count_for_data,case_code,JsonorStr,FC_pre_exam_uriAPI);
	}
	
	
	
	
	
}
