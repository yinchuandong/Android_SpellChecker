package com.yin.spellchecker.main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yin.spellchecker.R;

public class FragEdit extends Fragment {

	private View view;
	private EditText inputEdit;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.frag_edit, container, false);
		inputEdit = (EditText)view.findViewById(R.id.edt_input_edit);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 外部接口，获得输入框的内容
	 * @return
	 */
	public String getInputText(){
		return inputEdit.getText().toString();
	}
	
	
	
	
	

}
