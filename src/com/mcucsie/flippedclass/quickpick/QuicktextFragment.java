package com.mcucsie.flippedclass.quickpick;



import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.MainActivity;
import com.mcucsie.flippedclass.R;
import android.widget.PopupWindow.OnDismissListener;

public class QuicktextFragment extends Fragment{
	private EditText et_quicktext_input;
	private Button btn_quicktext_send;
	private Button btn_quicktext_add,piccount;
	private String course_ID;
	private String quicktext_tablename;
	private String Account;
	private String getOldResult=null;
	private Byte check_ID;   //0=學生  1=老師
	private TextView tv_title;
	
	private String dialogue;
	private String[][] list;
	private int count_a;
	private int check_send=0;
	
	
	GetNowCourseInfo course_info;	
	private pickpopwindow ppw;
	//---------------------
	private GetNowCourseInfo gnci;
	int xy=0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	
		 return inflater.inflate(R.layout.fragment_quickpick_quicktext, container, false);
		 
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getArguments();
		Account = bundle.getString("Account");
		check_ID = bundle.getByte("check_ID");
		Toast.makeText(getActivity(), Account, Toast.LENGTH_LONG).show();
		
		
		MainActivity tag = new MainActivity();
		if(tag.quickpick_page_tag == 1)
			tag.quickpick_page_tag = 2;
	//----------------------------------------------
	
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
		
		tv_title = (TextView)getView().findViewById(R.id.tv_qt_title);
		if(course_ID.equals("null"))
			tv_title.setText("─ "+"請選擇課程"+"─\n");		
		else
		{
			gnci=new GetNowCourseInfo(getActivity());
			tv_title.setText(gnci.getNowCourseID()+"  "+gnci.getNowCourseName());	
			
			quicktext_tablename = "qp_quicktext_"+course_ID+"_"+Account;
			DataFromDatabase MySQL_DB = new DataFromDatabase();
			MySQL_DB.Quickpick_getCourseList(quicktext_tablename);
			Thread waitfordata = new Thread(new getdata(MySQL_DB));
			waitfordata.start();			
		}
	 	
		
	//----------------------------------------------
				

		piccount = (Button)getView().findViewById(R.id.btn_piccount);
		piccount.setVisibility(View.GONE);
		
