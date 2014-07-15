package com.beelzik.topquotes.parse.callback;

import com.beelzik.topquotes.parse.data.QuoteData;

public interface FindRandomQuoteCallback {

	public static int FIND_RESULT_OK=0;
	public static int FIND_RESULT_ERROR=1;

	
	public void findRandomQuoteCallback(QuoteData quote, int resultCode);
}
