package com.beelzik.topquotes.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.beelzik.topquotes.R;
import com.beelzik.topquotes.parse.callback.OnQuoteLikedCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.parse.data.UserData;
import com.beelzik.topquotes.parse.data.storage.TitleListStorage;
import com.beelzik.topquotes.ui.activity.QuoteAutorActivity;
import com.beelzik.topquotes.util.AnimateFirstDisplayListener;
import com.beelzik.topquotes.util.NetConnectionRespondent;
import com.beelzik.topquotes.util.ShareQuoteUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class QuotesStreamListAdapter extends BaseAdapter implements OnQuoteLikedCallback {

	ArrayList<QuoteData> data;
	LayoutInflater inflater;
	OnQuotesListBtnShareClickListener btnShareClickListener;
	OnQuotesListBtnLikeClickListener btnLikeClickListener;
	OnQuotesListIvAvatarClickListener ivAvatarClickListener;
	TitleListStorage titleListHolder;
	protected ImageLoader imageLoader;
	Context ctx;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	
	public QuotesStreamListAdapter(Context ctx) {
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		animateFirstListener = new AnimateFirstDisplayListener();
		
		data=new ArrayList<QuoteData>();
		inflater=LayoutInflater.from(ctx);
		this.ctx=ctx;
	}
	
	public void setBtnShareClickListener(
			OnQuotesListBtnShareClickListener btnShareClickListener) {
		this.btnShareClickListener = btnShareClickListener;
	}
	
	public void setBtnLikeClickListener(
			OnQuotesListBtnLikeClickListener btnLikeClickListener) {
		this.btnLikeClickListener = btnLikeClickListener;
	}
	
	public void setIvAvatarClickListener(
			OnQuotesListIvAvatarClickListener ivAvatarClickListener) {
		this.ivAvatarClickListener = ivAvatarClickListener;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public QuoteData getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public String getQuoteId(int quotePosition){
		return data.get(quotePosition).getQuoteId();
	}
	
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		final QuoteData quote= getItem(position);
		final ViewHolder holder;
		
		if (view==null) {
			view=inflater.inflate(R.layout.list_item_stream_quote, parent, false);
			holder= new ViewHolder();
			holder.tvListQuote= (TextView) view.findViewById(R.id.tvLisStreamQuote);
			holder.tvListSerialName= (TextView) view.findViewById(R.id.tvListStreamSerialName);
			holder.tvListLbNumSeason=(TextView) view.findViewById(R.id.tvListStreamlbNumSeason);
			holder.tvListLbNumSeries=(TextView) view.findViewById(R.id.tvListStreamlbNumSeries);
			holder.tvListNumSeason=(TextView) view.findViewById(R.id.tvListStreamNumSeason);
			holder.tvListNumSeries=(TextView) view.findViewById(R.id.tvListStreamNumSeries);
			holder.tvListUserWut=(TextView) view.findViewById(R.id.tvListUserWut);
			holder.ibtnListShare= (ImageButton) view.findViewById(R.id.ibtnListShare);
			holder.ibtnListStreamLike= (ImageButton) view.findViewById(R.id.ibtnListStreamLike);
			holder.ivListStreamUserAvatar= (ImageView) view.findViewById(R.id.ivListStreamUserAvatar);
			QuoteData.checkQuoteLikeStatus(ctx,holder.ibtnListStreamLike,quote, position);
			
			view.setTag(holder);
			
		}else{
			holder=(ViewHolder) view.getTag();
		}
		
		holder.tvListQuote.setText(quote.getQuote());
		holder.tvListSerialName.setText(quote.getTitle().getTitleName());
		if (quote.getSeason()==-1) {
			holder.tvListNumSeason.setText("");
			holder.tvListLbNumSeason.setVisibility(View.GONE);
		}else{
			holder.tvListNumSeason.setText(quote.getSeason()+"");
			holder.tvListLbNumSeason.setVisibility(View.VISIBLE);
		}
		
		if (quote.getEpisode()==-1) {
			holder.tvListNumSeries.setText("");
			holder.tvListLbNumSeries.setVisibility(View.GONE);
		}else{
			holder.tvListNumSeries.setText(quote.getEpisode()+"");
			holder.tvListLbNumSeries.setVisibility(View.VISIBLE);
		}
		
		holder.tvListUserWut.setText(quote.getUser().getString(UserData.COLUMN_USER_NAME_DISPLAY));
		
		holder.ibtnListShare.setOnClickListener(new QuoteOnClick(view,position) {
			
			@Override
			public void onClick(View v) {
				ShareQuoteUtil.shareQuote(ctx, quote);
				if(btnShareClickListener!=null){
					
					btnShareClickListener.
					onBtnShareClickListener(view, position);
				}
			}
		});
		
		holder.ibtnListStreamLike.setOnClickListener(new QuoteOnClick(view,position) {
					
					@Override
					public void onClick(View v) {
						if(NetConnectionRespondent.checkNetConection(ctx)){
							QuoteData.likeQuoteInParse(v, getItem(position),  QuotesStreamListAdapter.this);
						}
						if(btnLikeClickListener!=null){
							btnLikeClickListener.
							onBtnLikeClickListener(v, position);
						}
					}
				});
		holder.ivListStreamUserAvatar.setClickable(true);
		holder.ivListStreamUserAvatar.setOnClickListener(new QuoteOnClick(view,position) {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ctx,QuoteAutorActivity.class);
				intent.putExtra(UserData.COLUMN_USER_ID, quote.getUser().getObjectId());
				intent.putExtra(UserData.COLUMN_USER_NAME_DISPLAY, quote.getUser().getString(UserData.COLUMN_USER_NAME_DISPLAY));
				ctx.startActivity(intent);
				
				if(ivAvatarClickListener!=null){
					
					
					ivAvatarClickListener.
					onIvAvatarClickListener(view, position);
				}
			}
		});
	
		QuoteData.checkQuoteLikeStatus(ctx,holder.ibtnListStreamLike,quote, position);
	
		imageLoader.displayImage(quote.getUser().getString(UserData.COLUMN_USER_AVATA_URL),
				holder.ivListStreamUserAvatar, options, animateFirstListener);
		
		
		
		return view;
	}

	
	public void addAll(List<QuoteData> list){
		data.addAll(list);
	}
	
	public void add(QuoteData quoteData){
		data.add(quoteData);
	}
	
	public void clean(){
		if(data!=null){
			data.clear();
		}
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
	
	

	
	private class ViewHolder{
		
		TextView tvListQuote;
		TextView tvListSerialName;
		TextView tvListNumSeason;
		TextView tvListNumSeries;
		TextView tvListLbNumSeason;
		TextView tvListLbNumSeries;
		TextView tvListUserWut;
		ImageButton ibtnListShare;
		ImageButton ibtnListStreamLike;
		ImageView ivListStreamUserAvatar;
	}

	@Override
	public void onQuoteLikedCallback(View view,boolean isLiked) {
		if(isLiked){
			
			 ((ImageButton) view).setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_liked));
		}else{
			((ImageButton) view).setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_like));
			}
		
	}
}
