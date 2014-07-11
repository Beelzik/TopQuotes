package com.beelzik.topquotes.parse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.data.QuizeRecordData;
import com.beelzik.topquotes.data.QuoteData;
import com.beelzik.topquotes.data.TitleData;
import com.beelzik.topquotes.data.UserData;
import com.beelzik.topquotes.ui.activity.OnUserAuthListener;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class ParseQuoteDataManager {		
	private final static int PARSE_TYPE_MODERATED=1;
	private final static int PARSE_TYPE_DEFAULT=0;
	
	private final static int PARSE_THREAD_POOL=1;
	
	private final static int MAX_PARSE_QUERY_LIMIT=20;
	private final static String PIN_TOP_QUTE="Top_quote";
	private final static String PIN_TITLE_NAME="Title_name";
	
	public final static String DEFAULT_PARSE_USER_PASSWORD="0000";
	
	static Context context;
	ConnectivityManager conMng;
	static ExecutorService execLikesService;
	HashMap<Integer,ArrayList<String>> mapTileList;
	SharedPreferences sp;
	
	
	public ParseQuoteDataManager(Context context) {
		this.context=context;
		conMng=(ConnectivityManager) context.
				getSystemService(Service.CONNECTIVITY_SERVICE);
		
		
		mapTileList= new HashMap<Integer, ArrayList<String>>();
		 execLikesService=Executors.newFixedThreadPool(PARSE_THREAD_POOL);
		sp=PreferenceManager.getDefaultSharedPreferences(context);
		
	}
	
	public void setTitleList(int langFlag,ArrayList<String> titleList) {
		mapTileList.put(langFlag,titleList);
	}
	
	public ArrayList<String> getTitleList(int langFlag) {
		return mapTileList.get(langFlag);
	}
	
	

	public void addUser(OnUserAuthListener userAuthListener){
		Log.d(GlobConst.LOG_TAG, "  addUser()");
		String userNameDisplay=sp.getString(GlobConst.SP_FLAG_USER_DISPLAY_NAME,null);
		String acountName=sp.getString(GlobConst.SP_FLAG_ACOUNT_NAME,null);
		String userAvatarUrl= sp.getString(GlobConst.SP_FLAG_USER_AVATAR_URL,null);
		String userEmail= sp.getString(GlobConst.SP_FLAG_ACOUNT_NAME,null);
		
		
		ParseUser user=new ParseUser();
		user.setUsername(acountName);
		user.put(UserData.COLUMN_USER_NAME_DISPLAY, userNameDisplay);
		user.setPassword(DEFAULT_PARSE_USER_PASSWORD);
		user.setEmail(userEmail);
		user.put(UserData.COLUMN_USER_AVATA_URL,userAvatarUrl);
	
		doAuth(user,acountName,userAuthListener);
		
	}
	
	protected void doAuth(final ParseUser user, final String acountName,final OnUserAuthListener userAuthListener) {
		Log.d(GlobConst.LOG_TAG, "  doAuth  acountName: "+ acountName);
		Log.d(GlobConst.LOG_TAG, " ParseUser.getCurrentUser() "+ParseUser.getCurrentUser());
		Log.d(GlobConst.LOG_TAG, " ParseUser user "+user);
		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					doWallet(user, userAuthListener); // the user is new, create wallet for him
					Log.d(GlobConst.LOG_TAG, " ParseUser.getCurrentUser() "+ParseUser.getCurrentUser());
				} 
				if(e!=null){
					signIn(acountName,userAuthListener); // user already registered, sign in
					Log.d(GlobConst.LOG_TAG, "error signUp: "+e.getMessage());
				}
			}
		});
	}

	protected void doWallet(final ParseUser user,final OnUserAuthListener userAuthListener) {
	    final ParseObject wallet;
		wallet = new ParseObject("Wallet");
		wallet.put("total", 0);
		wallet.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException arg0) {
				user.put("wallet", wallet);
				user.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException e) {
						if (userAuthListener!=null) {
							Log.d(GlobConst.LOG_TAG, "doWallet cur User: "+ ParseUser.getCurrentUser());
							userAuthListener.onUserAuth();
						}
					}
				});
			}
		});
	}

	protected void signIn(String acountName,final OnUserAuthListener userAuthListener) {
		ParseUser.logInInBackground(acountName, DEFAULT_PARSE_USER_PASSWORD, new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException e) {
				if(e==null){
					Log.d(GlobConst.LOG_TAG, "logIn OK ");
					Log.d(GlobConst.LOG_TAG, "sign in cur User: "+ ParseUser.getCurrentUser());
					if (userAuthListener!=null) {
						userAuthListener.onUserAuth();
					}
				}else{
					Log.d(GlobConst.LOG_TAG, "logIn ERROR: "+e.getMessage());
				}
			}
		});
		
	}
	
	
	public void addRecordInParse(String date, int score, ParseUser user,SaveCallback callback ){
		
		QuizeRecordData recordData=new QuizeRecordData();
		
		recordData.putDate(date);
		recordData.putScore(score);
		recordData.putUser(user);
		
		recordData.saveInBackground(callback);
	}
	
	public void findRecords(FindCallback<QuizeRecordData> callback){
		
		ParseQuery<QuizeRecordData> query= ParseQuery.getQuery(QuizeRecordData.class);
		
		query.setLimit(10);
		query.orderByDescending(QuizeRecordData.COLUMN_QUIZE_CREATED_AT);
		
		query.findInBackground(callback);
		
		
	}
	
	
	public void likeQuoteInParse(final View view,final QuoteData targetQuote, final OnQuoteLikedCallback likedCallback){

	Log.d(GlobConst.LOG_TAG, "likeQuoteInParse \n"+
	"quote: "+targetQuote.getQuote()+
	"\nquoteId:"+targetQuote.getQuoteId());
		
				ParseUser user=ParseUser.getCurrentUser();
				
				ParseRelation<QuoteData> quoteLikedBy=user.getRelation(UserData.COLUMN_USER_RELATION);

				ParseRelation<ParseUser> userLikes=targetQuote.getRelation(QuoteData.COLUMN_QUOTE_LIKES);
				
				if (!targetQuote.isLiked()) {
					quoteLikedBy.add(targetQuote);
					
				
					userLikes.add(user);
					targetQuote.setLiked(!targetQuote.isLiked());
					targetQuote.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException e) {
							if (e==null) {
								if(likedCallback!=null){
									likedCallback.onQuoteLikedCallback(view,targetQuote.isLiked());
								}
							}
						}
					});
					Log.d(GlobConst.LOG_TAG, "add leke");
				}else{
					quoteLikedBy.remove(targetQuote);
		
					userLikes.remove(user);
					targetQuote.setLiked(!targetQuote.isLiked());
					targetQuote.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException e) {
							if (e==null) {
								if(likedCallback!=null){
									likedCallback.onQuoteLikedCallback(view,targetQuote.isLiked());
								}
							}
						}
					});
					Log.d(GlobConst.LOG_TAG, "del like ");
				}
		
				
}


	
	public void addQuoteInParse(final String quote,String titleName,final int season,final int episode,final ParseUser user, final int lang){
		
		final TitleData title=ParseObject.create(TitleData.class);
		title.putTitleName(titleName);
		title.putLanguage(lang);
		ParseQuery<TitleData> queryTitle= ParseQuery.getQuery(TitleData.class);
		queryTitle.whereEqualTo(TitleData.COLUMN_TITLE_NAME, titleName);
		queryTitle.findInBackground(new FindCallback<TitleData>() {
			
			@Override
			public void done(List<TitleData> objects, ParseException e) {
				if(e==null){
					
					Log.d(GlobConst.LOG_TAG,"title query e==null");
					if(objects.size()==0){
						title.saveInBackground(new SaveCallback() {
							
							@Override
							public void done(ParseException e) {
								saveQuote(title,quote,season,episode,user,lang);
							}
						});
					}else {
					
						saveQuote(objects.get(0),quote,season,episode,user,lang);
					}
				}else{
					title.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException e) {
							if(e==null){
								Log.d(GlobConst.LOG_TAG,"title saved e==null");
							}else{
								Log.d(GlobConst.LOG_TAG,"title query e!=null");
							}
							saveQuote(title,quote,season,episode,user,lang);
						}
					});
					Log.d(GlobConst.LOG_TAG,"title query e!=null");
				}
			}
		});
		
		
		
		
	}
	
	private void saveQuote(TitleData title, String quote,int season,int episode,ParseUser user, int lang){
		
		QuoteData sendQuote= ParseObject.create(QuoteData.class);
		
		sendQuote.putTitle(title);
		sendQuote.putQuote(quote);
		sendQuote.putSeason(season);
		sendQuote.putEpisode(episode);
		sendQuote.putQuoteType(QuoteData.DEFAULT_QUOTE_TYPE);
		sendQuote.putUser(user);
		sendQuote.putLanguage(lang);
		
	
		
		sendQuote.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e==null) {
					Log.d(GlobConst.LOG_TAG,"test Quote saved");
				}
				else{
					e.printStackTrace();
					Log.d(GlobConst.LOG_TAG,"test Quote saved error: "+e.getMessage());
				}
			}
		});
		
	}
	
	
	public void findAlterTitleQuotes(final String titleName,final int langFlag,final FindQuotesCallback callback){
		
		 final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case FindQuotesCallback.FIND_RESULT_OK:
					if (callback!=null) {
						callback.findQuotesCallback((List<QuoteData>) msg.obj,
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
				ParseQuery<QuoteData> query=ParseQuery.getQuery(QuoteData.class);
				query.whereEqualTo(QuoteData.COLUMN_QUOTE_LANGUAGE, langFlag);
				query.setLimit(MAX_PARSE_QUERY_LIMIT);
				query.include(QuoteData.COLUMN_QUOTE_TITLE);
				query.include(QuoteData.COLUMN_QUOTE_USER);
				query.whereEqualTo(QuoteData.COLUMN_QUOTE_TYPE, PARSE_TYPE_MODERATED);
				if(titleName!=null){
					ParseQuery<TitleData> subQ= ParseQuery.getQuery(TitleData.class);
					Log.d(GlobConst.LOG_TAG, " titleName: "+ titleName);
					subQ.whereEqualTo(TitleData.COLUMN_TITLE_NAME, titleName);
					
					query.include(QuoteData.COLUMN_QUOTE_TITLE);
					
					query.whereMatchesQuery("title", subQ);
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
					query.fromPin(pinTag);
					Log.d(GlobConst.LOG_TAG, "!haveNetCon query.fromPin: "+pinTag);
				}
				try {
					final List<QuoteData> inQuotes=query.find();
					
					for (QuoteData quoteData : inQuotes) {
						Log.d(GlobConst.LOG_TAG, "quoteData: "+quoteData.getQuote());
					}
					if (haveNetCon && (titleName==null)) {
						pinQuoteToLocaleDataStore(inQuotes, pinTag);
					}
					
					for (int i = 0; i < inQuotes.size(); i++) {
						final int position=i;
						ParseRelation<QuoteData> testQuote=ParseUser.getCurrentUser().getRelation(UserData.COLUMN_USER_RELATION);
						ParseQuery<QuoteData> qtestQuot=testQuote.getQuery();
						qtestQuot.whereEqualTo(QuoteData.COLUMN_QUOTE_QUOTE, inQuotes.get(0).getQuote());
						if (!haveNetCon) {
								qtestQuot.fromPin(pinTag);
							}
						
						/*qtestQuot.whereEqualTo(QuoteData.COLUMN_QUOTE_TYPE, QuoteData.DEFAULT_QUOTE_TYPE);
						qtestQuot.whereEqualTo(QuoteData.COLUMN_QUOTE_LANGUAGE, langFlag);*/
						qtestQuot.findInBackground(new FindCallback<QuoteData>() {
							
							@Override
							public void done(List<QuoteData> objects, ParseException e) {
								if(e==null){
									if (objects.size()==1) {
										Log.d(GlobConst.LOG_TAG,"quoteData liked: "+objects.get(0).getQuote());
											//inQuotes.get(position).setLiked(true);
											//holder.ibtnListStreamLike.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_like));
										}else{
									
											//inQuotes.get(position).setLiked(false);
											//holder.ibtnListStreamLike.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_liked));
										}	
								}
								
								
							}
						});
						
						
					//	ParseRelation<ParseUser> qouteLikedBy=inQuotes.get(position).getRelation(QuoteData.COLUMN_QUOTE_LIKES);
					//	ParseQuery<ParseUser> queryLekedBy=qouteLikedBy.getQuery();
					//	Log.d(GlobConst.LOG_TAG,"ParseUser.getCurrentUser().getObjectId(): "+ParseUser.getCurrentUser().getObjectId());
					//	query.whereEqualTo(UserData.COLUMN_USER_ID, ParseUser.getCurrentUser().getObjectId());
						//GetCallback<ParseUser> quoteFindCallBack= new QuoteFindCallBack(holder);
				
				/*	try {
						ParseUser user=	queryLekedBy.getFirst();
						
						if(user==null){
							inQuotes.get(position).setLiked(false);
							//holder.ibtnListStreamLike.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_like));
						}else{
							Log.d(GlobConst.LOG_TAG,"ParseUser.getCurrentUser().getObjectId(): "+user.getObjectId());
							Log.d(GlobConst.LOG_TAG,"user!=null");
							inQuotes.get(position).setLiked(true);
							//holder.ibtnListStreamLike.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_liked));
						}	
						
					} catch (Exception e) {
						Log.d(GlobConst.LOG_TAG,"inQuotes error: "+e.getMessage());
						inQuotes.get(position).setLiked(false);
					}	*/
					
				
					}
					Log.d(GlobConst.LOG_TAG, " inTitlesList.size(): "+ inQuotes.size());
					
					Message msg=new Message();
					msg.what=FindQuotesCallback.FIND_RESULT_OK;
					msg.obj=inQuotes;
					handler.sendMessage(msg);
					} catch (ParseException e) {
					e.printStackTrace();
					Log.d(GlobConst.LOG_TAG, " FIND_RESULT_ERROR: ");
					handler.sendEmptyMessage(FindQuotesCallback.FIND_RESULT_ERROR);
				}
				
			}
		});
		thread.setDaemon(true);
		thread.start();
		//executorService.execute(thread);
	}
	
	
	public void findQuotes(int limit,int skipedQuote,final String userId,final String titleName,final int langFlag,final FindQuotesCallback callback){
		final boolean haveNetCon=checkNetConection();
		
		
				final String pinTag;
				ParseQuery<QuoteData> query=ParseQuery.getQuery(QuoteData.class);
				query.whereEqualTo(QuoteData.COLUMN_QUOTE_LANGUAGE, langFlag);
				query.setLimit(limit);
				query.setSkip(skipedQuote);
				query.include(QuoteData.COLUMN_QUOTE_TITLE);
				query.include(QuoteData.COLUMN_QUOTE_USER);
				query.whereEqualTo(QuoteData.COLUMN_QUOTE_TYPE, PARSE_TYPE_MODERATED);
				
				
				if(titleName!=null){
					query.whereMatchesQuery(QuoteData.COLUMN_QUOTE_TITLE, getTitleSubQuery(titleName));
				}
				
				if (userId!=null) {
					query.whereMatchesQuery(QuoteData.COLUMN_QUOTE_USER, getUserSubQuery(userId));
				}
				
				pinTag=PIN_TOP_QUTE+langFlag;
				
				/*if(titleName==null){
					if(GlobConst.DEBUG){
						Log.d(GlobConst.LOG_TAG, "titleName.equals(null)");
					}
					pinTag=PIN_TOP_QUTE+langFlag;
				}else{
					pinTag=titleName+langFlag;
				}*/
				
				
				if (!haveNetCon) {
					query.fromPin(pinTag);
					Log.d(GlobConst.LOG_TAG, "!haveNetCon query.fromPin: "+pinTag);
				}
			
					query.findInBackground(new FindCallback<QuoteData>() {
						
						@Override
						public void done(final List<QuoteData> inQuotes, ParseException e) {
							if(e==null){
								/*for (QuoteData quoteData : inQuotes) {
									Log.d(GlobConst.LOG_TAG, "quoteData: "+quoteData.getQuote());
								}*/
								if (haveNetCon && (titleName==null)) {
									pinQuoteToLocaleDataStore(inQuotes, pinTag);
								}
								callback.findQuotesCallback(inQuotes,FindQuotesCallback.FIND_RESULT_OK);
								}else{
									callback.findQuotesCallback(inQuotes,FindQuotesCallback.FIND_RESULT_ERROR);
								}
							}	
					});
	}
	
	
	
	
	
	private ParseQuery<TitleData>  getTitleSubQuery(String titleName){
		ParseQuery<TitleData> subQ= ParseQuery.getQuery(TitleData.class);
		Log.d(GlobConst.LOG_TAG, " titleName: "+ titleName);
		subQ.whereEqualTo(TitleData.COLUMN_TITLE_NAME, titleName);	
		return subQ;
	}
	
	
	
	private ParseQuery<ParseUser>  getUserSubQuery(String userId){
		ParseQuery<ParseUser> subQ= ParseQuery.getQuery(ParseUser.class);
		Log.d(GlobConst.LOG_TAG, " userId: "+ userId);
		subQ.whereEqualTo(UserData.COLUMN_USER_ID, userId);	
		return subQ;
	}
	
	public void findUserQuotes(int limit,int skipedQuote,final String userId,final int langFlag,final FindQuotesCallback callback){
		findQuotes(limit,skipedQuote,userId, null, langFlag, callback);
	}
	
	public void findTitleQuotes(int limit,int skipedQuote,final String titleName,final int langFlag,final FindQuotesCallback callback){
		findQuotes(limit,skipedQuote,null, titleName, langFlag, callback);
	}
	
	
	
	public void findAllTitlesQuotes(int limit,int skipedQuote,int langFlag,FindQuotesCallback callback){
		findTitleQuotes(limit,skipedQuote,null,langFlag, callback);
		
	}
	
	public void findAllLikedQuotes(int limit,int skipedQuote,final int langFlag,final FindQuotesCallback callback){
		final boolean haveNetCon=checkNetConection();
		
		
				final String pinTag;
				ParseUser user=ParseUser.getCurrentUser();
				
			
				ParseRelation<QuoteData> relation=user.getRelation(UserData.COLUMN_USER_RELATION);
				
				ParseQuery<QuoteData> query=relation.getQuery();
				
				
				query.whereEqualTo(QuoteData.COLUMN_QUOTE_LANGUAGE, langFlag);
				query.setLimit(limit);
				query.setSkip(skipedQuote);
				query.include(QuoteData.COLUMN_QUOTE_TITLE);
				query.include(QuoteData.COLUMN_QUOTE_USER);
				query.whereEqualTo(QuoteData.COLUMN_QUOTE_TYPE, PARSE_TYPE_MODERATED);
				
				
		
				if (!haveNetCon) {
					//query.fromPin(pinTag);
					//Log.d(GlobConst.LOG_TAG, "!haveNetCon query.fromPin: "+pinTag);
				}
			
				query.findInBackground(new FindCallback<QuoteData>() {
					
					@Override
					public void done(List<QuoteData> inQuotes, ParseException e) {
						if(e==null){
						callback.findQuotesCallback(inQuotes,FindQuotesCallback.FIND_RESULT_OK);
						}else{
							callback.findQuotesCallback(inQuotes,FindQuotesCallback.FIND_RESULT_ERROR);
						}
					}
				});
				
				
	}
	
	public void findRandomQuote(final int langFlag,final FindRandomQuoteCallback callback){
	getQuotesCount(langFlag, new CountCallback() {
						
		@Override
		public void done(int count, ParseException e) {
				if(e==null){
					Random random=new Random();
					int skipedQuotes=random.nextInt(count);
					int targetQuotePosition=skipedQuotes+1;
								
					findAllTitlesQuotes(targetQuotePosition,
						skipedQuotes, langFlag,new FindQuotesCallback() {
											
							@Override
							public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
									if (FindRandomQuoteCallback.FIND_RESULT_OK==resultCode) {
										if(quotesList.size()>0){
											final int FIRST=0;
											QuoteData quote=quotesList.get(FIRST);
											callback.findRandomQuoteCallback(quote, FindRandomQuoteCallback.FIND_RESULT_OK);
										}else{
											callback.findRandomQuoteCallback(null, FindRandomQuoteCallback.FIND_RESULT_ERROR);
										}
									}else{
										callback.findRandomQuoteCallback(null, FindRandomQuoteCallback.FIND_RESULT_ERROR);
									}
												
							}
						});
					}else{
						callback.findRandomQuoteCallback(null, FindRandomQuoteCallback.FIND_RESULT_ERROR);	
					}
							
		}
	});
	}
	
	public void getQuotesCount(int langFlag, CountCallback callback){
		ParseQuery<QuoteData> query=ParseQuery.getQuery(QuoteData.class);
		
		query.whereEqualTo(QuoteData.COLUMN_QUOTE_LANGUAGE, langFlag);
		query.countInBackground(callback);
	}	
	
	public void findAllTitleName(final int langFlag,final FindTitlesNameCallback callback){
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
					
					ParseQuery<TitleData> query=ParseQuery.getQuery(TitleData.class);
					query.whereEqualTo(TitleData.COLUMN_TITLE_LANGUAGE, langFlag);
					query.whereEqualTo(TitleData.COLUMN_TITLE_TYPE, TitleData.MODERATE_TITLE_TYPE);
					
					ArrayList<String> titleNames= new ArrayList<String>();
					
					if (!haveNetCon) {
						//query.fromLocalDatastore();
						query.fromPin(pinTag);
					}
				
					try {
						List<TitleData> inTitlesList=query.find();
			
						if (haveNetCon) {
							pinTitleToLocaleDataStore(inTitlesList, pinTag);
						}
						
						for (TitleData titleData : inTitlesList) {
							titleNames.add(titleData.getTitleName());
						}
						
						Log.d(GlobConst.LOG_TAG, " inTitlesList.size(): "+ inTitlesList.size());
						
						Message msg=new Message();
						msg.what=FindQuotesCallback.FIND_RESULT_OK;
						msg.obj=titleNames;
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
	
	/*public void shutDownAllActions(){
		executorService.shutdown();
	}*/
	
	private boolean checkNetConection(){
		 if (conMng.getActiveNetworkInfo() != null
                 && conMng.getActiveNetworkInfo().isAvailable()
                 && conMng.getActiveNetworkInfo().isConnected()) {
			 return true;
		 }else {
			 return false;
		 }
	}
	
	
	
	public void pinTitleToLocaleDataStore(final List<TitleData> inTitlesList, final String tag){
		
		Log.d(GlobConst.LOG_TAG, "pinToLocaleDataStore"+tag);
		
		Log.d(GlobConst.LOG_TAG, "objects.size(): "+inTitlesList.size());
		
		if(inTitlesList!=null){
		ParseObject.unpinAllInBackground(tag,new DeleteCallback() {
			
			@Override
			public void done(ParseException e) {
				
				if(e==null){
				ParseObject.pinAllInBackground(tag, inTitlesList, new SaveCallback() {
					
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
	

	public void pinQuoteToLocaleDataStore(final List<QuoteData> inQuotes, final String tag){
		
		Log.d(GlobConst.LOG_TAG, "pinToLocaleDataStore"+tag);
		
		Log.d(GlobConst.LOG_TAG, "objects.size(): "+inQuotes.size());
		
		if(inQuotes!=null){
				ParseObject.pinAllInBackground(tag, inQuotes, new SaveCallback() {
					
					@Override
					public void done(ParseException e) {
						if (e==null) {
							Log.d(GlobConst.LOG_TAG, "Data pinned at locale by tag: "+tag);
						}else{
							Log.d(GlobConst.LOG_TAG, "Data DIDNT pinned at locale");
						}
						
					}
				} );
				
		}
	}
	
	public void checkQuoteLikeStatusOld(final ImageButton ibtnLikeStatus,final QuoteData quote){
		String quoteId=quote.getQuoteId();
		Log.d(GlobConst.LOG_TAG,"checkQuoteLikeStatus quoteId: "+quoteId);
		ParseRelation<QuoteData> testQuote=ParseUser.getCurrentUser().getRelation(UserData.COLUMN_USER_RELATION);
		ParseQuery<QuoteData> qtestQuot=testQuote.getQuery();
		qtestQuot.whereEqualTo(QuoteData.COLUMN_QUOTE_ID, quoteId);
		qtestQuot.findInBackground(new FindCallback<QuoteData>() {

			@Override
			public void done(List<QuoteData> objects, ParseException e) {
				if (e==null) {
					if (objects.size()>0) {
						quote.setLiked(true);
						ibtnLikeStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
						Log.d(GlobConst.LOG_TAG, "---------------> checkQuote objectId: "+objects.get(0).getQuoteId());
					}else{
						Log.d(GlobConst.LOG_TAG, "---------------> null obj with sach id: ");
						ibtnLikeStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
						quote.setLiked(false);
					}
				
				}else{
					Log.d(GlobConst.LOG_TAG, "---------------> error: "+e.getMessage());
				}			
			}	
		});	
	}
	
	public static void checkQuoteLikeStatus(final ImageButton ibtnLikeStatus,final QuoteData quote){
		LikeTask likeTask=new LikeTask(ibtnLikeStatus, quote);
	
		likeTask.executeOnExecutor(execLikesService);
	}
	
	private static class LikeTask extends AsyncTask<Void, Void, Integer>{
		
		WeakReference<ImageButton> ibtnLikeStatusWeak;
		WeakReference<QuoteData> quoteWeak;
		
		final static int RESULT_OK_LIKED=0;
		final static int RESULT_OK_UNLIKE=1;
		final static int RESULT_ERROR=2;
		
		public LikeTask(ImageButton ibtnLikeStatus,QuoteData quote) {
			ibtnLikeStatusWeak=new WeakReference<ImageButton>(ibtnLikeStatus);
			
			this.quoteWeak=new WeakReference<QuoteData>(quote);
		}
		
		@Override
		protected Integer doInBackground(Void... params) {
			
			try {
			String quoteId=quoteWeak.get().getQuoteId();
			Log.d(GlobConst.LOG_TAG,"checkQuoteLikeStatus quoteId: "+quoteId);
			ParseRelation<QuoteData> testQuote=ParseUser.getCurrentUser().getRelation(UserData.COLUMN_USER_RELATION);
			ParseQuery<QuoteData> qtestQuot=testQuote.getQuery();
			qtestQuot.whereEqualTo(QuoteData.COLUMN_QUOTE_ID, quoteId);
			List<QuoteData> likedQuotes;
			
			likedQuotes = qtestQuot.find();
			
			
			if(likedQuotes.size()>0){
				quoteWeak.get().setLiked(true);
				return RESULT_OK_LIKED;
			}else{
			
				quoteWeak.get().setLiked(false);
				return RESULT_OK_UNLIKE;
			}
			
			
			} catch (ParseException e) {
				e.printStackTrace();
				return RESULT_ERROR;
			}
			

			
			
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(isCancelled()){
				return;
			}
			switch(result){
			case RESULT_OK_LIKED:
				ibtnLikeStatusWeak.get().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
				break;
			case RESULT_OK_UNLIKE:
				ibtnLikeStatusWeak.get().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
				break;
			case RESULT_ERROR:
				break;
			}
		}
		
		
		
	}
	
}
