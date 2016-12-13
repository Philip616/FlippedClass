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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ExamEditExamContentFragment extends Fragment{

	EditText edit_ques,edit_chooseA,edit_chooseB,edit_chooseC,edit_chooseD,edit_point;
	RadioGroup radiogroup_editans;
	RadioButton rb_ansA,rb_ansB,rb_ansC,rb_ansD;
	
	String result;
	int ques_code;
	String ques,point,ans,edit_ans;
	String[] choose_choose = new String[4];
	String[] choose_truefalse = new String[2];
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_exam_edit_exam_content, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		edit_ques = (EditText)getView().findViewById(R.id.edit_edit_ques);
		edit_chooseA = (EditText)getView().findViewById(R.id.edit_edit_chooseA);
		edit_chooseB = (EditText)getView().findViewById(R.id.edit_edit_chooseB);
		edit_chooseC = (EditText)getView().findViewById(R.id.edit_edit_chooseC);
		edit_chooseD = (EditText)getView().findViewById(R.id.edit_edit_chooseD);
		edit_point = (EditText)getView().findViewById(R.id.edit_edit_point);
		
		radiogroup_editans = (RadioGroup)getView().findViewById(R.id.radiogroup_edit_ans);
		
		radiogroup_editans.setOnCheckedChangeListener(radiogroup_editans_click);
		
		rb_ansA = (RadioButton)getView().findViewById(R.id.rb_edit_chooseA);
		rb_ansB = (RadioButton)getView().findViewById(R.id.rb_edit_chooseB);
		rb_ansC = (RadioButton)getView().findViewById(R.id.rb_edit_chooseC);
		rb_ansD = (RadioButton)getView().findViewById(R.id.rb_edit_chooseD);

		Bundle bundle = getArguments();
        if (bundle != null) {
        	result = bundle.getString("result");
        	ques_code = bundle.getInt("ques_code");
        }
        
        JSONArray jsonArray;
        
        try {
				jsonArray = new JSONArray(result);
			
				JSONObject jsonData = jsonArray.getJSONObject(ques_code);
				
				Log.d("=====>", "現在創建第" + ques_code +"個Fragment");
			
				ques = jsonData.getString("question_content");
				point = jsonData.getString("point");
				ans = jsonData.getString("answer");
				
				edit_ques.setText(ques);
				edit_point.setText(point);
				
				String type = jsonData.getString("type");
				
				switch(type)
				{
					case "1":
						choose_choose[0] = jsonData.getString("choose_A");
						choose_choose[1] = jsonData.getString("choose_B");
						choose_choose[2] = jsonData.getString("choose_C");
						choose_choose[3] = jsonData.getString("choose_D");
						
						edit_chooseA.setText(choose_choose[0]);
						edit_chooseB.setText(choose_choose[1]);
						edit_chooseC.setText(choose_choose[2]);
						edit_chooseD.setText(choose_choose[3]);
						
					break;
					
					case "2":
						choose_truefalse[0] = jsonData.getString("choose_A");
						choose_truefalse[1] = jsonData.getString("choose_B");
						
						edit_chooseA.setText(choose_truefalse[0]);
						edit_chooseB.setText(choose_truefalse[1]);
						edit_chooseC.setFocusable(false);
						edit_chooseD.setFocusable(false);
						
						rb_ansC.setClickable(false);
						rb_ansD.setClickable(false);
						
					break;
				}
				
				switch(ans)
				{
					case "A":
						rb_ansA.setChecked(true);
						break;
					
					case "B":
						rb_ansB.setChecked(true);
						break;
						
					case "C":
						rb_ansC.setChecked(true);
						break;
							
					case "D":
						rb_ansD.setChecked(true);
						break;
					
					default:
						break;
				}			
				
				edit_ans = ans;
		} 
        catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        }
	}
	
	private RadioGroup.OnCheckedChangeListener radiogroup_editans_click = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			
			switch(checkedId)
			{
				case R.id.rb_edit_chooseA:
					edit_ans = "A";
					break;
				case R.id.rb_edit_chooseB:
					edit_ans = "B";
					break;
				case R.id.rb_edit_chooseC:
					edit_ans = "C";
					break;
				case R.id.rb_edit_chooseD:
					edit_ans = "D";
					break;
			}
			
		}
		
	};
	
}
