package com.mcucsie.flippedclass.group;

import com.mcucsie.flippedclass.GetNowAccountInfo;
import com.mcucsie.flippedclass.GetNowCourseInfo;
import com.mcucsie.flippedclass.R;

import android.R.color;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class GroupMainDiscuss extends Fragment {

	Button public_btn,group_btn;
	Bundle bundle;
	
	GetNowAccountInfo gnai;
	GetNowCourseInfo gnci;
	
	String Type;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_group_discuss_main, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		gnai = new GetNowAccountInfo(getActivity());
		gnci = new GetNowCourseInfo(getActivity());
		
		bundle = getArguments();
		Type = gnai.getNowAccountType();
		
		
				
		public_btn = (Button)getView().findViewById(R.id.public_btn);
		group_btn = (Button)getView().findViewById(R.id.group_btn);
		
		public_btn.setOnClickListener(public_btn_Click);
		group_btn.setOnClickListener(group_btn_Click);
		
		initGroupRootTextView();
	}
	
	private OnClickListener public_btn_Click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated methpublic_btnod stub	
			if(Type.equals("teacher")){		
			GroupTchPublicDiscuss frag_TchpublicDiscuss = new GroupTchPublicDiscuss();
			frag_TchpublicDiscuss.setArguments(bundle);	
			
			getFragmentManager()
			.beginTransaction()
			.replace(R.id.frame_group_content, frag_TchpublicDiscuss)
			.addToBackStack(null)
			.commit();	
			}
			
			else{
				GroupStdPublicDiscuss frag_StdpublicDiscuss = new GroupStdPublicDiscuss();
				frag_StdpublicDiscuss.setArguments(bundle);
				
				getFragmentManager()
				.beginTransaction()
				.replace(R.id.frame_group_content, frag_StdpublicDiscuss)
				.addToBackStack(null)
				.commit();			
			}
		}
		
	};
	
	private OnClickListener group_btn_Click = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(Type.equals("teacher")){		
				GroupTchTeamDiscuss frag_TchteamDiscuss = new GroupTchTeamDiscuss();
				frag_TchteamDiscuss.setArguments(bundle);	
				
				getFragmentManager()
				.beginTransaction()
				.replace(R.id.frame_group_content, frag_TchteamDiscuss)
				.addToBackStack(null)
				.commit();	
				}
				
				else{
					GroupStdTeamDiscuss frag_StdteamDiscuss = new GroupStdTeamDiscuss();
					frag_StdteamDiscuss.setArguments(bundle);
					
					getFragmentManager()
					.beginTransaction()
					.replace(R.id.frame_group_content, frag_StdteamDiscuss)
					.addToBackStack(null)
					.commit();			
				}
			
		}
		
	};
	
	public void initGroupRootTextView() {
		// TODO Auto-generated method stub
		
		if(gnci.getNowCourseID().equals("null")){
			public_btn.setEnabled(isHidden());
			group_btn.setEnabled(isHidden());
		}
		
		else
		{
			public_btn.setEnabled(isVisible());
			group_btn.setEnabled(isVisible());
		}
		
	}
	
}
