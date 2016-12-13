package com.mcucsie.flippedclass.group;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;



































































import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;


@SuppressWarnings("deprecation")
@SuppressLint("SimpleDateFormat")
public class GroupDiscussBoard extends Fragment {
	
	
	private Button btn_discuss_send,btn_discuss_other;
	private EditText text;
	private DisplayMetrics mPhone;
	private final static int CAMERA = 66 ;
	private final static int PHOTO = 99 ;
	private initPupWinOther mPW;
	// PopupWindow的高跟寬 
    //private int mPopupWindowWidth,mPopupWindowHeight; 
    private AlertDialog.Builder mdialog;
   
    private ProgressDialog mProgressDialog = null;
    private Button uploadButton,btnselectpic;
    private ImageView imageview;
    private TextView messageText;
    private int count_a,serverResponseCode = 0;
	private Byte check_ID;
	private String Account,course_ID,boardtext_tablename,dialogue,board_name;
	private String DiscussType,groupNum,tablename;
	private String imagepath=null,upLoadServerUri = "http://1.34.20.54/FlippedClass/UploadToServer.php";
	private String imageUrl ="http://1.34.20.54/FlippedClass/uploads/";
	private String[][] list;
	private String[] imageName;
	private int sendType=0; //0=文字,1=圖片
	private Boolean isWaitData = false;
	private ListView list_board;
	
	LinearLayout  gallery;
	ArrayList<Bitmap> bitmapImageList;
    /**
     * GridView所使用的Adapter
     */
    private PhotoGalleryAdapter photoAdpater;
	
	Uri uri;
	
	
	uploadFile uploadFile;
	GetNowCourseInfo course_info;
	//getGallery getGallery;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		return inflater.inflate(R.layout.fragment_group_discuss_board,null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
				super.onActivityCreated(savedInstanceState);
				
	
			    btn_discuss_send = (Button)getView().findViewById(R.id.btnSend);
			    btn_discuss_other = (Button)getView().findViewById(R.id.btn_other);
			    gallery = (LinearLayout)getView().findViewById(R.id.gallery);
			    list_board = (ListView)getView().findViewById(R.id.discuss_list);
			    
			    btn_discuss_send.setOnClickListener(btn_discuss_send_click);
			    btn_discuss_other.setOnClickListener(btn_discuss_other_click);
 
			    
			    Bundle bundle = getArguments();
				Account = bundle.getString("Account");
				check_ID = bundle.getByte("check_ID");
				board_name = bundle.getString("board_name");
				DiscussType = bundle.getString("DiscussType");
				groupNum = bundle.getString("groupNum");
				Toast.makeText(getActivity(), Account, Toast.LENGTH_LONG).show();
				
				mdialog = new AlertDialog.Builder(getActivity());
	
				//讀取手機解析度
			      mPhone = new DisplayMetrics();
			      getActivity().getWindowManager().getDefaultDisplay().getMetrics(mPhone);

			//----------------------------------------------
			
					
					
					course_info = new GetNowCourseInfo(getActivity());
					course_ID = course_info.getNowCourseID();
				
					course_info=new GetNowCourseInfo(getActivity());
					
					isWaitData = true;
					
					DataFromDatabase MySQL_DB = new DataFromDatabase();
				  	boardtext_tablename = "discuss_"+course_ID+"_"+DiscussType;
					MySQL_DB.FC_BoardCourseList(boardtext_tablename,board_name,groupNum);
					Thread waitfordata = new Thread(new getdata(MySQL_DB));
					waitfordata.start();
					
