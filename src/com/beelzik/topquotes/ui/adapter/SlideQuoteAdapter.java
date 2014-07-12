package com.beelzik.topquotes.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.beelzik.topquotes.data.QuoteData;
import com.beelzik.topquotes.parse.ParseQuoteDataManager;
import com.beelzik.topquotes.ui.fragment.SlideFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

public class SlideQuoteAdapter extends FragmentStatePagerAdapter {


	ParseQuoteDataManager  parseQuoteDataManager;
	
    public SlideQuoteAdapter(FragmentManager fm, ParseQuoteDataManager  parseQuoteDataManager) {
		super(fm);
		this.parseQuoteDataManager=parseQuoteDataManager;
	}
	
   
    
    
    @Override
    public Fragment getItem(int position) {
      return SlideFragment.newInstance(position,parseQuoteDataManager);
    }

    @Override
    public int getCount() {
  
    	 return Integer.MAX_VALUE;
    }
    
    

  }

