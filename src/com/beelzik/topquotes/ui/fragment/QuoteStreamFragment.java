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
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.data.QuoteData;
import com.beelzik.topquotes.parse.FindQuotesCallback;
import com.beelzik.topquotes.parse.ParseQuoteDataManager;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.ui.adapter.OnQuotesListBtnLikeClickListener;
import com.beelzik.topquotes.ui.adapter.OnQuotesListBtnShareClickListener;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;


public class QuoteStreamFragment extends Fragment implements OnQuotesListBtnShareClickListener, 
RefreshQuoteListener, OnRefreshListener, OnQuotesListBtnLikeClickListener{
	
	
	ListView  lvStreamQuotes;
	SwipeRefreshLayout swStreamCont;
	QuotesStreamListAdapter quotesAdapter;
	ParseQuoteDataManager parseQuoteDataManager;
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
		
		parseQuoteDataManager=((TopQuotesApplication) getActivity().
				getApplication()).getParseQuoteDataManager();
		sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		quotesAdapter=new QuotesStreamListAdapter(getActivity(),parseQuoteDataManager);
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
		
		
		parseQuoteDataManager.findAllTitlesQuotes(20,0,langFlag,new FindQuotesCallback() {
				
				@Override
				public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
				if (resultCode==FindQuotesCallback.FIND_RESULT_OK) {
					quotesAdapter.clean();
					quotesAdapter.addAll(quotesList);
					quotesAdapter.notifyDataSetChanged();
					swStreamCont.setRefreshing(false);
					
					QuoteStreamListScrollListener scrollListener=new QuoteStreamListScrollListener(20);
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
		int step=10;
		int langFlag;
		
		int pastTotalCount=0;
		
		
		public QuoteStreamListScrollListener(int count) {
			this.count=count;
		}
		
		public void setStep(int step) {
			this.step = step;
		}
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, final int totalItemCount) {
			if(((firstVisibleItem+visibleItemCount)==totalItemCount) && pastTotalCount!=totalItemCount){
				pastTotalCount=totalItemCount;
				langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
				
				parseQuoteDataManager.findAllTitlesQuotes(step, count, langFlag, new FindQuotesCallback() {
					
					@Override
					public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
						quotesAdapter.addAll(quotesList);
						quotesAdapter.notifyDataSetChanged();
						Log.d(GlobConst.LOG_TAG, "quotesStreamListAdapter.getCount(): "+quotesAdapter.getCount());
					}
				});
				
				count+=step;
			}
		}
		
	}
}

