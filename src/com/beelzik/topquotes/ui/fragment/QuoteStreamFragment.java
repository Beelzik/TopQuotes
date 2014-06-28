package com.beelzik.topquotes.ui.fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.db.FindQuotesCallback;
import com.beelzik.topquotes.db.FindTitlesNameCallback;
import com.beelzik.topquotes.db.ParseQuoteDataManager;
import com.beelzik.topquotes.db.QuotesData;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.ui.adapter.OnQuotesListBtnShareClickListener;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;


public class QuoteStreamFragment extends Fragment implements OnQuotesListBtnShareClickListener, RefreshQuoteListener, OnRefreshListener{
	
	private static final String ARG_SECTION_NUMBER = "section_number";
	
	ListView  lvStreamQuotes;
	SwipeRefreshLayout swStreamCont;
	QuotesStreamListAdapter quotesAdapter;
	ParseQuoteDataManager parseQuoteDataManager;
	SharedPreferences sp;
	int langFlag;
	
	public static QuoteStreamFragment newInstance(int sectionNumber) {
		QuoteStreamFragment fragment = new QuoteStreamFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
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
				ARG_SECTION_NUMBER),this);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		quotesAdapter=new QuotesStreamListAdapter(getActivity());
		quotesAdapter.setBtnShareClickListener(this);
		lvStreamQuotes.setAdapter(quotesAdapter);
		
		parseQuoteDataManager=((TopQuotesApplication) getActivity().
				getApplication()).getParseQuoteDataManager();
		refreshQuotes();
	}


	@Override
	public void onStop() {
		super.onStop();
		parseQuoteDataManager.shutDownAllActions();
	}
	
	@Override
	public void onBtnShareClickListener(View view, int position) {
		if (GlobConst.DEBUG) {
			Log.d(GlobConst.LOG_TAG, "onBtnShareClickListener: "+position);	
		}	
	}

	@Override
	public void refreshQuotes() {
		sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
		langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
		
		swStreamCont.setRefreshing(true);
			parseQuoteDataManager.findAllTitlesQuotes(langFlag,new FindQuotesCallback() {
				
				@Override
				public void findQuotesCallback(List<QuotesData> quotesList, int resultCode) {
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
}

