package com.yin.spellchecker.main;

import com.yin.spellchecker.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

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

    private FinalHttp finalHttp = null;
	private OnHttpCallback onHttpCallback = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        finalHttp = new FinalHttp();
		onHttpCallback = new OnHttpCallback();

		init();
		bindEvent();
	}


	private void init() {
		
		editView = (TextView)findViewById(R.id.main_edit_view);
		correctView = (TextView)findViewById(R.id.main_correct_view);
		
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
                fragmentTransac = fragmentManager.beginTransaction();
                fragmentTransac.hide(fragCorrect).show(fragEdit).commit();
            }
        });
		
		correctView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                fragmentTransac = fragmentManager.beginTransaction();
                fragmentTransac.hide(fragEdit).show(fragCorrect).commit();
                String ariticle = fragEdit.getInputText();
				AjaxParams params = new AjaxParams();
				params.put("article", ariticle);
				finalHttp.post(C.api.correct, params, onHttpCallback);
            }
        });
	}


    private class OnHttpCallback extends AjaxCallBack<String>{
        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            super.onFailure(t, errorNo, strMsg);
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

}
