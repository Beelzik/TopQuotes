package com.beelzik.topquotes.ui.fragment;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.parse.callback.FindQuotesCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.ui.adapter.OnQuotesListBtnLikeClickListener;
import com.beelzik.topquotes.ui.adapter.OnQuotesListBtnShareClickListener;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;


public class QuoteStreamFragment extends Fragment implements OnQuotesListBtnShareClickListener, 
RefreshQuoteListener, OnRefreshListener, OnQuotesListBtnLikeClickListener{
	
	
	ListView  lvStreamQuotes;
	SwipeRefreshLayout swStreamCont;
	QuotesStreamListAdapter quotesAdapter;
	SharedPreferences sp;
	int langFlag;

	
	public static QuoteStreamFragment newInstance(int sectionNumber) {
		QuoteStreamFragment fragment = new QuoteStreamFragment();
		Bundle args = new Bundle();
		args.putInt(GlobConst.ARG_SECTION_FRAGMENT_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public QuoteStreamFragment() {
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_quote_stream, container,
				false);
		lvStreamQuotes=(ListView) rootView.findViewById(R.id.lvStreamQuotes);
		swStreamCont = (SwipeRefreshLayout) rootView.findViewById(R.id.swStreamCont);
		swStreamCont.setOnRefreshListener(this);
		swStreamCont.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
		
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				GlobConst.ARG_SECTION_FRAGMENT_NUMBER),this);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		quotesAdapter=new QuotesStreamListAdapter(getActivity());
		quotesAdapter.setBtnShareClickListener(this);
		quotesAdapter.setBtnLikeClickListener(this);
		//quotesAdapter.setIvAvatarClickListener(this);
		lvStreamQuotes.setAdapter(quotesAdapter);
		
		
		
		refreshQuotes();
	}


	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onBtnShareClickListener(View view, int position) {
		if (GlobConst.DEBUG) {
			Log.d(GlobConst.LOG_TAG, "onBtnShareClickListener: "+position);	
		}	
	}

	@Override
	public void refreshQuotes() {
		Log.d(GlobConst.LOG_TAG,"QuoteStrem refreshQuotes() ");
		
		langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
		
		swStreamCont.setRefreshing(true);
		
		//scrollListener=new StreamScrollListener();
		
		
		QuoteData.findAllTitlesQuotes(getActivity(),GlobConst.QUITES_TO_LOADE,0,langFlag,new FindQuotesCallback() {
				
				@Override
				public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
				if (resultCode==FindQuotesCallback.FIND_RESULT_OK) {
					quotesAdapter.clean();
					quotesAdapter.addAll(quotesList);
					quotesAdapter.notifyDataSetChanged();
					swStreamCont.setRefreshing(false);
					
					QuoteStreamListScrollListener scrollListener=new QuoteStreamListScrollListener(GlobConst.QUITES_TO_LOADE);
					lvStreamQuotes.setOnScrollListener(scrollListener);
				}
				}
			});
	}

	@Override
	public void onRefresh() {
		
		refreshQuotes();
		//scrollListener.refreshStreamListener();
		
	}

	@Override
	public void onBtnLikeClickListener(View view, final int position) {

	}
	
	
	private class QuoteStreamListScrollListener implements OnScrollListener{
		
		int count;
		int step=GlobConst.QUITES_LOADING_STEP;
		int langFlag;
		
		int pastTotalCount=0;
		
		
		public QuoteStreamListScrollListener(int count) {
			this.count=count;
		}
		

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, final int totalItemCount) {
			if(((firstVisibleItem+visibleItemCount+step)==totalItemCount) && pastTotalCount!=totalItemCount){
				pastTotalCount=totalItemCount;
				langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
				
				QuoteData.findAllTitlesQuotes(getActivity(),step, count, langFlag, new FindQuotesCallback() {
					
					@Override
					public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
						quotesAdapter.addAll(quotesList);
						quotesAdapter.notifyDataSetChanged();
						
					}
				});
				
				count+=step;
			}
		}
		
	}
}

