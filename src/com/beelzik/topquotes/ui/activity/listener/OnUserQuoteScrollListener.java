package com.beelzik.topquotes.ui.activity.listener;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.parse.callback.FindQuotesCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;

public class OnUserQuoteScrollListener implements OnScrollListener{
	
	int count;
	int step=GlobConst.QUITES_LOADING_STEP;
	
	int pastTotalCount=0;
	
	String userId;
	SharedPreferences sp;
	QuotesStreamListAdapter adapter;
	Context ctx;
	int langFlag;
	
	public OnUserQuoteScrollListener(Context ctx,int count, String userId,SharedPreferences sp,
			 QuotesStreamListAdapter adapter) {
		this.adapter=adapter;
		this.count = count;
		this.userId = userId;
		this.sp = sp;
		this.ctx=ctx;
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
		if(((firstVisibleItem+visibleItemCount+step)==totalItemCount) && pastTotalCount!=totalItemCount){
			pastTotalCount=totalItemCount;
	
		    langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
		
			
			QuoteData.findUserQuotes(ctx,step,count,userId, langFlag, new FindQuotesCallback() {
			
			@Override
			public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
				if(FindQuotesCallback.FIND_RESULT_OK==resultCode){
					
	
					adapter.addAll(quotesList);
					adapter.notifyDataSetChanged();
					
				}
				
			}
		});
			count+=step;
		}
	}
	
}