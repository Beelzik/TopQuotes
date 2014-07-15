package com.beelzik.topquotes.ui.fragment;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.ui.adapter.SlideQuoteAdapter;

public class PagerFragment extends Fragment  implements RefreshQuoteListener{

	ViewPager pgSlide;

	SharedPreferences sp;
	int langFlag;
	SlideQuoteAdapter slideAdapter;
	
	public static PagerFragment newInstance(int sectionNumber) {
		PagerFragment fragment = new PagerFragment();
		Bundle args = new Bundle();
		args.putInt(GlobConst.ARG_SECTION_FRAGMENT_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView=inflater.inflate(R.layout.fragment_pager, container,false);

		
		sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
		langFlag=sp.getInt(GlobConst.SP_FLAG_WUT_LANG, GlobConst.DEFAULT_LANG_FLAG);
		slideAdapter= new SlideQuoteAdapter(getActivity().getSupportFragmentManager());
		
		
		
		
		pgSlide=(ViewPager) rootView.findViewById(R.id.pgSlide);
		pgSlide.setAdapter(slideAdapter);
		pgSlide.setOnPageChangeListener(new OnPageChangeListener() {

		      @Override
		      public void onPageSelected(int position) {
		        Log.d(GlobConst.LOG_TAG, "onPageSelected, position = " + position);
		      }

		      @Override
		      public void onPageScrolled(int position, float positionOffset,
		          int positionOffsetPixels) {
		    	  Log.d(GlobConst.LOG_TAG, "onPageScrolled, position = " + position+" ,positionOffset: "+positionOffset);
		      }

		      @Override
		      public void onPageScrollStateChanged(int state) {
		      }
		    });
		
		
		return rootView;
		}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				GlobConst.ARG_SECTION_FRAGMENT_NUMBER),null);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		refreshQuotes();
	}

	@Override
	public void refreshQuotes() {
		 Log.d(GlobConst.LOG_TAG,"PagerFrag refreshQuotes()");
	}
	
}
