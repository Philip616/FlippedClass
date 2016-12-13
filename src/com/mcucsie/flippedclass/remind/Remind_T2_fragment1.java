
package com.mcucsie.flippedclass.remind;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;
//import com.mcucsie.flippedclass.remind.RemindFragment.Resources.downloadTask;
import com.mcucsie.flippedclass.quickpick.QuidckList;


public class Remind_T2_fragment1 extends Fragment {
	private Button btn_download;
	private ProgressBar mProgressbar;
	private TextView mMessageView;
	private TextView tv_note,tv_title;
	Spinner sp;
	
	String ques[][];
	String title;
	int columns;
	private static Handler mHandler;
	private static Handler mHandler_2;
	private static Handler mHandler_3;
	private String[][] courselist;
	private int count_a;
	private int downloadedAllSize=0;
	private Boolean isRunning=false;
	int check_done=0;
	
	String[] courseInfo;
	String[][] course_remind;
	String[][] str;
	Remind_courseInfo frag_course;
	Remind_titleInfo titleinfo;
	int getcount=0;
	
	String downloadUrl = "";
	String fileName = "";
	int threadNum = 5;
	String filepath ;
	private GetNowCourseInfo gnci;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		tv_note = (TextView)getView().findViewById(R.id.tv_note);
		sp = (Spinner)getView().findViewById(R.id.remind_sp_t);
		tv_title  = (TextView)getView().findViewById(R.id.remind_t2_1_title);
		gnci=new GetNowCourseInfo(getActivity());
		if(!gnci.getNowCourseID().equals("null")){
			tv_title.setText(gnci.getNowCourseID()+"  "+gnci.getNowCourseName());
		}
		mHandler=new Handler();
		mHandler_2=new Handler();
		mHandler_3=new Handler();
		frag_course = new Remind_courseInfo(getActivity());
		courseInfo = frag_course.getCourseInfo();
		course_remind = frag_course.getCourselist();
		isRunning=true;
		Thread waitfordata = new Thread(new waiting());
		waitfordata.start(); 
		
		mProgressbar = (ProgressBar) getView().findViewById(R.id.download_progress);
		mMessageView = (TextView)getView().findViewById(R.id.download_message);
		btn_download=(Button)getView().findViewById(R.id.download_btn_1);
		btn_download.setOnClickListener(btn_download_Click);
		
