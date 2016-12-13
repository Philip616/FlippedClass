package com.mcucsie.flippedclass;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.R;
import com.mcucsie.flippedclass.exam.ExamCreatExamFromDatabaseAdapter;
import com.mcucsie.flippedclass.exam.ExamGridInfoAdapter;
import com.mcucsie.flippedclass.exam.ExamGridListTitleAdapter;
import com.mcucsie.flippedclass.remind.FileDownloadThread;
import com.mcucsie.flippedclass.remind.Remind_list;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

public class BulletinBoardMain extends Fragment{
	
	protected ListView noticeList;
	private TextView user_tv;
	private GetNowCourseInfo gnci;
	private GetNowAccountInfo gnai;
	private String Type ,course_ID,filepath,account_ID;
	private String downloadUrl="";
	private String[] item = new String[]{"第一週","第二週","第三週","第四週","第五週","第六週","第七週","第八週","第九週","第十週","第十一週","第十二週","第十三週","第十四週","第十五週","第十六週"};
	private static Handler mHandler;
	private static Handler mHandler_1;
	AlertDialog.Builder dialog,dialog2;
	ArrayList<HashMap<String, String>> list_item,viewlist,gradelist;
	BulletinBoardListAdapter BBListAdapter;
	private GridView gv_title;
	private GridView gv_info;
	ProgressBar pgb,pgb2;
	String[] courseInfo;
	String[][] course_remind;
	//private ArrayAdapter<String> listAdapter;
	public Spinner spinner;
	BulletinBoardCourseInfo frag_course;
	private Boolean isRunning=false;
	public int num,week,positionnum;
	private int downloadedAllSize=0;
	Thread 	waitfordata; 
	private BBPopupUpload pw;
	private BBPopupChatDl pw_ChDl;
	private EditText BBsettitle,BBeditnote;
	private TextView file_path,BBTitle,BBFile,BBRemind;
	public Uri Pwuri=null;
	private ListView BBChatlistView;
	private ProgressBar mProgressbar;
	ProgressDialog uploaddialog = null;
	BulletinBoardChatInfo chatinfo;
	DataFromDatabase getPath ,MySQL_DB_Exam,MySQL_DB_Grade,MySQL_DB_PreExamGrade;
	View grade;
	boolean downloadGradeinfo_runnable_isRunning = false;
	String[][] str;
	String[] info_account_ID ;
	String[] info_grade;
	Button btn_upload;
	int count_for_member;
	int check_done=0;
	int getcount=0;
	int RefreshNum;
	int nofile=0;
	int exam_code=0;
	String exam_name = "";
	Bundle bundle,bundle_setView;
	@Override
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
		Log.d("=====>", "Create MainFragmain success!");
		return inflater.inflate(R.layout.fragment__bulletin_board_main, container, false);  

	 }
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		dialog = new AlertDialog.Builder(getActivity());
		//user_tv = (TextView) getView().findViewById(R.id.tv_main_1);
		gnci=new GetNowCourseInfo(getActivity());
		gnai=new GetNowAccountInfo(getActivity());
		Type=gnai.getNowAccountType();//判斷是老師或學生並改變布告欄輸出模式
		course_ID=gnci.getNowCourseID();
		account_ID = gnai.getNowAccountID();
		week=1;
		pgb2=(ProgressBar)getView().findViewById(R.id.pgb_BBmain);
		user_tv=(TextView)getView().findViewById(R.id.BBlayout_Title);
		pgb2.setVisibility(View.VISIBLE);
		pgb2.setVisibility(View.FOCUS_FORWARD);
		bundle_setView = new Bundle();
		mHandler=new Handler();
		mHandler_1=new Handler(){
			
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
				case REFRESH_DATA:
					if(RefreshNum==1)
				     //runtheThread(week);
					{	        
						resetListView();
					}
					if(RefreshNum==2)
				     {  chatinfo = new BulletinBoardChatInfo(course_remind[positionnum][1], String.valueOf(week));
			        	str=chatinfo.getTitleInfo();
			        	check_done=chatinfo.getCheck();
			        	getcount=chatinfo.getCount();
						Thread waitfordata = new Thread(new waitingListview(course_remind[positionnum][1]));
			    		waitfordata.start();}
				break;
								
				}
			}
		};
		isRunning=true;
		getPath = new DataFromDatabase();
		downloadUrl = getPath.downloadUrl;
		setonlistview();
		setonspinner();
		btn_upload = (Button)getView().findViewById(R.id.BBupload);
		btn_upload.setOnClickListener(upload_onclick);
		if(Type.equals("student"))
		{
			btn_upload.setVisibility(View.GONE);
		}
		bundle = getArguments();
        if (bundle != null) {
            pw = new BBPopupUpload(getActivity(), getView());
        	pw.BundleReturn(bundle.getString("title"), bundle.getString("filepath"), bundle.getString("note"));
			pw.setOnDismissListener(listener);
			exam_code = Integer.parseInt(bundle.getString("exam_code"));
			exam_name = bundle.getString("exam_name");
			bundle.clear();
        }
        resetListView();
		
	}
	
	public void setonspinner(){
	spinner = (Spinner)getView().findViewById(R.id.spinner_mainlayout);
	ArrayAdapter<String> spinadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, item);
	spinadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
	spinner.setAdapter(spinadapter);
	initTextView();
	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
     public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) 
	 {	
 			week=position+1;
			pgb2.setVisibility(View.VISIBLE);
			pgb2.setVisibility(View.VISIBLE);
			pgb2.setVisibility(View.FOCUS_FORWARD);
			noticeList.setVisibility(View.GONE);
 	        resetListView();

	  }
		  

	   public void onNothingSelected(AdapterView<?> arg0) {
		       // TODO Auto-generated method stub
	   }
	   });//以上為對SPINNER連動Listview的監聽設置*/
	   }
	
	OnClickListener upload_onclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			pw = new BBPopupUpload(getActivity(), getView());
     		pw.setOnDismissListener(listener);
		}
	};
	
	
	public void setonlistview(){
		noticeList = (ListView)getView().findViewById(R.id.listView_mainlayout);
		noticeList.setAdapter(BBListAdapter);
		}
		
		private OnDismissListener listener = new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub

			}
		};
		
		private OnDismissListener listener2 = new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				
			}
		};
		
		
		
	public void resetListView(){
		    MySQL_DB_Exam = new DataFromDatabase();
			MySQL_DB_Exam.FC_GetPreExamList(course_ID);
					
			MySQL_DB_Grade = new DataFromDatabase();
			MySQL_DB_Grade.FC_GetPreExamGrade(account_ID, course_ID);
			
			DataFromDatabase MySQL_DB_BBData = new DataFromDatabase();
			MySQL_DB_BBData.Bulletin_Board_getcourseInfo(course_ID, String.valueOf(week));
			
			Thread waitforexamgrade = new Thread(new BulletinBoardWaitForBBDataAndExamInfo(MySQL_DB_BBData,MySQL_DB_Exam, MySQL_DB_Grade));
	        waitforexamgrade.start();
	}
	//spinner當前狀態儲存
	public void showFileChooser(){//檔案選擇器
		Intent opensource = new Intent(Intent.ACTION_GET_CONTENT);
		opensource.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		opensource.setType("*/*");
		Intent chosedesk = Intent.createChooser(opensource, "選擇上傳檔案");
		startActivityForResult(chosedesk, 0);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
		if ( resultCode == Activity.RESULT_OK)
		{	
			Uri uri = data.getData();
			if (uri != null) {
				// 單選
				File[] file = getFilesFromUris(getActivity(), new Uri[]{uri});
				file_path.setText(file[0].getAbsolutePath());
				Pwuri=Uri.parse(file[0].getAbsolutePath());
				}
			else {
                Toast.makeText(this.getView().getContext(), "檔案路徑不對請重新選擇", Toast.LENGTH_LONG).show();

			}}
		/*	Uri uri = data.getData();
					
			if(uri!=null){
				file_path.setText(uri.getPath());

				Pwuri=uri;
				
			}
			else {
                Toast.makeText(this.getView().getContext(), "檔案路徑不對請重新選擇", Toast.LENGTH_LONG).show();

			}
		}
		*/

	}
		public static File[] getFilesFromUris(final Context context, final Uri[] uris) {
			ArrayList<File> fileList = new ArrayList<File>();
			int urisLength = uris.length;
			for (int i = 0; i < urisLength; i++) {
			Uri uri = uris[i];
			File file = getFileFromUri(context, uri);
			if (file != null) {
			fileList.add(file);
			}
			}
			File[] files = new File[fileList.size()];
			fileList.toArray(files);
			return files;
			}
		@SuppressLint("NewApi")
		public static File getFileFromUri(final Context context, final Uri uri) {
		if (uri == null) {
		return null;
		}
		// 判斷是否為Android 4.4之後的版本
		final boolean after44 = Build.VERSION.SDK_INT >= 19;
		if (after44 && DocumentsContract.isDocumentUri(context, uri)) {
		// 如果是Android 4.4之後的版本，而且屬於文件URI
		final String authority = uri.getAuthority();
		// 判斷Authority是否為本地端檔案所使用的
		if ("com.android.externalstorage.documents".equals(authority)) {
		// 外部儲存空間
		final String docId = DocumentsContract.getDocumentId(uri);
		final String[] divide = docId.split(":");
		final String type = divide[0];
		if ("primary".equals(type)) {
		String path = Environment.getExternalStorageDirectory() + "/" + divide[1];
		return createFileObjFromPath(path);
		}
		} else if ("com.android.providers.downloads.documents".equals(authority)) {
		// 下載目錄
		final String docId = DocumentsContract.getDocumentId(uri);
		final Uri downloadUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
		String path = queryAbsolutePath(context, downloadUri);
		return createFileObjFromPath(path);
		} else if ("com.android.providers.media.documents".equals(authority)) {
		// 圖片、影音檔案
		final String docId = DocumentsContract.getDocumentId(uri);
		final String[] divide = docId.split(":");
		final String type = divide[0];
		Uri mediaUri = null;
		if ("image".equals(type)) {
		mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		} else if ("video".equals(type)) {
		mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		} else if ("audio".equals(type)) {
		mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		} else {
		return null;
		}
		mediaUri = ContentUris.withAppendedId(mediaUri, Long.parseLong(divide[1]));
		String path = queryAbsolutePath(context, mediaUri);
		return createFileObjFromPath(path);
		}
		} else {
		// 如果是一般的URI
		final String scheme = uri.getScheme();
		String path = null;
		if ("content".equals(scheme)) {
		// 內容URI
		path = queryAbsolutePath(context, uri);
		} else if ("file".equals(scheme)) {
		// 檔案URI
		path = uri.getPath();
		}
		return createFileObjFromPath(path);
		}
		return null;
		}
		public static File createFileObjFromPath(final String path) {
			if (path != null) {
			try {
			File file = new File(path);
			if (true) {
			file.setReadable(true);
			if (!file.canRead()) {
			return null;
			}
			}
			return file.getAbsoluteFile();
			} catch (Exception ex) {
			ex.printStackTrace();
			}
			}
			return null;
			}
		public static String queryAbsolutePath(final Context context, final Uri uri) {
			final String[] projection = {MediaStore.MediaColumns.DATA};
			Cursor cursor = null;
			try {
			cursor = context.getContentResolver().query(uri, projection, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
			final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
			return cursor.getString(index);
			}
			} catch (Exception ex) {
			ex.printStackTrace();
			if (cursor != null) {
			cursor.close();
			}
			}
			return null;
			}
	private void uploadData(Uri uri){//上傳文字資料進資料庫()
		String filename;
		//先行定義時間格式
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        //取得現在時間
        Date dt=new Date();
        //透過SimpleDateFormat的format方法將Date轉為字串
        String dts=sdf.format(dt);
		
		course_ID=gnci.getNowCourseID();
		
		if(uri==null)
		{
				
		filename = "";
		Log.d("uploadData", "uri==null");
		}
		else
		{
			File file = new File(uri.toString());
			
			filename = file.getName();
			filename = Uri.decode(filename);
			Log.d("uploadData", "uri!=null");

		}
		DataFromDatabase BBInput = new DataFromDatabase();
		BBInput.Bulletin_BoardInput(course_ID, BBsettitle.getText().toString(), String.valueOf(week), dts, filename, BBeditnote.getText().toString(),exam_code,exam_name);
		Log.d("uploadData", "upload complete");

	}
	
	/*private void uploadFile(Uri uri){//上傳檔案到PHP_flippedclass upload
		if(uri!=null){
		myAsyncTask task = new myAsyncTask(getActivity(), uri.getPath());
		
		task.execute();
		Toast.makeText(this.getView().getContext(), "File上傳完畢", Toast.LENGTH_LONG).show();
		}
	}*/
	
	
	////////////////////////////////////
	class uploadThread implements Runnable{ 
	int serverResponseCode = 0;
	
	String upLoadServerUri = getPath.actionUrl;
	String sourceFileUri;
	String fileName ;
	HttpURLConnection conn = null;
	DataOutputStream dos = null;  
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";
	int bytesRead, bytesAvailable, bufferSize,progress;
	byte[] buffer;
	int maxBufferSize = 1 * 1024 * 1024; 
	File sourceFile ;
	
	public uploadThread(Uri sourceUri) {
		sourceFileUri = Uri.decode(sourceUri.getPath());
		fileName = Uri.decode(sourceUri.getPath());
		sourceFile = new File(sourceFileUri);
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
	
        
             try { 
                  
                   // open a URL connection to the Servlet
                 FileInputStream fileInputStream = new FileInputStream(sourceFile);
                 URL url = new URL(upLoadServerUri);

                 // Open a HTTP  connection to  the URL
                 conn = (HttpURLConnection) url.openConnection(); 
                 
                 conn.setDoInput(true); // Allow Inputs
                 conn.setDoOutput(true); // Allow Outputs
                 conn.setUseCaches(false); // Don't use a Cached Copy
                 
                 conn.setRequestMethod("POST");
                 conn.setRequestProperty("Connection", "Keep-Alive");
                 //conn.setRequestProperty("ENCTYPE", "multipart/form-data");
     			 //conn.setRequestProperty("Charset", "UTF-8");//++

                 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                 //conn.setRequestProperty("uploadedfile", fileName); 
          		
                 dos = new DataOutputStream(conn.getOutputStream());
                 dos.writeBytes(twoHyphens + boundary + lineEnd); 
                 dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                                           + fileName + "\"" + lineEnd);
                  
                 dos.writeBytes(lineEnd);
        
                 // create a buffer of  maximum size
                 bytesAvailable = fileInputStream.available(); 
        
                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
                 buffer = new byte[bufferSize];
        
                 // read file and write it into form...
                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                    
                 while (bytesRead > 0) {
                    
                   dos.write(buffer, 0, bufferSize);
                   bytesAvailable = fileInputStream.available();
                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
                    
                  }
        
                 // send multipart form data necesssary after file data...
                 dos.writeBytes(lineEnd);
                 dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
         		//Toast.makeText(this.getView().getContext(), "File上傳完畢", Toast.LENGTH_LONG).show();

                 // Responses from the server (code and message)
                 serverResponseCode = conn.getResponseCode();
                 String serverResponseMessage = conn.getResponseMessage();
                   
                 Log.i("uploadFile", "HTTP Response is : "
                         + serverResponseMessage + ": " + serverResponseCode);
                         
                  
                 //close the streams //
                 fileInputStream.close();
                 dos.flush();
                 dos.close();
                   
            } catch (MalformedURLException ex) {
                 
                uploaddialog.dismiss();  
                ex.printStackTrace();
                                               
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
            } catch (Exception e) {
                 
                uploaddialog.dismiss();  
                e.printStackTrace();
                
                
                Log.e("Upload file to server Exception", "Exception : "
                                                 + e.getMessage(), e);  
            }
            uploaddialog.dismiss();       
            return; 
             
         }

	
		
	} // End else block 
        
	
	
	class BBPopupUpload {
		private PopupWindow pw;
		Bundle bundle = new Bundle();
		public Button btn_getfile , btn_upload , btn_setpreexam;
		OnDismissListener listener;

		public BBPopupUpload(Context context,View v){
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			pw = new PopupWindow(inflater.inflate(R.layout.popupwindow_bulletin_board_upload, null)
					,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,true);
			// 點外面消失
					pw.setBackgroundDrawable(new BitmapDrawable());  
					// 點窗外消失  
					pw.setOutsideTouchable(true);  
					// 設為可點擊
					pw.setFocusable(true);  
					
					setListener();

					pw.setAnimationStyle(R.style.PopupAnimation);
					pw.showAtLocation(v, Gravity.BOTTOM, 0, 0);
					pw.update();
					// 顯示窗戶
					//pw.showAsDropDown(v);
		}
		
		public void setListener(){
			file_path=(TextView)pw.getContentView().findViewById(R.id.file_path);
			BBsettitle=(EditText)pw.getContentView().findViewById(R.id.BBsettitle);
			BBeditnote=(EditText)pw.getContentView().findViewById(R.id.BBeditnote);
			btn_getfile=(Button)pw.getContentView().findViewById(R.id.btn_getfile);
			btn_upload=(Button)pw.getContentView().findViewById(R.id.btn_upload);
			btn_setpreexam=(Button)pw.getContentView().findViewById(R.id.btn_setpreexam);
			btn_getfile.setOnClickListener(get_file_Listener);
			btn_upload.setOnClickListener(upload_Listener);
			btn_setpreexam.setOnClickListener(setpreexam_Listener);
		}
			
		private OnClickListener get_file_Listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showFileChooser();
				//MagicFileChooser=new MagicFileChooser(getActivity());
				//MagicFileChooser.showFileChooser();
			}
			
		};
		private OnClickListener upload_Listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RefreshNum=1;
				uploadData(Pwuri);
				//uploadFile(Pwuri);
				if(Pwuri!=null)
				{
				uploaddialog = ProgressDialog.show(pw.getContentView().getContext(),"","上傳中...",false);
				Thread uploadThread = new Thread(new uploadThread(Pwuri));
				uploadThread.start();
				}
				pw.dismiss();
				DataFromDatabase send_GCM = new DataFromDatabase();
				send_GCM.GCM_sendMessageToCourseMember(course_ID,"已在第"+week+"週上傳新提醒");
				mHandler_1.sendEmptyMessage(REFRESH_DATA);
				
			}
		};
		private OnClickListener setpreexam_Listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Intent intent=new Intent();
				bundle.putString("ExamName",BBsettitle.getText().toString());
				
				intent.putExtras(bundle);
				intent.setClass(pw.getContentView().getContext(), BulletinBoardSetPreExamFragment.class);
		        startActivityForResult(intent, 0);*/
				bundle.putString("title", BBsettitle.getText().toString());
				if(Pwuri!=null){
				bundle.putString("filepath",Pwuri.toString() );
				}
				else{
				bundle.putString("filepath",null);
				}
				bundle.putString("note", BBeditnote.getText().toString());
				
				
				BulletinBoardSetPreExamFragment frag_examcreate = new BulletinBoardSetPreExamFragment();
				frag_examcreate.setArguments(bundle);
        		getFragmentManager().beginTransaction().replace(R.id.frame_bulletinboard_content, frag_examcreate).commit();
				//transaction.addToBackStack(null);
				
				pw.dismiss();
			}
		};
		
		public void setOnDismissListener(OnDismissListener listener){
		    this.listener=listener;
		    pw.setOnDismissListener(listener);
		}
		
		public void BundleReturn(String title,String filepath,String note){
			if(filepath!=null)
			{
			Pwuri = Uri.parse(filepath);
			file_path.setText(Pwuri.getPath());
			}
			BBeditnote.setText(note);
			BBsettitle.setText(title);

		}
	}
	
	class BBPopupChatDl{
		private PopupWindow pw_ChDl;
		public BulletinBoardPopupChat pw_chatboard;
		private ProgressBar BBDlprogress;
		private Button btn_Dl , btn_Chat,btn_dopreexam;
		OnDismissListener listener2;
		String exam_code ;
		String exam_name ;
		String exam_state;

		public BBPopupChatDl(Context context,View v,int positionnum){
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			pw_ChDl = new PopupWindow(inflater.inflate(R.layout.popupwindow_bulletin_board_chatdl, null)
					,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,true);
			BulletinBoardMain.this.positionnum = positionnum;
			
			
			// 點外面消失
			pw_ChDl.setBackgroundDrawable(new BitmapDrawable());  
			// 點窗外消失  
			pw_ChDl.setOutsideTouchable(true);  
			// 設為可點擊
			pw_ChDl.setFocusable(true);  
			setListener();
			
			pw_ChDl.setAnimationStyle(R.style.PopupAnimation);
			pw_ChDl.showAtLocation(v, Gravity.BOTTOM, 0, 0);
			pw_ChDl.update();
			// 顯示窗戶
			//pw.showAsDropDown(v);
		}
	
		public void setListener(){
			BBTitle=(TextView)pw_ChDl.getContentView().findViewById(R.id.BBtitle);
			BBFile=(TextView)pw_ChDl.getContentView().findViewById(R.id.BBFile);
			mProgressbar=(ProgressBar)pw_ChDl.getContentView().findViewById(R.id.BBDlprogress);
			BBRemind=(TextView)pw_ChDl.getContentView().findViewById(R.id.BBremind);
			btn_dopreexam=(Button)pw_ChDl.getContentView().findViewById(R.id.btn_dopreexam);
			Log.d("=====>", "course_remind"+course_remind);	

			BBTitle.setText(course_remind[positionnum][1]);
			BBRemind.setText(course_remind[positionnum][5]);
			
			BBDlprogress=(ProgressBar)pw_ChDl.getContentView().findViewById(R.id.BBDlprogress);
			btn_Dl=(Button)pw_ChDl.getContentView().findViewById(R.id.btn_Dl);
			exam_code = course_remind[positionnum][6];
			exam_name = course_remind[positionnum][7];
			if(course_remind[positionnum][4].equals(""))
			{
			BBFile.setText("無上傳檔案");
			btn_Dl.setVisibility(View.GONE);
			BBDlprogress.setVisibility(View.GONE);
			Log.d("=====>", "Button & Progress 消失"+BBFile.getText().toString());	
			}
			else
			{
			BBFile.setText(course_remind[positionnum][4]);	
			btn_Dl.setVisibility(View.VISIBLE);
			BBDlprogress.setVisibility(View.VISIBLE);
			Log.d("=====>", "Button & Progress 出現"+BBFile.getText());	

					
			}
			btn_Chat=(Button)pw_ChDl.getContentView().findViewById(R.id.btn_Chat);
			btn_Dl.setOnClickListener(btn_Dl_Listener);
			btn_Chat.setOnClickListener(btn_Chat_Listener);
			btn_dopreexam.setOnClickListener(btn_dopreexam_listener);
			if(Type.equals("student"))
			{
			if(course_remind[positionnum][6].equals("0") )//沒有前測為0,有前測非0
			{
				btn_dopreexam.setVisibility(View.GONE);
				Log.d("=====>", "前測Button消失"+course_remind[positionnum][6]);	

			}
			else
			{
				btn_dopreexam.setVisibility(View.VISIBLE);
				Log.d("=====>", "前測Button出現"+course_remind[positionnum][6]);	

			}
			}
			if(Type.equals("teacher"))
			{				
				btn_dopreexam.setBackgroundResource(R.drawable.spacew_btn);
				btn_dopreexam.setText("檢視前測成績");
				btn_dopreexam.setTextColor(0xff6dc2c7);
				btn_dopreexam.setTextSize(20);
								        
			if(course_remind[positionnum][6].equals("0") )//沒有前測為0,有前測非0
			{
				btn_dopreexam.setBackgroundResource(R.drawable.spacew_btn);
				btn_dopreexam.setVisibility(View.GONE);
				Log.d("=====>", "前測Button消失"+course_remind[positionnum][6]);	

			}
			else
			{
				btn_dopreexam.setVisibility(View.VISIBLE);
				Log.d("=====>", "前測Button出現"+course_remind[positionnum][6]);	

			}	
			}
		
		}
			private OnClickListener btn_Dl_Listener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					doDownload();
				}
			};
			private OnClickListener btn_Chat_Listener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pw_ChDl.dismiss();
					Log.d("=====>", "Dismiss~");	
					pw_chatboard = new BulletinBoardPopupChat(getActivity(), getView());
					pw_ChDl.setOnDismissListener(listener3);
					
				}
			};
			private void CreateQuestionFragment(){
				Log.d("creatquest", exam_code + "+" + exam_name);
				Bundle bundle2 = new Bundle();
				bundle2.putString("exam_code", exam_code);
				bundle2.putString("exam_state", exam_state);
				bundle2.putString("exam_name", exam_name);
				BulletinBoardDoPreExamFragment frag_ques = new BulletinBoardDoPreExamFragment();
				frag_ques.setArguments(bundle2);
				getFragmentManager().beginTransaction().replace(R.id.frame_bulletinboard_content, frag_ques).commit();
			}
			private OnClickListener btn_dopreexam_listener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//抓pre_exam_grade分數
					//判斷是否測驗過
					//再行開啟
					if(Type.equals("student"))
					{
					dialog.setTitle("前測測驗");
					
					if(!list_item.get(Integer.parseInt(exam_code)-1).get("exam_grade").equals("未作答"))
					{
					dialog.setMessage("已作答完畢,得到"+list_item.get(Integer.parseInt(exam_code)-1).get("exam_grade")+"分");
					dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					dialog.setNegativeButton("檢視測驗", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							exam_state="close";
							CreateQuestionFragment();
							pw_ChDl.dismiss();
						}
						
					});
					}
					
					else
					{
					dialog.setMessage("尚未進行測驗,即將開始作答");
					dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							exam_state="open";
							CreateQuestionFragment();
							pw_ChDl.dismiss();
						}
						
					});
					}
						
					dialog.show();	
				}
					if(Type.equals("teacher"))
					{	
				        grade=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bulletin_board_gridview, null);
						dialog2 = new AlertDialog.Builder(getActivity());
						dialog2.setView(grade);
						gv_title=(GridView)grade.findViewById(R.id.gv_preexam_gradetitle);
						gv_info=(GridView)grade.findViewById(R.id.gv_preexam_gradeinfo);
						pgb=(ProgressBar)grade.findViewById(R.id.gv_pgb);
						gv_info.setVisibility(View.GONE);
						pgb.setVisibility(View.VISIBLE);
						pgb.setVisibility(View.FOCUS_FORWARD);
						
						dialog2.show();
						MySQL_DB_PreExamGrade = new DataFromDatabase();
						MySQL_DB_PreExamGrade.FC_GetAllStdPreExamGrade(course_ID,course_remind[positionnum][6]);
						downloadGradeinfo_runnable_isRunning = true;
						downloadGradeinfo_runnable downloadGradeinfo_runnable = new downloadGradeinfo_runnable(MySQL_DB_PreExamGrade);
						
						Thread setGridViewThread=new Thread(downloadGradeinfo_runnable);
						setGridViewThread.start();			
					}
				
				}
			};
			private OnDismissListener listener3 = new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					
				}
			};
			public void setOnDismissListener(OnDismissListener listener2){
			    this.listener2=listener2;
			    pw_ChDl.setOnDismissListener(listener2);
			}
		}
		
	public class BulletinBoardPopupChat {
		private PopupWindow pw_chatboard;
		private EditText BBChat_edit;
		private Button btn_uploadchat ;
		//private ListView BBChatlistView;
		//private ArrayAdapter<String> BBChatlistAdapter;
		OnDismissListener chatlistener;
		

		public BulletinBoardPopupChat(Context context,View v){
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			pw_chatboard =  new PopupWindow(inflater.inflate(R.layout.popupwindow_bulletin_board_chatboard, null)
					,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,true);
			
			pw_chatboard.setBackgroundDrawable(new BitmapDrawable());  
			// 點窗外消失  
			pw_chatboard.setOutsideTouchable(true);  
			// 設為可點擊
			pw_chatboard.setFocusable(true);  
			//BBChatlistAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
			setListener();
			
			chatinfo = new BulletinBoardChatInfo(course_remind[positionnum][1], String.valueOf(week));
        	str=chatinfo.getTitleInfo();
        	check_done=chatinfo.getCheck();
        	getcount=chatinfo.getCount();
        	Thread waitfordata = new Thread(new waitingListview(course_remind[positionnum][1]));
    		waitfordata.start();
			
			pw_chatboard.setAnimationStyle(R.style.PopupAnimation);
			pw_chatboard.showAtLocation(v, Gravity.BOTTOM, 0, 0);
			pw_chatboard.update();

			
			
		}

		public void setListener(){
			BBChat_edit = (EditText)pw_chatboard.getContentView().findViewById(R.id.BBChat_edit);
			btn_uploadchat = (Button)pw_chatboard.getContentView().findViewById(R.id.btn_uploadchat);
			BBChatlistView = (ListView)pw_chatboard.getContentView().findViewById(R.id.BBChatlistView);
			//BBChatlistView.setAdapter(BBChatlistAdapter);
			//BBChatlistAdapter.add(course_remind[positionnum][1]+"討論區");
			btn_uploadchat.setOnClickListener(btn_upload_listener);
		}
		
		
		private OnClickListener btn_upload_listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//insert chat into database
				String note,Chat_course_ID,Chat_title,Account;
				Chat_course_ID = course_remind[positionnum][0];
				Chat_title = course_remind[positionnum][1];
				Account = gnai.getNowAccountID();
				note = BBChat_edit.getText().toString();
				BBChat_edit.setText("");
				RefreshNum=2;

				//先行定義時間格式
	            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
	            //取得現在時間
	            Date dt=new Date();
	            //透過SimpleDateFormat的format方法將Date轉為字串
	            String dts=sdf.format(dt);
	           
	            DataFromDatabase MySQL_DB_btn_uploadChat = new DataFromDatabase();
	            MySQL_DB_btn_uploadChat.Bulletin_Board_InputChat(Chat_course_ID, Chat_title, String.valueOf(week), dts, Account, note);
	            MySQL_DB_btn_uploadChat.GCM_sendMessageToCourseMember(course_ID,account_ID+"已上傳新留言到"+Chat_title+"提醒中");
				Toast.makeText(getActivity(), "上傳完成", Toast.LENGTH_LONG).show();
				mHandler_1.sendEmptyMessage(REFRESH_DATA);
			}
		};			
	}
	/**
	 * 下載準備工作，獲取SD卡路徑、開啟線程
	 */
	private void doDownload() {
		// 獲取SD卡路徑
		String path = Environment.getExternalStorageDirectory()
				+ "/Download/";
		/*String path = Environment.getExternalStorageDirectory()
				+ "/Download/";*/
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
		filepath = path + course_remind[positionnum][4];
		downloadUrl=getPath.downloadUrl;
		downloadUrl+=course_remind[positionnum][4];
		downloadTask task = new downloadTask(downloadUrl, threadNum, filepath);
		Log.d("download info", "downloadURL:"+downloadUrl+"  filepath"+filepath);
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

						if((int)(temp*100)==100){
						Toast.makeText(getActivity(), "下載完成！", Toast.LENGTH_LONG).show();
						}
						//mMessageView.setText("下載進度:" + progress + " %");
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
	
	class waitingListview implements Runnable//chatinfo
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
				   str=chatinfo.getTitleInfo();
				   check_done=chatinfo.getCheck();
				   getcount=chatinfo.getCount();
				 }
			 } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			if(check_done==1&&isRunning){
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//Toast.makeText(getActivity(), str[0], Toast.LENGTH_LONG).show();
						//ListView noticeList;
						//noticeList = (ListView) getView().findViewById(R.id.BBChatlistView);	
							if(str[0][0].equals("teacher")){
								String[] Text = {"暫無學生回覆"};
								BBChatlistView.setAdapter(
										new ArrayAdapter<String>(
							         getActivity(), android.R.layout.simple_list_item_1, Text));
								BBChatlistView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

							}
							else{	            			      
				            	Remind_list adp = new Remind_list(getcount, getActivity(), str);			         
				            	BBChatlistView.setAdapter(adp);
							}
				            	
					}
				});
			} 
			
		}		
	}
	
	public void initTextView(){
    	/*SQLiteDatabase db=getActivity().openOrCreateDatabase("FlippedClass_Temporal", getActivity().MODE_PRIVATE, null);
    	Cursor cursor=db.rawQuery("Select * FROM account", null);
    	cursor.moveToFirst();
    	String Account=cursor.getString(0);
    	String Type=gnai.getNowAccountType();
    	
    	
    	String now_course="null";
    	String now_course_ID="null";
    
    	now_course=gnci.getNowCourseName();
		now_course_ID=gnci.getNowCourseID();
    	
    	
		user_tv.setText("使用者 "+Account+" 您好!\n"+"現在的模式為 "+Type+"\n現在所選的課程為 : "+now_course+"\n現在所選的課程代碼為 : "+now_course_ID);
    	
    	db.close();*/
		if(gnci.getNowCourseID().equals("null"))
		{	
			user_tv.setText("-尚未選取課程-");
		}
		else
		{
		user_tv.setText("-佈告欄-");
		RefreshNum=1;
		mHandler_1.sendEmptyMessage(REFRESH_DATA);
		Toast.makeText(this.getView().getContext(), "現在課程:"+gnci.getNowCourseID() + "  " + gnci.getNowCourseName(), Toast.LENGTH_LONG).show();
		}

    }
	protected static final int REFRESH_DATA = 0x00000001;
	
	
	class downloadGradeinfo_runnable implements Runnable
	{
		DataFromDatabase MySQL_DB_Grade;
		int times = 0;
		
		public downloadGradeinfo_runnable(DataFromDatabase MySQL_DB_Grade)
		{
			this.MySQL_DB_Grade = MySQL_DB_Grade;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {	
						
					Thread.sleep(500);
						
					String result_Grade = MySQL_DB_Grade.return_result;
				
					while(result_Grade==null && times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "ExamFragment Waitfodata 多等了1秒");
						result_Grade = MySQL_DB_Grade.return_result;
						times++;
					}
						
					Log.d("=====>", "ExamFragment Waitfodata get result_Grade = " + result_Grade);
						
						
					JSONArray jsonArray_Grade;
						
					try {
							
							jsonArray_Grade = new JSONArray(result_Grade);
								
							info_account_ID = new String[jsonArray_Grade.length()];
							info_grade = new String[jsonArray_Grade.length()];
								
							count_for_member = jsonArray_Grade.length();
								
							for(int i = 0 ; i<jsonArray_Grade.length() ; i++)
							{
								JSONObject jsonData = jsonArray_Grade.getJSONObject(i);
									
								info_account_ID[i] = jsonData.getString("account_ID");
								info_grade[i] = jsonData.getString("grade");
							}
	
								if(downloadGradeinfo_runnable_isRunning)
								{		
									mHandler.post(setGlidViewRunnable);
								}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}									
						
				} 	

	
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	private Runnable setGlidViewRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			initGridView();
		}
	};
	private void initGridView() {
		// TODO Auto-generated method stub
		
		Log.d("=====>", "ExamGradeTeacherFragment get in initGridView");
		
		gv_title.setNumColumns(2);
		gv_title.setAdapter(new ExamGridListTitleAdapter(getActivity(),2));
		gv_info.setNumColumns(2);
		gv_info.setAdapter(new ExamGridInfoAdapter(getActivity(),2,info_account_ID,info_grade));
		gv_info.setVisibility(View.VISIBLE);
		pgb.setVisibility(View.GONE);
	}
	
	
	class BulletinBoardWaitForBBDataAndExamInfo implements Runnable//examinfo
	{ 	
		String[] exam_code = null,exam_name = null,exam_openorclose = null,exam_grade = null,grade_code = null;
		String[][] courselist;
		String[] courseInfo;
		int count=0;
		DataFromDatabase MySQL_DB_Exam,MySQL_DB_Grade,MySQL_DB_BBData;
		int times = 0;
		
		public BulletinBoardWaitForBBDataAndExamInfo(DataFromDatabase MySQL_DB_BBData,DataFromDatabase MySQL_DB_Exam,DataFromDatabase MySQL_DB_Grade)
		{
			this.MySQL_DB_BBData = MySQL_DB_BBData;
			this.MySQL_DB_Exam = MySQL_DB_Exam;
			this.MySQL_DB_Grade = MySQL_DB_Grade;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					Thread.sleep(500);
					
					String result_Exam = MySQL_DB_Exam.return_result;
					String result_Grade = MySQL_DB_Grade.return_result;
					String result_BBData = MySQL_DB_BBData.return_result;
					if(Type.equals("student"))
					{
					while(result_Exam==null && result_Grade==null&&result_BBData==null &&times<=10)
					{
						Thread.sleep(1000);
						Log.d("=====>", "ExamFragment Waitfodata 多等了1秒");
						
						result_BBData = MySQL_DB_BBData.return_result;
						result_Exam = MySQL_DB_Exam.return_result;
						result_Grade = MySQL_DB_Grade.return_result;
						times++;
					}
					
					Log.d("=====>", "ExamFragment Waitfodata get result_Exam = " + result_Exam);
					Log.d("=====>", "ExamFragment Waitfodata get result_Grade = " + result_Grade);
					Log.d("=====>", "ExamFragment Waitfodata get result_BBData = " + result_BBData);

					
					
					JSONArray jsonArray_Exam,jsonArray_Grade,jsonArray_BBData;
					
					try {
							viewlist = new ArrayList<HashMap<String,String>>();
							gradelist = new ArrayList<HashMap<String,String>>();
							list_item = new ArrayList<HashMap<String, String>>();
							jsonArray_BBData = new JSONArray(result_BBData);

							jsonArray_Exam = new JSONArray(result_Exam);
							jsonArray_Grade = new JSONArray(result_Grade);
							
							courselist = new String[jsonArray_BBData.length()][8];
							for(int i = 0 ; i<jsonArray_BBData.length() ; i++)
							{
								JSONObject jsonBBData = jsonArray_BBData.getJSONObject(i);
								courselist[i][0] = jsonBBData.getString("course_ID");
								courselist[i][1] = jsonBBData.getString("title");
								courselist[i][2] = jsonBBData.getString("week");
								courselist[i][3] = jsonBBData.getString("date");
								courselist[i][4] = jsonBBData.getString("file");
								courselist[i][5] = jsonBBData.getString("note");
								courselist[i][6] = jsonBBData.getString("exam_times");
								courselist[i][7] = jsonBBData.getString("exam_name");
								Log.d("=====>", "RemindFragment 找到的Json = "+courselist[i][1]);
								
								HashMap<String, String> view_item = new HashMap<String, String>();
								view_item.put("title",courselist[i][1]);
								view_item.put("date", courselist[i][3]);
								view_item.put("file", courselist[i][4]);
								view_item.put("exam_times", courselist[i][6]);
								view_item.put("exam_name", courselist[i][7]);
								viewlist.add(view_item);
							}
							count = jsonArray_BBData.length();
							Log.d("=====>", "course_remind"+course_remind);	
							course_remind=courselist;
							courseInfo = new String[count];				
							if(count>0){			
								for(int i=0;i<count;i++)
								{
									courseInfo[i] = courselist[i][1];
								}		
							}
							else{
								courseInfo[0]="暫無相關資料";
							}
							Log.d("=====>", Integer.toString(count));	
							
							exam_code = new String[jsonArray_Exam.length()];
							exam_name = new String[jsonArray_Exam.length()];
							exam_openorclose = new String[jsonArray_Exam.length()];	
							exam_grade = new String[jsonArray_Exam.length()];
							grade_code = new String[jsonArray_Exam.length()];
							
							for(int i = 0 ; i<jsonArray_Exam.length() ; i++)
							{								
								grade_code[i] = "";
								exam_grade[i] = "未作答";
							}
							
							
							for(int i = 0 ; i<jsonArray_Grade.length() ; i++)
							{
								JSONObject jsonData = jsonArray_Grade.getJSONObject(i);
								
								grade_code[i] = jsonData.getString("exam_times");
							}
							
							for(int i = 0 ; i<jsonArray_Exam.length() ; i++)
							{
								JSONObject jsonData = jsonArray_Exam.getJSONObject(i);
								
								exam_code[i] = jsonData.getString("exam_times");
								exam_name[i] = jsonData.getString("exam_name");

								
								if( jsonData.getString("open_or_close").equals("1"))
									exam_openorclose[i] = "open";
								
								if( jsonData.getString("open_or_close").equals("0"))
									exam_openorclose[i] = "close";
								
								for(int j = 0; j<jsonArray_Grade.length() ; j++)
								{
									JSONObject jsonData_Grade = jsonArray_Grade.getJSONObject(j);
									
									if(String.valueOf(i+1).equals(grade_code[j]))
									{
										exam_grade[i] = jsonData_Grade.getString("grade");
									}									
								}
								
								HashMap<String, String> item = new HashMap<String, String>();
								item.put("exam_code", exam_code[i]);
								item.put("exam_name", exam_name[i]);
							    item.put("exam_state", exam_openorclose[i]);
							    item.put("exam_grade", exam_grade[i]);
							    list_item.add(item);
							    gradelist.add(item);
							}
							mHandler.post(setBBDataAdapter);
							
					
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}}									
					
					if(Type.equals("teacher"))
					{

						while(result_Exam==null && result_BBData==null &&times<=10)
						{
							Thread.sleep(1000);
							Log.d("=====>", "ExamFragment Waitfodata 多等了1秒");
							
							result_BBData = MySQL_DB_BBData.return_result;
							result_Exam = MySQL_DB_Exam.return_result;
							times++;
						}
						
						Log.d("=====>", "ExamFragment Waitfodata get result_Exam = " + result_Exam);
						Log.d("=====>", "ExamFragment Waitfodata get result_BBData = " + result_BBData);

						
						
						JSONArray jsonArray_Exam,jsonArray_BBData;
						
						try {
								viewlist = new ArrayList<HashMap<String,String>>();
								gradelist = new ArrayList<HashMap<String,String>>();
								list_item = new ArrayList<HashMap<String, String>>();
								jsonArray_BBData = new JSONArray(result_BBData);

								jsonArray_Exam = new JSONArray(result_Exam);
								
								courselist = new String[jsonArray_BBData.length()][8];
								for(int i = 0 ; i<jsonArray_BBData.length() ; i++)
								{
									JSONObject jsonBBData = jsonArray_BBData.getJSONObject(i);
									courselist[i][0] = jsonBBData.getString("course_ID");
									courselist[i][1] = jsonBBData.getString("title");
									courselist[i][2] = jsonBBData.getString("week");
									courselist[i][3] = jsonBBData.getString("date");
									courselist[i][4] = jsonBBData.getString("file");
									courselist[i][5] = jsonBBData.getString("note");
									courselist[i][6] = jsonBBData.getString("exam_times");
									courselist[i][7] = jsonBBData.getString("exam_name");
									Log.d("=====>", "RemindFragment 找到的Json = "+courselist[i][1]);
									
									HashMap<String, String> view_item = new HashMap<String, String>();
									view_item.put("title",courselist[i][1]);
									view_item.put("date", courselist[i][3]);
									view_item.put("file", courselist[i][4]);
									view_item.put("exam_times", courselist[i][6]);
									view_item.put("exam_name", courselist[i][7]);
									viewlist.add(view_item);
								}
								count = jsonArray_BBData.length();
								courseInfo = new String[count];		
								course_remind=courselist;
								if(count>0){			
									for(int i=0;i<count;i++)
									{
										courseInfo[i] = courselist[i][1];
									}		
								}
								else{
									courseInfo[0]="暫無相關資料";
								}
								Log.d("=====>", Integer.toString(count));	
								
								
								
								
								
								
								exam_code = new String[jsonArray_Exam.length()];
								exam_name = new String[jsonArray_Exam.length()];
								exam_openorclose = new String[jsonArray_Exam.length()];	
								exam_grade = new String[jsonArray_Exam.length()];
								grade_code = new String[jsonArray_Exam.length()];
								
								for(int i = 0 ; i<jsonArray_Exam.length() ; i++)
								{								
									grade_code[i] = "";
								}
								
								
								
								
								for(int i = 0 ; i<jsonArray_Exam.length() ; i++)
								{
									JSONObject jsonData = jsonArray_Exam.getJSONObject(i);
									
									exam_code[i] = jsonData.getString("exam_times");
									exam_name[i] = jsonData.getString("exam_name");

									
									if( jsonData.getString("open_or_close").equals("1"))
										exam_openorclose[i] = "open";
									
									if( jsonData.getString("open_or_close").equals("0"))
										exam_openorclose[i] = "close";
									
									
									
									HashMap<String, String> item = new HashMap<String, String>();
									item.put("exam_code", exam_code[i]);
									item.put("exam_name", exam_name[i]);
								    item.put("exam_state", exam_openorclose[i]);
								    gradelist.add(item);
									Log.d("=====>", "ExamFragment Waitfodata set exam_code["+ i + "] = " + exam_code[i]);
									Log.d("=====>", "ExamFragment Waitfodata set grade["+ i + "] = " + exam_grade[i]);
								}
								mHandler.post(setBBDataAdapter);
								
						
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
						}
						
					}
				} 	

	
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
private Runnable setBBDataAdapter = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			bundle_setView.putString("type", Type);
			BBListAdapter = new BulletinBoardListAdapter(getActivity(), viewlist, gradelist, bundle_setView);
			noticeList.setAdapter(BBListAdapter);
			pgb2.setVisibility(View.GONE);
			noticeList.setVisibility(View.VISIBLE);


		}
	};
	
	public class BulletinBoardListAdapter extends BaseAdapter{
		Context context;
		ArrayList<HashMap<String, String>> list,gradelist;
		Bundle bundle;
		private LayoutInflater myInflater;
		BulletinBoardMain BBmain;
		BBPopupChatDl PopChatDl;
		ViewTag ViewTag;
		String type;
		int positionnum[];
		public BulletinBoardListAdapter(Context context,ArrayList<HashMap<String, String>> list,ArrayList<HashMap<String, String>> gradelist,Bundle bundle)
		{
			
			this.context = context;
			this.myInflater = LayoutInflater.from(context);
			this.list = list;
			this.gradelist = gradelist;
			this.bundle = bundle;
			type=bundle.getString("type");
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			if(convertView==null){
				ViewTag = new ViewTag();
				convertView=myInflater.inflate(R.layout.listview_bulletin_board_mainlist, null);
				ViewTag.text1 = (TextView)convertView.findViewById(R.id.List_Title);
				ViewTag.text2 = (TextView)convertView.findViewById(R.id.quiz_grade);
				ViewTag.btn1 = (Button)convertView.findViewById(R.id.Date);
				ViewTag.img1 = (ImageView)convertView.findViewById(R.id.file);
				ViewTag.img2 = (ImageView)convertView.findViewById(R.id.nofile);
				convertView.setTag(ViewTag);
			}
			else{
				ViewTag = (ViewTag) convertView.getTag();
			}
			if(position==list.size()+1){
				ViewTag.text1.setText("上傳");

			}
			else{
			ViewTag.text1.setText(list.get(position).get("title"));
			ViewTag.btn1.setText(list.get(position).get("date"));
			//以下為檔案
			if(list.get(position).get("file").equals("")){
				ViewTag.img2.setVisibility(View.VISIBLE);
			}
			else{
				ViewTag.img1.setVisibility(View.VISIBLE);
			}
			//以下為分數
			if(type.equals("student")){
			if(list.get(position).get("exam_times").equals("0"))
			{
				ViewTag.text2.setText("無前測");
			}
			else {
				if(gradelist.get(Integer.parseInt(list.get(position).get("exam_times"))-1).get("exam_grade").equals("未作答")){
					ViewTag.text2.setText("未作答");

				}
				else{
					ViewTag.text2.setText(gradelist.get(Integer.parseInt(list.get(position).get("exam_times"))-1).get("exam_grade"));
				}
			}}
			else{
				if(!list.get(position).get("exam_times").equals("0"))
				ViewTag.text2.setText("有前測");
				else{
				ViewTag.text2.setText("無前測");

				}
			}
			convertView.setOnClickListener(new convertOnclick(context, position));
			}
			return convertView;
		}
		
		public class ViewTag {
		    TextView text1,text2;
		    Button btn1;
		    ImageView img1,img2;
		}
		

		class convertOnclick implements android.view.View.OnClickListener{
			private int position;
		    private Context context;
			TextView setlist;
			public convertOnclick(Context context, int pos) {
			this.context = context;
			this.position = pos;
			}
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopChatDl = new BBPopupChatDl(context, v, position);
				PopChatDl.setOnDismissListener(listener2);
			}
			
		
		
		
		}
		OnDismissListener listener2 = new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				
			}
		};
		
	}

}
