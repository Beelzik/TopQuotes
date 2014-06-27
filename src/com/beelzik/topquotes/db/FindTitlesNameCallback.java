package com.beelzik.topquotes.db;

import java.util.List;

public interface FindTitlesNameCallback {

	public static int FIND_RESULT_OK=0;
	public static int FIND_RESULT_ERROR=1;
	
	public void findTitleNameCallback(List<String> titleNameList, int resultCode);
	

}
