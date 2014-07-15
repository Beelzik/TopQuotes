package com.beelzik.topquotes.parse.callback;

import java.util.ArrayList;

import com.beelzik.topquotes.parse.data.QuizeRecordData;

public interface FindTopTenAndUserRecordsCallback {

	
	public static int FIND_RESULT_OK=0;
	public static int FIND_RESULT_ERROR=1;

	
	public void findTopTenAndUserRecordsCallback(ArrayList<QuizeRecordData> topTenRecordsList, 
			QuizeRecordData userTopRecord, int resultCode);
}
