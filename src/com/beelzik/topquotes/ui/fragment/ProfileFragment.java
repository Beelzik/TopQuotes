package com.beelzik.topquotes.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.parse.callback.FindQuotesCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.parse.data.UserData;
import com.beelzik.topquotes.ui.actionbar.mpdel.SpinnerNavItem;
import com.beelzik.topquotes.ui.activity.AuthActivity;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.ui.activity.listener.OnUserQuoteScrollListener;
import com.beelzik.topquotes.ui.adapter.QuotesStreamListAdapter;
import com.beelzik.topquotes.ui.adapter.TitleNavigationAdapter;
import com.beelzik.topquotes.util.AnimateFirstDisplayListener;
import com.beelzik.topquotes.util.GooglePlusClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment implements RefreshQuoteListener, OnClickListener {
	
	ListView lvProfileQuotes;
	Button btnProfileSignOut;
	
	TextView tvProfileUserName; 
	TextView tvProfileUserEmail; 
	TextView tvProfilePublishedQuotes; 
	ImageView ivProfileAvatar;
	
	GooglePlusClient googlePlusClient;
	
	QuotesStreamListAdapter quotesStreamListAdapter;

	
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
	
	
	public static ProfileFragment newInstance(int sectionNumber) {
		ProfileFragment fragment = new ProfileFragment();
		Bundle args = new Bundle();
		args.putInt(GlobConst.ARG_SECTION_FRAGMENT_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view=inflater.inflate(R.layout.fragment_profile, container, false);
		
		
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
		
	
		
		sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
		googlePlusClient=((TopQuotesApplication) getActivity().getApplication())
				.getGooglePlusClient();
		googlePlusClient.setActivity(getActivity());
		//googlePlusClient.googlePlusConnect();
		quotesStreamListAdapter= new QuotesStreamListAdapter(getActivity());
		
		checkedLaguages=getResources().getStringArray(R.array.check_languages);
		
		lvProfileQuotes=(ListView) view.findViewById(R.id.lvProfileQuotes);
		lvProfileQuotes.setAdapter(quotesStreamListAdapter);
		
		
		btnProfileSignOut=(Button) view.findViewById(R.id.btnProfileSignOut);
		ivProfileAvatar=(ImageView) view.findViewById(R.id.ivProfileAvatar);
		
		tvProfileUserName= (TextView) view.findViewById(R.id.tvProfileUserName);
		tvProfileUserEmail= (TextView) view.findViewById(R.id.tvProfileUserEmail);
		tvProfilePublishedQuotes= (TextView) view.findViewById(R.id.tvProfilePublishedQuotes);
		
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
		

		
		findQuotes();
		return view;
	}
	
	@Override
	public void onClick(View v) {
		googlePlusClient.googlePlusSignOut();
		ParseUser.logOut();
		
		Editor editor=sp.edit();
		editor.putBoolean(GlobConst.SP_FLAG_USER_IS_LOGIN,false);
		editor.commit();
		
		startActivity(new Intent(getActivity(), AuthActivity.class));
		getActivity().finish();
	}
	
	public void checkCurrentlanFlag(){
		 sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
	      langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
	}

	
	
	@Override
	public void onAttach(Activity activity) {
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				GlobConst.ARG_SECTION_FRAGMENT_NUMBER),this);
		super.onAttach(activity);
	}
	

	@Override
	public void refreshQuotes() {
	
		findQuotes();
		
	}
	
public void findQuotes(){
		checkCurrentlanFlag();
		QuoteData.getQuotesCount(getActivity(),langFlag,new CountCallback() {
			
			@Override
			public void done(int count, ParseException e) {
				tvProfilePublishedQuotes.setText(count+"");
			}
		});
		QuoteData.findUserQuotes(getActivity(),GlobConst.QUITES_TO_LOADE,0,userId, langFlag, new FindQuotesCallback() {
				
				@Override
				public void findQuotesCallback(List<QuoteData> quotesList, int resultCode) {
					if(FindQuotesCallback.FIND_RESULT_OK==resultCode){
						
						
						quotesStreamListAdapter.clean();
						quotesStreamListAdapter.addAll(quotesList);
						quotesStreamListAdapter.notifyDataSetChanged();
					
						OnUserQuoteScrollListener listener= new OnUserQuoteScrollListener(getActivity(),GlobConst.QUITES_TO_LOADE, 
								userId,sp, quotesStreamListAdapter);
						lvProfileQuotes.setOnScrollListener(listener);
						
						
					}
					
				}
			});
	}

}
