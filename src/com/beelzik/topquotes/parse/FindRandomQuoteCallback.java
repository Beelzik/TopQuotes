package com.beelzik.topquotes.parse;

import java.util.List;

import com.beelzik.topquotes.data.QuoteData;

public interface FindRandomQuoteCallback {

	public static int FIND_RESULT_OK=0;
	public static int FIND_RESULT_ERROR=1;

	
	public void findRandomQuoteCallback(QuoteData quote, int resultCode);
}