		et_quicktext_input=(EditText)getView().findViewById(R.id.et_qt_input);
		btn_quicktext_send=(Button)getView().findViewById(R.id.btn_qt_send);
	    btn_quicktext_send.setOnClickListener(btn_quicktext_send_click);
	    btn_quicktext_add = (Button)getView().findViewById(R.id.btn_add);
	    btn_quicktext_add.setOnClickListener(btn_quicktext_add_click);
	}
	
	
	class getdata implements Runnable
	{

    	private DataFromDatabase MySQL_DB;
		// 建構子，設定要傳的字串
		public getdata(DataFromDatabase MySQL_DB_1)
		{
			this.MySQL_DB=MySQL_DB_1;
		}

		@Override
		public void run()
		{	
			// TODO Auto-generated method stub
			String result=null;
			int times=0;
			try {
					//Thread.sleep(1000);
					result=MySQL_DB.return_result;
					
					while(times<=10&&result==null){
					   Thread.sleep(1000);
					   result=MySQL_DB.return_result;
					   Log.d("=====>", "ThreadDownload的多休息了1秒");	
					   times++;
					}
					
					if(check_send==1){
						check_send=0;
						int times2=0;
						int test=0;
						while(getOldResult!=null && getOldResult.equals(result) ){
						quicktext_tablename = "qp_quicktext_"+course_ID+"_"+Account;
						MySQL_DB.Quickpick_getCourseList(quicktext_tablename);
						result=MySQL_DB.return_result;
						
							while(times2<=10&&result==null){
								Thread.sleep(1000);
								result=MySQL_DB.return_result;
								Log.d("=====>", "ThreadDownload的多休息了1秒");	
								times2++;
							}
							test++;
						}
						Log.d("============test>", Integer.toString(test));
					}
					
					
					if(times<=10){
						Log.d("=====>", "成功抓取ID的對話"+result);
	
						
						JSONArray jsonArray;
						try {
								jsonArray = new JSONArray(result);							
								list = new String[jsonArray.length()][2];
								
								for(int i = 0 ; i<jsonArray.length() ; i++)
								{
									
									JSONObject jsonData = jsonArray.getJSONObject(i);
									if(check_ID==0){
										if(jsonData.getString("tage").equals("0"))
											list[i][0] = "我" +"  日期:"+jsonData.getString("date")+"  時間:"+jsonData.getString("time");
										else
											list[i][0] = "老師" +"  日期:"+jsonData.getString("date")+"  時間:"+jsonData.getString("time");
									}
									else if(check_ID==1){
										if(jsonData.getString("tage").equals("0"))
											list[i][0] =Account  +"  日期:"+jsonData.getString("date")+"  時間:"+jsonData.getString("time");
										else
											list[i][0] = "我" +"  日期:"+jsonData.getString("date")+"  時間:"+jsonData.getString("time");				
									}
									list[i][1] = jsonData.getString("dialogue");
									
									Log.d("=====>", "Quickpick_Fragment 找到的Json = "+list[i]);
									String a = list[i][1]+"  ";
									Log.d("=====>", a);									
								}
								count_a = jsonArray.length();
								mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						getOldResult = result;
					}
					else
					{
						Log.d("=====>", "ThreadDownload下載太多次，下載失敗..."+times);
					}
					
					
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	protected static final int REFRESH_DATA = 0x00000001;
	
	Handler mHandler = new Handler()
	{
		
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// 顯示網路上抓取的資料
				case REFRESH_DATA:
					
					ListView list_quickpick = (ListView)getView().findViewById(R.id.lv_qt);
					//String course[] = new String[rows_num];	
					Log.d("===============>","計數"+count_a);
					
					if(count_a>0){			
						if(!list[0][0].equals("我" +"  日期:empty"+"  時間:")){
							QuidckList adp = new QuidckList(count_a, getActivity(), list);
							list_quickpick.setAdapter(adp);
						}
						else{
							String course_1[]=new String[1];
							course_1[0]="暫無相關資料";
							list_quickpick.setAdapter(new ArrayAdapter<String>(getActivity(),
					                android.R.layout.simple_list_item_1, course_1));			
						}					
					}
					else{
							String course_1[]=new String[1];
							course_1[0]="暫無相關資料";
							list_quickpick.setAdapter(new ArrayAdapter<String>(getActivity(),
					                android.R.layout.simple_list_item_1, course_1));			
					}
					if(check_ID==1){
						tv_title.setText("─ "+Account+"的訊息 ─\n");
					}
					list_quickpick.setChoiceMode(ListView.CHOICE_MODE_SINGLE);																													
				break;
			}
		}
	};
	
	private OnClickListener btn_quicktext_add_click = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub     	
			ppw=new pickpopwindow(getActivity(), Account, v,check_ID);		
			ppw.setOnDismissListener(dismiss_listener);		
		}
	};
	
	private OnDismissListener dismiss_listener=new OnDismissListener() {

		@Override
		public void onDismiss() {
			// TODO Auto-generated method stub
			String picID = null; 

			picID= ppw.sendback();
			if(picID!=null){
				sendmsg(picID);
			} 
		}
		
	};
	
	private OnClickListener btn_quicktext_send_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			EditText ed_input = (EditText)getView().findViewById(R.id.et_qt_input);
			dialogue = ed_input.getText().toString();
			if(!dialogue.equals("")){
				ed_input.setText("");
				sendmsg(dialogue);
			}
		}
	};
	
	public void sendmsg(String dlog){	
		check_send=1;
		if(course_ID.equals("null")){
			Toast.makeText(getActivity(), "尚未選擇課程", Toast.LENGTH_LONG).show();
		}
		else{
			String []a=new String[5];
			String []b=new String[7];
			
			
			//先行定義時間格式
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            //取得現在日期
            Date dt=new Date();
            //透過SimpleDateFormat的format方法將Date轉為字串
            String data=sdf.format(dt);
            
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            //取得現在時間
            Date dt2=new Date();
            //透過SimpleDateFormat的format方法將Date轉為字串
            String time=sdf2.format(dt2);
			
			if(check_ID==0)
				a[1]="0";
			else
				a[1]="1";
			this.dialogue = dlog;
			a[2]=data;
			a[3]=time;
			a[4]=dialogue;
		
			DataFromDatabase MySQL_DB = new DataFromDatabase();
			MySQL_DB.Quickpick_Insert(course_ID+"_"+Account, a[1], a[2], a[3], a[4]);
		
			if(check_ID!=1){
				b[0]=course_ID;
				b[1]=Account;
				b[2]=dialogue;
				b[3]="23:25";
				b[4]="06/04";
				b[5]="0";
				b[6]=Integer.toString(count_a);
				DataFromDatabase MySQL_DB_1 = new DataFromDatabase();
				MySQL_DB_1.Quickpick_Insert_T(b[0], b[1], b[2], b[3], b[4], b[5], b[6]);	
			} 		
			
			
			String tablename = "qp_quicktext_"+course_ID+"_"+Account;
			DataFromDatabase MySQL_Refresh = new DataFromDatabase();
			MySQL_Refresh.Quickpick_getCourseList(tablename);	
			/*
			int x=0;
			MySQL_Refresh.Quickpick_getCourseList(tablename);			
			while(getResult==null && x<=10){	
				for(int delay_a=0;delay_a<=100;delay_a++){				
					for(int delay_b=0;delay_b<=100;delay_b++){		
					}
				}		
				getResult = MySQL_Refresh.return_result;
				x++;
			}
			Log.d("=========================getResult==>", "getResult= "+getResult);		
			Log.d("=========================getOldResult==>", "getOldResult= "+getOldResult);
			Toast.makeText(getActivity(), Integer.toString(x), Toast.LENGTH_LONG).show();
			*/
			
			
			Thread waitfordata = new Thread(new getdata(MySQL_Refresh));
			waitfordata.start();
		 
			Toast.makeText(getActivity(), "傳送完畢!", Toast.LENGTH_LONG).show();
			
			GetNowAccountInfo account_info = new GetNowAccountInfo(getActivity());			
			String account_Type = account_info.getNowAccountType();
			if(account_Type.equals("teacher"))
				MySQL_Refresh.GCM_sendMessageToOneUser(Account,"TAG3:"+course_ID+"回覆了訊息" );
			else if(account_Type.equals("student"))
				MySQL_Refresh.GCM_sendMessageToCourseTeacher(course_ID, "TAG3:"+Account+"傳送了一則新訊息");
		}
	}
	
	public void init_quicktext(){
		//tv_title.setText("─ "+"請選擇課程"+"的訊息列表 ─\n");
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
		
		
		if(course_ID.equals("null"))
			tv_title.setText("─ "+"請選擇課程"+" ─\n");		
		else
		{
			gnci=new GetNowCourseInfo(getActivity());
			tv_title.setText(gnci.getNowCourseID()+"  "+gnci.getNowCourseName());	
			
			DataFromDatabase MySQL_DB = new DataFromDatabase();
			quicktext_tablename = "qp_quicktext_"+course_ID+"_"+Account;
			MySQL_DB.Quickpick_getCourseList(quicktext_tablename);
			Thread waitfordata = new Thread(new getdata(MySQL_DB));
			waitfordata.start();
		}
		Log.d("=====================>", "速貼重新整理完畢");
	
	}  
	
}