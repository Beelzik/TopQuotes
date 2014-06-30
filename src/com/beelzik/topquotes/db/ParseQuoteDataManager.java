package com.beelzik.topquotes.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.beelzik.topquotes.GlobConst;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
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
	public final static String COLUMN_TITLE_TYPE="type";
	
	
	public final String TABLE_USER_NAME="User";
	
	public final static String COLUMN_USER_NAME="username";
	public final static String COLUMN_USER_AVATA_URL="userAvatarUrl";
	
	
	
	private final static int PARSE_TYPE_MODERATED=1;
	private final static int PARSE_TYPE_DEFAULT=0;
	
	private final static int PARSE_THREAD_POOL=10;
	
	private final static int MAX_PARSE_QUERY_LIMIT=1000;
	private final static String PIN_TOP_QUTE="Top_quote";
	private final static String PIN_TITLE_NAME="Title_name";
	
	Context context;
	ConnectivityManager conMng;
	ExecutorService executorService;
	ArrayList<String> titleList;
	SharedPreferences sp;
	
	
	public ParseQuoteDataManager(Context context) {
		this.context=context;
		conMng=(ConnectivityManager) context.
				getSystemService(Service.CONNECTIVITY_SERVICE);
		executorService=Executors.newFixedThreadPool(PARSE_THREAD_POOL);
		titleList=new ArrayList<String>();
		sp=PreferenceManager.getDefaultSharedPreferences(context);
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
		sentQuote.put(COLUMN_QUOTE_TYPE, PARSE_TYPE_DEFAULT);
		
		sentQuote.saveInBackground();
		
		
	    checkTitleName(quoteTitle);
	}
	
	public void addUser(){
		ParseUser user=new ParseUser();
		//ParseObject user=new ParseObject(TABLE_USER_NAME);
		
		user.setUsername(sp.getString(GlobConst.SP_FLAG_USER_DISPLAY_NAME,null));
		user.setPassword("0000");
	//	user.put("username", sp.getString(GlobConst.SP_FLAG_USER_DISPLAY_NAME,null));
		user.put(COLUMN_USER_AVATA_URL, sp.getString(GlobConst.SP_FLAG_USER_AVATAR_URL,null));
		//user.si
		user.signUpInBackground(new SignUpCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e==null){
					Log.d(GlobConst.LOG_TAG,"signUp");
				}
				
			}
		});
		
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
		proposeTitle.put(COLUMN_TITLE_TYPE, PARSE_TYPE_DEFAULT);
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
				String pinTag;
				ParseQuery<ParseObject> query=new ParseQuery<ParseObject>(TABLE_QUOTE_NAME);
				query.whereEqualTo(COLUMN_QUOTE_LANGUAGE, langFlag);
				query.setLimit(MAX_PARSE_QUERY_LIMIT);
				query.whereEqualTo(COLUMN_QUOTE_TYPE, PARSE_TYPE_MODERATED);
				if(titleName!=null){
					query.whereEqualTo(COLUMN_QUOTE_SERIAL_NAME, titleName);
				}
				
				
				if(titleName==null){
					if(GlobConst.DEBUG){
						Log.d(GlobConst.LOG_TAG, "titleName.equals(null)");
					}
					pinTag=PIN_TOP_QUTE+langFlag;
				}else{
					pinTag=titleName+langFlag;
				}
				if (!haveNetCon) {
				//	query.fromLocalDatastore();
					query.fromPin(PIN_TOP_QUTE+langFlag);
					Log.d(GlobConst.LOG_TAG, "!haveNetCon query.fromPin: "+pinTag);
				}
				try {
					List<ParseObject> inTitlesList=query.find();
					
					if (haveNetCon && (titleName==null)) {
						pinToLocaleDataStore(inTitlesList, PIN_TOP_QUTE+langFlag);
					}
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
					
					Log.d(GlobConst.LOG_TAG, " inTitlesList.size(): "+ inTitlesList.size());
					
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
		thread.setDaemon(true);
		thread.start();
		//executorService.execute(thread);
	}
	
	public void findAllTitlesQuotes(int langFlag,FindQuotesCallback callback){
		findTitleQuotes(null,langFlag, callback);
	}
	
	
	public void findAllTitleName(final int langFlag, final FindTitlesNameCallback callback){
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
					String pinTag=PIN_TITLE_NAME+langFlag;
					ParseQuery<ParseObject> query=new ParseQuery<ParseObject>(TABLE_TITLES_NAME);
					query.setLimit(MAX_PARSE_QUERY_LIMIT);
					query.whereEqualTo(COLUMN_TITLE_TYPE, PARSE_TYPE_MODERATED);
					
					
					if (!haveNetCon) {
						//query.fromLocalDatastore();
						query.fromPin(PIN_TITLE_NAME);
					}
				
					try {
						List<ParseObject> inTitlesList=query.find();
						ArrayList<String> outTitleNameList= new ArrayList<String>();
						
						if (haveNetCon) {
							pinToLocaleDataStore(inTitlesList, PIN_TITLE_NAME);
						}
						
						String titleName;
						for (ParseObject parseObject : inTitlesList) {
							titleName=parseObject.getString(COLUMN_TITLE_NAME);
							outTitleNameList.add(titleName);
						}
						
						Log.d(GlobConst.LOG_TAG, " inTitlesList.size(): "+ inTitlesList.size());
						
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
			
			thread.setDaemon(true);
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
	
	
	
	public void pinToLocaleDataStore(final List<ParseObject> objects, final String tag){
		
		Log.d(GlobConst.LOG_TAG, "pinToLocaleDataStore");
		
		Log.d(GlobConst.LOG_TAG, "objects.size(): "+objects.size());
		
		
		ParseObject.unpinAllInBackground(tag,new DeleteCallback() {
			
			@Override
			public void done(ParseException e) {
				
				if(e==null){
				ParseObject.pinAllInBackground(tag, objects, new SaveCallback() {
					
					@Override
					public void done(ParseException e) {
						if (e==null) {
							Log.d(GlobConst.LOG_TAG, "Data pinned at locale by tag: "+tag);
						}else{
							Log.d(GlobConst.LOG_TAG, "Data DIDNT pinned at locale");
						}
						
					}
				} );
				}else{
					Log.d(GlobConst.LOG_TAG,"unping error: "+e.getMessage());
				}
			}
		});
		
	}
	
	
	
	
}
