package com.mcucsie.flippedclass.group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GroupFragment extends Fragment {
	private TextView tv_group;
	private GetNowCourseInfo gnci;
	private int count;
	private SQLiteDatabase db;
	public String[] course_member;
	private GroupMemberInfoFragment groupInfo;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	    return inflater.inflate(R.layout.fragment_group, container, false);  
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		runable_finish_count=0;
		tv_group=(TextView)getView().findViewById(R.id.tv_group);
		gnci=new GetNowCourseInfo(getActivity());
		groupInfo = new GroupMemberInfoFragment();
//		gnai=new GetNowAccountInfo(getActivity());
		initGroupRootTextView();
		updateAllCoursemember();
	}
	
	@SuppressWarnings("static-access")
	private void updateAllCoursemember() {
		// TODO Auto-generated method stub
		count=getallcoursecount();
		db=getActivity().openOrCreateDatabase("flippedclass_database", getActivity().MODE_PRIVATE, null);
		getActivity().deleteDatabase("flippedclass_groupmemberlist");
		SQLiteDatabase group_db=getActivity().openOrCreateDatabase("flippedclass_groupmemberlist", getActivity().MODE_PRIVATE, null);
		final Cursor cursor=db.rawQuery("SELECT course_ID FROM course_information", null);
		String[] course_ID=new String[cursor.getCount()];
		Thread[] downloadcoursememberlist=new Thread[cursor.getCount()];
		Thread[] downloadgroupmemberlist=new Thread[cursor.getCount()];
//		DataFromDatabase[] getJson=new DataFromDatabase[cursor.getCount()];
//		getJson[0].FC_queryForCourseMemberList("course_memberlist_36101");
//		DataFromDatabase testdd=new DataFromDatabase();
//		getJson[0].FC_queryForCourseMemberList("course_memberlist_36101");
		
		cursor.moveToFirst();
		for(int i=0;i<count;i++){
			course_ID[i]=cursor.getString(0);

			creatCoursememberTable(group_db,course_ID[i]);
			downloadcoursememberlist[i]=new Thread(new GroupMemberlistTableRunnable(group_db,course_ID[i],i));
			downloadcoursememberlist[i].start();
			downloadgroupmemberlist[i]=new Thread(new DownloadGroupMemberRunnable(group_db,course_ID[i],i));
			downloadgroupmemberlist[i].start();
			if(i<count-1)
			cursor.moveToNext();
		}
		
		
		
	}
    
    class GroupMemberlistTableRunnable implements Runnable
	{
		
    	private String course_ID;
    	private String table_name;
    	private DataFromDatabase testdd=new DataFromDatabase();
    	private SQLiteDatabase db;
		// 建構子，設定要傳的字串
		public GroupMemberlistTableRunnable(SQLiteDatabase group_db,String course_ID,int i)
		{
			this.db=group_db;
			this.course_ID=course_ID;
			this.table_name="course_memberlist_"+course_ID;
		}

		@Override
		public void run()
		{

			testdd.FC_queryForCourseMemberList(table_name);
			Log.d("=====>", "Runable Download成員名單的課程ID="+course_ID);
			// TODO Auto-generated method stub
			String result=null;
			int times=0;
			try {
					Thread.sleep(1000);
					
					result=testdd.return_result;
					Log.d("Group",result);
					
					while(times<=10&&result==null){
					   Thread.sleep(1000);
					   result=testdd.return_result;
					   //Log.d("=====>", "測試CLASS ARRAY 執行開始ˋ!");
					   Log.d("=====>", "ThreadDownload的多休息了1秒");	
					   times++;
				   }
					if(times<=10){
						Log.d("=====>", "成功抓取課程成員名單"+result);
						JSONArray jsonArray;
						ContentValues cv=new ContentValues();
						try {
								jsonArray = new JSONArray(result);
								
								course_member = new String[jsonArray.length()];
								for(int i = 0 ; i<jsonArray.length() ; i++)
								{
									JSONObject jsonData = jsonArray.getJSONObject(i);
									course_member[i] = jsonData.getString("account_ID");
									cv.put("account_ID", course_member[i]);
//									Log.d("=====>", "GroupFragment 找到的Json = "+course_member[i]);
									db.insert(table_name, null, cv);
								}
								
//								data.putInt("count", 1);
//								msg.setData(data);
								/*runable_finish_count++;
								if(runable_finish_count>=count);
								{
									 db.close();
									 Log.d("=====>", "Runable Download 完成insert次數="+runable_finish_count+" 關閉Database");
								}*/
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
    
    
    class DownloadGroupMemberRunnable implements Runnable
	{
		
    	private String course_ID;
    	private String table_name;
    	private DataFromDatabase testdd=new DataFromDatabase();
    	private SQLiteDatabase db;
		// 建構子，設定要傳的字串
		public DownloadGroupMemberRunnable(SQLiteDatabase group_db,String course_ID,int i)
		{
			this.db=group_db;
			this.db=getActivity().openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
			this.course_ID=course_ID;
			this.table_name="course_grouplist_"+course_ID;
		}

		@Override
		public void run()
		{

			testdd.FC_checkGroupMemberList(table_name);
//			Log.d("=====>", "測試CLASS ARRAY 執行開始ˋ!");
			Log.d("<==GroupFragment==>", "Runable Download成員名單的課程ID="+course_ID);
			// TODO Auto-generated method stub
			String result=null;
			int times=0;
			try {
					
					Thread.sleep(1000);
					result=testdd.return_result;
					
					while(times<=10 && result==null){
					   Thread.sleep(1000);
					   result=testdd.return_result;
					   Log.d("<==GroupFragment==>", "ThreadDownload的多休息了1秒");	
					   times++;
				   }
					if(times<=10){
						Log.d("<==GroupFragment==>", "成功抓取結果"+result);
						if(result.equals("null"))
						{
							String group_tablename="GroupMember_"+course_ID;
							String createMemberTable="CREATE TABLE if not exists "+group_tablename+" ( groupnum text not null,"
						  			+ "member text not null)";
							db.execSQL(createMemberTable);
							db.delete(group_tablename, null, null);
							Log.d("<==GroupFragment==>", "這個班級沒有分組資料 From MySQL");
						}
						else
						{
							//groupInfo.getGroupmember_data();
							Log.d("<==GroupFragment==>", "Hi");
							JSONparsetoSQLite(db,result,course_ID);
						}
					}
					else
					{
						Log.d("<==GroupFragment==>", "ThreadDownload下載太多次，下載失敗..."+times);
					}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		private void JSONparsetoSQLite(SQLiteDatabase db2, String result,String course_ID) {
			// TODO Auto-generated method stub
			db2=getActivity().openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
			String group_tablename="GroupMember_"+course_ID;
			String createMemberTable="CREATE TABLE if not exists "+group_tablename+" ( groupnum text not null,"
		  			+ "member text not null)";	
			db2.execSQL(createMemberTable);
			db2.delete(group_tablename, null, null);
			JSONArray jsonArray;
			ContentValues cv=new ContentValues();
			try {
					jsonArray = new JSONArray(result);
					
					String[] account_ID = new String[jsonArray.length()];
					String[] groupnum = new String[jsonArray.length()];
					
					for(int i = 0 ; i<jsonArray.length() ; i++)
					{
						JSONObject jsonData = jsonArray.getJSONObject(i);
						account_ID[i] = jsonData.getString("account_ID");
						groupnum[i] = jsonData.getString("groupnum");
						cv.put("member", account_ID[i]);
						cv.put("groupnum", groupnum[i]);
//						Log.d("=====>", "GroupFragment 找到的Json = "+course_member[i]);
						db2.insert(group_tablename, null, cv);
					}
					Log.d("<==GroupFragment==>", "下載分組名單並insert進SQLite : "+course_ID);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
    
    
    
    
	private void creatCoursememberTable(SQLiteDatabase db,String course_ID) {
		// TODO Auto-generated method stub
		String str_creattable="CREATE TABLE if not exists `course_memberlist_"+course_ID+"`(`account_ID` TEXT)";
		db.execSQL(str_creattable);
		Log.d("=====>", "創建了新的成員TABLE"+"course_memberlist_"+course_ID);
	}
	@SuppressWarnings("static-access")
	public int getallcoursecount(){
		SQLiteDatabase db=getActivity().openOrCreateDatabase("flippedclass_database", getActivity().MODE_PRIVATE, null);
		Cursor cursor=db.rawQuery("SELECT course_ID FROM course_information", null);
		
		if(cursor.getCount()!=0)
			db.close();
		
		return cursor.getCount();
	}
	public void initGroupRootTextView() {
		// TODO Auto-generated method stub
		
		if(gnci.getNowCourseID().equals("null"))
			tv_group.setText("請選擇課程");
		
		else
		tv_group.setText(gnci.getNowCourseID() + "  " + gnci.getNowCourseName());
		
	}
	
}
