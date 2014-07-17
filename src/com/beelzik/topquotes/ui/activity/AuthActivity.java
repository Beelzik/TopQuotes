package com.beelzik.topquotes.ui.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.parse.callback.FindQuotesCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.ui.activity.listener.OnUserAuthListener;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;
import com.beelzik.topquotes.util.GooglePlusClient;
import com.beelzik.topquotes.util.GooglePlusClientListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.parse.ParseUser;



public class AuthActivity extends Activity implements GooglePlusClientListener, OnUserAuthListener  {
private static final String TAG = "myLogs";



GooglePlusClient googlePlusClient;

SignInButton signIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		
		googlePlusClient=((TopQuotesApplication) getApplication())
				.getGooglePlusClient();
		
		googlePlusClient.setActivity(this);
		googlePlusClient.setOnAuthListener(this);
		
		signIn = (SignInButton) findViewById(R.id.sinBtnAuth);
		
		signIn.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				googlePlusClient.goolePlusDisconnect();
				googlePlusClient.googlePlusSignIn();
				
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		googlePlusClient.goolePlusDisconnect();
	}

	@Override
	public void onGooglePlusConnect() {
		//startActivity(new Intent(this, MainActivity.class));
		
	}

	@Override
	public void onUserAuth() {
		
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	

	
}