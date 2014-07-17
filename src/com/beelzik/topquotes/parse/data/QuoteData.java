package com.beelzik.topquotes.parse.data;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.parse.callback.FindQuotesCallback;
import com.beelzik.topquotes.parse.callback.FindRandomQuoteCallback;
import com.beelzik.topquotes.parse.callback.OnQuoteLikedCallback;
import com.beelzik.topquotes.util.NetConnectionRespondent;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
	public final static String COLUMN_QUOTE_CREATED_AT="createdAt";
	public final static String COLUMN_QUOTE_UPDATED_AT="updatedAt";
	
	public final static int DEFAULT_QUOTE_TYPE=0;
	
	
	private final static String PIN_TOP_QUTE="Top_quote";
	
	private final static String PIN_USER_LIKES="User_likes";
	
	private final static int PARSE_TYPE_MODERATED=1;
	private final static int PARSE_TYPE_DEFAULT=0;
	
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
		return super.getInt(COLUMN_QUOTE_SEASON);
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
		super.put(COLUMN_QUOTE_SEASON,season);
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
	
	
	
	public static void likeQuoteInParse(final View view,final QuoteData targetQuote, final OnQuoteLikedCallback likedCallback){
	
	ParseQuery< QuoteData> query=ParseQuery.getQuery(QuoteData.class);
	query.fromPin(PIN_USER_LIKES);
	query.whereEqualTo(QuoteData.COLUMN_QUOTE_ID,targetQuote.getQuoteId());
	query.getFirstInBackground(new GetCallback<QuoteData>() {
		
		@Override
		public void done(QuoteData object, ParseException e) {
			
		
				if(object!=null){
					unlikeQuote(targetQuote, likedCallback, view);
				}else{
					likeQuote(targetQuote, likedCallback, view);
				}
		
			
			
		}
	});				
}

private static void likeQuote(final QuoteData targetQuote,final OnQuoteLikedCallback likedCallback,final View view){
	ParseUser user=ParseUser.getCurrentUser();
	
	ParseRelation<QuoteData> quoteLikedBy=user.getRelation(UserData.COLUMN_USER_RELATION);

	ParseRelation<ParseUser> userLikes=targetQuote.getRelation(QuoteData.COLUMN_QUOTE_LIKES);
	
	quoteLikedBy.add(targetQuote);
	userLikes.add(user);
	
	user.saveInBackground();
	targetQuote.saveInBackground();

	
	if (GlobConst.DEBUG) {
		Log.d(GlobConst.LOG_TAG, "add leke");
	}
	
	
	
	targetQuote.pinInBackground(PIN_USER_LIKES, new SaveCallback() {
		
		@Override
		public void done(ParseException e) {
			if (e==null) {
				if(likedCallback!=null){
					likedCallback.onQuoteLikedCallback(view,true);
				}
			}
		}
	});
	
}

private static void unlikeQuote(final QuoteData targetQuote,final OnQuoteLikedCallback likedCallback, final View view){
	ParseUser user=ParseUser.getCurrentUser();
	ParseRelation<QuoteData> quoteLikedBy=user.getRelation(UserData.COLUMN_USER_RELATION);
	ParseRelation<ParseUser> userLikes=targetQuote.getRelation(QuoteData.COLUMN_QUOTE_LIKES);
	quoteLikedBy.remove(targetQuote);
	userLikes.remove(user);
	
	user.saveInBackground();
	targetQuote.saveInBackground();
	targetQuote.unpinInBackground(PIN_USER_LIKES, new DeleteCallback() {
		
		@Override
		public void done(ParseException e) {
			if (e==null) {
				if(likedCallback!=null){
					likedCallback.onQuoteLikedCallback(view,false);
				}
			}
		}
	});
	
	if (GlobConst.DEBUG) {
		Log.d(GlobConst.LOG_TAG, "del like ");
	}

}
	
	
	
	
	
