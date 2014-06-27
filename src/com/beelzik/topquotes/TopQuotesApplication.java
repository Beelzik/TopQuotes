package com.beelzik.topquotes;

import android.app.Application;

import com.beelzik.topquotes.db.ParseQuoteDataManager;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class TopQuotesApplication extends Application {

	ParseQuoteDataManager parseQuoteDataManager;
	final private String  PARSE_APPLICATION_ID= "g7J6qKtb2QBOkfLGIv169L9fQprYsrP0ifoQ6ZOP";
	final private String   PARSE_CLIENT_KEY="1SGNwpn8zgC32vA7gxqzZ4WdSSqmGP924K0vKrfh";
	
	@Override
	public void onCreate() {
		
		
		  Parse.initialize(this,PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		  
		  PushService.setDefaultPushCallback(this, MainActivity.class);
		  ParseInstallation.getCurrentInstallation().saveInBackground();
		  parseQuoteDataManager= new ParseQuoteDataManager(getApplicationContext());
		
		  Parse.enableLocalDatastore(this);
		    Parse.initialize(this,PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
	}
	
	public ParseQuoteDataManager getParseQuoteDataManager() {
		return parseQuoteDataManager;
	}
}
