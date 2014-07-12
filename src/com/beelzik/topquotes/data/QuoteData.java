package com.beelzik.topquotes.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("QuoteData")
public class QuoteData extends ParseObject{

	
	public final static String COLUMN_QUOTE_ID="objectId";
	public final static String COLUMN_QUOTE_EPISODE="episode";
	public final static String COLUMN_QUOTE_LANGUAGE="language";
	public final static String COLUMN_QUOTE_SEASON="season";
	public final static String COLUMN_QUOTE_QUOTE="text";
	public final static String COLUMN_QUOTE_TITLE="title";
	public final static String COLUMN_QUOTE_TYPE="type";
	public final static String COLUMN_QUOTE_USER="user";
	public final static String COLUMN_QUOTE_LIKES="likes";
	
	private boolean liked=false;
	
	public final static int DEFAULT_QUOTE_TYPE=0;
	
	public boolean isLiked() {
		return liked;
	}
	
	public void setLiked(boolean isLiked){
		liked=isLiked;
	}
	
	public String getQuoteId(){
		return super.getObjectId();
	}
	
	public int getEpisode(){
		return super.getInt(COLUMN_QUOTE_EPISODE);
	}
	
	public int getLanguage(){
		return super.getInt(COLUMN_QUOTE_LANGUAGE);
	}
	
	public int getSeason(){
		return super.getInt(COLUMN_QUOTE_LANGUAGE);
	}
	
	public String getQuote(){
		return super.getString(COLUMN_QUOTE_QUOTE);
	}
	
	public TitleData getTitle(){
		return (TitleData) super.get(COLUMN_QUOTE_TITLE);
	}
	
	public int getQuoteType(){
		return super.getInt(COLUMN_QUOTE_TYPE);
	}
	
	public ParseUser getUser(){
		return (ParseUser) super.get(COLUMN_QUOTE_USER);
	}
	
	public ParseRelation<ParseUser> getQuoteLikes(){
		return super.getRelation(COLUMN_QUOTE_LIKES);
	}
	
	
	public void putEpisode(int episode){
		super.put(COLUMN_QUOTE_EPISODE,episode);
	}
	
	public void putLanguage(int lang){
		super.put(COLUMN_QUOTE_LANGUAGE, lang);
	}
	
	public void putSeason(int season){
		super.put(COLUMN_QUOTE_LANGUAGE,season);
	}
	
	public void putQuote(String quote){
		super.put(COLUMN_QUOTE_QUOTE,quote);
	}
	
	public void putTitle(TitleData title){
		super.put(COLUMN_QUOTE_TITLE, title);
	}
	
	public void putQuoteType(int type){
		super.put(COLUMN_QUOTE_TYPE, type);
	}
	
	public void  putUser(ParseUser user){
		super.put(COLUMN_QUOTE_USER, user);
	}
	
	
}
