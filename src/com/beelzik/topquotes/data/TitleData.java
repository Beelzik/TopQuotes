package com.beelzik.topquotes.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("TitleData")
public class TitleData extends ParseObject{

	
	public final static String COLUMN_TITLE_ID="objectId";
	public final static String COLUMN_TITLE_NAME="serialName";
	public final static String COLUMN_TITLE_TYPE="type";
	public final static String COLUMN_TITLE_LANGUAGE="lang";
	
	public final static int DEFAULT_TITLE_TYPE=0;
	public static final int MODERATE_TITLE_TYPE = 1;
	
	public String getTitleId(){
		return super.getObjectId();
	}
	
	public String getTitleName(){
		return super.getString(COLUMN_TITLE_NAME);
	}
	
	public int getTitleType(){
		return super.getInt(COLUMN_TITLE_TYPE);
	}
	
	
	public  void putTitleName(String titleName){
		super.put(COLUMN_TITLE_NAME, titleName);
	}
	
	public void putTitleType(){
		super.put(COLUMN_TITLE_TYPE,DEFAULT_TITLE_TYPE);
	}
	
	public void putLanguage(int langFlag){
		super.put(COLUMN_TITLE_LANGUAGE,langFlag);
	}
	
}
