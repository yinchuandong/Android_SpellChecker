package com.yin.spellchecker.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yin.spellchecker.R;
import com.yin.spellchecker.util.DictUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragCorrect extends Fragment {

    private final static String TAG = "FragCorrect";

	//主界面
	private View view;
	private TextView crtLeftView;
	private TextView crtRightView;

	//弹出框
    private AlertDialog candiDialog;
    private View dlgView;
	private TextView dlgDetailView;
	private ListView dlgListView;
	private WordAdapter dlgWordAdapter;

	//句子结果
	private String[][] oldWordMatrix;
	private String[][] newWordMatrix;
	private String[] punctArr;
    private HashMap<String, ArrayList<String>> candidateSet;

    //需要换行的标点
    private HashSet<String> wrapFlag;

	SpannableStringBuilder oldSpanBuilder;
	SpannableStringBuilder newSpanBuilder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.frag_correct, container, false);
		initView();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        this.oldSpanBuilder = new SpannableStringBuilder();
		this.newSpanBuilder = new SpannableStringBuilder();

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        this.dlgView = inflater.inflate(R.layout.dlg_candi_words, null, false);
		this.dlgDetailView = (TextView)dlgView.findViewById(R.id.dlg_detail_view);
        this.dlgListView = (ListView)dlgView.findViewById(R.id.dlg_list_view);
		this.dlgWordAdapter = new WordAdapter(this);
		this.dlgListView.setAdapter(dlgWordAdapter);
        this.wrapFlag = new HashSet<String>();

        initData();
        bindEvent();
	}

	private void initView() {
		crtLeftView = (TextView) view.findViewById(R.id.crt_left_view);
        crtRightView = (TextView) view.findViewById(R.id.crt_right_view);
	}

    private void initData(){
        wrapFlag.add(".");
        wrapFlag.add("?");
        wrapFlag.add(";");

        candiDialog = new AlertDialog.Builder(getActivity()).setTitle("候选词").setView(dlgView).create();
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
        oldSpanBuilder.clear();
        newSpanBuilder.clear();
		//输出
		for (int i = 0; i < oldWordMatrix.length; i++) {
			for (int j = 0; j < oldWordMatrix[i].length; j++) {
                SpannableString oldSpanStr;
				SpannableString newSpanStr;
				String oldWord = oldWordMatrix[i][j];
				String newWord = newWordMatrix[i][j];

				if (!oldWord.equals(newWord)) {
                    oldSpanStr = new SpannableString(oldWord + " ");
                    newSpanStr = new SpannableString(newWord + " ");
                    ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor(getString(R.color.red_s)));
                    oldSpanStr.setSpan(redSpan,
                           0, oldWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    newSpanStr.setSpan(new MyClickableSpan(oldWord),
                            0, newWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}else{
                    oldSpanStr = new SpannableString(oldWord + " ");
					newSpanStr = new SpannableString(newWord + " ");
				}
                oldSpanBuilder.append(oldSpanStr);
				newSpanBuilder.append(newSpanStr);
			}
			//在每句最后一个空格前插入标点符号
            oldSpanBuilder.insert(oldSpanBuilder.length() - 1, punctArr[i]);
			newSpanBuilder.insert(newSpanBuilder.length() - 1, punctArr[i]);
            if(wrapFlag.contains(punctArr[i])){
                oldSpanBuilder.append(new SpannableString("\n"));
                newSpanBuilder.append(new SpannableString("\n"));
            }
		}
		
		crtLeftView.setText(oldSpanBuilder);
		crtLeftView.setMovementMethod(LinkMovementMethod.getInstance());
        crtRightView.setText(newSpanBuilder);
        crtRightView.setMovementMethod(LinkMovementMethod.getInstance());
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

            ArrayList<String> list = candidateSet.get(oldWord);
            if(list == null){
                list = new ArrayList<String>();
            }
            //将更改的单词放到第一个
            int oldId = list.indexOf(newWord);
            if(oldId > 0){
                list.remove(oldId);
                list.add(0, newWord);
            }
            dlgWordAdapter.updateData(list);
            candiDialog.show();
            showTranslate(newWord);
		}
	}


    private void bindEvent(){
        dlgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String word = (String) dlgWordAdapter.getItem(position);
                showTranslate(word);
            }
        });
    }

    /**
     * 显示翻译
     * @param key
     */
    private void showTranslate(String key){
        DictUtil util = DictUtil.getInstance();
        String jsonStr = util.find(key);

        if(jsonStr == null){
            dlgDetailView.setText("暂无词义");
        }

        try {
            JSONObject obj = new JSONObject(jsonStr);

//            Log.d(TAG, obj.toString());
//            Log.d(TAG, obj.toString().length() + "");
            SpannableStringBuilder builder = new SpannableStringBuilder();
            String word = obj.optString("word");
            SpannableString wordSpan = new SpannableString(word);
            wordSpan.setSpan(new AbsoluteSizeSpan(30), 0, word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(wordSpan);
            builder.append("\n\n");

            //变换形式
            if(obj.optString("pt").length() > 0){
                builder.append("过去式：" + obj.optString("pt"));
                builder.append("\n");
            }
            if(obj.optString("p.p").length() > 0){
                builder.append("过去分词：" + obj.optString("p.p"));
                builder.append("\n");
            }
            if(obj.optString("p.pr").length() > 0){
                builder.append("现在分词：" + obj.opt("p.pr"));
                builder.append("\n");
            }
            if(obj.optString("3ps").length() > 0){
                builder.append("三人称单数：" + obj.optString("3ps"));
                builder.append("\n");
            }
            if(obj.optString("plural").length() > 0){
                builder.append("复数：" + obj.optString("plural"));
                builder.append("\n");
            }
            builder.append("\n");

            //解析词性和词义
            JSONArray posArr = obj.optJSONArray("pos");
            if(posArr != null){
                for (int i = 0; i < posArr.length(); i++){
                    JSONObject posObj = posArr.getJSONObject(i);
                    String posKey = (String)posObj.keys().next();
                    JSONArray chnArr = posObj.getJSONArray(posKey);

                    String posLine = posKey + ": ";
                    for(int j = 0; j < chnArr.length(); j++){
                       posLine += chnArr.getString(j);
                    }

                    builder.append(posLine + "\n\n");
                }
            }

            dlgDetailView.setText(builder);
            dlgDetailView.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, jsonStr);
    }





	
	
	

}
