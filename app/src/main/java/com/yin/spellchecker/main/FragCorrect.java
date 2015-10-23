package com.yin.spellchecker.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yin.spellchecker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragCorrect extends Fragment {

	private View view;
	private TextView correctView;
	
	private ListView wordListView;
	private WordAdapter wordAdapter;

	private String[][] oldWordMatrix;
	private String[][] newWordMatrix;
	private String[] punctArr;
    private HashMap<String, ArrayList<String>> candidateSet;

	SpannableStringBuilder spannableBuilder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.frag_correct, container, false);
		initView();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.spannableBuilder = new SpannableStringBuilder();
		this.wordAdapter = new WordAdapter(this);
		this.wordListView.setAdapter(wordAdapter);
	}

	private void initView() {
		correctView = (TextView) view.findViewById(R.id.crt_text_view);
		wordListView = (ListView)view.findViewById(R.id.crt_list_view);
	}

	/**
	 * 外部调用，由ajax返回过来的结果
	 * @param dataJson
	 * @param candidateJson
	 */
	public void setCorrectResult(JSONObject dataJson, JSONObject candidateJson){
		try {
            //处理句子
			JSONArray oldMatrixJson = dataJson.getJSONArray("oldMatrix");
			JSONArray newMatrixJson = dataJson.getJSONArray("newMatrix");
			JSONArray punctJson = dataJson.getJSONArray("punct");
			oldWordMatrix = new String[oldMatrixJson.length()][];
			newWordMatrix = new String[newMatrixJson.length()][];
			punctArr = new String[punctJson.length()];

			for(int i = 0; i < oldWordMatrix.length; i++){
				JSONArray oldRowJson = oldMatrixJson.getJSONArray(i);
				JSONArray newRowJson = newMatrixJson.getJSONArray(i);
				oldWordMatrix[i] = new String[oldRowJson.length()];
				newWordMatrix[i] = new String[newRowJson.length()];
				punctArr[i] = punctJson.getString(i);

				for(int j = 0; j < oldWordMatrix[i].length; j++){
					oldWordMatrix[i][j] = oldRowJson.getString(j);
					newWordMatrix[i][j] = newRowJson.getString(j);
				}
			}

            //处理候选词
            candidateSet = new HashMap<String, ArrayList<String>>();
            Iterator<String> iter = candidateJson.keys();
            while(iter.hasNext()){
                String key = iter.next();
                JSONArray candiWords = candidateJson.optJSONArray(key);
                ArrayList<String> tmpList = new ArrayList<String>();
                for(int i = 0; i < candiWords.length(); i++){
                    tmpList.add(candiWords.getString(i));
                }
                candidateSet.put(key, tmpList);
            }

            //显示数据
            displayData();
        } catch (JSONException e) {
			e.printStackTrace();
		}

	}


	private void displayData() {
        spannableBuilder.clear();
		//输出
		for (int i = 0; i < oldWordMatrix.length; i++) {
			for (int j = 0; j < oldWordMatrix[i].length; j++) {
				SpannableString oldSpanStr = null;
				String oldStr = "";
				String oldWord = oldWordMatrix[i][j];
				String newWord = newWordMatrix[i][j];
				if (!oldWord.equals(newWord)) {
					oldStr += oldWord + "/" + newWord + " ";
					oldSpanStr = new SpannableString(oldStr);
					oldSpanStr.setSpan(new ForegroundColorSpan(Color.parseColor(getString(R.color.red_s))),
							oldStr.length() - newWord.length() - oldWord.length() - 2,
							oldStr.length() - newWord.length() - 2,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					checkSpell(oldWord, oldSpanStr, oldStr.length() - newWord.length() - 1, oldStr.length() - 1);
				}else{
					oldStr += oldWord + " ";
					oldSpanStr = new SpannableString(oldStr);
				}
				spannableBuilder.append(oldSpanStr);
			}
			//在每句最后一个空格前插入标点符号
			spannableBuilder.insert(spannableBuilder.length() - 1, punctArr[i]);
		}
		
		correctView.setText(spannableBuilder);
		correctView.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void checkSpell(String word, SpannableString spannableStr, int start, int end) {
		spannableStr.setSpan(new MyClickableSpan(word), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

	}

	private class MyClickableSpan extends ClickableSpan {
		private String oldWord;
		private String newWord;
		private int start;
		private int end;

		public MyClickableSpan(String oldWord) {
			this.oldWord = oldWord;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(Color.parseColor(getString(R.color.green_s)));
			ds.setUnderlineText(false);// 去掉下划线
		}

		@Override
		public void onClick(View widget) {
			TextView textView = (TextView)widget;
			Layout layout = textView.getLayout();
			
			this.start = textView.getSelectionStart();
			this.end = textView.getSelectionEnd();
			newWord =textView.getText().subSequence(start, end).toString();
			
//			ArrayList<String> list = new ArrayList<String>();
//			for(int i = 0; i < 10; i++){
//				list.add(oldWord + ":" + newWord + "-" + i);
//			}
            ArrayList<String> list = candidateSet.get(oldWord);
            if(list == null){
                list = new ArrayList<String>();
            }
			wordAdapter.updateData(list);
			Toast.makeText(getActivity(), "clickdspan:" + this.oldWord, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	
	
	
	
	

}
