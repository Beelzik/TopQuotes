package com.beelzik.topquotes.parse.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.parse.callback.FindQuotesCallback;
import com.beelzik.topquotes.parse.callback.FindTitlesNameCallback;
import com.beelzik.topquotes.util.NetConnectionRespondent;
import com.parse.DeleteCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

@ParseClassName("TitleData")
public class TitleData extends ParseObject{

	
	public final static String COLUMN_TITLE_ID="objectId";
	public final static String COLUMN_TITLE_NAME="serialName";
	public final static String COLUMN_TITLE_TYPE="type";
	public final static String COLUMN_TITLE_LANGUAGE="lang";
	

	private final static String PIN_TITLE_NAME="Title_name";
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
	
	public static void findAllTitleName(Context ctx,final int langFlag,final FindTitlesNameCallback callback){
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
			
			final boolean haveNetCon=NetConnectionRespondent.checkNetConection(ctx);
			
			Thread thread=new Thread(new Runnable() {
				
				@Override
				public void run() {
					String pinTag=PIN_TITLE_NAME+langFlag;
					
					ParseQuery<TitleData> query=ParseQuery.getQuery(TitleData.class);
					query.whereEqualTo(TitleData.COLUMN_TITLE_LANGUAGE, langFlag);
					query.whereEqualTo(TitleData.COLUMN_TITLE_TYPE, TitleData.MODERATE_TITLE_TYPE);
					
					ArrayList<String> titleNames= new ArrayList<String>();
					
					if (!haveNetCon) {
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
		
	}

	
	
	public static void pinTitleToLocaleDataStore(final List<TitleData> inTitlesList, final String tag){
		
		
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
	
}
