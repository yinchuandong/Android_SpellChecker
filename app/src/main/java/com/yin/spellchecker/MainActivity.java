package com.yin.spellchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.yin.spell.RealWordCorrect;
import com.yin.spellchecker.lib.SpellChecker;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private FragmentManager fragmentManager = null;
	private FragmentTransaction fragmentTransac = null;

	private FragCorrect fragCorrect = null;
	private FragEdit fragEdit = null;
	
	private TextView editView = null;
	private TextView correctView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
		
		new Thread(){
			@Override
			public void run(){
				long start = System.currentTimeMillis();
				SpellChecker checker = new SpellChecker();
//				String[] path = checker.init();
//				Log.d("path", path[2]+"---");
//				checker.test();
				
				HashMap<String, String> map = checker.getTranMap();
//				ArrayList<String> candiStrings = checker.loadCandidateMap(new String[]{"i", "like", "you"});
//				HashMap<String, Double> initProb = checker.loadInitProb(new String[]{"i", "like", "you"});
//				HashMap<String, Double> tranProb = checker.loadTranProb(new String[]{"i|like", "he|like", "you|like"});
//				HashSet<String> set = checker.loadOxfordWords();
				
//				RealWordCorrect.main(null);
				
				
				long end = System.currentTimeMillis();
				long delay = end - start;
				System.out.println("delay: " + delay + "ms");
			}
		}.start();
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
			}
		});
	}

}
