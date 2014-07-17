package com.beelzik.topquotes.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.parse.callback.FindQuotesCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.parse.data.UserData;
import com.beelzik.topquotes.parse.data.storage.TitleListStorage;
import com.beelzik.topquotes.ui.actionbar.mpdel.SpinnerNavItem;
import com.beelzik.topquotes.ui.activity.listener.OnUserQuoteScrollListener;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;
import com.beelzik.topquotes.ui.adapter.TitleNavigationAdapter;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class QuoteAutorActivity extends ActionBarActivity implements OnNavigationListener{

	ListView lvAutor;
	QuotesStreamListAdapter quotesStreamListAdapter;

	int langFlag;
	ActionBar actionBar;
	private ArrayList<SpinnerNavItem> navSpinner;
	private TitleNavigationAdapter adapter;
	private String checkedLaguages[];
	SharedPreferences sp;
	private String userNameDisplay;
	private String userId;
	private String quoteAutorActivityTitle="Quotes by ";
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quote_by_autor);
		
		checkedLaguages=getResources().getStringArray(R.array.check_languages);
		

		
		quotesStreamListAdapter= new QuotesStreamListAdapter(this);
		
		userNameDisplay=getIntent().getStringExtra(UserData.COLUMN_USER_NAME_DISPLAY);
		userId=getIntent().getStringExtra(UserData.COLUMN_USER_ID);
		
		quoteAutorActivityTitle+=userNameDisplay;
		
		lvAutor=(ListView) findViewById(R.id.lvAutor);
		lvAutor.setAdapter(quotesStreamListAdapter);
		
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
		actionBar.setTitle(quoteAutorActivityTitle);
		
        navSpinner = new ArrayList<SpinnerNavItem>();
       
        navSpinner.add(new SpinnerNavItem(checkedLaguages[0], R.drawable.ic_flag_russia));
        navSpinner.add(new SpinnerNavItem(checkedLaguages[1], R.drawable.ic_flag_brit));
         
        adapter = new TitleNavigationAdapter(this, navSpinner);
 
        actionBar.setListNavigationCallbacks(adapter, this);
        
      checkCurrentlanFlag();
      
       actionBar.setSelectedNavigationItem(langFlag);
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
		QuoteData.findUserQuotes(this,GlobConst.QUITES_TO_LOADE,0,userId, langFlag, new FindQuotesCallback() {
				
				@Override
				public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
					if(FindQuotesCallback.FIND_RESULT_OK==resultCode){
						
						
						quotesStreamListAdapter.clean();
						quotesStreamListAdapter.addAll(quotesList);
						quotesStreamListAdapter.notifyDataSetChanged();
						
						OnUserQuoteScrollListener scrollListener=
								new OnUserQuoteScrollListener(QuoteAutorActivity.this,GlobConst.QUITES_TO_LOADE, userId, sp, quotesStreamListAdapter);
						lvAutor.setOnScrollListener(scrollListener);
					
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