		DataFromDatabase getPath = new DataFromDatabase();
		downloadUrl = getPath.downloadUrl;
	}
	
	protected static final int REFRESH_DATA = 0x00000001;
		
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  


	return inflater.inflate(R.layout.fragment_remind_t2_1, container, false);  
 
	}
	
	
	//----------------------------------以下為下載相關---------------------
	
	private  OnClickListener btn_download_Click=new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.download_btn_1) {
				doDownload();
			}
		}	
	};

	

	/**
	 * 下載準備工作，獲取SD卡路徑、開啟線程
	 */
	private void doDownload() {
		// 獲取SD卡路徑
		String path = Environment.getExternalStorageDirectory()
				+ "/FlippedClass_download/";
		File file = new File(path);
		// 如果SD卡目錄不存在創建
		if (!file.exists()) {
			file.mkdir();
		}
		// 設置progressBar初始化
		mProgressbar.setProgress(0);
 
		// 簡單起見，我先把URL和文件名稱寫死，其實這些都可以通過HttpHeader獲取到
		//String downloadUrl = "http://gdown.baidu.com/data/wisegame/91319a5a1dfae322/baidu_16785426.apk";
		//String fileName = "baidu_16785426.apk";
		//String downloadUrl = "http://10.0.2.2/upload/testimg.jpg";
		//String fileName = "testimg.jpg";
		int threadNum = 5;
		//String filepath = path + fileName;

		filepath = path + fileName;
		downloadUrl+=fileName;
		downloadTask task = new downloadTask(downloadUrl, threadNum, filepath);
		task.start();
	}

	/**
	 * 多線程文件下載
	 * 
	 * @author yangxiaolong
	 * @2014-8-7
	 */
	class downloadTask extends Thread {
		private String downloadUrl;// 下載連結地址
		private int threadNum;// 開啟的線程數
		private String filePath;// 保存文件路徑地址
		private int blockSize;// 每一個線程的下載量

		public downloadTask(String downloadUrl, int threadNum, String fileptah) {
			this.downloadUrl = downloadUrl;
			this.threadNum = threadNum;
			this.filePath = fileptah;
		}

		@Override
		public void run() {

			FileDownloadThread[] threads = new FileDownloadThread[threadNum];
			try {
				URL url = new URL(downloadUrl);
				
				URLConnection conn = url.openConnection();
				// 讀取下載文件總大小
				int fileSize = conn.getContentLength();
				if (fileSize <= 0) {
					System.out.println("讀取文件失敗");
					return;
				}
				// 設置ProgressBar最大的長度為文件Size
				mProgressbar.setMax(fileSize);

				// 計算每條線程下載的數據長度
				blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
						: fileSize / threadNum + 1;

				
				File file = new File(filePath);
				for (int i = 0; i < threads.length; i++) {
					// 啟動線程，分別下載每個線程需要下載的部分
					threads[i] = new FileDownloadThread(url, file, blockSize,
							(i + 1));
					threads[i].setName("Thread:" + i);
					threads[i].start();
				}

				boolean isfinished = false;
				downloadedAllSize = 0;
				while (!isfinished) {
					isfinished = true;
					// 當前所有線程下載總量
					downloadedAllSize = 0;
					for (int i = 0; i < threads.length; i++) {
						downloadedAllSize += threads[i].getDownloadLength();
						if (!threads[i].isCompleted()) {
							isfinished = false;
						}
					}
					// 通知handler去更新視圖組件
					Message msg = new Message();
					msg.getData().putInt("size", downloadedAllSize);
										if(isRunning){
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mProgressbar.setProgress(downloadedAllSize);

						float temp = (float) mProgressbar.getProgress()
								/ (float) mProgressbar.getMax();

						int progress = (int) (temp * 100);
						if (progress == 100) {
							//Toast.makeText(RemindFragment.this, "下載完成！", Toast.LENGTH_LONG).show();
						}
						mMessageView.setText("下載進度:" + progress + " %");
					}
				});
				}
					Thread.sleep(1000);// 休息1秒後再讀取下載進度
				}
			

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
}
//------------------------以上為下載
	
	
	class waiting implements Runnable
	{

		@Override
		public void run() {			
			// TODO Auto-generated method stub
			 try {
				 int times=0;
				 while(courseInfo[0]==null && times<=100){
				   Thread.sleep(100);				
				   Log.d("=====>", "ThreadDownload的多休息了1秒");	
				   times++;
				   course_remind = frag_course.getCourselist();
				   courseInfo = frag_course.getCourseInfo();
	
				 }
			 } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			if(courseInfo[0]!=null&&isRunning){
				mHandler_2.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						tv_note.setText(course_remind[0][4]);

						ArrayAdapter<String> lunchList; 
						lunchList = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, courseInfo);
						sp.setAdapter(lunchList);
						sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				            public void onItemSelected(AdapterView<?> parent, View view,
				                    int position, long id) {
				            	//Toast.makeText(getActivity(), "你選的是"+courseInfo[position], Toast.LENGTH_SHORT).show();
				            	tv_note.setText(course_remind[position][4]);
				            	fileName=course_remind[position][3];
				            	
				            	titleinfo = new Remind_titleInfo(courseInfo[position]);
				            	str=titleinfo.getTitleInfo();
				            	check_done=titleinfo.getCheck();
				            	getcount=titleinfo.getCount();
				            	Thread waitfordata = new Thread(new waitingListview(courseInfo[position]));
				        		waitfordata.start();
				            }

				            @Override
				            public void onNothingSelected(AdapterView<?> parent) {
				            }
						}); 
					}
				});
			} 
		}		
	}
	
	protected static final int REFRESH_DATA_1 = 0x00000001;
	/*
	Handler mHandler_2 = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			
			switch (msg.what)
			{
				// 顯示網路上抓取的資料
				case REFRESH_DATA_1:

					tv_note.setText(course_remind[0][4]);

					ArrayAdapter<String> lunchList; 
					lunchList = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, courseInfo);
					sp.setAdapter(lunchList);
					sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			            public void onItemSelected(AdapterView<?> parent, View view,
			                    int position, long id) {
			            	//Toast.makeText(getActivity(), "你選的是"+courseInfo[position], Toast.LENGTH_SHORT).show();
			            	tv_note.setText(course_remind[position][4]);
			            
			            	titleinfo = new Remind_titleInfo(courseInfo[position]);
			            	str=titleinfo.getTitleInfo();
			            	check_done=titleinfo.getCheck();
			            	getcount=titleinfo.getCount();
			            	Thread waitfordata = new Thread(new waitingListview(courseInfo[position]));
			        		waitfordata.start();
			            }

			            @Override
			            public void onNothingSelected(AdapterView<?> parent) {
			            }
					}); 

				break;
			}
		}
	};
	*/
	class waitingListview implements Runnable
	{
		private String courseTitle=null;
		
		public waitingListview(String courseTitle){
			this.courseTitle = courseTitle;
		}
				
		@Override
		public void run() {			
			// TODO Auto-generated method stub
			 try {
				 int times=0;
				 while(check_done==0 && times<=100){
				   Thread.sleep(100);				
				   Log.d("=====>", "ThreadDownload的多休息了1秒");	
				   times++;
				   str=titleinfo.getTitleInfo();
				   check_done=titleinfo.getCheck();
				   getcount=titleinfo.getCount();
				 }
			 } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			if(check_done==1&&isRunning){
				mHandler_3.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//Toast.makeText(getActivity(), str[0], Toast.LENGTH_LONG).show();
						ListView noticeList;
						noticeList = (ListView) getView().findViewById(R.id.remind_lv_t);	
							if(str[0][0].equals("empty")){
								String[] Text = {"暫無學生回覆"};
								noticeList.setAdapter(
										new ArrayAdapter<String>(
							         getActivity(), android.R.layout.simple_list_item_1, Text));
								noticeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

							}
							else{	            			      
				            	Remind_list adp = new Remind_list(getcount, getActivity(), str);			         
				            	noticeList.setAdapter(adp);
							}
				            	
					}
				});
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
	    this.isRunning=false;
	}
	/*
	Handler mHandler_3 = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			
			ListView noticeList;
			switch (msg.what)
			{
				// 顯示網路上抓取的資料
				case REFRESH_DATA_1:
							//Toast.makeText(getActivity(), str[0], Toast.LENGTH_LONG).show();
					noticeList = (ListView) getView().findViewById(R.id.remind_lv_t);	
						if(str[0][0].equals("empty")){
							String[] Text = {"暫無學生回覆"};
							noticeList.setAdapter(
									new ArrayAdapter<String>(
						         getActivity(), android.R.layout.simple_list_item_1, Text));
							noticeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

						}
						else{	            			      
			            	Remind_list adp = new Remind_list(getcount, getActivity(), str);			         
			            	noticeList.setAdapter(adp);
						}
			            	

				break;
			}
		}
	};
	*/
}
