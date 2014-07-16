package com.beelzik.topquotes;

import android.app.Application;

import com.beelzik.topquotes.parse.data.QuizeRecordData;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.parse.data.TitleData;
import com.beelzik.topquotes.parse.data.UserData;
import com.beelzik.topquotes.parse.data.storage.TitleListStorage;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.util.GooglePlusClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

public class TopQuotesApplication extends Application  {

	private static TitleListStorage titleListHolder= new TitleListStorage();;
	
	final private String  PARSE_APPLICATION_ID= "w92VVUGRWNzEN9y2yHempFBzpnAgu7xjiDgZYiUU";
	final private String   PARSE_CLIENT_KEY="epAFQRXkQxlaoAuEWkP2OEkMlj57r29Vs4FOc3Mk";
	
	private GooglePlusClient googlePlusClient;
	@Override
	public void onCreate() {
		
		googlePlusClient= new GooglePlusClient(this);
		
		ParseUser.registerSubclass(UserData.class);
		ParseObject.registerSubclass(QuizeRecordData.class);
		ParseObject.registerSubclass(TitleData.class);
		ParseObject.registerSubclass(QuoteData.class);
		  Parse.enableLocalDatastore(this);
			 Parse.initialize(this,PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
			 
	  ParseInstallation.getCurrentInstallation().saveInBackground();
			  
			   ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
	            .build();
	        ImageLoader.getInstance().init(config);
	    
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		titleListHolder=null;
	}
	
	public TitleListStorage getTitleListHolder() {
		return titleListHolder;
	}
	
	public GooglePlusClient getGooglePlusClient() {
		return googlePlusClient;
	}
}
