package com.mcucsie.flippedclass.exam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mcucsie.flippedclass.R;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ExamQuestionContentFragment extends Fragment{

	TextView txt_ques;
	ListView list_quescontentlist;
	String result,ques;
	int ques_code;
	public int user_ans = -1;
	String[] choose_choose = new String[4];
	String[] choose_truefalse = new String[2];
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_question_choose, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		txt_ques = (TextView)getView().findViewById(R.id.txt_question);
		list_quescontentlist = (ListView)getView().findViewById(R.id.list_quescontentlist);
		
		list_quescontentlist.setOnItemClickListener(new list_quescontentlist_click());
		
        Bundle bundle = getArguments();
        if (bundle != null) {
            result = bundle.getString("result");
            ques_code = bundle.getInt("ques_code");
        }
        
        Log.d("=====>", "ExamQuestionContentFragment result = " + result + " ques_num = " + ques_code);
        
        JSONArray jsonArray;
        
        try {
				jsonArray = new JSONArray(result);
			
				JSONObject jsonData = jsonArray.getJSONObject(ques_code);
				
				Log.d("=====>", "現在創建第" + ques_code +"個Fragment");
			
				ques = jsonData.getString("question_content");
				
				String type = jsonData.getString("type");
				
				switch(type)
				{
					case "1":
						choose_choose[0] = jsonData.getString("choose_A");
						choose_choose[1] = jsonData.getString("choose_B");
						choose_choose[2] = jsonData.getString("choose_C");
						choose_choose[3] = jsonData.getString("choose_D");
						
						list_quescontentlist.setAdapter(new ArrayAdapter<String>(getActivity(),
				                android.R.layout.simple_list_item_single_choice, choose_choose));
						list_quescontentlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					break;
					
					case "2":
						choose_truefalse[0] = jsonData.getString("choose_A");
						choose_truefalse[1] = jsonData.getString("choose_B");
						
						list_quescontentlist.setAdapter(new ArrayAdapter<String>(getActivity(),
				                android.R.layout.simple_list_item_single_choice, choose_truefalse));
						list_quescontentlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					break;
				}	
				txt_ques.setText(ques);
		} 
        catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	class list_quescontentlist_click implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
				
			Log.d("=====>", "QuescontentList 按下第" + position +"項");
			
			user_ans = position;
		}
	}	
}
