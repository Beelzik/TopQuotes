package com.beelzik.topquotes.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beelzik.topquotes.R;
import com.beelzik.topquotes.data.QuizeRecordData;
import com.beelzik.topquotes.data.QuoteData;
import com.beelzik.topquotes.data.UserData;

public class QuizeRecordAdapter extends BaseAdapter {

	
	ArrayList<QuizeRecordData> data;
	LayoutInflater inflater;
	Context ctx;
	
	public QuizeRecordAdapter(Context ctx) {
		this.ctx=ctx;
		data=new ArrayList<QuizeRecordData>();
		inflater=LayoutInflater.from(ctx);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public QuizeRecordData getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void addAll(List<QuizeRecordData> list){
		data.addAll(list);
	}
	
	public void add(QuizeRecordData record){
		data.add(record);
	}
	
	public void clean(){
		if(data!=null){
			data.clear();
		}
	}
	

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		final QuizeRecordData record= getItem(position);
	
		final ViewHolder holder;
		if (view==null) {
			view=inflater.inflate(R.layout.list_item_dialog_quize_record, parent, false);
			holder= new ViewHolder();
			holder.tvQuizePlace= (TextView) view.findViewById(R.id.tvItemDialQuizePlace);
			holder.tvQuizeDate= (TextView) view.findViewById(R.id.tvItemDialQuizeDate);
			holder.tvQuizeUserName=(TextView) view.findViewById(R.id.tvItemDialQuizeUserName);
			holder.tvQuizeScore=(TextView) view.findViewById(R.id.tvItemDialQuizeScore);
			
			
			view.setTag(holder);
			
		}else{
			holder=(ViewHolder) view.getTag();
		}
		
		holder.tvQuizePlace.setText((position+1)+"");
		holder.tvQuizeDate.setText(record.getQuizeRecordDate());
		holder.tvQuizeUserName.setText(record.getQuizeRecordUser().getString(UserData.COLUMN_USER_NAME_DISPLAY));
		holder.tvQuizeScore.setText(record.getQuizeRecordScore()+"");
		
		return view;
	}
	
	private class ViewHolder {
		
		 TextView tvQuizePlace;
		 TextView tvQuizeDate;
		 TextView tvQuizeUserName;
		 TextView tvQuizeScore;
		 
	}

}
