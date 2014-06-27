package com.beelzik.topquotes.db;

import java.util.List;

public interface FindQuotesCallback{
	
	public static int FIND_RESULT_OK=0;
	public static int FIND_RESULT_ERROR=1;
	
	public void findQuotesCallback(List<QuotesData> quotesList, int resultCode);
}
