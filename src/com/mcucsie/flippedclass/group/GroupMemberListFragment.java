package com.mcucsie.flippedclass.group;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

public class GroupMemberListFragment extends Fragment{
	
	private TextView tv1;
	private GridView gv_list;
	private GridView gv_list_title;
	private Button btn_modechange;
	private ModifyModePopupWindow mmp;
	private String group_tablename="null";
	private GetNowCourseInfo gnci;
	private GetNowAccountInfo gnai;
	private int nowmode=0;   // 0=>Normal mode    1=>modify mode
	private int clicked_num=0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 return inflater.inflate(R.layout.fragment_group_member, container, false);  
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		//find all view
		tv1=(TextView)getView().findViewById(R.id.tv_grouplist);
		gv_list=(GridView)getView().findViewById(R.id.gv_grouplist);
		gv_list_title=(GridView)getView().findViewById(R.id.gv_grouplist_title);
		btn_modechange=(Button)getView().findViewById(R.id.btn_grouplist_mode);
		gnci=new GetNowCourseInfo(getActivity());
		gnai=new GetNowAccountInfo(getActivity());
		group_tablename="GroupMember_"+gnci.getNowCourseID();
		if(gnai.getNowAccountType().equals("student")){
			btn_modechange.setVisibility(View.GONE);
			tv1.setGravity(Gravity.CENTER);
			tv1.setText("現在是檢視模式");
			
		}
		
		
		
		if(!gnci.getNowCourseName().equals("null"))
		{
		//讀取資料庫 → 動態新增gridview網格 → 依照人數做伸展。
			try {
				initGridview();
				btn_modechange.setOnClickListener(btn_modechange_click);
				
				
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(getActivity(), "這個課程還沒有分組完成喔!", Toast.LENGTH_LONG).show();
			}
		
		}
		else
		Toast.makeText(getActivity(), "你還沒有選課喔!", Toast.LENGTH_LONG).show();
		
	}
	private void initGridview(){
		SQLiteDatabase db_group=getActivity().openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
		String str_selectTable="SELECT * FROM "+group_tablename+" WHERE groupnum='0'";
		Cursor cursor=db_group.rawQuery(str_selectTable, null);
		int columnum=cursor.getCount()+1;
		gv_list.setNumColumns(columnum);
		gv_list_title.setNumColumns(columnum);
		str_selectTable="SELECT * FROM "+group_tablename;
		cursor=db_group.rawQuery(str_selectTable, null);
		GroupGridListTitleAdapter title_adpater=new GroupGridListTitleAdapter(getActivity(), columnum);
		GroupGridListAdapter adapter=new GroupGridListAdapter(getActivity(),cursor.getCount(),columnum);
		gv_list_title.setAdapter(title_adpater);
		gv_list.setAdapter(adapter);
		db_group.close();
		
		gv_list.setOnItemLongClickListener(item_long_press);
		gv_list.setOnItemClickListener(item_click);
	}
	private OnItemClickListener item_click=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if(nowmode==1){
				TextView tv=(TextView)view;
				if(view.getTag().equals("other"))
				{
					if(clicked_num<2)
					{
						tv.setBackgroundColor(Color.YELLOW);
						view.setTag("clicked");
						clicked_num++;
					}
				}
				else
				{
					tv.setBackgroundColor(Color.WHITE);
					view.setTag("other");
					if(clicked_num>0)
						clicked_num--;
				}
			}
			else if(nowmode==0)
			{
				
			}
			else if(nowmode==3)
			{
				nowmode=1;
			}
		}
	};
	private OnClickListener btn_modechange_click = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			clicked_num=0;
			if(nowmode==0)
			{
				nowmode=1;
				tv1.setText("現在是編輯模式");
				mmp=new ModifyModePopupWindow(getActivity());
				mmp.setOnDismissListener(dismissListener);
				mmp.showAtBottom(getView());
			}
			else if(nowmode==1)
			{
				nowmode=0;
				tv1.setText("現在是檢視模式");
				mmp.dismiss();
			}
