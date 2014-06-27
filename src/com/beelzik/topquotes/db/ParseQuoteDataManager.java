package com.beelzik.topquotes.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.beelzik.topquotes.GlobConst;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.SaveCallback;

public class ParseQuoteDataManager {

	public final String TABLE_QUOTE_NAME="UQuote";
	
	public final static String COLUMN_QUOTE_EPISODE="episode";
	public final static String COLUMN_QUOTE_LANGUAGE="language";
	public final static String COLUMN_QUOTE_SEASON="season";
	public final static String COLUMN_QUOTE_QUOTE="text";
	public final static String COLUMN_QUOTE_SERIAL_NAME="title";
	public final static String COLUMN_QUOTE_TYPE="type";
	public final static String COLUMN_QUOTE_USER="user";
	
	public final String TABLE_TITLES_NAME="Titles";
	
	public final static String COLUMN_TITLE_NAME="serialName";
	
	
	private final static int PARSE_THREAD_POOL=10;
	
	private final static int MAX_PARSE_QUERY_LIMIT=1000;
	
	
	
	Context context;
	ConnectivityManager conMng;
	ExecutorService executorService;
	ArrayList<String> titleList;
	
	
	public ParseQuoteDataManager(Context context) {
		this.context=context;
		conMng=(ConnectivityManager) context.
				getSystemService(Service.CONNECTIVITY_SERVICE);
		executorService=Executors.newFixedThreadPool(PARSE_THREAD_POOL);
		titleList=new ArrayList<String>();
	}
	
	public void setTitleList(ArrayList<String> titleList) {
		this.titleList = titleList;
	}
	
	public ArrayList<String> getTitleList() {
		return titleList;
	}
	
	public void addQuoteInParse(QuotesData quote){
		
		String quoteText=quote.getQuote();
		String quoteTitle=quote.getSerialName();
		String numSeason=quote.getNumSeason();
		String numSereis=quote.getNumSeries();
		String userWut=quote.getUserWut();
		int numLanguege=quote.getNumLanguage();
		
		ParseObject sentQuote=new ParseObject(TABLE_QUOTE_NAME);
		
		sentQuote.put(COLUMN_QUOTE_QUOTE, quoteText);
		sentQuote.put(COLUMN_QUOTE_SERIAL_NAME, quoteTitle);
		sentQuote.put(COLUMN_QUOTE_SEASON, numSeason);
		sentQuote.put(COLUMN_QUOTE_EPISODE, numSereis);
		sentQuote.put(COLUMN_QUOTE_USER, userWut);
		sentQuote.put(COLUMN_QUOTE_LANGUAGE, numLanguege);
		
		sentQuote.saveInBackground();
		
	    checkTitleName(quoteTitle);
		
	}
	
