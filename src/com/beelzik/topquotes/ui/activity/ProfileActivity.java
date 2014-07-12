package com.beelzik.topquotes.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.data.QuoteData;
import com.beelzik.topquotes.data.UserData;
import com.beelzik.topquotes.parse.FindQuotesCallback;
import com.beelzik.topquotes.parse.ParseQuoteDataManager;
import com.beelzik.topquotes.ui.actionbar.mpdel.SpinnerNavItem;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;
import com.beelzik.topquotes.ui.adapter.TitleNavigationAdapter;
import com.beelzik.topquotes.util.AnimateFirstDisplayListener;
import com.beelzik.topquotes.util.GooglePlusClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.ParseUser;

public class ProfileActivity extends ActionBarActivity implements OnClickListener, OnNavigationListener{
	
	ListView lvProfileQuotes;
	Button btnProfileSignOut;
	
	TextView tvProfileUserName; 
	TextView tvProfileUserEmail; 
	TextView tvProfilePublishedQuotes; 
	ImageView ivProfileAvatar;
	
	GooglePlusClient googlePlusClient;
	
	QuotesStreamListAdapter quotesStreamListAdapter;
	ParseQuoteDataManager parseQuoteDataManager;
	
	SharedPreferences sp;
	ActionBar actionBar;
	private ArrayList<SpinnerNavItem> navSpinner;
	private TitleNavigationAdapter adapter;
	
	private String checkedLaguages[];
	
	private String userNameDisplay;
	private String userId;
	private String userAvatarUrl;
	private String userEmail;
	
	ParseUser user;
	
	int langFlag;
	
	protected ImageLoader imageLoader;
	DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_profile);
		
		
		  options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.displayer(new RoundedBitmapDisplayer(20))
			.build();
		  imageLoader=ImageLoader.getInstance();
		  animateFirstListener = new AnimateFirstDisplayListener();
		
		  
		  
		user=ParseUser.getCurrentUser();
		
	
		
		sp=PreferenceManager.getDefaultSharedPreferences(this);
		googlePlusClient=((TopQuotesApplication) getApplication())
				.getGooglePlusClient();
		googlePlusClient.setActivity(this);
		
		parseQuoteDataManager=((TopQuotesApplication) getApplication()).getParseQuoteDataManager();
		quotesStreamListAdapter= new QuotesStreamListAdapter(this, parseQuoteDataManager);
		
		checkedLaguages=getResources().getStringArray(R.array.check_languages);
		
		lvProfileQuotes=(ListView) findViewById(R.id.lvProfileQuotes);
		lvProfileQuotes.setAdapter(quotesStreamListAdapter);
		
		
		btnProfileSignOut=(Button) findViewById(R.id.btnProfileSignOut);
		ivProfileAvatar=(ImageView) findViewById(R.id.ivProfileAvatar);
		
		tvProfileUserName= (TextView) findViewById(R.id.tvProfileUserName);
		tvProfileUserEmail= (TextView) findViewById(R.id.tvProfileUserEmail);
		tvProfilePublishedQuotes= (TextView) findViewById(R.id.tvProfilePublishedQuotes);
		
		if (user!=null) {
			userId=user.getObjectId();
			userNameDisplay=user.getString(UserData.COLUMN_USER_NAME_DISPLAY);
			userAvatarUrl=user.getString(UserData.COLUMN_USER_AVATA_URL);
			userEmail=user.getEmail();
			
			tvProfileUserName.setText(userNameDisplay);
			tvProfileUserEmail.setText(userEmail);
			
			  imageLoader.displayImage(userAvatarUrl,ivProfileAvatar, 
					  options, animateFirstListener);
		}
		
		btnProfileSignOut.setOnClickListener(this);
		
		initActionBar();
		
		findQuotes();
	}
	
	
	public void initActionBar(){
		actionBar=getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		actionBar.setDisplayShowTitleEnabled(true);
		
        navSpinner = new ArrayList<SpinnerNavItem>();
       
        navSpinner.add(new SpinnerNavItem(checkedLaguages[0], R.drawable.ic_flag_russia));
        navSpinner.add(new SpinnerNavItem(checkedLaguages[1], R.drawable.ic_flag_brit));
         
        adapter = new TitleNavigationAdapter(this, navSpinner);
 
        actionBar.setListNavigationCallbacks(adapter, this);
        
      checkCurrentlanFlag();
      
       actionBar.setSelectedNavigationItem(langFlag);
	}

	@Override
	public void onClick(View v) {
		googlePlusClient.googlePlusSignOut();
		ParseUser.logOut();
		
		Editor editor=sp.edit();
		editor.putBoolean(GlobConst.SP_FLAG_USER_IS_LOGIN,false);
		editor.commit();
		
		startActivity(new Intent(this, AuthActivity.class));
	}
	
	public void checkCurrentlanFlag(){
		 sp=PreferenceManager.getDefaultSharedPreferences(this);
	      langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		if (GlobConst.DEBUG) {
			Log.d(GlobConst.LOG_TAG, "position: "+position+" id: "+id);
		}
		checkCurrentlanFlag();
		
 		if(langFlag!=position){
 			langFlag=position;
 			
 	  		Editor editor=sp.edit();
 	  		editor.putInt(GlobConst.SP_FLAG_WUT_LANG, position);
 	  		editor.commit();
 	  		
 	  		findQuotes();
 		}
 		
 		return true;
	}
	
	public void findQuotes(){
		 parseQuoteDataManager.findUserQuotes(20,0,userId, langFlag, new FindQuotesCallback() {
				
				@Override
				public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
					if(FindQuotesCallback.FIND_RESULT_OK==resultCode){
						
						for (QuoteData quoteData : quotesList) {
							Log.d(GlobConst.LOG_TAG, "autor quote: "+quoteData.getObjectId());
						}
						quotesStreamListAdapter.clean();
						quotesStreamListAdapter.addAll(quotesList);
						quotesStreamListAdapter.notifyDataSetChanged();
						
						tvProfilePublishedQuotes.setText(quotesList.size()+"");
					}
					
				}
			});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

}