//			Toast.makeText(getActivity(), "現在Mode = "+nowmode, Toast.LENGTH_LONG).show();
		}
	};
	
	private OnDismissListener dismissListener=new OnDismissListener() {
		
		@Override
		public void onDismiss() {
			// TODO Auto-generated method stub
			int changetag=mmp.getChangetag();
			int times=0;
			String change_acc_A="null";
			String change_acc_B="null";
			
			if(changetag==1)
			{
				mmp.showAtBottom(getView());
				for(int i=0;i<gv_list.getChildCount();i++)
				{
					if(gv_list.getChildAt(i).getTag().equals("clicked"))
					{
						TextView tv=(TextView)gv_list.getChildAt(i);
						if(times==0)
						{
							change_acc_A=tv.getText().toString();
							times++;
						}
						else
						{
							change_acc_B=tv.getText().toString();
						}
					}
				}
//				Toast.makeText(getActivity(), "ChangeAccA = "+change_acc_A+"\nChangeAccB = "+change_acc_B, Toast.LENGTH_LONG).show();
				changeMember(change_acc_A,change_acc_B);
				initGridview();
				clicked_num=0;
			}
			else if(changetag==99)
			{
				Log.d("<==GroupMemberListFragment==>", "Ondismiss 在例外的狀況");
			}
			else
			{
				for(int i=0;i<gv_list.getChildCount();i++)
				{
					if(gv_list.getChildAt(i).getTag().equals("clicked"))
					{
						TextView tv=(TextView)gv_list.getChildAt(i);
						tv.setBackgroundColor(Color.WHITE);
						tv.setTag("other");
					}
				}
			   nowmode=0;   //退回正常檢視模式
			   Thread FC_insertThread=new Thread(new GroupMemberlistupdateRunnable(getActivity(),gnci.getNowCourseID(),group_tablename));
			   FC_insertThread.start();
			   clicked_num=0;
			   tv1.setText("現在是檢視模式");
			}
		}

		private void changeMember(String change_acc_A, String change_acc_B) {
			// TODO Auto-generated method stub
			try {
				
			SQLiteDatabase db_group=getActivity().openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
			String now_course_ID=gnci.getNowCourseID();
			String table_name="GroupMember_"+now_course_ID;
//			String A_group_num="null";
//			String B_group_num="null";
			ContentValues cv=new ContentValues();
			Cursor cursor;
			cursor=db_group.rawQuery("SELECT groupnum FROM "+table_name+" WHERE member = '"+change_acc_A+"'", null);
			cursor.moveToFirst();
//			A_group_num=cursor.getString(0);
			cursor=db_group.rawQuery("SELECT groupnum FROM "+table_name+" WHERE member = '"+change_acc_B+"'", null);
			cursor.moveToFirst();
//			B_group_num=cursor.getString(0);
			/*if(change_acc_A.contains("null"))
				change_acc_A="null";
			if(change_acc_B.contains("null"))
				change_acc_B="null";*/
			
			
			if(!change_acc_A.equals("null")&&!change_acc_B.equals("null"))
			{
				cv.put("member", change_acc_B+"T");
				db_group.update(table_name, cv, "member='"+change_acc_A+"'", null);
			
				cv=new ContentValues();
				cv.put("member", change_acc_A);
				db_group.update(table_name, cv, "member='"+change_acc_B+"'", null);		
		
				cv=new ContentValues();
				cv.put("member", change_acc_B);
				db_group.update(table_name, cv, "member='"+change_acc_B+"T'", null);
				db_group.close();
			}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
	
	private OnItemLongClickListener item_long_press=new OnItemLongClickListener() {
		

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			TextView tv = (TextView)view;
			if(!tv.getTag().equals("false")){
				Toast.makeText(getActivity(), "你長按了 : "+tv.getText(), Toast.LENGTH_LONG).show();
				PersonInfoPopupWindow mpw=new PersonInfoPopupWindow(getActivity(), tv.getText().toString());
				mpw.showAtDownDrop(view);
				
				if(nowmode==1){
					if(view.getTag().equals("other"))
						view.setBackgroundColor(Color.WHITE);
					else
						view.setBackgroundColor(Color.YELLOW);
					nowmode=3;
				}
			}
			return false;
		}
	};
	public void exceptDismiss(){
		if(mmp!=null){
		 mmp.setChangetag(99);
		 mmp.dismiss();
		}
	}
}
class GroupMemberlistupdateRunnable implements Runnable
{
	private DataFromDatabase mydb;
    private String now_course_ID="";
    private SQLiteDatabase sqlitedb;
    private String group_tablename="";
	// 建構子，設定要傳的字串
	public GroupMemberlistupdateRunnable(Context context,String now_course_ID,String group_tablename) {
		// TODO Auto-generated constructor stub
		mydb=new DataFromDatabase();
		this.now_course_ID=now_course_ID;
		this.group_tablename=group_tablename;
		this.sqlitedb=context.openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
	}

	@Override
	public void run()
	{
		Cursor cursor=sqlitedb.rawQuery("SELECT * FROM "+group_tablename, null);
		cursor.moveToFirst();
		mydb.FC_deleteGroupMemberList("course_grouplist_"+now_course_ID);
		for(int i=0;i<cursor.getCount();i++){
			try {
				Thread.sleep(50);
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mydb.FC_insertGroupMemberList("course_grouplist_"+now_course_ID, cursor.getString(1), cursor.getString(0));
			if(i<cursor.getCount()-1)
				cursor.moveToNext();
		}
		Log.d("<==GroupMemberListFragment==>", "MySQL完成Insert");
		mydb.GCM_sendMessageToCourseMember(now_course_ID, "TAG2 : 班級"+now_course_ID+"的分組名單改變了!!");
	}
	
	
}