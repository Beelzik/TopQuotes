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
import android.widget.ListView;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.parse.callback.FindQuotesCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.ui.adapter.OnQuotesListBtnLikeClickListener;
import com.beelzik.topquotes.ui.adapter.OnQuotesListBtnShareClickListener;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;

public class TopFavoritesFragment extends Fragment implements OnQuotesListBtnShareClickListener, 
RefreshQuoteListener, OnRefreshListener, OnQuotesListBtnLikeClickListener{
	
	
	ListView  lvStreamQuotes;
	SwipeRefreshLayout swStreamCont;
	QuotesStreamListAdapter quotesAdapter;
	SharedPreferences sp;
	int langFlag;
	
	public static TopFavoritesFragment newInstance(int sectionNumber) {
		TopFavoritesFragment fragment = new TopFavoritesFragment();
		Bundle args = new Bundle();
		args.putInt(GlobConst.ARG_SECTION_FRAGMENT_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public TopFavoritesFragment() {
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
		
		sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
		langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
		
		swStreamCont.setRefreshing(true);
		
		QuoteData.findAllLikedQuotes(getActivity(),20, 0, langFlag, new FindQuotesCallback() {

			@Override
			public void findQuotesCallback(List<QuoteData> quotesList,
					int resultCode) {
				if (resultCode==FindQuotesCallback.FIND_RESULT_OK) {
					quotesAdapter.clean();
					quotesAdapter.addAll(quotesList);
					quotesAdapter.notifyDataSetChanged();
					swStreamCont.setRefreshing(false);
				}
			}
			
			
		});
	}

	@Override
	public void onRefresh() {
		refreshQuotes();
		
	}

	@Override
	public void onBtnLikeClickListener(View view, final int position) {

	}
}
