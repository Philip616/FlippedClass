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
		dialog.setTitle("���յ��G");
		dialog.setPositiveButton("����", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                // ���UPositiveButton�n������  
                Toast.makeText(getActivity(), "���ը���...", Toast.LENGTH_SHORT).show();
            }  
        }); 
		dialog.setNegativeButton("�T�w", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                // ���UPositiveButton�n������  
             Toast.makeText(getActivity(), "���սT�{���\!", Toast.LENGTH_SHORT).show();
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
//				Toast.makeText(v.getContext(), "�A�H������"+g, Toast.LENGTH_LONG).show();
				getActivity();
				dialog.setMessage(sb_dialog.toString());
				dialog.show();
			}
			else
			{
				Toast.makeText(v.getContext(), "�A�٨S��ܽҵ{...", Toast.LENGTH_LONG).show();
			}
		}
	};
	

	@SuppressWarnings("static-access")
	private void random_sort(){
		//�ܼƫŧi
//		int datacount=0;
//		int randcount1=0,randcount2=0,tag=0;
		String now_course_ID="null";
		SQLiteDatabase db_memberlist;
		GetNowCourseInfo gnci;
		int all_account_count=0;
		int member_count;	
		int group_count;
		
		
		//�ܼƪ�l�ƻP�k�s
		sb_dialog=new StringBuilder();
		gnci=new GetNowCourseInfo(getActivity());
		now_course_ID=gnci.getNowCourseID();
		
		all_account_count = gnci.getAllMemberCount(now_course_ID);     //���o�ثe�ҵ{���`�H��
		member_count=seekbar.getProgress();	                           //���o�����W�]�w�����դH��
		group_count=all_account_count/member_count;
		if(all_account_count%member_count!=0)
			group_count++;
		db_memberlist=getActivity().openOrCreateDatabase("flippedclass_groupmemberlist", getActivity().MODE_PRIVATE, null);
		Cursor cursor=db_memberlist.rawQuery("SELECT * FROM course_memberlist_"+now_course_ID, null);
		cursor.moveToFirst();

		Log.d("=====>", "���t���� �C�ճ̦h"+member_count+"�ӡA�`�ռ�= "+group_count);
		sb_dialog.append("�ҵ{�`�H�� = "+all_account_count);
		sb_dialog.append("\n�C�@�ճ̦h "+member_count+" �ӤH\n�`�ռ� = "+group_count);
		sb_dialog.append("\n�T�w�n�H�o�ӵ��G�i����ն�?\n(���i���s���թάO��ʧ��)");


	}

	@SuppressWarnings("static-access")
	private void insertDataToSQLite() {
		// TODO Auto-generated method stub
		//�ܼƫŧi
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
				
				
				//�ܼƪ�l�ƻP�k�s
				sb_dialog=new StringBuilder();
				gnci=new GetNowCourseInfo(getActivity());
				now_course_ID=gnci.getNowCourseID();
				group_tablename="GroupMember_"+now_course_ID;
				
				
				all_account_count = gnci.getAllMemberCount(now_course_ID);     //���o�ثe�ҵ{���`�H��
				member_count=seekbar.getProgress();	                           //���o�����W�]�w�����դH��
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
				//SQlite ��ƪ��l�ƻP�M��
				  String createMemberTable="CREATE TABLE if not exists "+group_tablename+" ( groupnum text not null,"
			  			+ "member text not null)";
				  db_group=getActivity().openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
				  db_group.execSQL(createMemberTable);
				  db_group.delete(group_tablename, null, null);
				  createMemberTable="CREATE TABLE if not exists GroupMemberInfo ( name text not null,"
				  			+ "grade integer not null)";
				  db_group.execSQL(createMemberTable);
				  db_group.close();
				  Log.d("=====>","GroupSortFragment �M����ƪ�"+group_tablename);
				  
				// MySQL��ƪ�M��  
				  deldb.FC_deleteGroupMemberList("course_grouplist_"+now_course_ID);
				  deldb.FC_deleteGroupDiscussList("discusslist_"+now_course_ID+"_team",now_course_ID);
				//Account_ID�~�P
				Random r=new Random(seed);
				for(int i=0;i<100;i++){
					int a=r.nextInt(all_account_count);
					int b=r.nextInt(all_account_count);
					
					String temp=account_ID[a];
					account_ID[a]=account_ID[b];
					account_ID[b]=temp;
				}
				Log.d("=====>", "�~�P���� ��0�� = "+account_ID[0]);
//				Log.d("=====>", "�~�P���� ��1�� = "+account_ID[1]);
				
				
				//���t�����i�JGroup_container�e��
				for(int i=0;i<all_account_count;i++){
						group_container_ID[i%group_count][group_container_countstack[i%group_count]]=account_ID[i];
						group_container_countstack[i%group_count]++;
				}
				Log.d("=====>", "���t���� �C�ճ̦h"+member_count+"�ӡA�`�ռ�= "+group_count);
				db_group=getActivity().openOrCreateDatabase("FlippedClass_DataBase", Activity.MODE_PRIVATE, null);
				sb=new StringBuilder();
				db_group.beginTransaction();
				for(int i=0;i<group_count;i++){
//					Log.d("=====>", "�H�U�O�p�ժ���"+i+"��");
//					sb.append("\n�H�U�O�p�ժ���"+i+"��");
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
//							Log.d("=====>", "��"+j+"�� = "+group_container_ID[i][j]);
//							sb.append("\n��"+j+"�� = "+group_container_ID[i][j]);
					}
					
				}
				db_group.setTransactionSuccessful();
				db_group.endTransaction();
				db_group.close();
					
				Thread FC_insertThread=new Thread(new GroupMemberlistinsertRunnable(now_course_ID,group_container_ID,group_count,member_count,deldb));
				FC_insertThread.start();
				
				sb.append("����");

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
	// �غc�l�A�]�w�n�Ǫ��r��
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
//			Log.d("=====>", "�H�U�O�p�ժ���"+i+"��");
//			sb.append("\n�H�U�O�p�ժ���"+i+"��");
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
		Log.d("<==GroupSortFragment==>", "MySQL����Insert");
		deldb.GCM_sendMessageToCourseMember(now_course_ID, "TAG2 : �Z��"+now_course_ID+"�����զW����ܤF!!");
	}
}
