
package com.mcucsie.flippedclass.remind;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;




import com.mcucsie.flippedclass.remind.Remind_T2_fragment1.downloadTask;
import com.mcucsie.flippedclass.remind.Remind_T2_fragment1.waiting;
import com.mcucsie.flippedclass.remind.Remind_T2_fragment1.waitingListview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class Remind_S extends Fragment {
	private TextView tv_note,tv_title;
	private EditText ed_note;
	private Button btn_note;
	private Button btn_download;
	
	private String Account;
	private String course_ID;
	private String title;
	int columns;
	int rows_num;
	
	private int downloadedAllSize;
	private static Handler mHandler;
	private static Handler mHandler_2;
	private String a;
	private String[][] courselist;
	private int count_a;
	private Boolean isRunning=false;
	
	Remind_courseInfo frag_course;
	String[] courseInfo;
	String[][] course_remind;
	Spinner sp;
	String S_course_ID,S_title;
	
	/** ��ܤU���i��TextView */
	private TextView mMessageView;
	/** ��ܤU���i��ProgressBar */
	private ProgressBar mProgressbar;
	
	String downloadUrl = "";
	String fileName = "";
	int threadNum = 5;
	String filepath ;
	private GetNowCourseInfo gnci;
	@Override
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		return inflater.inflate(R.layout.fragment_remind_resources, container, false);
	}
	 
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		gnci=new GetNowCourseInfo(getActivity());
		if(!gnci.getNowCourseID().equals("null")){
			tv_title = (TextView)getView().findViewById(R.id.tv_group_1);
			tv_title.setText(gnci.getNowCourseID()+"  "+gnci.getNowCourseName());
		} 
		mHandler=new Handler(); 
		mHandler_2=new Handler();
		tv_note = (TextView)getView().findViewById(R.id.tv_note1);
		btn_note=(Button)getView().findViewById(R.id.btn_note);
		sp = (Spinner) getView().findViewById(R.id.remind_sp_s);
		
		frag_course = new Remind_courseInfo(getActivity());
		courseInfo = frag_course.getCourseInfo();
		course_remind = frag_course.getCourselist();
		
		GetNowAccountInfo account_info = new GetNowAccountInfo(getActivity());
		Account = account_info.getNowAccountID();
		
		isRunning=true;
		Thread waitfordata = new Thread(new waiting());
		waitfordata.start(); 

		btn_download=(Button)getView().findViewById(R.id.download_btn_1_1);
		mMessageView = (TextView)getView().findViewById(R.id.download_message1);
		mProgressbar = (ProgressBar)getView().findViewById(R.id.download_progress1);
		btn_download.setOnClickListener(btn_download_Click);

		DataFromDatabase getPath = new DataFromDatabase();
		downloadUrl = getPath.downloadUrl;
	}
	

	
	
	
	private  OnClickListener btn_note_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ed_note=(EditText)getView().findViewById(R.id.ed_note);
			String note;
            note = ed_note.getText().toString();
            ed_note.setText("");
			
            //����w�q�ɶ��榡
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            //���o�{�b�ɶ�
            Date dt=new Date();
            //�z�LSimpleDateFormat��format��k�NDate�ର�r��
            String dts=sdf.format(dt);

            
			DataFromDatabase MySQL_DB_btn_note = new DataFromDatabase();
			MySQL_DB_btn_note.Remind_Input(S_course_ID,S_title,Account,dts,note);
			Toast.makeText(getActivity(), "�W�Ǧ��\", Toast.LENGTH_LONG).show();		
			MySQL_DB_btn_note.GCM_sendMessageToCourseTeacher(S_course_ID, "TAG3:"+Account+" �w�s�W�^�_");				
		}
	};		
	
	//----------------�H�U���U��
	private  OnClickListener btn_download_Click=new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.download_btn_1_1) {
				doDownload();
			}
		}	
	};

	

	/**
	 * �U���ǳƤu�@�A���SD�d���|�B�}�ҽu�{
	 */
	private void doDownload() {
		// ���SD�d���|
		String path = Environment.getExternalStorageDirectory()
				+ "/FlippedClass_download/";
		File file = new File(path);
		// �p�GSD�d�ؿ����s�b�Ы�
		if (!file.exists()) {
			file.mkdir();
		}
		// �]�mprogressBar��l��
		mProgressbar.setProgress(0);
 
		// ²��_���A�ڥ���URL�M���W�ټg���A���o�ǳ��i�H�q�LHttpHeader�����
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
	 * �h�u�{���U��
	 * 
	 * @author yangxiaolong
	 * @2014-8-7
	 */
	class downloadTask extends Thread {
		private String downloadUrl;// �U���s���a�}
		private int threadNum;// �}�Ҫ��u�{��
		private String filePath;// �O�s�����|�a�}
		private int blockSize;// �C�@�ӽu�{���U���q

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
				// Ū���U������`�j�p
				int fileSize = conn.getContentLength();
				if (fileSize <= 0) {
					System.out.println("Ū����󥢱�");
					return;
				}
				// �]�mProgressBar�̤j�����׬����Size
				mProgressbar.setMax(fileSize);

				// �p��C���u�{�U�����ƾڪ���
				blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
						: fileSize / threadNum + 1;

				
				File file = new File(filePath);
				for (int i = 0; i < threads.length; i++) {
					// �Ұʽu�{�A���O�U���C�ӽu�{�ݭn�U��������
					threads[i] = new FileDownloadThread(url, file, blockSize,
							(i + 1));
					threads[i].setName("Thread:" + i);
					threads[i].start();
				}

				boolean isfinished = false;
				downloadedAllSize = 0;
				while (!isfinished) {
					isfinished = true;
					// ��e�Ҧ��u�{�U���`�q
					downloadedAllSize = 0;
					for (int i = 0; i < threads.length; i++) {
						downloadedAllSize += threads[i].getDownloadLength();
						if (!threads[i].isCompleted()) {
							isfinished = false;
						}
					}
					// �q��handler�h��s���ϲե�
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
							//Toast.makeText(RemindFragment.this, "�U�������I", Toast.LENGTH_LONG).show();
						}
						mMessageView.setText("�U���i��:" + progress + " %");
					}
				});
				}
					Thread.sleep(1000);// ��1���AŪ���U���i��
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
	//------------------------�H�W���U��
	
	
	
