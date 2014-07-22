package com.beelzik.topquotes.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.parse.data.storage.TitleListStorage;
import com.beelzik.topquotes.ui.actionbar.mpdel.SpinnerNavItem;
import com.beelzik.topquotes.ui.adapter.TitleNavigationAdapter;
import com.beelzik.topquotes.ui.fragment.PagerFragment;
import com.beelzik.topquotes.ui.fragment.ProfileFragment;
import com.beelzik.topquotes.ui.fragment.QuizFragment;
import com.beelzik.topquotes.ui.fragment.QuoteStreamFragment;
import com.beelzik.topquotes.ui.fragment.NavigationDrawerFragment;
import com.beelzik.topquotes.ui.fragment.RefreshQuoteListener;
import com.beelzik.topquotes.ui.fragment.TitleQuotesFragment;
import com.beelzik.topquotes.ui.fragment.TopFavoritesFragment;


public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks,ActionBar.OnNavigationListener {


    private ArrayList<SpinnerNavItem> navSpinner;
    private TitleNavigationAdapter adapter;
    
    private TitleListStorage titleListHolder;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private String checkedLaguages[];
	
	private CharSequence mTitle;
	RefreshQuoteListener refreshQuoteListener;
	SharedPreferences sp;
	int langFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		titleListHolder=((TopQuotesApplication) this.
				getApplication()).getTitleListHolder();
		QuoteData.syncAllLikesFromParse(this);
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		
		mTitle = getTitle();
		
		
		
		
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		checkedLaguages=getResources().getStringArray(R.array.check_languages);
		
		sp=PreferenceManager.getDefaultSharedPreferences(this);
		langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		switch(position){
		case GlobConst.DRAW_MENU_ITEM_MENU_POS:
		
			break;
		case 1:
			QuoteStreamFragment quoteStremFragment= QuoteStreamFragment.newInstance(position);
			replaceFragment(quoteStremFragment);
			
			break;
		case 2:
			QuizFragment quizFragment= QuizFragment.newInstance(position);
			
			replaceFragment(quizFragment);
			break;
		case 3:
			TopFavoritesFragment favoriteFragment= TopFavoritesFragment.newInstance(position);
			replaceFragment(favoriteFragment);
			break;
		case 4:
			PagerFragment pagerFragment= PagerFragment.newInstance(position);	
			replaceFragment(pagerFragment);
			
			break;
		case 5:
			ProfileFragment profileFragment= ProfileFragment.newInstance(position);
			
			replaceFragment(profileFragment);	
			break;
		case GlobConst.DRAW_MENU_ITEM_TITLES_POS:	
			break;
		default:
			TitleQuotesFragment titleQuotesFragment=TitleQuotesFragment.newInstance(position);
			replaceFragment(titleQuotesFragment);
			
			break;
		}
		
	}

	public void replaceFragment(Fragment fragment){
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						fragment).commit();
	}
	
	public void onSectionAttached(int number, RefreshQuoteListener listener) {
		Log.d(GlobConst.LOG_TAG," onSectionAttached");
	ArrayList<String> titleList=TitleListStorage.getTitleList(langFlag);
	if (titleList!=null) {
		Log.d(GlobConst.LOG_TAG," mTitle: "+mTitle);
		mTitle=titleList.get(number);
	}
		
		mNavigationDrawerFragment.setRefreshQuoteListener(listener);
		setRefreshQuoteListener(listener);
		
	}
	
	public void setRefreshQuoteListener(
			RefreshQuoteListener refreshQuoteListener) {
		this.refreshQuoteListener = refreshQuoteListener;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
		
        navSpinner = new ArrayList<SpinnerNavItem>();
       
        navSpinner.add(new SpinnerNavItem(checkedLaguages[0], R.drawable.ic_flag_russia));
        navSpinner.add(new SpinnerNavItem(checkedLaguages[1], R.drawable.ic_flag_brit));
         
        adapter = new TitleNavigationAdapter(this, navSpinner);
 
        actionBar.setListNavigationCallbacks(adapter, this);
        
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        int wutLang=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
        actionBar.setSelectedNavigationItem(wutLang);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		
		return super.onOptionsItemSelected(item);
	}

	
	@Override
  	public boolean onNavigationItemSelected(int position, long id) {
		if (GlobConst.DEBUG) {
			Log.d(GlobConst.LOG_TAG, "position: "+position+" id: "+id);
		}
		sp=PreferenceManager.getDefaultSharedPreferences(this);
		langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
  		if(langFlag!=position){
  			langFlag=position;
  			
  	  		Editor editor=sp.edit();
  	  		editor.putInt(GlobConst.SP_FLAG_WUT_LANG, position);
  	  		editor.commit();
  	  		
  	  	//	mNavigationDrawerFragment.selectItem(0);
  	  		
  	  		mNavigationDrawerFragment.refreshNavigationTitleList(langFlag);
  	  		if (refreshQuoteListener!=null) {
  	    		refreshQuoteListener.refreshQuotes();
			}
  	
  		}
  		return true;
  	}

	

}
