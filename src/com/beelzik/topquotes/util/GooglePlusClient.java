package com.beelzik.topquotes.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.parse.data.UserData;
import com.beelzik.topquotes.ui.activity.listener.OnUserAuthListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

public class GooglePlusClient implements ConnectionCallbacks,
OnConnectionFailedListener {

	private PlusClient plusClient;
	SharedPreferences sp;
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	Context context;
	Activity activity;
	GooglePlusClientListener googlePlusClientListener;
	OnUserAuthListener authListener;
	
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (!plusClient.isConnected()) {
				plusClient.disconnect();
				plusClient.connect();
			}
			super.handleMessage(msg);
		}
	};
	
	ProgressDialog mConnectionProgressDialog;
	
	public GooglePlusClient(Context context) {
		this.context=context;
		plusClient= new PlusClient.Builder(context, this, this).build();
		sp=PreferenceManager.getDefaultSharedPreferences(context);
		plusClient.disconnect();
		

	}

	public void setActivity(Activity activity) {
		this.activity = activity;
		
		mConnectionProgressDialog = new ProgressDialog(activity);
		mConnectionProgressDialog.setMessage("Signing in...");
	}
	
	public void setGooglePlusClientListener(
			GooglePlusClientListener googlePlusClientListener) {
		this.googlePlusClientListener = googlePlusClientListener;
	}
	
	public void setOnAuthListener(OnUserAuthListener authListener) {
		this.authListener = authListener;
	}
	
	public void googlePlusConnect(){
		if (plusClient!=null) {
			plusClient.connect();
		}
	}
	
	public void googlePlusSignIn(){
		mConnectionProgressDialog.show();
		plusClient.connect();
		handler.sendEmptyMessageDelayed(0, 1000);
		Log.d(GlobConst.LOG_TAG,"plusClient.isConnected() "+plusClient.isConnected());
		Log.d(GlobConst.LOG_TAG,"plusClient.isConnecting() "+plusClient.isConnecting());
		Log.d(GlobConst.LOG_TAG,"plusClient: "+plusClient);
		Log.d(GlobConst.LOG_TAG,"googlePlusSignIn");
	}
	
	public void googlePlusSignOut(){
		if (plusClient.isConnected()) {
			plusClient.clearDefaultAccount();
			plusClient.disconnect();
			plusClient.connect();
        }
	}
	
	public void goolePlusDisconnect(){
		if (plusClient.isConnected()) {
			plusClient.disconnect();
		}
		
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(activity, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				
				Log.d(GlobConst.LOG_TAG,"onConnectionFailed e: "+e.getMessage());
				
				plusClient.connect();
			}
		}else{
			Log.d(GlobConst.LOG_TAG,"result.hasResolution() ");
			plusClient.connect();
		}
	}

	
	
	@Override
	public void onConnected(Bundle connectionHint) {

		if (GlobConst.DEBUG) {
			Log.d(GlobConst.LOG_TAG,"onConnected  plusClient");
		}
		
		String accountName = plusClient.getAccountName();
		Person person=plusClient.getCurrentPerson();
		String userDisplayName=person.getDisplayName();
		String userName=person.getName().getGivenName();
		String userFamily=person.getName().getFamilyName();
		String userAvatarUrl=person.getImage().getUrl();
		try {
			userAvatarUrl=userAvatarUrl.substring(0,userAvatarUrl.indexOf("?"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Editor editor=sp.edit();
		editor.putBoolean(GlobConst.SP_FLAG_USER_IS_LOGIN, true);
		editor.commit();   
		
		if (GlobConst.DEBUG) {
			Log.d(GlobConst.LOG_TAG," accountName: "+accountName+
					"\n userName: "+userName+
					"\n userFamily: "+userFamily+
					"\n userAvatarUrl: "+userAvatarUrl);
		}
		
		UserData.addUser(userDisplayName, accountName, userAvatarUrl, accountName, authListener);
		
		mConnectionProgressDialog.cancel();
		
		if (googlePlusClientListener!=null) {
			googlePlusClientListener.onGooglePlusConnect();
		}
		
	}

	
	
	
	@Override
	public void onDisconnected() {
	}
}