public static void addQuoteInParse(final String quote,String titleName,final int season,final int episode,final ParseUser user, final int lang){
		
		final TitleData title=ParseObject.create(TitleData.class);
		title.putTitleName(titleName);
		title.putLanguage(lang);
		ParseQuery<TitleData> queryTitle= ParseQuery.getQuery(TitleData.class);
		queryTitle.whereEqualTo(TitleData.COLUMN_TITLE_NAME, titleName);
		queryTitle.findInBackground(new FindCallback<TitleData>() {
			
			@Override
			public void done(List<TitleData> objects, ParseException e) {
				if(e==null){
					
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
							saveQuote(title,quote,season,episode,user,lang);
						}
					});
				}
			}
		});
		
		
		
		
	}
	
	private static void saveQuote(TitleData title, String quote,int season,int episode,ParseUser user, int lang){
		
		QuoteData sendQuote= ParseObject.create(QuoteData.class);
		
		sendQuote.putTitle(title);
		sendQuote.putQuote(quote);
		sendQuote.putSeason(season);
		sendQuote.putEpisode(episode);
		sendQuote.putQuoteType(QuoteData.DEFAULT_QUOTE_TYPE);
		sendQuote.putUser(user);
		sendQuote.putLanguage(lang);
		
	
		
		sendQuote.saveInBackground();
		
	}

	public static void findQuotes(Context ctx,int limit,int skipedQuote,final String userId,final String titleName,
			final int langFlag,final FindQuotesCallback callback){
		final boolean haveNetCon=NetConnectionRespondent.checkNetConection(ctx);
		FindQuoteTask findQuoteTask=new FindQuoteTask(limit, skipedQuote, userId, titleName, langFlag, haveNetCon, callback);
		findQuoteTask.execute();
		}
	
	
	private static class FindQuoteTask extends AsyncTask<Void, Void, Integer>{
		
		int limit;
		int skipedQuote;
		String userId;
		String titleName;
		int langFlag;
		FindQuotesCallback callback;
		boolean haveNetConnection;
		List <QuoteData> inQuotes;
		
		public FindQuoteTask(int limit, int skipedQuote, String userId,
				String titleName, int langFlag, boolean haveNetConnection, FindQuotesCallback callback) {
			super();
			this.limit = limit;
			this.skipedQuote = skipedQuote;
			this.userId = userId;
			this.titleName = titleName;
			this.langFlag = langFlag;
			this.callback = callback;
			this.haveNetConnection = haveNetConnection;
		}
		
		
		@Override
		protected Integer doInBackground(Void... params) {
			
			try {
				
			final String pinTag;
			ParseQuery<QuoteData> query=ParseQuery.getQuery(QuoteData.class);
			query.whereEqualTo(QuoteData.COLUMN_QUOTE_LANGUAGE, langFlag);
			query.setLimit(limit);
			query.setSkip(skipedQuote);
			query.orderByDescending(QuoteData.COLUMN_QUOTE_CREATED_AT);
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
			
			if (!haveNetConnection) {
				query.fromPin(pinTag);
				
			}
		
			
			
			
				inQuotes=query.find();
				
				if (haveNetConnection && (titleName==null)) {
					inQuotes =QuoteData.fetchAllIfNeeded(inQuotes);
					pinQuoteToLocaleDataStore(inQuotes, pinTag);
				}
				
				
				return FindQuotesCallback.FIND_RESULT_OK;
			} catch (ParseException e) {
				e.printStackTrace();
				return FindQuotesCallback.FIND_RESULT_ERROR;
			}
			
		}
		@Override
		protected void onPostExecute(Integer result) {
			if(FindQuotesCallback.FIND_RESULT_OK==result){
				callback.findQuotesCallback(inQuotes,FindQuotesCallback.FIND_RESULT_OK);
			}else{
				callback.findQuotesCallback(inQuotes,FindQuotesCallback.FIND_RESULT_ERROR);
			}
		}
	}
	
	
	
	
	private static ParseQuery<TitleData>  getTitleSubQuery(String titleName){
		ParseQuery<TitleData> subQ= ParseQuery.getQuery(TitleData.class);
		
		subQ.whereEqualTo(TitleData.COLUMN_TITLE_NAME, titleName);	
		return subQ;
	}
	
	
	
	private static ParseQuery<ParseUser>  getUserSubQuery(String userId){
		ParseQuery<ParseUser> subQ= ParseQuery.getQuery(ParseUser.class);
		
		subQ.whereEqualTo(UserData.COLUMN_USER_ID, userId);	
		return subQ;
	}
	
	public static void findUserQuotes(Context ctx,int limit,int skipedQuote,final String userId,final int langFlag,final FindQuotesCallback callback){
		findQuotes(ctx,limit,skipedQuote,userId, null, langFlag, callback);
	}
	
	public static void findTitleQuotes(Context ctx,int limit,int skipedQuote,final String titleName,final int langFlag,final FindQuotesCallback callback){
		findQuotes(ctx,limit,skipedQuote,null, titleName, langFlag, callback);
	}
	
	
	
	public static void findAllTitlesQuotes(Context ctx,int limit,int skipedQuote,int langFlag,FindQuotesCallback callback){
		findTitleQuotes(ctx,limit,skipedQuote,null,langFlag, callback);
		
	}
	
	public static void findAllLikedQuotes(Context ctx,int limit,int skipedQuote,final int langFlag,final FindQuotesCallback callback){
		final boolean haveNetCon=NetConnectionRespondent.checkNetConection(ctx);
		
		ParseQuery<QuoteData> query=null;
			if (haveNetCon) {
				ParseUser user=ParseUser.getCurrentUser();
				ParseRelation<QuoteData> relation=user.getRelation(UserData.COLUMN_USER_RELATION);	
				 query=relation.getQuery();
				query.whereEqualTo(QuoteData.COLUMN_QUOTE_LANGUAGE, langFlag);
				query.setLimit(limit);
				query.setSkip(skipedQuote);
				query.include(QuoteData.COLUMN_QUOTE_TITLE);
				query.include(QuoteData.COLUMN_QUOTE_USER);
				query.whereEqualTo(QuoteData.COLUMN_QUOTE_TYPE, PARSE_TYPE_MODERATED);
			}	
				
				
				
		
				if (!haveNetCon) {
				  query=ParseQuery.getQuery(QuoteData.class);
				  	query.whereEqualTo(QuoteData.COLUMN_QUOTE_LANGUAGE, langFlag);
					query.fromPin(PIN_USER_LIKES);
					query.include(QuoteData.COLUMN_QUOTE_TITLE);
					query.include(QuoteData.COLUMN_QUOTE_USER);
					query.setLimit(limit);
					query.setSkip(skipedQuote);
					query.whereEqualTo(QuoteData.COLUMN_QUOTE_TYPE, PARSE_TYPE_MODERATED);
					
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
	
	public static void findRandomQuote(Context ctx,final int langFlag,final FindRandomQuoteCallback callback){
	getQuotesCount(ctx,langFlag, new CountCallback() {
						
		@Override
		public void done(int count, ParseException e) {
				if(e==null){
					Random random=new Random();
					int skipedQuotes=random.nextInt(count);
					int targetQuotePosition=skipedQuotes+1;
								
					
					FindQuoteTask findQuoteTask=new FindQuoteTask(targetQuotePosition, skipedQuotes, null,null, langFlag, false, new FindQuotesCallback() {
						
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
					findQuoteTask.execute();
					}else{
						callback.findRandomQuoteCallback(null, FindRandomQuoteCallback.FIND_RESULT_ERROR);	
					}
				
					
							
		}
	});
	}
	
	public static void getQuotesCount(Context ctx,int langFlag, CountCallback callback){
		
		boolean haveNetCon=NetConnectionRespondent.checkNetConection(ctx);
		
		ParseQuery<QuoteData> query=ParseQuery.getQuery(QuoteData.class);

		query.fromPin(PIN_TOP_QUTE+langFlag);
		query.whereEqualTo(QuoteData.COLUMN_QUOTE_TYPE, PARSE_TYPE_MODERATED);
		query.whereEqualTo(QuoteData.COLUMN_QUOTE_LANGUAGE, langFlag);
		query.countInBackground(callback);
	}
	
	public static void pinQuoteToLocaleDataStore(final List<QuoteData> inQuotes, final String tag){
		
		if(inQuotes!=null){
				ParseObject.pinAllInBackground(tag, inQuotes);
				
		}
	}
	
	public static void syncAllLikesFromParse(Context ctx){
		boolean isConnectedToNet=NetConnectionRespondent.checkNetConection(ctx);
		if (isConnectedToNet) {
			ParseRelation<QuoteData> testQuote=ParseUser.getCurrentUser().getRelation(UserData.COLUMN_USER_RELATION);
			ParseQuery<QuoteData> qtestQuot=testQuote.getQuery();
			
			qtestQuot.findInBackground(new FindCallback<QuoteData>() {
				
				@Override
				public void done(final List<QuoteData> objects, ParseException e) {
					QuoteData.unpinAllInBackground(PIN_USER_LIKES, new DeleteCallback() {
						
						@Override
						public void done(ParseException e) {
							QuoteData.pinAllInBackground(PIN_USER_LIKES, objects);
						}
					});
					
					
					
					
				}
			});
		}
	}
	
	
	public static void checkQuoteLikeStatus(Context ctx,final ImageButton ibtnLikeStatus,final QuoteData quote, int position){
	LikeTask likeTask=new LikeTask(ctx,ibtnLikeStatus, quote,position);
	likeTask.execute();
	}
	
	private static class LikeTask extends AsyncTask<Void, Void, Integer>{
		
		WeakReference<ImageButton> ibtnLikeStatusWeak;
		WeakReference<QuoteData> quoteWeak;
		Context ctx;
		
		int position;
		final static int RESULT_OK_LIKED=0;
		final static int RESULT_OK_UNLIKE=1;
		final static int RESULT_ERROR=2;
		
		public LikeTask(Context ctx,ImageButton ibtnLikeStatus,QuoteData quote, int position) {
			ibtnLikeStatusWeak=new WeakReference<ImageButton>(ibtnLikeStatus);
			this.quoteWeak=new WeakReference<QuoteData>(quote);
			this.position=position;
			this.ctx=ctx;
		}
		
		@Override
		protected Integer doInBackground(Void... params) {
			
			if (isCancelled()) {
				return RESULT_ERROR;
			}
			try {
			ParseQuery< QuoteData> query=ParseQuery.getQuery(QuoteData.class);
			query.fromPin(PIN_USER_LIKES);
			query.whereEqualTo(QuoteData.COLUMN_QUOTE_ID,quoteWeak.get().getQuoteId());
			List<QuoteData> likedQuotes=query.find();
			
			if(quoteWeak.get()!=null){
			if(likedQuotes.size()>0){
				
				return RESULT_OK_LIKED;
				
			}else{

				return RESULT_OK_UNLIKE;
				
			}
			}
			
			} catch (ParseException e) {
				e.printStackTrace();
				return RESULT_ERROR;
			}
			
			return RESULT_ERROR;
			
			
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(isCancelled()){
				return;
			}
			switch(result){
			case RESULT_OK_LIKED:
				if (ibtnLikeStatusWeak.get()!=null) {
					ibtnLikeStatusWeak.get().setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_liked));
				}
			
				break;
			case RESULT_OK_UNLIKE:
				if (ibtnLikeStatusWeak.get()!=null) {
					ibtnLikeStatusWeak.get().setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_like));
				}
			
				break;
			case RESULT_ERROR:
				break;
			}
		}
		
		
		
	}
	
}
