package com.yin.spellchecker;

import java.util.ArrayList;

import android.R.integer;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class FragCorrect extends Fragment {

	private View view;
	private TextView correctView;
	
	private ListView wordListView;
	private WordAdapter wordAdapter;
	
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
		initData();
	}

	private void initView() {
		correctView = (TextView) view.findViewById(R.id.crt_text_view);
		wordListView = (ListView)view.findViewById(R.id.crt_list_view);
	}

	private void initData() {
		String oldArticle = "there is lots of appe whih I like, he do love you. he really love you";
		if (!oldArticle.endsWith(".")) {
			oldArticle += ".";
		}
		oldArticle = oldArticle.replaceAll("\\s+", " ");

		String newArticle = "there are lots of apple which I like, he does love you. he really love you.";
		newArticle = newArticle.replaceAll("\\s+", " ");

		String[] oldSentences = oldArticle.split("[,.;]");
		String[][] oldLines = new String[oldSentences.length][];
		String[] newSentences = newArticle.split("[,.;]");
		String[][] newLines = new String[newSentences.length][];

		//标点符号
		String[] punctArr = new String[oldSentences.length];
		int offset = 0;
		for (int i = 0; i < oldSentences.length; i++) {
			offset += oldSentences[i].length() + 1;
			punctArr[i] = oldArticle.substring(offset - 1, offset);
			String oldLine = oldSentences[i].trim();
			String[] oldLineArr = oldLine.split(" ");
			oldLines[i] = oldLineArr;

			String newLine = newSentences[i].trim();
			String[] newLineArr = newLine.split(" ");
			newLines[i] = newLineArr;
		}

		
		//输出
		for (int i = 0; i < oldLines.length; i++) {
			for (int j = 0; j < oldLines[i].length; j++) {
				SpannableString oldSpanStr = null;
				String oldStr = "";
				String oldWord = oldLines[i][j];
				String newWord = newLines[i][j];
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
			
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < 10; i++){
				list.add(oldWord + ":" + newWord + "-" + i);
			}
			wordAdapter.updateData(list);
			Toast.makeText(getActivity(), "clickdspan:" + this.oldWord, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	
	
	
	
	

}
