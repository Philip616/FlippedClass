package com.mcucsie.flippedclass.group;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;

import com.mcucsie.flippedclass.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PhotoGalleryAdapter extends BaseAdapter{

	private Context context;
	private Activity activity;
	private ArrayList<String> imageList;
	private ArrayList<Bitmap> bitmapImageList;
	private ArrayList<Boolean> isLoadImageList;
	private boolean isLoadImage = false;
	ImageDialog dialog;
	
	
	public PhotoGalleryAdapter(Context context,ArrayList<String> imageList){
		this.context = context;
		this.activity = (Activity)context;
		this.imageList = imageList;
		bitmapImageList = new ArrayList<Bitmap>();
		isLoadImageList = new ArrayList<Boolean>();
		for(int i=0;i<imageList.size();i++){
			isLoadImageList.add(false);
		}
		
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		//Log.i("msg","position:"+position);
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.fragment_group_discuss_board_photo_view, null);
			holder.imageview = (ImageView) convertView.findViewById(R.id.photo);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		
		if(isLoadImageList.get(position))	
			holder.imageview.setImageBitmap(bitmapImageList.get(position));
	
		else{
			//Toast.makeText(context, "等一下啦!!!不要一直按", Toast.LENGTH_SHORT).show();
			if(!isLoadImage)
				getBitmap(position,holder);	
			
			//holder.imageview.destroyDrawingCache();
		}
		
		convertView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isLoadImageList.get(position)){
				new ImageDialog(context,bitmapImageList.get(position)).show();
				Log.d("adapter","result="+position);
				}
			}
			
		});
		
		return convertView;
	}
	
	
	
	
	
	private void getBitmap(final int position, final ViewHolder holder){
		//Log.i("msg","poossiittioonn"+position);
		
		if(position == imageList.size()){
			isLoadImage = true;
		}
			activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {

				new AsyncTask<String, Void, Bitmap>() {
					@Override
					protected Bitmap doInBackground(String... params) {
						String url = params[0];
						return getBitmapFromURL(url);
					}

					@Override
					protected void onPostExecute(Bitmap result) {
						
						isLoadImageList.set(position, true);
						holder.imageview.setImageBitmap(result);
						bitmapImageList.add(result);
						//Log.d("isLoad","result="+isLoadImageList);
						super.onPostExecute(result);
					}
				}.execute(imageList.get(position));
			}
		});
	}
	

	@Override
	public int getCount() {
		return imageList.size();
	}
	@Override
	public Object getItem(int position) {
		return null;
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	

	private class ViewHolder {
		ImageView imageview;
	}

	private static Bitmap getBitmapFromURL(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			//Bitmap bitmap = BitmapFactory.decodeStream(input);
			
			
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			// 獲取資源圖片
			//InputStream is = getResources().openRawResource(resId);
			return BitmapFactory.decodeStream(input, null, opt);
			
			
//			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	public class ImageDialog extends Dialog {
		
		
		public ImageDialog(Context context,Bitmap bm){
			super(context);	
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_photo_gallery_adapter);
			//getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

			DisplayMetrics metrics = new DisplayMetrics();  
		    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

		   
		 //metrics.widthPixels    metrics.heightPixels
		   
			
			ImageView iv = (ImageView)findViewById(R.id.iv_dialog_photo_gallery_adapter);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(metrics.widthPixels-100,metrics.heightPixels-300);
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setLayoutParams(layoutParams);
			Drawable d = new BitmapDrawable(context.getResources(), bm);
			iv.setImageDrawable(d);
			//setCanceledOnTouchOutside(false);
			// TODO Auto-generated constructor stub
		}
		
	}

	
	

}
