package com.beelzik.topquotes.parse;

import java.util.List;

import android.util.Log;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.data.QuoteData;
import com.beelzik.topquotes.data.TitleData;
import com.beelzik.topquotes.data.UserData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils.Permissions.User;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ParseRelationTester {

	int counter =0;
	
	String TABLE_QUOTE="TestQuotes";
	String TABLE_TITLE="TestTitle";

	
	public void addTestQuote(String titleName){
		
		
		final ParseObject testTitle= new ParseObject(TABLE_TITLE);
		testTitle.put("titleName", titleName);
		
		ParseQuery<ParseObject> queryTitle= ParseQuery.getQuery(TABLE_TITLE);
		queryTitle.whereEqualTo("titleName", titleName);
		queryTitle.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if(e==null){
					
					Log.d(GlobConst.LOG_TAG,"title query e==null");
					if(objects.size()==0){
						testTitle.saveInBackground(new SaveCallback() {
							
							@Override
							public void done(ParseException e) {
								saveTestQuote(testTitle);
							}
						});
					}else {
						saveTestQuote(objects.get(0));
					}
				}else{
					testTitle.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException e) {
							if(e==null){
								Log.d(GlobConst.LOG_TAG,"title saved e==null");
							}else{
								Log.d(GlobConst.LOG_TAG,"title query e!=null");
							}
							saveTestQuote(testTitle);
						}
					});
					Log.d(GlobConst.LOG_TAG,"title query e!=null");
				}
			}
		});
		
		
		
		
	}
	
	public void saveTestQuote(ParseObject testTitle){
		String quote="test quote_"+counter;
		counter++;
		
		
		ParseObject testQuote= new ParseObject(TABLE_QUOTE);
		
		testQuote.put("TestQuote", quote);
		testQuote.put("title", testTitle);
		
		testQuote.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				Log.d(GlobConst.LOG_TAG,"test Quote saved");
				
			}
		});
		
	}
	
	public void checkAllTestedQuote(){
		
	/*	ParseQuery<ParseObject> testTitlefind=ParseQuery.getQuery(TABLE_TITLE);
		
		te*/
		
		ParseQuery<ParseObject> subQ= ParseQuery.getQuery(TABLE_TITLE);
		
		subQ.whereEqualTo("titleName", "daFAQ");
		
		
		ParseQuery<ParseObject> testQuery= ParseQuery.getQuery(TABLE_QUOTE);
		
		testQuery.include("title");
		
		testQuery.whereMatchesQuery("title", subQ);
		
		testQuery.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e==null) {
					for (ParseObject parseObject : objects) {
						ParseObject title=(ParseObject) parseObject.get("title");
						Log.d(GlobConst.LOG_TAG, title.getString("titleName")+"");
					}
				}
				
			}
		});
	}
	
	

	
	public void checkAllQuote(){
		
	/*	ParseQuery<ParseObject> testTitlefind=ParseQuery.getQuery(TABLE_TITLE);
		
		te*/
		
		ParseQuery<TitleData> subQ= ParseQuery.getQuery(TitleData.class);
		
		subQ.whereEqualTo(TitleData.COLUMN_TITLE_NAME, "FLCL");
		
		
		ParseQuery<QuoteData> testQuery= ParseQuery.getQuery(QuoteData.class);
		
		testQuery.include("title");
		
		testQuery.whereMatchesQuery("title", subQ);
		
		testQuery.findInBackground(new FindCallback<QuoteData>() {
			
			@Override
			public void done(List<QuoteData> objects, ParseException e) {
				if (e==null) {
					for (ParseObject parseObject : objects) {
						TitleData title=(TitleData) parseObject.get("title");
						Log.d(GlobConst.LOG_TAG, title.getTitleName()+"");
					}
				}
				
			}
		});
	}
}
