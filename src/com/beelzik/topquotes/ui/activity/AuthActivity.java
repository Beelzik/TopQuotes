package com.beelzik.topquotes.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.beelzik.topquotes.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.Builder;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusOneButton.OnPlusOneClickListener;


public class AuthActivity extends Activity implements OnClickListener,
ConnectionCallbacks, OnConnectionFailedListener {

	 private static final String TAG = "ExampleActivity";
	    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	    private ProgressDialog mConnectionProgressDialog;
	    private PlusClient mPlusClient;
	    private PlusOneButton plusOneButton;
	    private ConnectionResult mConnectionResult;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_auth);
	        mPlusClient = new PlusClient.Builder(this, this, this).build();
	      plusOneButton=(PlusOneButton) findViewById(R.id.plus_one_button);
	      plusOneButton.setOnPlusOneClickListener(new OnPlusOneClickListener() {
			
			public void onPlusOneClick(Intent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	        // Если ошибку соединения не удастся разрешить, будет отображаться индикатор выполнения.
	        mConnectionProgressDialog = new ProgressDialog(this);
	        mConnectionProgressDialog.setMessage("Signing in...");
	    }

	    @Override
	    protected void onStart() {
	        super.onStart();
	        mPlusClient.connect();
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
	        // Сохраним результат и разрешим ошибку соединения по нажатию пользователя.
	        mConnectionResult = result;
	    }

	    @Override
	    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
	        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
	            mConnectionResult = null;
	            mPlusClient.connect();
	        }
	    }

	    
	    @Override
	    public void onConnected(Bundle arg0) {
	    	 String accountName = mPlusClient.getAccountName();
		        Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
	    }

	    @Override
	    public void onDisconnected() {
	        Log.d(TAG, "disconnected");
	    }

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	}