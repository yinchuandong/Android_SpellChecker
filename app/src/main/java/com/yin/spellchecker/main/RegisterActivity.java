package com.yin.spellchecker.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yin.spellchecker.R;
import com.yin.spellchecker.lib.SpellChecker;
import com.yin.spellchecker.util.AppUtil;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends Activity {

    EditText passwordEdit;
    TextView submitView;
    FinalHttp finalHttp;
    OnHttpCallback onHttpCallback;
    String[] initParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SpellChecker checker = new SpellChecker();
        initParams = checker.init();
        if (initParams == null || initParams.length != 2){
            Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        finalHttp = new FinalHttp();
        onHttpCallback = new OnHttpCallback();
        init();
        bindEvent();
    }

    private void init(){
        SharedPreferences sp = AppUtil.getSharedPreferences(this);
        if(sp.getBoolean(C.sp.KEY_SIGN, false)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        passwordEdit = (EditText)findViewById(R.id.reg_password);
        submitView = (TextView)findViewById(R.id.reg_submit);
    }

    private void bindEvent(){
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEdit.getText().toString();
                if(password.equals("")){
                    Toast.makeText(RegisterActivity.this, "请输入注册密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                long curMillis = System.currentTimeMillis();
                AjaxParams params = new AjaxParams();
                params.put("password", password);
                params.put("token", AppUtil.getToken(RegisterActivity.this, initParams[0], initParams[1], curMillis));
                params.put("time", String.valueOf(curMillis));
				finalHttp.post(C.api.register, params, onHttpCallback);
            }
        });
    }

    private class OnHttpCallback extends AjaxCallBack<String> {
        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            super.onFailure(t, errorNo, strMsg);
            Toast.makeText(RegisterActivity.this, "注册失败，网络错误", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(String json) {
            super.onSuccess(json);
            try {
                JSONObject ret = new JSONObject(json);
                if(ret.getInt("status") == 1){
                    SharedPreferences.Editor editor = AppUtil.getSharedPreferences(RegisterActivity.this).edit();
                    editor.putBoolean(C.sp.KEY_SIGN, true);
                    editor.apply();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this, "注册失败，" + ret.getString("info"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
