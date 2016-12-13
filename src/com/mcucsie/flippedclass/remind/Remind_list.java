package com.mcucsie.flippedclass.remind;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcucsie.flippedclass.R;
public class Remind_list extends BaseAdapter{
	private int count_a;	
	private String [][]list;
	private Context context;

	public Remind_list(int count_a,Context context, String [][]list){
		this.count_a = count_a;
		this.context = context;
		this.list = list;
		
		
	}
	
	public final class ViewHolder {
		public ImageView img;
		public TextView ID;
		public TextView data;
		public TextView text;
	}	

	// 因繼承BaseAdapter，需覆寫以下method

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return count_a;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	//list
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			// 初始化holder的text與icon
			holder = new ViewHolder();
			// 使用自定義的Layout
			//LayoutInflater inflater = getActivity().getLayoutInflater();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			convertView = inflater.inflate(R.layout.listview_remind,
				parent, false);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.ID = (TextView) convertView.findViewById(R.id.remindList_ID);
			holder.text = (TextView) convertView.findViewById(R.id.remindList_text);
			holder.data = (TextView) convertView.findViewById(R.id.remindList_data);

			convertView.setTag(holder);
		} 
		else {
			holder = (ViewHolder) convertView.getTag();
		}
			
		// 設定要顯示的資訊
		//holder.img.setImageResource(R.drawable.icon_group);
		holder.ID.setText(list[position][0]);
		holder.text.setText("   "+list[position][2]);
		holder.data.setText(list[position][1]);
				
		return convertView;
	}
}

