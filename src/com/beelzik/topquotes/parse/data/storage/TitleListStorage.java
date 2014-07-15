package com.beelzik.topquotes.parse.data.storage;

import java.util.ArrayList;
import java.util.HashMap;

import com.beelzik.topquotes.parse.callback.FindTopTenAndUserRecordsCallback;

import android.content.SharedPreferences;

public class TitleListStorage{		

	
	
	

	
	
	static HashMap<Integer,ArrayList<String>> mapTileList;
	SharedPreferences sp;
	
	FindTopTenAndUserRecordsCallback recordsCallback;
	
	
	public TitleListStorage() {
		mapTileList= new HashMap<Integer, ArrayList<String>>();
	}
	
	public static void setTitleList(int langFlag,ArrayList<String> titleList) {
		mapTileList.put(langFlag,titleList);
	}
	
	
	public static ArrayList<String> getTitleList(int langFlag) {
		return mapTileList.get(langFlag);
	}
	
	
}
