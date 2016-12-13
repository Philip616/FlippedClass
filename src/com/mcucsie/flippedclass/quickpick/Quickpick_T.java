package com.mcucsie.flippedclass.quickpick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.drawable;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcucsie.flippedclass.DataFromDatabase;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

public class Quickpick_T extends Fragment {

	private String course[][];
	private String course_serial[];
	private int check_mode = 0,lastSerial = 0;

	private String[][] list;
	private int count_a;
	private String[][] qp_copy;
	private JSONObject jsonData_1[];

	GetNowCourseInfo course_info;
	QuickpickPictureCount FragementPicCount;
	TextView tv_title, tv_position;
	Button piccount;
	
	String quicktext_tablename;
	String course_ID;

	Bundle bundle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_quickpick_quicktext,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.LL_qt);
		ll.setVisibility(View.GONE);

		piccount = (Button)getView().findViewById(R.id.btn_piccount);
		tv_title = (TextView) getView().findViewById(R.id.tv_qt_title);
		tv_position = (TextView) getView().findViewById(R.id.tv_position);

		piccount.setVisibility(View.GONE);
		
		// -------------------------------------------------------------
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();

		if (course_ID.equals("null"))
			tv_title.setText("─ " + "請選擇課程" + "─\n");
		else {
			tv_title.setText(course_info.getNowCourseID() + "  "
					+ course_info.getNowCourseName());
			tv_position.setText("─歷史訊息 ─");

			quicktext_tablename = "quicktext_" + course_ID;
			DataFromDatabase MySQL_DB = new DataFromDatabase();
			MySQL_DB.Quickpick_getCourseListT(quicktext_tablename,"0");
			Thread waitfordata = new Thread(new getdata(MySQL_DB));
			waitfordata.start();
		}

		// -------------------------------------------------------------

		check_mode = 0;
		Button btn_change = new Button(getActivity());
		btn_change.setId(2);
		btn_change.setText("切   換");
		btn_change.setTextColor(Color.rgb(255, 255, 255));
		btn_change.setBackgroundResource(com.mcucsie.flippedclass.R.drawable.space_btn);
		btn_change.setOnClickListener(btn_change_click);
		LinearLayout r1 = (LinearLayout) getActivity().findViewById(
				R.id.LL_qt_1);
		r1.addView(btn_change);
	}

	class getdata implements Runnable {

		private DataFromDatabase MySQL_DB;

		// 建構子，設定要傳的字串
		public getdata(DataFromDatabase MySQL_DB_1) {
			this.MySQL_DB = MySQL_DB_1;
		}

		@Override
		public void run() {

			// TODO Auto-generated method stub
			String result = null;
			int times = 0;
			try {
				Thread.sleep(1000);
				result = MySQL_DB.return_result;
				while (times <= 10 && result == null) {
					Thread.sleep(1000);
					result = MySQL_DB.return_result;
					Log.d("=====>", "ThreadDownload的多休息了1秒");
					times++;
				}
				if (times <= 10) {
					Log.d("=====>", "成功抓取ID的課程" + result);
					/*
					 * if(result.equals("empty")){ count_a=0;
					 * mHandler.obtainMessage(REFRESH_DATA).sendToTarget(); }
					 * else{
					 */
					JSONArray jsonArray;
					try {

						jsonArray = new JSONArray(result);
						list = new String[jsonArray.length()][2];
						qp_copy = new String[jsonArray.length()][6];
						if (jsonArray.length() == 0)
							list[0][0] = "暫無訊息";
						else {
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonData = jsonArray
										.getJSONObject(i);
								list[i][0] = jsonData.getString("ID") + "  日期:"
										+ jsonData.getString("date") + "  時間:"
										+ jsonData.getString("time");

								list[i][1] = jsonData.getString("msg");

								qp_copy[i][0] = jsonData.getString("ID");
								qp_copy[i][1] = jsonData.getString("msg");
								qp_copy[i][2] = jsonData.getString("time");
								qp_copy[i][3] = jsonData.getString("date");
								qp_copy[i][4] = jsonData.getString("tage_check");
								qp_copy[i][5] = jsonData.getString("serial");

								/*Log.d("=====>", "Quickpick_T 找到的Json = "
										+ list[i]);
								String a = list[i][1];*/
								
							}
						}
						count_a = jsonArray.length();
						
						lastSerial = Integer.valueOf(qp_copy[count_a-1][5]);
						

						mHandler.obtainMessage(REFRESH_DATA, result)
								.sendToTarget();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// }
				else {
					Log.d("=====>", "ThreadDownload下載太多次，下載失敗..." + times);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	protected static final int REFRESH_DATA = 0x00000001;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 顯示網路上抓取的資料
			case REFRESH_DATA:
				// Toast.makeText(getActivity(), list[0],
				// Toast.LENGTH_LONG).show();

				ListView list_quickpick = (ListView) getView().findViewById(
						R.id.lv_qt);

				String course[] = new String[count_a];
				if (count_a > 0) {
					if (!qp_copy[0][0].equals("empty")) {

						for (int i = 0; i < count_a; i++) {
							course[i] = list[i][1];
						}
						QuidckList adp = new QuidckList(count_a, getActivity(),
								list);
						list_quickpick.setAdapter(adp);

					} else {
						String course_1[] = new String[1];
						course_1[0] = "暫無相關資料";
						list_quickpick.setAdapter(new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_list_item_1, course_1));
					}
					
				} else {
					String course_1[] = new String[1];
					course_1[0] = "暫無相關資料";
					list_quickpick.setAdapter(new ArrayAdapter<String>(
							getActivity(), android.R.layout.simple_list_item_1,
							course_1));
				}
				list_quickpick.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				list_quickpick.setOnItemClickListener(list_quickpick_click);

				if (check_mode == 1) {
					clicked();
				}
				break;
			}
		}
	};
	// listView點入
	private OnItemClickListener list_quickpick_click = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			String ID = course_ID;
			Bundle bundle = new Bundle();
			if (check_mode == 1) {
				DataFromDatabase MySQL_DB = new DataFromDatabase();
				MySQL_DB.Quickpick_updateCourseList(ID,
						course_serial[position], "1");
				bundle.putString("Account",
						qp_copy[Integer.parseInt(course_serial[position])][0]);
				Log.d("=====>", "onItemClick的if crash");
			} else {
				bundle.putString("Account", qp_copy[position][0]);
				Toast.makeText(getActivity(), qp_copy[position][0],
						Toast.LENGTH_LONG).show();
				Log.d("=====>", "onItemClick的else crash");
			}
			Log.d("=====>", "onItemClick crash");
			bundle.putByte("check_ID", (byte) 1);
			QuicktextFragment frag = new QuicktextFragment();
			frag.setArguments(bundle);
			getFragmentManager().beginTransaction().addToBackStack(null)
					.replace(R.id.container, frag).commit();
		}

	};

	private OnClickListener btn_change_click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (check_mode == 0)
				clicked();
			else {
				check_mode = 0;
				Toast.makeText(getActivity(), "此為歷史訊息", Toast.LENGTH_LONG)
						.show();
				init_quicktext();
			}
		}
	};

	public void clicked() {
		Toast.makeText(getActivity(), "此為未讀訊息", Toast.LENGTH_LONG).show();

		piccount.setVisibility(View.VISIBLE);
		piccount.setOnClickListener(btn_piccount);
		
		LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.LL_qt);
		ll.setVisibility(View.GONE);

		ListView list_quickpick = (ListView) getView().findViewById(R.id.lv_qt);

		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();
		if (course_ID.equals("null"))
			tv_title.setText("─ " + "請選擇課程" + "─\n");
		else {
			// tv_title.setText("─ "+course_ID+"的未讀訊息 ─\n");
			tv_title.setText(course_info.getNowCourseID() + "  "
					+ course_info.getNowCourseName());
			tv_position.setText("─未讀訊息 ─");
		}

		int count = 0;
		for (int count_a_copy = count_a - 1; count_a_copy >= 0; count_a_copy--) {
			int tage = 0;
			if (count_a_copy == count_a - 1
					&& qp_copy[count_a_copy][4].equals("0")) {
				count++;// 計算未讀訊息數
			} else {
				for (int i = count_a_copy + 1; i <= count_a - 1; i++) {
					if (qp_copy[count_a_copy][0].equals(qp_copy[i][0])) {
						tage = 1;
					}
				}
				if (tage == 0 && qp_copy[count_a_copy][4].equals("0")) {
					count++;
				}
			}
		}
		course = new String[count][2];
		course_serial = new String[count];

		count = 0;
		for (int count_a_copy = count_a - 1; count_a_copy >= 0; count_a_copy--) {

			int tage = 0;
			if (count_a_copy == count_a - 1
					&& qp_copy[count_a_copy][4].equals("0")) {
				course[count][0] = qp_copy[count_a_copy][0] + "      日期:"
						+ qp_copy[count_a_copy][3] + "   時間:"
						+ qp_copy[count_a_copy][2];
				course[count][1] = qp_copy[count_a_copy][1];
				course_serial[count] = qp_copy[count_a_copy][5];
				count++;

			} else {
				for (int i = count_a_copy + 1; i <= count_a - 1; i++) {
					if (qp_copy[count_a_copy][0].equals(qp_copy[i][0]))
						tage = 1;

				}
				if (tage == 0 && qp_copy[count_a_copy][4].equals("0")) {
					Toast.makeText(getActivity(), qp_copy[count_a_copy][4],
							Toast.LENGTH_LONG).show();
					course[count][0] = qp_copy[count_a_copy][0] + "      日期:"
							+ qp_copy[count_a_copy][3] + "   時間:"
							+ qp_copy[count_a_copy][2];
					course[count][1] = qp_copy[count_a_copy][1];
					course_serial[count] = qp_copy[count_a_copy][5];
					count++;
				}
			}
		}

		QuidckList adp = new QuidckList(count, getActivity(), course);
		list_quickpick.setAdapter(adp);
		list_quickpick.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		check_mode = 1;// 未讀訊息

		list_quickpick.setOnItemClickListener(list_quickpick_click);

		
		// -------------------------------------------------------------

	}

	private OnClickListener btn_piccount = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			bundle = getActivity().getIntent().getExtras();
			bundle.putInt("lastSerial", lastSerial);
			
			FragementPicCount = new QuickpickPictureCount();
			
			getFragmentManager()
			.beginTransaction()
			.replace(R.id.container, FragementPicCount)
			.addToBackStack(null)
			.commit();
			
			FragementPicCount.setArguments(bundle);
		}
		
	};

	// 讓貼圖變已讀
	public void init_count(){
		String ID = course_ID;
		Bundle bundle = new Bundle();
		for (int count_a_copy = count_a - 1; count_a_copy >= 0; count_a_copy--) {
			if(qp_copy[count_a_copy][4].equals("0")
					&& (qp_copy[count_a_copy][1].equals("#000")|| 
							qp_copy[count_a_copy][1].equals("#001")|| 
							qp_copy[count_a_copy][1].equals("#002")|| 
							qp_copy[count_a_copy][1].equals("#003"))){			
				DataFromDatabase MySQL_DB = new DataFromDatabase();
				MySQL_DB.Quickpick_updateCourseList(ID, course_serial[count_a_copy], "1");
				//bundle.putString("Account", qp_copy[Integer.parseInt(course_serial[count_a_copy])][0]);
			}	
		
			else{
				bundle.putString("Account", qp_copy[count_a_copy][0]);
				Toast.makeText(getActivity(), qp_copy[count_a_copy][0], Toast.LENGTH_LONG).show();
			}
		}
		bundle.putByte("check_ID", (byte) 1);
		QuicktextFragment frag = new QuicktextFragment();
	}

	public void init_quicktext() {
		

		// -------------------------------------
		course_info = new GetNowCourseInfo(getActivity());
		course_ID = course_info.getNowCourseID();

		if (course_ID.equals("null"))
			tv_title.setText("─ " + "請選擇課程" + "─\n");
		else {
			tv_title.setText(course_info.getNowCourseID() + "  "
					+ course_info.getNowCourseName());
			tv_position.setText("─歷史訊息 ─");

			quicktext_tablename = "quicktext_" + course_ID;
			DataFromDatabase MySQL_DB = new DataFromDatabase();
			MySQL_DB.Quickpick_getCourseListT(quicktext_tablename,"0");
			Thread waitfordata = new Thread(new getdata(MySQL_DB));
			waitfordata.start();

		}
		Log.d("=====================>", "okokokokookok");
	}

	public int quicktext_mode() {

		return check_mode;
	}

	

	
}
