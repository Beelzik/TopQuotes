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
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.data.QuoteData;

import com.beelzik.topquotes.parse.FindQuotesCallback;
import com.beelzik.topquotes.parse.ParseQuoteDataManager;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.ui.adapter.OnQuotesListBtnShareClickListener;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;


public class TitleQuotesFragment extends Fragment implements OnQuotesListBtnShareClickListener, RefreshQuoteListener, OnRefreshListener{
	
	
	ListView  lvTitleQuotes;
	SwipeRefreshLayout swTitleCont;
	QuotesStreamListAdapter quotesAdapter;
	ParseQuoteDataManager parseQuoteDataManager;
	private int wutFragment;
	SharedPreferences sp;
	int langFlag;
	String titleName;
	
	public static TitleQuotesFragment newInstance(int sectionNumber) {
		TitleQuotesFragment fragment = new TitleQuotesFragment();
		Bundle args = new Bundle();
		args.putInt(GlobConst.ARG_SECTION_FRAGMENT_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public TitleQuotesFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		wutFragment=getArguments().getInt(GlobConst.ARG_SECTION_FRAGMENT_NUMBER);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_title_quotes, container,
				false);
		lvTitleQuotes=(ListView) rootView.findViewById(R.id.lvTitleQuotes);
		swTitleCont = (SwipeRefreshLayout) rootView.findViewById(R.id.swTitleCont);
		swTitleCont.setOnRefreshListener(this);
		swTitleCont.setColorScheme(android.R.color.holo_blue_bright, 
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
		
		quotesAdapter=new QuotesStreamListAdapter(getActivity(), parseQuoteDataManager);
		quotesAdapter.setBtnShareClickListener(this);
		lvTitleQuotes.setAdapter(quotesAdapter);
		
		
		
		onRefresh();
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
		//((MainActivity) getActivity()).onNavigationDrawerItemSelected(0);
	}

	@Override
	public void onRefresh() {
		Log.d(GlobConst.LOG_TAG,"TitleQuote refreshQuotes() "+wutFragment+" wutLang: "+langFlag);
		sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
	    langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
		
		swTitleCont.setRefreshing(true);

		titleName=parseQuoteDataManager.getTitleList(langFlag).get(wutFragment);
		
		Log.d(GlobConst.LOG_TAG,"cur title name: "+titleName);
		parseQuoteDataManager.findTitleQuotes(20,0,titleName,langFlag,new FindQuotesCallback() {
				
				@Override
				public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
					if (resultCode==FindQuotesCallback.FIND_RESULT_OK) {
						quotesAdapter.clean();
						quotesAdapter.addAll(quotesList);
						quotesAdapter.notifyDataSetChanged();
						swTitleCont.setRefreshing(false);
						
						QuoteTitleListScrollListener scrollListener=new QuoteTitleListScrollListener(20);
						lvTitleQuotes.setOnScrollListener(scrollListener);
					}
				}
			});
	}
	
	private class QuoteTitleListScrollListener implements OnScrollListener{
		
		int count;
		int step=10;
		int langFlag;
		
		int pastTotalCount=0;
		
		
		public QuoteTitleListScrollListener(int count) {
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
						
					}
				});
				
				parseQuoteDataManager.findTitleQuotes(step, count, titleName, totalItemCount, new FindQuotesCallback() {
					
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