					DataFromDatabase MySQL = new DataFromDatabase();;
					tablename = "discuss_"+course_ID+"_picture";			
					MySQL.FC_getIamgeName(tablename,groupNum,board_name);
					Thread waitForName = new Thread(new downloadImage(MySQL));
					waitForName.start();
					
					
					
	}
	
	

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		endAllThread();
		isWaitData = false;
	}

	
	class getdata implements Runnable
	{

    	private DataFromDatabase MySQL_DB;
    	String result=null;
		// 建構子，設定要傳的字串
		public getdata(DataFromDatabase MySQL_DB)
		{
			this.MySQL_DB = MySQL_DB;
		}

		@Override
		public void run()
		{		
			// TODO Auto-generated method stub
					
			while(isWaitData){
				try {
					Thread.sleep(1500);
							
					result = MySQL_DB.return_result;
					Log.d("result", result);	
			
				
					
					int times=0;
					while(times<=10&&result==null){
						   Thread.sleep(1000);
						   result = MySQL_DB.return_result;
						   Log.d("=====>", "ThreadDownload的多休息了1秒");	
						   times++;
						}				
	
			
					if(times<=10){
						Log.d("=====>", "成功抓取討論區對話"+result);
						
						
						JSONArray jsonArray;
						result = result.trim();
						
						if(!result.equals("null")){
							try {
								jsonArray = new JSONArray(result);							
								list = new String[jsonArray.length()][2];
								
								for(int i = 0 ; i<jsonArray.length() ; i++)
								{
									
									JSONObject jsonData = jsonArray.getJSONObject(i);
									if(check_ID==0)
										if(jsonData.getString("tage").equals("1"))
											list[i][0] = "Teacher"+"  "+jsonData.getString("date")+"  "+jsonData.getString("time");
										else
											list[i][0] = jsonData.getString("ID")+"  "+jsonData.getString("date")+"  "+jsonData.getString("time");
									
									else if(check_ID==1){
										if(jsonData.getString("tage").equals("0"))
											list[i][0] = jsonData.getString("ID")  +"  "+jsonData.getString("date")+"  "+jsonData.getString("time");
										else
											list[i][0] = "我" +"  "+jsonData.getString("date")+"  "+jsonData.getString("time");				
									}
									list[i][1] = jsonData.getString("dialogue");
									
									//Log.d("=====>", "DiscussBoard 找到的Json = "+list[i]);
									
								}
								count_a = jsonArray.length();
								
								mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
								
							} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							} 
						}
						
						else{
							count_a = 0;
							mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
						}
						
						if(isWaitData){
							
							Thread.sleep(3000);
					  		boardtext_tablename = "discuss_"+course_ID+"_"+DiscussType;
					  
					  		MySQL_DB.FC_BoardCourseList(boardtext_tablename,board_name,groupNum);
					  		
						}
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
		
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler()
		{
			
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
					// 顯示網路上抓取的資料
					case REFRESH_DATA:
						
						String course_1[]=new String[1];
						//list_board = new ListView(getActivity());
						
						Log.d("===============>","計數"+count_a);
						
						if(count_a>0){			
							if(!list[0][0].equals("0" +"  0000-00-00"+"  ")){
								GroupDiscussBoardList adp = new GroupDiscussBoardList(count_a, getActivity(), list);
								list_board.setAdapter(adp);
							}
							else{						
								course_1[0]="暫無相關資料";
								list_board.setAdapter(new ArrayAdapter<String>(getActivity(),
						                android.R.layout.simple_list_item_1, course_1));			
							}					
						}
						else{			
								course_1[0]="暫無相關資料";
								if(getActivity()!=null)
								list_board.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, course_1));			
						}
						list_board.setChoiceMode(ListView.CHOICE_MODE_SINGLE);																													
					break;
				}
			}
		};
	}

	
	private OnClickListener btn_discuss_send_click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			text = (EditText)getView().findViewById(R.id.enter_text);
			dialogue = text.getText().toString();
			
			if(sendType==0)
				if(!dialogue.equals("")){
				text.setText("");
				sendmsg(dialogue);
				}
			
			}
		
		
	};
	
	private OnClickListener btn_discuss_other_click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mPW = new initPupWinOther(getActivity(),v);
			mPW.setOnDismissListener(listener);
		}
		
		private OnDismissListener listener = new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				
			}
		};
		
	};
	
	@SuppressLint("SimpleDateFormat")
	public void sendmsg(String dlog){	
		
		if(course_ID.equals("null")){
			Toast.makeText(getActivity(), "尚未選擇課程", Toast.LENGTH_LONG).show();
		}
		else{
			String []a=new String[8];
			//String []b=new String[7];
			
		/*	
			//先行定義時間格式
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            //取得現在日期
            Date dt=new Date();
            //透過SimpleDateFormat的format方法將Date轉為字串
            String data=sdf.format(dt);
         */
			
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            //取得現在時間
            Date dt2=new Date();
            //透過SimpleDateFormat的format方法將Date轉為字串
            String time=sdf2.format(dt2);
        
			
			Time mtime = new Time();
			mtime.setToNow();
			
			int day = mtime.monthDay;
			int month = mtime.month;
			int year = mtime.year;
			
			String date = year+"-"+(month+1)+"-"+day;
			
			if(check_ID==0)
				a[1]="0";
			else
				a[1]="1";
			
			this.dialogue = dlog;
			a[2]=date;
			a[3]=time;
			a[4]=dialogue;
			a[5]=Account;
			a[6]=board_name;
			a[7]=groupNum;

			DataFromDatabase MySQL_DB = new DataFromDatabase();
			MySQL_DB.FC_BoardInsertText(boardtext_tablename, a[1], a[2], a[3], a[4],a[5],a[6],a[7]);
		
			/*if(check_ID!=1){
				b[0]=course_ID;
				b[1]=Account;
				b[2]=dialogue;
				b[3]="23:25";
				b[4]="06/04";
				b[5]="0";
				b[6]=Integer.toString(count_a);
				DataFromDatabase MySQL_DB_1 = new DataFromDatabase();
				MySQL_DB_1.Quickpick_Insert_T(b[0], b[1], b[2], b[3], b[4], b[5], b[6]);	
			} 	*/	
			
			
			Toast.makeText(getActivity(), "傳送完畢!", Toast.LENGTH_LONG).show();
			
			GetNowAccountInfo account_info = new GetNowAccountInfo(getActivity());			
			String account_Type = account_info.getNowAccountType();
			if(account_Type.equals("teacher"))
				MySQL_DB.GCM_sendMessageToCourseMember(course_ID,"TAG3:老師回覆了訊息" );
			else if(account_Type.equals("student"))
				MySQL_DB.GCM_sendMessageToCourseTeacher(course_ID, "TAG3:"+Account+"傳送了一則新訊息");
		}
	}

	private class initPupWinOther {
		PopupWindow mPW;
		Button btn_choose_picture,btn_open_camera;
		public initPupWinOther(Context context,View v){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mPW = new PopupWindow(inflater.inflate(R.layout.popupwindow_discuss_other, null)
					,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT,true);
			
		btn_choose_picture = (Button)mPW.getContentView().findViewById(R.id.btn_choose_picture);
		
		
		btn_choose_picture.setOnClickListener(btn_choose_picture_click);
		
			// 點外面消失
			
			mPW.setBackgroundDrawable(new BitmapDrawable());  
				// 點窗外消失  
			mPW.setOutsideTouchable(true);  
				// 設為可點擊
			mPW.setFocusable(true);  

			mPW.showAtLocation(v.findViewById(R.id.btn_other), Gravity.BOTTOM, 0, 80); 
			mPW.setAnimationStyle(R.style.PopupAnimation);
			mPW.update();
				// 顯示窗戶
				//pw.showAsDropDown(v);
	
		}
		
		private OnClickListener btn_choose_picture_click = new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		            ininDialog();
		            mdialog.show();
			}
			
		};
		
		
			public void setOnDismissListener(OnDismissListener listener){
			    mPW.setOnDismissListener(listener);
			}

	}	
	
	private void ininDialog(){
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.dialog_group_upload_picture ,null);
	
		mdialog.setTitle("選擇圖片").setView(v);
		
		uploadButton = (Button)v.findViewById(R.id.uploadButton);
        btnselectpic = (Button)v.findViewById(R.id.button_selectpic);
        imageview = (ImageView)v.findViewById(R.id.imageView_pic);
        messageText = (TextView)v.findViewById(R.id.messageText);
        
        btnselectpic.setOnClickListener(btnselectpic_click);
        uploadButton.setOnClickListener(uploadButton_click);
 
	}
	
	private OnClickListener btnselectpic_click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PHOTO);
		}
		
	};
	
	private OnClickListener uploadButton_click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stu
			mProgressDialog = ProgressDialog.show(getActivity(), "", "Uploading file...", true);
			messageText.setText("uploading started.....");
			if(imagepath!=null)
			{
				uploadFile = new uploadFile(imagepath) ;
				uploadFile.execute();
			}
				
		}
		
	};
	
	public void onActivityResult(int requestCode, int resultCode,Intent data)
	   {
	      //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
	      if ((requestCode == CAMERA || requestCode == PHOTO ) && data != null)
	      {
	         //取得照片路徑uri
	        
	       
	 	    try {
	 	    	uri = data.getData();
	 	    	Uri mediaUri=null;
	 	    	
	 	    	ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(uri, "r");
	 	 	    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
	 	 	    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				parcelFileDescriptor.close();
				imageview.setImageBitmap(image);
				
				boolean after44 = Build.VERSION.SDK_INT > 19;
				if (after44){
					final String authority = uri.getAuthority();
					if ("com.android.externalstorage.documents".equals(authority)) {
						// 外部儲存空間
						final String docId = DocumentsContract.getDocumentId(uri);
						final String[] divide = docId.split(":");
						final String type = divide[0];
						if ("primary".equals(type)) {
							imagepath = Environment.getExternalStorageDirectory() + "/" + divide[1];
							
						}
					} else if ("com.android.providers.downloads.documents".equals(authority)) {
						// 下載目錄
						final String docId = DocumentsContract.getDocumentId(uri);
						final Uri downloadUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
						imagepath = queryAbsolutePath(getActivity(), downloadUri);
						
					} else if ("com.android.providers.media.documents".equals(authority)) {
						// 圖片、影音檔案
						final String docId = DocumentsContract.getDocumentId(uri);
						final String[] divide = docId.split(":");
						final String type = divide[0];
						
						if ("image".equals(type)) {
							mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
						}
											
						mediaUri = ContentUris.withAppendedId(mediaUri, Long.parseLong(divide[1]));
						imagepath = queryAbsolutePath(getActivity(), mediaUri);
						
					}

				}else {
					// 如果是一般的URI
					final String scheme = uri.getScheme();
					
					if ("content".equals(scheme)) {
						// 內容URI
						imagepath = queryAbsolutePath(getActivity(), uri);
					} else if ("file".equals(scheme)) {
						// 檔案URI
						imagepath = uri.getPath();
					}
					
				}
	 	    
				imageName = imagepath.split("/");
				
                
               // Log.d("imagepath","result"+imagepath);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	      }
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
	
	 	
	 	
	 	class uploadFile extends AsyncTask<String, String, String> {
	 		
	 		String fileName;
	 	
	          HttpURLConnection conn = null;
	          DataOutputStream dos = null;  
	          String lineEnd = "\r\n";
	          String twoHyphens = "--";
	          String boundary = "*****";
	          int bytesRead, bytesAvailable, bufferSize;
	          byte[] buffer;
	          int maxBufferSize = 1 * 1024 * 1024; 
	          File sourceFile; 
	          
	 		 public uploadFile(String sourceUri) {
	 			fileName = sourceUri;
	 			sourceFile = new File(sourceUri); 
	 		 }
	 		
	 		@Override
	 	    protected void onPreExecute() {
	 	        super.onPreExecute();
	 	        
	 	    }
	 		
	 		protected void onProgressUpdate(Integer... progress) {
	 			mProgressDialog.setProgress(progress[0]);
		 	    }

	 		
	 		@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
	 			
	
		        	  if (!sourceFile.isFile()) {
			               
		        		  	mProgressDialog.dismiss(); 
			                
			               Log.e("uploadFile", "Source File not exist :"+imagepath);
			                
			               getActivity().runOnUiThread(new Runnable() {
			                   public void run() {
			                       messageText.setText("Source File not exist :"+ imagepath);
			                   }
			               }); 
			            
			          }
			          else
			          {
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
			                   conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			                   conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			                   conn.setRequestProperty("uploaded_file", fileName); 
               
			                   dos = new DataOutputStream(conn.getOutputStream());
			          
			                   
			                   dos.writeBytes(twoHyphens + boundary + lineEnd); 
			                   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
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
			          
			                   // Responses from the server (code and message)
			                   serverResponseCode = conn.getResponseCode();
			                   String serverResponseMessage = conn.getResponseMessage();
			                     
			                   Log.i("uploadFile", "HTTP Response is : "
			                           + serverResponseMessage + ": " + serverResponseCode);
			                    
			                   if(serverResponseCode == 200){
			                        
			                       getActivity().runOnUiThread(new Runnable() {
			                            public void run() {	
			                            	DataFromDatabase MySQL = new DataFromDatabase();
			                            	String table_name = "discuss_"+course_ID+"_picture";
			                            	MySQL.FC_UploadImage(table_name, groupNum, imageName[imageName.length-1],board_name);
					                    	
			                            	
			                                String msg = "File Upload Completed.\n\n See uploaded file your server. \n\n";
			                                messageText.setText(msg);
			                                Toast.makeText(getActivity(), "File Upload Complete.", Toast.LENGTH_SHORT).show();
			                            }
			                        });                
			                   }    
			                    
			                   //close the streams //
			                   fileInputStream.close();
			                   dos.flush();
			                   dos.close();
			                     
			              } catch (MalformedURLException ex) {
			                   
			            	  mProgressDialog.dismiss();
			                  ex.printStackTrace();
			                   
			                  getActivity().runOnUiThread(new Runnable() {
			                      public void run() {
			                    	  
			                          messageText.setText("MalformedURLException Exception : check script url.");
			                          Toast.makeText(getActivity(), "MalformedURLException", Toast.LENGTH_SHORT).show();
			                      }
			                  });
			                   
			                  Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
			              } catch (Exception e) {
			                   
			            	  mProgressDialog.dismiss(); 
			                  e.printStackTrace();
			                   
			                  getActivity().runOnUiThread(new Runnable() {
			                      public void run() {
			                    	  
			                    	  
			                          messageText.setText("Got Exception : see logcat ");
			                          Toast.makeText(getActivity(), "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
			                      }
			                  });
			                  Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);  
			              }  
			               mProgressDialog.dismiss(); 
			           }
		        	  
		            	   
				return null;
			}
	 		
	 		
		 	    @Override
		 	    protected void onPostExecute(String unused) {

		 	    }

	}
	 	
	 	
	 
	 	class downloadImage implements Runnable{

	 		DataFromDatabase MySQL_DB;
	 		String[] imageName;
	 		ArrayList<String> imageList;
	 		
	 		public downloadImage(DataFromDatabase MySQL_DB){
	 			this.MySQL_DB = MySQL_DB;
	 		}
			@SuppressWarnings("null")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				
				int time=0;
			
			try{
				result = MySQL_DB.return_result;
				while(result == null && time<10){
					result = MySQL_DB.return_result;
					Thread.sleep(500);
					time++;
				}
				Log.d("result","result="+result);
				
				JSONArray jsonArray;
				result = result.trim();
				
				if(!result.equals("null")){
				jsonArray = new JSONArray(result);
				imageList = new ArrayList<String>();
				for(int i = 0 ; i<jsonArray.length() ; i++){
					JSONObject jsonData = jsonArray.getJSONObject(i);
					imageList.add(imageUrl+jsonData.getString("Name"));
					}
				
					getActivity().runOnUiThread(new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						 photoAdpater = new PhotoGalleryAdapter(getActivity(),imageList);
						 LayoutParams lp;        
					        lp=gallery.getLayoutParams();
					        
					        lp.height=mPhone.heightPixels/10;        
					        gallery.setLayoutParams(lp);		
					        		   
						  for (int i = 0; i < photoAdpater.getCount(); i++) {					     
						     View view = photoAdpater.getView(i, null, null);   
						     gallery.addView(view);
						     
						  } 
						}
					});
				}
				
						
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			
		   }
	 };	
	 

	 private void endAllThread(){
		
	 }
	

}