	private void checkTitleName(final String titleName){
		ParseQuery<ParseObject> query= new ParseQuery<ParseObject>(TABLE_TITLES_NAME);
		query.whereEqualTo(COLUMN_TITLE_NAME, titleName);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if(e==null){
					if (objects.size()==0) {
						proposeTitle(titleName);
					}
				}else{
				}
			}
		});
	}
	
	private void proposeTitle(String title){
		
		ParseObject proposeTitle=new ParseObject(TABLE_TITLES_NAME);
		
		proposeTitle.put(COLUMN_TITLE_NAME, title);
		
		proposeTitle.saveInBackground();
	}
	
	
	public void findTitleQuotes(final String titleName,final int langFlag,final FindQuotesCallback callback){
		
		 final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case FindQuotesCallback.FIND_RESULT_OK:
					if (callback!=null) {
						callback.findQuotesCallback((List<QuotesData>) msg.obj,
								FindQuotesCallback.FIND_RESULT_OK);
					}
					break;
				case FindQuotesCallback.FIND_RESULT_ERROR:
					callback.findQuotesCallback(null,
							FindQuotesCallback.FIND_RESULT_ERROR);
					break;
					
				default:
					break;
				}
			}
		};
		
		final boolean haveNetCon=checkNetConection();
		
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				ParseQuery<ParseObject> query=new ParseQuery<ParseObject>(TABLE_QUOTE_NAME);
				query.whereEqualTo(COLUMN_QUOTE_LANGUAGE, langFlag);
				query.setLimit(MAX_PARSE_QUERY_LIMIT);
				if(titleName!=null){
					query.whereEqualTo(COLUMN_QUOTE_SERIAL_NAME, titleName);
				}
				if (!haveNetCon) {
					query.fromLocalDatastore();
				}
				try {
					List<ParseObject> inTitlesList=query.find();
					ArrayList<QuotesData> outQuotesList= new ArrayList<QuotesData>();
	
					QuotesData quotesData;
					for (ParseObject parseObject : inTitlesList) {
						String quote=parseObject.getString(COLUMN_QUOTE_QUOTE);
						String serialName=parseObject.getString(COLUMN_QUOTE_SERIAL_NAME);
						String numSeason=parseObject.getString(COLUMN_QUOTE_SEASON);
						String numSeries=parseObject.getString(COLUMN_QUOTE_EPISODE);
						String userWut=parseObject.getString(COLUMN_QUOTE_USER);
						int numLanguage=parseObject.getInt(COLUMN_QUOTE_LANGUAGE);
						quotesData= new QuotesData(quote, serialName, 
								numSeason, numSeries, userWut, numLanguage);
						outQuotesList.add(quotesData);
					}
					Message msg=new Message();
					msg.what=FindQuotesCallback.FIND_RESULT_OK;
					msg.obj=outQuotesList;
					handler.sendMessage(msg);
				} catch (ParseException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(FindQuotesCallback.FIND_RESULT_ERROR);
				}
			}
		});
		
		thread.start();
		//executorService.execute(thread);
	}
	
	public void findAllTitlesQuotes(int langFlag,FindQuotesCallback callback){
		findTitleQuotes(null,langFlag, callback);
	}
	
	
	public void findAllTitleName(int langFlag, final FindTitlesNameCallback callback){
		 final Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg) {
					switch(msg.what){
					case FindQuotesCallback.FIND_RESULT_OK:
						if (callback!=null) {
							callback.findTitleNameCallback((List<String>) msg.obj,
									FindQuotesCallback.FIND_RESULT_OK);
						}
						break;
					case FindQuotesCallback.FIND_RESULT_ERROR:
						callback.findTitleNameCallback(null,
								FindQuotesCallback.FIND_RESULT_ERROR);
						break;
						
					default:
						break;
					}
				}
			};
			
			final boolean haveNetCon=checkNetConection();
			
			Thread thread=new Thread(new Runnable() {
				
				@Override
				public void run() {
					ParseQuery<ParseObject> query=new ParseQuery<ParseObject>(TABLE_TITLES_NAME);
					query.setLimit(MAX_PARSE_QUERY_LIMIT);
					if (!haveNetCon) {
						query.fromLocalDatastore();
					}
				
					try {
						List<ParseObject> inTitlesList=query.find();
						ArrayList<String> outTitleNameList= new ArrayList<String>();
		
						String titleName;
						for (ParseObject parseObject : inTitlesList) {
							titleName=parseObject.getString(COLUMN_TITLE_NAME);
							outTitleNameList.add(titleName);
						}
						Message msg=new Message();
						msg.what=FindQuotesCallback.FIND_RESULT_OK;
						msg.obj=outTitleNameList;
						handler.sendMessage(msg);
					} catch (ParseException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(FindQuotesCallback.FIND_RESULT_ERROR);
					}
				}
			});
			
			thread.start();
		//	executorService.execute(thread);
	}
	
	public void shutDownAllActions(){
		executorService.shutdown();
	}
	
	private boolean checkNetConection(){
		 if (conMng.getActiveNetworkInfo() != null
                 && conMng.getActiveNetworkInfo().isAvailable()
                 && conMng.getActiveNetworkInfo().isConnected()) {
			 return true;
		 }else {
			 return false;
		 }
	}
	
	public void syncLocaleDataWithParse(){
		
		if(checkNetConection()){
			
			ParseQuery<ParseObject> quoteQuery=new ParseQuery<ParseObject>(TABLE_QUOTE_NAME);
			quoteQuery.setLimit(MAX_PARSE_QUERY_LIMIT);
			quoteQuery.findInBackground(new FindCallback<ParseObject>() {
				
				@Override
				public void done(final List<ParseObject> parseObjs, ParseException e) {
					if(e==null){
						ParseObject.unpinAllInBackground(TABLE_QUOTE_NAME, new DeleteCallback() {
							
							@Override
							public void done(ParseException e) {
								if(e==null){
									ParseObject.pinAllInBackground(TABLE_QUOTE_NAME,parseObjs);
									for (ParseObject parseObject : parseObjs) {
										Log.d(GlobConst.LOG_TAG,"author: "+parseObject.getString(COLUMN_QUOTE_USER));
									}
									Log.d(GlobConst.LOG_TAG,"obj.size: "+parseObjs.size());
								}else{
									Log.d(GlobConst.LOG_TAG,"quote unpin error: "+e.getMessage());
								}
							}
						});
					}else{
						Log.d(GlobConst.LOG_TAG,"quote find error: "+e.getMessage());
					}
					
				}
			});
			
			ParseQuery<ParseObject> titleQuery=new ParseQuery<ParseObject>(TABLE_TITLES_NAME);
			titleQuery.setLimit(MAX_PARSE_QUERY_LIMIT);
			titleQuery.findInBackground(new FindCallback<ParseObject>() {
				
				@Override
				public void done(final List<ParseObject> parseObjs, ParseException e) {
					if(e==null){
						ParseObject.unpinAllInBackground(TABLE_TITLES_NAME, new DeleteCallback() {
							
							@Override
							public void done(ParseException e) {
								if(e==null){
									ParseObject.pinAllInBackground(TABLE_TITLES_NAME,parseObjs);
									for (ParseObject parseObject : parseObjs) {
										Log.d(GlobConst.LOG_TAG,"title: "+parseObject.getString(COLUMN_TITLE_NAME));
									}
									
								}else{
									Log.d(GlobConst.LOG_TAG,"title unpin error: "+e.getMessage());
								}
								
							}
						});
					}else{
						Log.d(GlobConst.LOG_TAG,"title find error: "+e.getMessage());
					}
				}
			});	
			
		}else{

			ParseQuery<ParseObject> quoteQuery=new ParseQuery<ParseObject>(TABLE_QUOTE_NAME);
			quoteQuery.fromLocalDatastore();
			quoteQuery.fromPin();
			quoteQuery.setLimit(800);
			quoteQuery.findInBackground(new FindCallback<ParseObject>() {
				
				@Override
				public void done(final List<ParseObject> parseObjs, ParseException e) {
					if(e==null){
					
								//	ParseObject.pinAllInBackground(TABLE_QUOTE_NAME,parseObjs);
									for (ParseObject parseObject : parseObjs) {
										Log.d(GlobConst.LOG_TAG,"author: "+parseObject.getString(COLUMN_QUOTE_USER));
									}
									Log.d(GlobConst.LOG_TAG,"obj.size: "+parseObjs.size());
							
					}else{
						Log.d(GlobConst.LOG_TAG,"quote find error: "+e.getMessage());
					}
					
				}
			});
			
			ParseQuery<ParseObject> titleQuery=new ParseQuery<ParseObject>(TABLE_TITLES_NAME);
			titleQuery.fromLocalDatastore();
			titleQuery.fromPin();
			titleQuery.setLimit(MAX_PARSE_QUERY_LIMIT);
			titleQuery.findInBackground(new FindCallback<ParseObject>() {
				
				@Override
				public void done(final List<ParseObject> parseObjs, ParseException e) {
					if(e==null){
						
								//	ParseObject.pinAllInBackground(TABLE_TITLES_NAME,parseObjs);
									for (ParseObject parseObject : parseObjs) {
										Log.d(GlobConst.LOG_TAG,"title: "+parseObject.getString(COLUMN_TITLE_NAME));
									}
						
					}else{
						Log.d(GlobConst.LOG_TAG,"title find error: "+e.getMessage());
					}
				}
			});	
		}
	}
	
}
