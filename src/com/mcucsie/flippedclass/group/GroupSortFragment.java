package com.mcucsie.flippedclass.group;

import java.util.Random;




import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class GroupSortFragment extends Fragment{
	RadioButton rb_rand;
	StringBuilder sb,sb_dialog;
	Button btn_sort_send;
	SeekBar seekbar;
	TextView tv_memcount;
	AlertDialog.Builder dialog;
	int seekbar_progress;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 return inflater.inflate(R.layout.fragment_group_sort, container, false);  
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		dialog = new AlertDialog.Builder(getActivity());
		seekbar=(SeekBar) getView().findViewById(R.id.seekb_sort_memcount);
		rb_rand=(RadioButton)getView().findViewById(R.id.radio0);
		btn_sort_send=(Button)getView().findViewById(R.id.btn_sort_sort);
		tv_memcount=(TextView) getView().findViewById(R.id.tv_sort_memcount);
	    btn_sort_send.setOnClickListener(btn_sort_send_click);
	    seekbar.setOnSeekBarChangeListener(seekbar_change);
	    seekbar_progress=seekbar.getProgress();
	    tv_memcount.setText(String.valueOf(seekbar_progress));
	    initDialog();
	}
	private void initDialog() {
		// TODO Auto-generated method stub
//		dialog.setMessage("Hi this is test dialog");
		dialog.setTitle("分組結果");
		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                // 按下PositiveButton要做的事  
                Toast.makeText(getActivity(), "分組取消...", Toast.LENGTH_SHORT).show();
            }  
        }); 
		dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                // 按下PositiveButton要做的事  
             Toast.makeText(getActivity(), "分組確認成功!", Toast.LENGTH_SHORT).show();
             insertDataToSQLite();

            }
        }); 
	}
	private OnSeekBarChangeListener seekbar_change=new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if(progress==0)
			{
				seekbar.setProgress(1);
				progress=1;
			}
			seekbar_progress=progress;
			tv_memcount.setText(String.valueOf(seekbar_progress));
		}
	};
	private OnClickListener btn_sort_send_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			GetNowCourseInfo gnc=new GetNowCourseInfo(getActivity());
			
			if(!gnc.getNowCourseID().equals("null"))
			{
				random_sort();
//				Toast.makeText(v.getContext(), "你隨機分組"+g, Toast.LENGTH_LONG).show();
				getActivity();
				dialog.setMessage(sb_dialog.toString());
				dialog.show();
			}
			else
			{
				Toast.makeText(v.getContext(), "你還沒選擇課程...", Toast.LENGTH_LONG).show();
			}
		}
	};
	

	@SuppressWarnings("static-access")
	private void random_sort(){
		//變數宣告
//		int datacount=0;
//		int randcount1=0,randcount2=0,tag=0;
		String now_course_ID="null";
		SQLiteDatabase db_memberlist;
		GetNowCourseInfo gnci;
		int all_account_count=0;
		int member_count;	
		int group_count;
		
		
		//變數初始化與歸零
		sb_dialog=new StringBuilder();
		gnci=new GetNowCourseInfo(getActivity());
		now_course_ID=gnci.getNowCourseID();
		
		all_account_count = gnci.getAllMemberCount(now_course_ID);     //取得目前課程的總人數
		member_count=seekbar.getProgress();	                           //取得介面上設定的分組人數
		group_count=all_account_count/member_count;
		if(all_account_count%member_count!=0)
			group_count++;
		db_memberlist=getActivity().openOrCreateDatabase("flippedclass_groupmemberlist", getActivity().MODE_PRIVATE, null);
		Cursor cursor=db_memberlist.rawQuery("SELECT * FROM course_memberlist_"+now_course_ID, null);
		cursor.moveToFirst();

		Log.d("=====>", "分配完成 每組最多"+member_count+"個，總組數= "+group_count);
		sb_dialog.append("課程總人數 = "+all_account_count);
		sb_dialog.append("\n每一組最多 "+member_count+" 個人\n總組數 = "+group_count);
		sb_dialog.append("\n確定要以這個結果進行分組嗎?\n(日後可重新分組或是手動更改)");


	}

	@SuppressWarnings("static-access")
	private void insertDataToSQLite() {
		// TODO Auto-generated method stub
		//變數宣告
				long seed = System.currentTimeMillis();
//				int datacount=0;
//				int randcount1=0,randcount2=0,tag=0;
				String now_course_ID="null";
				SQLiteDatabase db_group;
				SQLiteDatabase db_memberlist;
				String group_tablename="null";
				String[] account_ID;
				String[][] group_container_ID;
				GetNowCourseInfo gnci;
				int all_account_count=0;
				int member_count;	
				int group_count;
				int tag=0;
				int[] randcountWithtag=new int[seekbar.getProgress()];
				int[] group_container_countstack;
				DataFromDatabase deldb=new DataFromDatabase();
				
				
				//變數初始化與歸零
				sb_dialog=new StringBuilder();
				gnci=new GetNowCourseInfo(getActivity());
				now_course_ID=gnci.getNowCourseID();
				group_tablename="GroupMember_"+now_course_ID;
				
				
				all_account_count = gnci.getAllMemberCount(now_course_ID);     //取得目前課程的總人數
				member_count=seekbar.getProgress();	                           //取得介面上設定的分組人數
				account_ID=new String[all_account_count];	
				group_count=all_account_count/member_count;
				if(all_account_count%member_count!=0)
					group_count++;
				group_container_ID=new String[group_count][member_count];
				group_container_countstack=new int[group_count];
				
				db_memberlist=getActivity().openOrCreateDatabase("flippedclass_groupmemberlist", getActivity().MODE_PRIVATE, null);
				Cursor cursor=db_memberlist.rawQuery("SELECT * FROM course_memberlist_"+now_course_ID, null);
				cursor.moveToFirst();
				
				for(int i=0;i<group_count;i++)
				{
					for(int j=0;j<member_count;j++)
					group_container_ID[i][j]="null";
					
					group_container_countstack[i]=0;
				}
				for(int i=0;i<all_account_count;i++)
				{		
					account_ID[i]=cursor.getString(0);
					if(i<cursor.getCount()-1)
					cursor.moveToNext();
				}
				for(int i=0;i<member_count;i++){
					randcountWithtag[i]=tag;
				}
				db_memberlist.close();
				//SQlite 資料表初始化與清空
				  String createMemberTable="CREATE TABLE if not exists "+group_tablename+" ( groupnum text not null,"
			  			+ "member text not null)";
				  db_group=getActivity().openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
				  db_group.execSQL(createMemberTable);
				  db_group.delete(group_tablename, null, null);
				  createMemberTable="CREATE TABLE if not exists GroupMemberInfo ( name text not null,"
				  			+ "grade integer not null)";
				  db_group.execSQL(createMemberTable);
				  db_group.close();
				  Log.d("=====>","GroupSortFragment 清除資料表"+group_tablename);
				  
				// MySQL資料表清空  
				  deldb.FC_deleteGroupMemberList("course_grouplist_"+now_course_ID);
				  deldb.FC_deleteGroupDiscussList("discusslist_"+now_course_ID+"_team",now_course_ID);
				//Account_ID洗牌
				Random r=new Random(seed);
				for(int i=0;i<100;i++){
					int a=r.nextInt(all_account_count);
					int b=r.nextInt(all_account_count);
					
					String temp=account_ID[a];
					account_ID[a]=account_ID[b];
					account_ID[b]=temp;
				}
				Log.d("=====>", "洗牌完成 第0個 = "+account_ID[0]);
//				Log.d("=====>", "洗牌完成 第1個 = "+account_ID[1]);
				
				
				//分配成員進入Group_container容器
				for(int i=0;i<all_account_count;i++){
						group_container_ID[i%group_count][group_container_countstack[i%group_count]]=account_ID[i];
						group_container_countstack[i%group_count]++;
				}
				Log.d("=====>", "分配完成 每組最多"+member_count+"個，總組數= "+group_count);
				db_group=getActivity().openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
				sb=new StringBuilder();
				db_group.beginTransaction();
				for(int i=0;i<group_count;i++){
//					Log.d("=====>", "以下是小組的第"+i+"組");
//					sb.append("\n以下是小組的第"+i+"組");
					for(int j=0;j<member_count;j++)
					{
						
							ContentValues insertCV_Timetible=new ContentValues();
							insertCV_Timetible.put("groupnum", String.valueOf(i));
							if(group_container_ID[i][j].contains("null"))
								{group_container_ID[i][j]+=i;group_container_ID[i][j]+=j;}
							insertCV_Timetible.put("member", group_container_ID[i][j]);
							db_group.insert(group_tablename, null, insertCV_Timetible);
							
							cursor=db_group.rawQuery("SELECT groupnum FROM GroupMember_"+now_course_ID, null);
//							deldb.FC_insertGroupMemberList("course_grouplist_"+now_course_ID, group_container_ID[i][j], String.valueOf(i));
//							Log.d("=====>", "第"+j+"個 = "+group_container_ID[i][j]);
//							sb.append("\n第"+j+"個 = "+group_container_ID[i][j]);
					}
					
				}
				db_group.setTransactionSuccessful();
				db_group.endTransaction();
				db_group.close();
					
				Thread FC_insertThread=new Thread(new GroupMemberlistinsertRunnable(now_course_ID,group_container_ID,group_count,member_count,deldb));
				FC_insertThread.start();
				
				sb.append("完成");

	}  
	
}

