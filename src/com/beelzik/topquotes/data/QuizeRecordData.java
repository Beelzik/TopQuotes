package com.beelzik.topquotes.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("QuizeRecordData")
public class QuizeRecordData extends ParseObject{

	public final static String COLUMN_QUIZE_ID="objectId";
	public final static String COLUMN_QUIZE_DATE="date";
	public final static String COLUMN_QUIZE_SCORE="score";
	public final static String COLUMN_QUIZE_USER="user";
	public final static String COLUMN_QUIZE_CREATED_AT="createAt";
	
	public String getQuizeRecordId(){
		return getObjectId();
	}
	
	public String getQuizeRecordDate(){
		return getString(COLUMN_QUIZE_DATE);
	}
	
	public int getQuizeRecordScore(){
		return getInt(COLUMN_QUIZE_SCORE);
	}
	
	public ParseUser getQuizeRecordUser(){
		return (ParseUser) get(COLUMN_QUIZE_USER);
	}
	
	
	public void putDate(String date){
		put(COLUMN_QUIZE_DATE, date);
	}
	
	public void putScore(int score){
		put(COLUMN_QUIZE_SCORE, score);
	}
	
	public void putUser(ParseUser user){
		put(COLUMN_QUIZE_USER, user);
	}
	
}
