package com.beelzik.topquotes.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.parse.callback.FindTitlesNameCallback;
import com.beelzik.topquotes.parse.data.TitleData;
import com.beelzik.topquotes.parse.data.storage.TitleListStorage;
import com.beelzik.topquotes.ui.actionbar.mpdel.SpinnerNavItem;
import com.beelzik.topquotes.ui.activity.AddQuoteActivity;
import com.beelzik.topquotes.ui.activity.ProfileActivity;
import com.beelzik.topquotes.ui.adapter.TitleNavigationAdapter;


public class NavigationDrawerFragment extends Fragment implements ActionBar.OnNavigationListener{

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private NavigationDrawerCallbacks mCallbacks;

    private ActionBarDrawerToggle mDrawerToggle;
    
    private ArrayList<SpinnerNavItem> navSpinner;
    private TitleNavigationAdapter adapter;
    

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    
    private String[] navConstItems;
    
    private ArrayList<String> navConstItemsList;
    private TitleListStorage titleListHolder;
    
	private String checkedLaguages[];
	
	private RefreshQuoteListener refreshQuoteListener;
	
	SharedPreferences sp;
	int langFlag;

    
    ArrayAdapter<String> navigationAdapter;
    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        checkedLaguages=getResources().getStringArray(R.array.check_languages);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
		
        navConstItemsList=new ArrayList<String>();
        navConstItems=getResources().getStringArray(R.array.navigation_drawer_const_item);
        for (String item : navConstItems) {
        	if (GlobConst.DEBUG) {
				Log.d(GlobConst.LOG_TAG,"item: "+item);
			}
        	
			navConstItemsList.add(item);
		}
        
        titleListHolder=((TopQuotesApplication) this.
				getActivity().getApplication()).getTitleListHolder();
        
       TitleListStorage.setTitleList(langFlag,navConstItemsList);
        
        
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        selectItem(mCurrentSelectedPosition);
       
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

   public ArrayAdapter<String> getTitleList(){
	   return navigationAdapter;
   }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        
        if (GlobConst.DEBUG) {
			Log.d(GlobConst.LOG_TAG, "NavigationDrawerFragment onCreateView");
		}
         
        navigationAdapter=new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                navConstItemsList);
       
        mDrawerListView.setAdapter(navigationAdapter);
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        
        sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
        langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
        
        refreshNavigationTitleList(langFlag);
        
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
      
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch(item.getItemId()){
        case R.id.action_add_quote:
        	 Toast.makeText(getActivity(), "Add quote action.", Toast.LENGTH_SHORT).show();
             Intent intent=new Intent(getActivity(), AddQuoteActivity.class);
             startActivity(intent);
        	 return true;
        case R.id.action_profile:
        	
        	startActivity(new Intent(getActivity(), ProfileActivity.class));
       	 
       	 return true;
        default:
        	break;
        }
      

        return super.onOptionsItemSelected(item);
    }

    public void setRefreshQuoteListener(
			RefreshQuoteListener refreshQuoteListener) {
		this.refreshQuoteListener = refreshQuoteListener;
	}
    
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    
        navSpinner = new ArrayList<SpinnerNavItem>();
        
        navSpinner.add(new SpinnerNavItem(checkedLaguages[0], R.drawable.ic_flag_russia));
        navSpinner.add(new SpinnerNavItem(checkedLaguages[1], R.drawable.ic_flag_brit));
         
        // title drop down adapter
        adapter = new TitleNavigationAdapter(getActivity(), navSpinner);
 
        // assigning the spinner navigation     
        actionBar.setListNavigationCallbacks(adapter, this);
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(
        		getActivity());
        int wutLang=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
        actionBar.setSelectedNavigationItem(wutLang);
     
        
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
    
    
  	@Override
  	public boolean onNavigationItemSelected(int position, long id) {
  		if (GlobConst.DEBUG) {
  			Log.d(GlobConst.LOG_TAG, "position: "+position+" id: "+id);
		}
  		sp=PreferenceManager.getDefaultSharedPreferences(
	  				getActivity());
  		langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
  		if(langFlag!=position){
  			langFlag=position;
  			
  	  		Editor editor=sp.edit();
  	  		editor.putInt(GlobConst.SP_FLAG_WUT_LANG, position);
  	  		editor.commit();
  	  		Log.d(GlobConst.LOG_TAG, "currentItemSelected: "+mCurrentSelectedPosition+" , new sel position: "+0);
  	  		
  	  		//mDrawerListView.performItemClick(null, 0, 0);
  	  		
  	  		refreshNavigationTitleList(langFlag);
  	  		if (refreshQuoteListener!=null) {
  				refreshQuoteListener.refreshQuotes();
  			}
  		}
  		
  		return true;
  	}
  	
  
  	
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public void refreshNavigationTitleList(final int langFlag){
    	Log.d(GlobConst.LOG_TAG, "refreshNavigationTitleList");
    	  
          TitleData.findAllTitleName(getActivity(),langFlag,new FindTitlesNameCallback() {
  			
  			@Override
  			public void findTitleNameCallback(List<String> titleNameList, int resultCode) {
  				if (resultCode==FindTitlesNameCallback.FIND_RESULT_OK) {
  					//navigationAdapter.clear();
  					
  					ArrayList<String> titleConteiner=new ArrayList<String>();
  					///titleConteiner.addAll(navConstItems);
  					
  					for (int i = 0; i < navConstItems.length; i++) {
						titleConteiner.add(navConstItems[i]); 
						
					}
  					
  					
  					int navItemLength=navigationAdapter.getCount();
  					for (int i =navConstItems.length; i <navItemLength ; i++) {
  						Log.d(GlobConst.LOG_TAG, "remove item: "+navigationAdapter.getItem(navConstItems.length));
  						navigationAdapter.remove(navigationAdapter.getItem(navConstItems.length));
					}
  					
  					
  					/*for (String  navConstItem : navConstItems) {
  						navigationAdapter.add(navConstItem);
					}*/
  					
  					for (String titleName : titleNameList) {
  						titleConteiner.add(titleName);
  						navigationAdapter.add(titleName);
  						Log.d(GlobConst.LOG_TAG, "title navigation name: "+titleName);
					}
  					
  					for (int i = 0; i < navigationAdapter.getCount(); i++) {
  						Log.d(GlobConst.LOG_TAG, "=====================title navigation name: "+navigationAdapter.getItem(i));
						
					}
  					
  					for (String string : titleConteiner) {
  						Log.d(GlobConst.LOG_TAG, "/////////////---------title cont: "+string);
					}
  					/*for (String string : titleNameList) {
  						
  						navigationAdapter.add(string);
  					}*/
  					titleListHolder.setTitleList(langFlag,titleConteiner);
  					navigationAdapter.notifyDataSetChanged();
  				}
  				
  				
  			}
  		});
    }

  
}
