package com.beelzik.topquotes.parse.callback;

import java.util.List;

import com.beelzik.topquotes.parse.data.QuoteData;

public interface FindQuotesCallback{
	
	public static int FIND_RESULT_OK=0;
	public static int FIND_RESULT_ERROR=1;

	
	public void findQuotesCallback(List<QuoteData> quotesList, int resultCode);
}