class waiting implements Runnable
{
	@Override
	public void run() {			
		// TODO Auto-generated method stub
		 try {
			 int times=0;
			 while(courseInfo[0]==null && times<=100){
			   Thread.sleep(100);				
			   Log.d("=====>", "ThreadDownload���h�𮧤F1��");	
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
					ArrayAdapter<String> lunchList; 
					lunchList = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, courseInfo);
					sp.setAdapter(lunchList);
					sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			            public void onItemSelected(AdapterView<?> parent, View view,
			                    int position, long id) {
			            	//Toast.makeText(getActivity(), "�A�諸�O"+courseInfo[position], Toast.LENGTH_SHORT).show();
			            	tv_note.setText(course_remind[position][4]);        
			            	S_course_ID = course_remind[position][0];
			            	S_title = course_remind[position][1];
			            	fileName=course_remind[position][3];
			            	
			            	btn_note.setOnClickListener(btn_note_click);
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

@Override
public void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
	endAllThread();
}

public void endAllThread(){
    this.isRunning=false;
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
			// ��ܺ����W��������
			case REFRESH_DATA_1:

				

				
				ArrayAdapter<String> lunchList; 
				lunchList = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, courseInfo);
				sp.setAdapter(lunchList);
				sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		            public void onItemSelected(AdapterView<?> parent, View view,
		                    int position, long id) {
		            	//Toast.makeText(getActivity(), "�A�諸�O"+courseInfo[position], Toast.LENGTH_SHORT).show();
		            	tv_note.setText(course_remind[position][4]);        
		            	S_course_ID = course_remind[position][0];
		            	S_title = course_remind[position][1];
		        		btn_note.setOnClickListener(btn_note_click);
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

}