class GroupMemberlistinsertRunnable implements Runnable
{
	
	private String now_course_ID;
	private int group_count;
	private int member_count;
	private String[][] group_container_ID;
	private DataFromDatabase deldb;
	private DataFromDatabase test = new DataFromDatabase();
	// 建構子，設定要傳的字串
	public GroupMemberlistinsertRunnable(String now_course_ID,
			String[][] group_container_ID, int group_count, int member_count,DataFromDatabase deldb) {
		// TODO Auto-generated constructor stub
		this.group_container_ID=group_container_ID;
		this.now_course_ID=now_course_ID;
		this.group_count=group_count;
		this.member_count=member_count;
		this.deldb=deldb;
				
	}

	@Override
	public void run()
	{
		test.FC_createGroupList(now_course_ID);
		test.FC_createGroupDiscussList("discussList_publicshare_"+now_course_ID+"_team");
		
		for(int i=0;i<group_count;i++){
//			Log.d("=====>", "以下是小組的第"+i+"組");
//			sb.append("\n以下是小組的第"+i+"組");
			deldb.FC_insertGroupDiscussList("discussList_publicshare_"+now_course_ID+"_team",i+1);
			for(int j=0;j<member_count;j++)
			{
					
					if(group_container_ID[i][j].contains("null"))
						{group_container_ID[i][j]+=i;group_container_ID[i][j]+=j;}
					
					deldb.FC_insertGroupMemberList("course_grouplist_"+now_course_ID, group_container_ID[i][j], String.valueOf(i));
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
		}
		Log.d("<==GroupSortFragment==>", "MySQL完成Insert");
		deldb.GCM_sendMessageToCourseMember(now_course_ID, "TAG2 : 班級"+now_course_ID+"的分組名單改變了!!");
	}
}
