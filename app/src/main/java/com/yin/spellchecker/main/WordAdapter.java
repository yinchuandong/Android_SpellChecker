package com.yin.spellchecker.main;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yin.spellchecker.R;

public class WordAdapter extends BaseAdapter {

	private Context context;
	private FragCorrect fragment;
	private LayoutInflater inflater;
	private ArrayList<String> list;
	
	
	private class ViewHolder{
		TextView wordView;
	}
	
	public WordAdapter(FragCorrect fragment) {
		this.fragment = fragment;
		this.context = fragment.getActivity();
		this.inflater = LayoutInflater.from(this.context);
		this.list = new ArrayList<String>();
	}
	
	public void updateData(ArrayList<String> list){
		this.list = list;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.tpl_crt_list, parent, false);
			holder.wordView = (TextView)convertView.findViewById(R.id.word_view);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.wordView.setText(list.get(position));
		
		return convertView;
	}

}
