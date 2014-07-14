package com.beelzik.topquotes.ui.activity;

import java.util.List;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.data.QuoteData;
import com.beelzik.topquotes.parse.FindQuotesCallback;
import com.beelzik.topquotes.parse.ParseQuoteDataManager;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;

class OnUserQuoteScrollListener implements OnScrollListener{
	
	int count;
	int step=10;
	
	int pastTotalCount=0;
	
	String userId;
	SharedPreferences sp;
	ParseQuoteDataManager parseQuoteDataManager;
	QuotesStreamListAdapter adapter;
	int langFlag;
	
	public OnUserQuoteScrollListener(int count, String userId,SharedPreferences sp,
			ParseQuoteDataManager parseQuoteDataManager, QuotesStreamListAdapter adapter) {
		this.adapter=adapter;
		this.count = count;
		this.userId = userId;
		this.sp = sp;
		this.parseQuoteDataManager = parseQuoteDataManager;
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
		
			
			parseQuoteDataManager.findUserQuotes(step,count,userId, langFlag, new FindQuotesCallback() {
			
			@Override
			public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
				if(FindQuotesCallback.FIND_RESULT_OK==resultCode){
					
					for (QuoteData quoteData : quotesList) {
						Log.d(GlobConst.LOG_TAG, "autor quote: "+quoteData.getObjectId());
					}
					Log.d(GlobConst.LOG_TAG, "autor quote.size(): "+quotesList.size());
					adapter.addAll(quotesList);
					adapter.notifyDataSetChanged();
					
					
					Log.d(GlobConst.LOG_TAG, "quotesStreamListAdapter.getCount(): "+adapter.getCount());
					Log.d(GlobConst.LOG_TAG, "totalItemCount: "+totalItemCount);
				}
				
			}
		});
			count+=step;
		}
	}
	
}