package com.beelzik.topquotes.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.parse.callback.FindRandomQuoteCallback;
import com.beelzik.topquotes.parse.callback.OnQuoteLikedCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.parse.data.UserData;
import com.beelzik.topquotes.ui.activity.QuoteAutorActivity;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;
import com.beelzik.topquotes.util.AnimateFirstDisplayListener;
import com.beelzik.topquotes.util.NetConnectionRespondent;
import com.beelzik.topquotes.util.ShareQuoteUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class SlideFragment extends Fragment{
	 static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
	  
	  int pageNumber;
	  int backColor;
	  
	  
	  
	  
	  TextView tvSlideQuote;
	  TextView tvSlideTitle;
	  TextView tvSlideSeason;
	  TextView tvSlideLbSeason;
	  TextView tvSlideLbEpisode;
	  TextView tvSlideEpisode;
	  TextView tvSlideUserWut;
	  ImageView ivSlideUserWut;
	  ImageButton ibtnSlideShare;
	  ImageButton ibtnSlideLike;
	  
	  QuoteData quote;
	  
	  SharedPreferences sp;
	  
	int langFlag;
	
	protected ImageLoader imageLoader;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	  
	  public SlideFragment() {
		 
		 
		  options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.displayer(new RoundedBitmapDisplayer(20))
			.build();
		  imageLoader=ImageLoader.getInstance();
		  animateFirstListener = new AnimateFirstDisplayListener();
		  
	  }
	  

	  public static SlideFragment newInstance(int page) {
		SlideFragment pageFragment = new SlideFragment();
	    Bundle arguments = new Bundle();
	    arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
	    pageFragment.setArguments(arguments);
	    return pageFragment;
	  }
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
	  }
	  
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	  View view=inflater.inflate(R.layout.fragment_slide, container, false);
	   
	  tvSlideQuote=(TextView) view.findViewById(R.id.tvSlideQuote);
	  tvSlideTitle=(TextView) view.findViewById(R.id.tvSlideSerialName);
	  tvSlideSeason=(TextView) view.findViewById(R.id.tvSlideNumSeason);
	  tvSlideLbSeason=(TextView) view.findViewById(R.id.tvSlidelbNumSeason);
	  tvSlideLbEpisode=(TextView) view.findViewById(R.id.tvSlidelbNumSeries);
	  tvSlideEpisode=(TextView) view.findViewById(R.id.tvSlideNumSeries);
	  tvSlideUserWut=(TextView) view.findViewById(R.id.tvSlideUserWut);
	  ivSlideUserWut=(ImageView) view.findViewById(R.id.ivSlideUserAvatar);
	  ibtnSlideShare= (ImageButton) view.findViewById(R.id.ibtnSlideShare);
	  ibtnSlideLike= (ImageButton) view.findViewById(R.id.ibtnSlideLike);
	  
	  
	  ivSlideUserWut.setClickable(true);
	  ivSlideUserWut.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent(getActivity(),QuoteAutorActivity.class);
			intent.putExtra(UserData.COLUMN_USER_ID, quote.getUser().getObjectId());
			intent.putExtra(UserData.COLUMN_USER_NAME_DISPLAY, quote.getUser().getString(UserData.COLUMN_USER_NAME_DISPLAY));
			startActivity(intent);
		}
	  });
	  
	  
	  ibtnSlideShare.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ShareQuoteUtil.shareQuote(getActivity(), quote);
			
		}
	});
	  
	  ibtnSlideLike.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(final View v) {
			if(NetConnectionRespondent.checkNetConection(getActivity())){
			QuoteData.likeQuoteInParse(v, quote,  new OnQuoteLikedCallback() {
				
				@Override
				public void onQuoteLikedCallback(View view, boolean isLiked) {
					if(isLiked){
						
						 ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.ic_liked));
					}else{
						((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
						}
					
				}
			});
			}
		}
	  });
	    return view;
	  }
	  
	  @Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
		FindSlideQuoteCallback callback=new FindSlideQuoteCallback();
		QuoteData.findRandomQuote(getActivity(),langFlag, callback);
		
		
		 // ivSlideUserWut;
	}
	  
	  
	private class FindSlideQuoteCallback  implements FindRandomQuoteCallback{

		@Override
		public void findRandomQuoteCallback(final QuoteData quote, int resultCode) {
			if(resultCode==FindRandomQuoteCallback.FIND_RESULT_OK){
				
				SlideFragment.this.quote=quote;
					 tvSlideQuote.setText(quote.getQuote());
					  tvSlideTitle.setText(quote.getTitle().getTitleName());
					  
					  if(quote.getSeason()>0){
					  tvSlideLbSeason.setVisibility(View.VISIBLE);
					  tvSlideSeason.setText(quote.getSeason()+"");
					  }else{
						  tvSlideLbSeason.setVisibility(View.GONE);
						  tvSlideSeason.setText("");
					  }
					  if(quote.getEpisode()>0){
						  tvSlideLbEpisode.setVisibility(View.VISIBLE);
						  tvSlideEpisode.setText(quote.getEpisode()+"");
					  }else{
						  tvSlideLbEpisode.setVisibility(View.GONE);
						  tvSlideEpisode.setText("");
					  }
					  tvSlideLbEpisode.setVisibility(View.VISIBLE);
					  tvSlideEpisode.setText(quote.getEpisode()+"");
					  tvSlideUserWut.setText(quote.getUser().getString(UserData.COLUMN_USER_NAME_DISPLAY));
					  imageLoader.displayImage(quote.getUser().getString(UserData.COLUMN_USER_AVATA_URL),
								ivSlideUserWut, options, animateFirstListener);
					 QuoteData.checkQuoteLikeStatus(getActivity(),ibtnSlideLike,quote,0);
				
			}else{
				
			}
		}

		
		
	}
	  
	}
