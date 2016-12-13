package com.mcucsie.flippedclass.setting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SettingUploadDatabase extends Fragment{
	Button StdInfoOpen,StdInfoUpload,ExamInfoOpen,ExamInfoUpload,btn_StdInfo,btn_ExamInfo,uploadinfo;
	Uri csvUri;
	TextView StdInfoCsvpath,ExamInfoCsvpath;
	String[][] Data;
	String course;
	int datacount,sw=0,stdswitch=1,examswitch=1;
	ListView Csvlist;
	RelativeLayout StdInfo,ExamInfo;
	Handler mHandler;
	AlertDialog.Builder dialog;
	private ArrayAdapter<String> CsvlistAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_upload_to_database, container, false);
		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mHandler = new Handler(){
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
				case REFRESH_DATA:
					if(stdswitch==2)
					{	StdInfo.setVisibility(View.VISIBLE);
						btn_StdInfo.setBackgroundResource(R.drawable.arrow_down);
						stdswitch=3;

					}
					if(stdswitch==1)
					{	StdInfo.setVisibility(View.GONE);
						btn_StdInfo.setBackgroundResource(R.drawable.arrow_right);
						
					}
					if(examswitch==2)
					{
						ExamInfo.setVisibility(View.VISIBLE);
						btn_ExamInfo.setBackgroundResource(R.drawable.arrow_down);
						examswitch=3;
					}
					if(examswitch==1)
					{
						ExamInfo.setVisibility(View.GONE);
						btn_ExamInfo.setBackgroundResource(R.drawable.arrow_right);
					}
				}
			}
		};
		StdInfoOpen = (Button)getView().findViewById(R.id.StdInfoOpen);
		StdInfoUpload = (Button)getView().findViewById(R.id.StdInfoUpload);
		ExamInfoOpen = (Button)getView().findViewById(R.id.ExamInfoOpen);
		ExamInfoUpload = (Button)getView().findViewById(R.id.ExamInfoUpload);
		StdInfoCsvpath = (TextView)getView().findViewById(R.id.StdInfoCsvPath);
		ExamInfoCsvpath = (TextView)getView().findViewById(R.id.ExamInfoCsvPath);
		
		uploadinfo = (Button)getView().findViewById(R.id.uploadInfo);
		
		StdInfo = (RelativeLayout)getView().findViewById(R.id.StdInfolayout);
		ExamInfo = (RelativeLayout)getView().findViewById(R.id.ExamInfolayout);
		
		btn_StdInfo = (Button)getView().findViewById(R.id.btn_StdInfo);
		btn_ExamInfo = (Button)getView().findViewById(R.id.btn_ExamInfo);
		
		

		Csvlist = (ListView)getView().findViewById(R.id.Csvlist);
		CsvlistAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		Csvlist.setAdapter(CsvlistAdapter);
		Data = new String[1000][1000];
		dialog = new AlertDialog.Builder(getActivity());
		
		StdInfoOpen.setOnClickListener(btn_StdInfoOpen);
		ExamInfoOpen.setOnClickListener(btn_ExamInfoOpen);

		StdInfoUpload.setOnClickListener(btn_StdInfoUpload);
		ExamInfoUpload.setOnClickListener(btn_ExamInfoUpload);

		btn_StdInfo.setOnClickListener(btn_StdInfoClick);
		btn_ExamInfo.setOnClickListener(btn_ExamInfoClick);
		
		uploadinfo.setOnClickListener(btn_uploadInfo);
		
		GetNowCourseInfo gnci = new GetNowCourseInfo(getActivity());
		course=gnci.getNowCourseID();

	}
	
	private OnClickListener btn_StdInfoOpen = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//讀擋
			sw=1;
			showFileChooser();
		}
	};
	
	private OnClickListener btn_StdInfoUpload = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//上傳
			DataFromDatabase MySQL_DB_test = new DataFromDatabase();
			DataFromDatabase MySQL_DB_personal_courselist = new DataFromDatabase();
			for(int i=0;i<datacount;i++)
			{
			
			MySQL_DB_test.insertStdInfo(Data[i][0], Data[i][1], Data[i][2], Data[i][3], Data[i][4], Data[i][5], Data[i][6]);
			MySQL_DB_test.insertaccount(Data[i][0]);
			MySQL_DB_test.insertcoursememberlist(course,Data[i][0]);
			MySQL_DB_personal_courselist.CreatPersonalCourselist(Data[i][0]);
			
			}
			Thread WaitforCreatTable = new Thread(new TchWaitforCreatTable(MySQL_DB_personal_courselist));
			WaitforCreatTable.start();

			Toast.makeText(getActivity(), "上傳完成", Toast.LENGTH_LONG).show();
			CsvlistAdapter.clear();
		}
	};
	private OnClickListener btn_ExamInfoOpen = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//讀擋
			sw=2;
			showFileChooser();
			
		}
	};
	private OnClickListener btn_ExamInfoUpload = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			DataFromDatabase MySQL_DB_test = new DataFromDatabase();
			for(int i=0;i<datacount;i++)
			{
			MySQL_DB_test.insertExamInfo(course,Data[i][0], Data[i][1], Data[i][2], Data[i][3], Data[i][4], Data[i][5], Data[i][6]);
			}
			Toast.makeText(getActivity(), "上傳完成", Toast.LENGTH_LONG).show();
			CsvlistAdapter.clear();
		}
	};
	
	private OnClickListener btn_StdInfoClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			

			if(stdswitch==1)
			{		
			stdswitch=2;
			mHandler.sendEmptyMessage(REFRESH_DATA);
			}
			if(stdswitch==3)
			{
			stdswitch=1;
			mHandler.sendEmptyMessage(REFRESH_DATA);
			}
		}
	};
	
	private OnClickListener btn_ExamInfoClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(examswitch==1)
			{
			examswitch=2;
			mHandler.sendEmptyMessage(REFRESH_DATA);
			}
			if(examswitch==3)
			{
			examswitch=1;
			mHandler.sendEmptyMessage(REFRESH_DATA);
			}
			
		}
	};
	private OnClickListener btn_uploadInfo = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog.setTitle("上傳資訊");
			dialog.setMessage("匯入資料僅可用CSV擋\n上傳學生資料的格式\n{學號,姓名,學系,年級,\n性別(男1女0),email,"
					+ "line}\n上傳題庫的格式\n{題目,a選項,b選項,c選項,d選項,答案,單選或是非題(是非2 單選1)}");
			
			dialog.show();
		}
			
				
			
		
	};
	
	public void showFileChooser(){//檔案選擇器
		Intent opensource = new Intent(Intent.ACTION_GET_CONTENT);
		opensource.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		opensource.setType("*/*");//deleted
		//opensource.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Intent chosedesk = Intent.createChooser(opensource, "選擇上傳CSV檔案");
		startActivityForResult(chosedesk, 0);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
		if ( resultCode == Activity.RESULT_OK)
		{	

			Uri uri = data.getData();
						
			if(uri!=null){
				//Csvpath.setText(uri.getPath());
				csvUri = uri;
				if(sw==1)
				{
				StdInfoCsvpath.setText(uri.getPath());
				readStdInfoCSVfile();
				}
				if(sw==2)
				{
				ExamInfoCsvpath.setText(uri.getPath());
				readExamInfoCSVfile();	
				}
			}
			else {
                Toast.makeText(this.getView().getContext(), "檔案路徑不對請重新選擇", Toast.LENGTH_LONG).show();

			}
		}
		
		
	}
	 private void readStdInfoCSVfile(){
		 try {
		   File csv = new File(csvUri.getEncodedPath());
		   BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv)));
		   String data = "";	
		   String Csvdata = "";
		   int i=0;
		   CsvlistAdapter.clear();
		   while ((data = br.readLine()) != null) {
		    String[] sarray = data.split(",");
		    for(int j=0;j<sarray.length;j++)
		    {	Data[i][j]=sarray[j];
		    	Csvdata = Csvdata+" "+sarray[j];
			    Log.d("readCSVFile:Data:",i+" "+j+" "+Data[i][j]);

		    	if(j==sarray.length-1)
		    	{
		    	CsvlistAdapter.add(Csvdata);
		    	Csvdata = "";
		    	i++;
		    	datacount=i;
		    	}
			    //Log.d("readCSVFile:i:",String.valueOf(j));
		    }
		    }
		   if(Data[0][0].length()==9)
		   {
		    Data[0][0]=Data[0][0].substring(1,9);
		    Log.d("Data:",Data[0][0]);
		    Log.d("Data2:",String.valueOf(Data[0][0].length()));
		   }
		   br.close();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
		 }
	 private void readExamInfoCSVfile(){
		 try {
		   File csv = new File(csvUri.getEncodedPath());
		   BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv)));
		   String data = "";	
		   String Csvdata = "";
		   int i=0;
		   CsvlistAdapter.clear();
		   while ((data = br.readLine()) != null) {
		    String[] sarray = data.split(",");
		    for(int j=0;j<sarray.length;j++)
		    {	Data[i][j]=sarray[j];
		    	Csvdata = Csvdata+" "+sarray[j];
			    Log.d("readCSVFile:Data:",i+" "+j+" "+Data[i][j]);

		    	if(j==sarray.length-1)
		    	{
		    	CsvlistAdapter.add(Csvdata);
		    	Csvdata = "";
		    	i++;
		    	datacount=i;
		    	}
			    //Log.d("readCSVFile:i:",String.valueOf(j));
		    }
		    }
		   br.close();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
		 }
		protected static final int REFRESH_DATA = 0x00000001;
		
		class TchWaitforCreatTable implements Runnable{
			
		DataFromDatabase MySQL_DB_personal_courselist;
		int times = 0;

		public TchWaitforCreatTable(DataFromDatabase MySQL_DB) 
		{
			this.MySQL_DB_personal_courselist=MySQL_DB;
		}	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(500);
					String msg = MySQL_DB_personal_courselist.return_result;
					while(msg==null && times<=10)
					{
						Thread.sleep(1000);
						 msg = MySQL_DB_personal_courselist.return_result;
						 times++;
					}
					Log.d("=====>", "Table Created:" + msg);
					for(int i=0;i<datacount;i++)
					{
					MySQL_DB_personal_courselist.InsertPersonalCourselist(course,Data[i][0]);
					}
					} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			
		}
}
