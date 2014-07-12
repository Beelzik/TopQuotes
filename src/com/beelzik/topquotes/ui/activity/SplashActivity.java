package com.beelzik.topquotes.ui.activity;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.data.QuoteData;
import com.beelzik.topquotes.data.TitleData;
import com.beelzik.topquotes.data.UserData;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class SplashActivity extends Activity{
	
	
	
	final static int HANDL_MSG_PARSE_INIT=100;
	final static int HANDL_MSG_PARSE_FAIL_INIT=200;
	
	final static int SPLASH_TIME_OUT_MLS=1000;
	
	final private String  PARSE_APPLICATION_ID= "w92VVUGRWNzEN9y2yHempFBzpnAgu7xjiDgZYiUU";
	final private String   PARSE_CLIENT_KEY="epAFQRXkQxlaoAuEWkP2OEkMlj57r29Vs4FOc3Mk";
	
	//final private String  PARSE_APPLICATION_ID= "g7J6qKtb2QBOkfLGIv169L9fQprYsrP0ifoQ6ZOP";
	//final private String   PARSE_CLIENT_KEY="1SGNwpn8zgC32vA7gxqzZ4WdSSqmGP924K0vKrfh";
	SharedPreferences sp;
	
	private Handler handler= new Handler(){
	
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case HANDL_MSG_PARSE_INIT:
				Log.d(GlobConst.LOG_TAG, "splash activity is user login: "+sp.getBoolean(GlobConst.SP_FLAG_USER_IS_LOGIN, false));
				if(sp.getBoolean(GlobConst.SP_FLAG_USER_IS_LOGIN, false) && (ParseUser.getCurrentUser()!=null)){
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
				}else{
					startActivity(new Intent(SplashActivity.this, AuthActivity.class));
				}
				
				SplashActivity.this.finish();
				
				break;
			case  HANDL_MSG_PARSE_FAIL_INIT:
				
				break;
			default:
				break;
			}
	
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_splash);
		
		sp=PreferenceManager.getDefaultSharedPreferences(this);
		//startActivity(new Intent(SplashActivity.this, AuthActivity.class));
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				  
				Log.d(GlobConst.LOG_TAG,"parse init start");
				  ParseUser.registerSubclass(UserData.class);
				  ParseObject.registerSubclass(TitleData.class);
				  ParseObject.registerSubclass(QuoteData.class);
				  Log.d(GlobConst.LOG_TAG,"parse init reg sub class");
				  Parse.enableLocalDatastore(getApplicationContext());
				  Log.d(GlobConst.LOG_TAG,"parse init enab locale");
					 Parse.initialize(getApplicationContext(),PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
				Log.d(GlobConst.LOG_TAG,"parse init .initialize");  
					  PushService.setDefaultPushCallback(getApplicationContext(), MainActivity.class);
					  Log.d(GlobConst.LOG_TAG,"setDefaultPushCallback");
						ParseInstallation.getCurrentInstallation().saveInBackground();
					
				
					  Log.d(GlobConst.LOG_TAG,"parse inited");
				 
				  handler.sendEmptyMessageDelayed(HANDL_MSG_PARSE_INIT, 0);
				 
			}
		});
		  handler.sendEmptyMessageDelayed(HANDL_MSG_PARSE_INIT, 1000);
		//thread.start();
	}

}
