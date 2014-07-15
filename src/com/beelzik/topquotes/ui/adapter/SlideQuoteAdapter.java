package com.beelzik.topquotes.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.beelzik.topquotes.ui.fragment.SlideFragment;

public class SlideQuoteAdapter extends FragmentPagerAdapter {


	
    public SlideQuoteAdapter(FragmentManager fm) {
		super(fm);
	}
	
   
    
    
    @Override
    public Fragment getItem(int position) {
      return SlideFragment.newInstance(position);
    }

    @Override
    public int getCount() {
  
    	 return Integer.MAX_VALUE;
    }
    
    

  }

