package com.beelzik.topquotes.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.db.ParseQuoteDataManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.parse.ParseUser;



public class AuthActivity extends Activity implements ConnectionCallbacks,
OnConnectionFailedListener {
private static final String TAG = "myLogs";
private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

private ProgressDialog mConnectionProgressDialog;
private PlusClient mPlusClient;
SharedPreferences sp;
// private ConnectionResult mConnectionResult;

ParseQuoteDataManager parseQuoteDataManager;

SignInButton signIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		
		parseQuoteDataManager=((TopQuotesApplication) getApplication())
				.getParseQuoteDataManager();
		
		mPlusClient = new PlusClient.Builder(this, this, this)
			
			.build();
		// Progress bar to be displayed if the connection failure is not
		// resolved.
		mPlusClient.disconnect();
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");
		
		signIn = (SignInButton) findViewById(R.id.sinBtnAuth);
		
		signIn.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				mConnectionProgressDialog.show();
				mPlusClient.connect();
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				mPlusClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mConnectionProgressDialog.dismiss();
		String accountName = mPlusClient.getAccountName();
		Person person=mPlusClient.getCurrentPerson();
		String userDisplayName=person.getDisplayName();
		String userName=person.getName().getGivenName();
		String userFamily=person.getName().getFamilyName();
		String userAvatarUrl=person.getImage().getUrl();
		try {
			userAvatarUrl=userAvatarUrl.substring(0,userAvatarUrl.indexOf("?"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		sp=PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor=sp.edit();
		editor.putString(GlobConst.SP_FLAG_ACOUNT_NAME, accountName);
		editor.putString(GlobConst.SP_FLAG_USER_NAME, userName);
		editor.putString(GlobConst.SP_FLAG_USER_FAMILY,userFamily);
		editor.putString(GlobConst.SP_FLAG_USER_AVATAR_URL,userAvatarUrl);
		editor.putString(GlobConst.SP_FLAG_USER_DISPLAY_NAME,userDisplayName);
		editor.commit();
		
		if (GlobConst.DEBUG) {
			Log.d(GlobConst.LOG_TAG," accountName: "+accountName+
					"\n userName: "+userName+
					"\n userFamily: "+userFamily+
					"\n userAvatarUrl: "+userAvatarUrl);
		}
		
		parseQuoteDataManager.addUser();
		
	}

	
	
	@Override
	public void onDisconnected() {
	}

	
}