package com.mcucsie.flippedclass.quickpick;

import com.mcucsie.flippedclass.R;
import com.mcucsie.flippedclass.R.id;
import com.mcucsie.flippedclass.R.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class QuickpickFragment extends Fragment {
	
	private Button btn_quickpicture,btn_quicktext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	    return inflater.inflate(R.layout.fragment_quickpick, container, false);  
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		btn_quickpicture=(Button)getView().findViewById(R.id.btn_quickpick_quickpicture);
		btn_quicktext=(Button)getView().findViewById(R.id.btn_quickpick_quicktext);
		btn_quickpicture.setOnClickListener(btn_quickpicture_click);
		btn_quicktext.setOnClickListener(btn_quicktext_click);
	}
    private  OnClickListener btn_quickpicture_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int backStackCount = getFragmentManager().getBackStackEntryCount();
			/*for(int i = 0; i < backStackCount; i++) {    
			    getFragmentManager().popBackStack();
			}*/
			// TODO Auto-generated method stub
			Log.d("=====>", "QuickFragment Btn_quickpicture Click!!");
			QuickpictureFragment frag_pic=new QuickpictureFragment();
			getFragmentManager().beginTransaction().replace(R.id.frame_quickpick_content, frag_pic).commit();
			Log.d("=====>", "QuickFragment Btn_quickpicture Fragment Commit!! popBackStack = "+backStackCount);
		}
	};
	private  OnClickListener btn_quicktext_click=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Bundle bundle = getArguments();
			int backStackCount = getFragmentManager().getBackStackEntryCount();
			QuicktextFragment frag_text=new QuicktextFragment();
			frag_text.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.container, frag_text).commit();
			
		}
	};
	
	private class QuickpictureFragment extends Fragment{
		private Button btn_pic1,btn_pic2,btn_pic3,btn_pic4;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			 return inflater.inflate(R.layout.fragment_quickpick_quickpicture, container, false);  
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);
			btn_pic1=(Button)getView().findViewById(R.id.btn_quickpicture1);
			btn_pic2=(Button)getView().findViewById(R.id.btn_quickpicture2);
			btn_pic3=(Button)getView().findViewById(R.id.btn_quickpicture3);
			btn_pic4=(Button)getView().findViewById(R.id.btn_quickpicture4);
			
			btn_pic1.setOnClickListener(btn_pic1_click);
			btn_pic2.setOnClickListener(btn_pic2_click);
			btn_pic3.setOnClickListener(btn_pic3_click);
			btn_pic4.setOnClickListener(btn_pic4_click);
		}
		private OnClickListener btn_pic1_click=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(v.getContext(), "你傳送了速貼圖片 : 1", Toast.LENGTH_SHORT).show();
			}
		};
		private OnClickListener btn_pic2_click=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(v.getContext(), "你傳送了速貼圖片 : 2", Toast.LENGTH_LONG).show();
			}
		};
		private OnClickListener btn_pic3_click=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(v.getContext(), "你傳送了速貼圖片 : 3", Toast.LENGTH_LONG).show();
			}
		};
		private OnClickListener btn_pic4_click=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(v.getContext(), "你傳送了速貼圖片 : 4", Toast.LENGTH_LONG).show();
			}
		};
		
	}
	
	
}
