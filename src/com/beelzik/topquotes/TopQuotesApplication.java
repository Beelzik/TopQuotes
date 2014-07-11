package com.beelzik.topquotes;

import android.app.Application;

import com.beelzik.topquotes.data.QuizeRecordData;
import com.beelzik.topquotes.data.QuoteData;
import com.beelzik.topquotes.data.TitleData;
import com.beelzik.topquotes.data.UserData;
import com.beelzik.topquotes.parse.ParseQuoteDataManager;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.util.GooglePlusClient;
import com.google.android.gms.plus.PlusClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

public class TopQuotesApplication extends Application  {

	ParseQuoteDataManager parseQuoteDataManager;
	
//	final private String  PARSE_APPLICATION_ID= "g7J6qKtb2QBOkfLGIv169L9fQprYsrP0ifoQ6ZOP";
	//final private String   PARSE_CLIENT_KEY="1SGNwpn8zgC32vA7gxqzZ4WdSSqmGP924K0vKrfh";
	
	final private String  PARSE_APPLICATION_ID= "w92VVUGRWNzEN9y2yHempFBzpnAgu7xjiDgZYiUU";
	final private String   PARSE_CLIENT_KEY="epAFQRXkQxlaoAuEWkP2OEkMlj57r29Vs4FOc3Mk";
	
	private GooglePlusClient googlePlusClient;
	@Override
	public void onCreate() {
		parseQuoteDataManager= new ParseQuoteDataManager(getApplicationContext());
		googlePlusClient= new GooglePlusClient(this);
		
		ParseUser.registerSubclass(QuizeRecordData.class);
		ParseUser.registerSubclass(UserData.class);
		  ParseObject.registerSubclass(TitleData.class);
		  ParseObject.registerSubclass(QuoteData.class);
		  Parse.enableLocalDatastore(this);
			 Parse.initialize(this,PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
			  
			  PushService.setDefaultPushCallback(this, MainActivity.class);
			  ParseInstallation.getCurrentInstallation().saveInBackground();
			  
			   ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
	            .build();
	        ImageLoader.getInstance().init(config);
	        
	/*	new Thread(new Runnable() {
			
			@Override
			public void run() {
				ParseUser.registerSubclass(UserData.class);
				  ParseObject.registerSubclass(TitleData.class);
				  ParseObject.registerSubclass(QuoteData.class);
				  Parse.enableLocalDatastore(TopQuotesApplication.this);
					 Parse.initialize(TopQuotesApplication.this,PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
					  
					  PushService.setDefaultPushCallback(TopQuotesApplication.this, MainActivity.class);
					  ParseInstallation.getCurrentInstallation().saveInBackground();
			}
		}).start();*/
	}
	
	public ParseQuoteDataManager getParseQuoteDataManager() {
		return parseQuoteDataManager;
	}
	
	public GooglePlusClient getGooglePlusClient() {
		return googlePlusClient;
	}
}
