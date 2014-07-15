package com.beelzik.topquotes.ui.activity;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.parse.data.TitleData;
import com.beelzik.topquotes.parse.data.UserData;
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
		
		handler.sendEmptyMessageDelayed(HANDL_MSG_PARSE_INIT, 1000);

	}

}
