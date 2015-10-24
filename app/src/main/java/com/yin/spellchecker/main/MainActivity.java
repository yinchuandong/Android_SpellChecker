package com.yin.spellchecker.main;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yin.spellchecker.R;
import com.yin.spellchecker.lib.SpellChecker;
import com.yin.spellchecker.util.AppUtil;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;


public class MainActivity extends Activity {

    public final static String TAG = "MainActivity";

	private FragmentManager fragmentManager = null;
	private FragmentTransaction fragmentTransac = null;

	private FragCorrect fragCorrect = null;
	private FragEdit fragEdit = null;

	private TextView editView = null;
	private TextView correctView = null;
    private TextView clearView = null;

    private FinalHttp finalHttp = null;
	private OnHttpCallback onHttpCallback = null;
    private String[] initParams = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        finalHttp = new FinalHttp();
		onHttpCallback = new OnHttpCallback();

        SpellChecker checker = new SpellChecker();
        initParams = checker.init();
        if(initParams == null || initParams.length != 2){
            showText("初始化失败");
            this.finish();
            return;
        }



		init();
		bindEvent();
	}


	private void init() {
		
		editView = (TextView)findViewById(R.id.main_edit_view);
		correctView = (TextView)findViewById(R.id.main_correct_view);
        clearView = (TextView)findViewById(R.id.main_clear_view);
		
		fragmentManager = getFragmentManager();
		fragmentTransac = fragmentManager.beginTransaction();

		fragCorrect = new FragCorrect();
		fragEdit = new FragEdit();
		
		fragmentTransac.add(R.id.main_frag_layout, fragEdit);
		fragmentTransac.add(R.id.main_frag_layout, fragCorrect);
		fragmentTransac.hide(fragCorrect);
		fragmentTransac.commit();
	}
	
	
	private void bindEvent(){
		editView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clearView.setEnabled(true);
                fragmentTransac = fragmentManager.beginTransaction();
                fragmentTransac.hide(fragCorrect).show(fragEdit).commit();
            }
        });
		
		correctView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clearView.setEnabled(false);
                fragmentTransac = fragmentManager.beginTransaction();
                fragmentTransac.hide(fragEdit).show(fragCorrect).commit();
                String article = fragEdit.getInputText();
                long curMillis = System.currentTimeMillis();
				AjaxParams params = new AjaxParams();
				params.put("article", article);
                params.put("token", AppUtil.getToken(MainActivity.this, initParams[0], initParams[1], curMillis));
                params.put("time", String.valueOf(curMillis));
				finalHttp.post(C.api.correct, params, onHttpCallback);
            }
        });

        clearView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fragEdit.clearInputText();
                fragmentTransac = fragmentManager.beginTransaction();
                fragmentTransac.hide(fragCorrect).show(fragEdit).commit();
            }
        });
	}


    private class OnHttpCallback extends AjaxCallBack<String>{
        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            super.onFailure(t, errorNo, strMsg);
            Toast.makeText(MainActivity.this, "注册失败，网络错误", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(String json) {
            super.onSuccess(json);

            JSONObject ret = null;
			try {
                ret = new JSONObject(json);
                if(ret.getInt("status") == 1){
                    Log.d(TAG, ret.toString());
                    fragCorrect.setCorrectResult(ret.getJSONObject("data"), ret.getJSONObject("candidate"));
                    fragmentTransac = fragmentManager.beginTransaction();
                    fragmentTransac.hide(fragEdit).show(fragCorrect).commit();
                }else{
                    Log.d(TAG, "status = 0");
                }
            } catch (Exception e){
				e.printStackTrace();
			}

            Log.d(TAG, json);
        }
    }


    private void showText(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }



}
