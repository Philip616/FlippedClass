package com.mcucsie.flippedclass.quickpick;

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


public class QuidckList extends BaseAdapter{

	private int count_a;	
	private String [][]list;
	private Context context;

	public QuidckList(int count_a,Context context, String [][]list){
		this.count_a = count_a;
		this.context = context;
		this.list = list;
		
		
	}
	
	public final class ViewHolder {
		public ImageView img,img_w;
		public TextView title;
		public TextView info;
		public TextView tv_test;
		public TextView tv_test_2;
		public Button viewBtn;
		
		public LinearLayout L_1,L_2;
	}
	
	
	
	//public class MyAdapter extends BaseAdapter {

		

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

				convertView = inflater.inflate(R.layout.listview_quickpick,
				 parent, false);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.img_w = (ImageView) convertView.findViewById(R.id.imageView1);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.info = (TextView) convertView.findViewById(R.id.info);

				holder.L_1 =(LinearLayout)convertView.findViewById(R.id.L_1);
				holder.L_2 =(LinearLayout)convertView.findViewById(R.id.L_2);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}


			
			// 設定要顯示的資訊
			holder.img.setImageResource(R.drawable.icon_group);
			//holder.img_w.setBackgroundResource((Integer) mData.get(position).get("img"));
			holder.img_w.setImageResource(R.drawable.icon);
			holder.title.setText(list[position][0]);
			holder.info.setText(list[position][1]);
			holder.L_1.setVisibility(View.VISIBLE);
			holder.L_2.setVisibility(View.GONE);
			
			if(list[position][1].equals("#000")){
				holder.L_1.setVisibility(View.GONE);
				holder.L_2.setVisibility(View.VISIBLE);
				holder.img_w.setImageResource(R.drawable.shock2);
			} 
			else if(list[position][1].equals("#001")){
				holder.L_1.setVisibility(View.GONE);
				holder.L_2.setVisibility(View.VISIBLE);
				holder.img_w.setImageResource(R.drawable.question);
			} 
			else if(list[position][1].equals("#002")){
				holder.L_1.setVisibility(View.GONE);
				holder.L_2.setVisibility(View.VISIBLE);
				holder.img_w.setImageResource(R.drawable.yes);
			} 
			else if(list[position][1].equals("#003")){
				holder.L_1.setVisibility(View.GONE);
				holder.L_2.setVisibility(View.VISIBLE);
				holder.img_w.setImageResource(R.drawable.no);
			} 
			
			return convertView;
		}
	//}		
}
