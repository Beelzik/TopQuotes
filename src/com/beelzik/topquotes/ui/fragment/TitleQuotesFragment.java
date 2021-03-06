package com.beelzik.topquotes.ui.fragment;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
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
import com.beelzik.topquotes.parse.callback.FindQuotesCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.parse.data.storage.TitleListStorage;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.ui.adapter.OnQuotesListBtnShareClickListener;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;


public class TitleQuotesFragment extends Fragment implements OnQuotesListBtnShareClickListener, RefreshQuoteListener, OnRefreshListener{
	
	
	ListView  lvTitleQuotes;
	SwipeRefreshLayout swTitleCont;
	QuotesStreamListAdapter quotesAdapter;
	TitleListStorage titleListHolder;
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
		titleListHolder=((TopQuotesApplication) this.
				getActivity().getApplication()).getTitleListHolder();
		
		quotesAdapter=new QuotesStreamListAdapter(getActivity());
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
		QuoteStreamFragment quoteStremFragment= QuoteStreamFragment.newInstance(1);
		((MainActivity) getActivity()).replaceFragment(quoteStremFragment);
		((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getResources().getStringArray(R.array.navigation_drawer_const_item)[1]);
		
	}

	@Override
	public void onRefresh() {
		Log.d(GlobConst.LOG_TAG,"TitleQuote refreshQuotes() "+wutFragment+" wutLang: "+langFlag);
		sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
	    langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
		
		swTitleCont.setRefreshing(true);
		
		
		titleName=titleListHolder.getTitleList(langFlag).get(wutFragment);
		
		Log.d(GlobConst.LOG_TAG,"cur title name: "+titleName);
		QuoteData.findTitleQuotes(getActivity(),GlobConst.QUITES_TO_LOADE,0,titleName,langFlag,new FindQuotesCallback() {
				
				@Override
				public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
					if (resultCode==FindQuotesCallback.FIND_RESULT_OK) {
						quotesAdapter.clean();
						quotesAdapter.addAll(quotesList);
						quotesAdapter.notifyDataSetChanged();
						swTitleCont.setRefreshing(false);
						
						QuoteTitleListScrollListener scrollListener=new QuoteTitleListScrollListener(GlobConst.QUITES_TO_LOADE);
						lvTitleQuotes.setOnScrollListener(scrollListener);
					}
				}
			});
	}
	
	private class QuoteTitleListScrollListener implements OnScrollListener{
		
		int count;
		int step=GlobConst.QUITES_LOADING_STEP;
		int langFlag;
		
		int pastTotalCount=0;
		
		
		public QuoteTitleListScrollListener(int count) {
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
						
					}
				});
				
				QuoteData.findTitleQuotes(getActivity(),step, count, titleName, totalItemCount, new FindQuotesCallback() {
					
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

