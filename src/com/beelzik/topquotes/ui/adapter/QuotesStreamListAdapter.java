package com.beelzik.topquotes.ui.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beelzik.topquotes.R;
import com.beelzik.topquotes.db.QuotesData;

public class QuotesStreamListAdapter extends BaseAdapter {

	ArrayList<QuotesData> data;
	LayoutInflater inflater;
	OnQuotesListBtnShareClickListener btnShareClickListener;
	
	public QuotesStreamListAdapter(Context ctx) {
		data=new ArrayList<QuotesData>();
		inflater=LayoutInflater.from(ctx);
	}
	
	public void setBtnShareClickListener(
			OnQuotesListBtnShareClickListener btnShareClickListener) {
		this.btnShareClickListener = btnShareClickListener;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public QuotesData getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		QuotesData quote= getItem(position);
		ViewHolder holder;
		if (view==null) {
			view=inflater.inflate(R.layout.list_item_stream_quote, parent, false);
			holder= new ViewHolder();
			holder.tvListQuote= (TextView) view.findViewById(R.id.tvLisStreamQuote);
			holder.tvListSerialName= (TextView) view.findViewById(R.id.tvListStreamSerialName);
			holder.tvListNumSeason=(TextView) view.findViewById(R.id.tvListStreamNumSeason);
			holder.tvListNumSeries=(TextView) view.findViewById(R.id.tvListStreamNumSeries);
			holder.tvListUserWut=(TextView) view.findViewById(R.id.tvListUserWut);
			holder.ibtnListShare= (ImageButton) view.findViewById(R.id.ibtnListShare);
			
			view.setTag(holder);
			
		}else{
			holder=(ViewHolder) view.getTag();
		}

		holder.tvListQuote.setText(quote.getQuote());
		holder.tvListSerialName.setText(quote.getSerialName());
		holder.tvListNumSeason.setText(quote.getNumSeason());
		holder.tvListNumSeries.setText(quote.getNumSeries());
		holder.tvListUserWut.setText(quote.getUserWut());
		holder.ibtnListShare.setOnClickListener(new QuoteOnClick(view,position) {
			
			@Override
			public void onClick(View v) {
				if(btnShareClickListener!=null){
					btnShareClickListener.
					onBtnShareClickListener(view, position);
				}
			}
		});
		
		
		
		
		
		return view;
	}

	
	public void addAll(List<QuotesData> list){
		data.addAll(list);
	}
	
	public void add(QuotesData quoteData){
		data.add(quoteData);
	}
	
	public void clean(){
		if(data!=null){
			data.clear();
		}
	}

	private class ViewHolder{
		
		TextView tvListQuote;
		TextView tvListSerialName;
		TextView tvListNumSeason;
		TextView tvListNumSeries;
		TextView tvListUserWut;
		ImageButton ibtnListShare;
		
	}
	
	private abstract class QuoteOnClick implements OnClickListener{
		
		View view;
		int position;
		
		public QuoteOnClick(View view, int position) {
			super();
			this.view = view;
			this.position = position;
		}
		
	}
}
