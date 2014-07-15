package com.beelzik.topquotes.parse.data;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.parse.callback.FindTopTenAndUserRecordsCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

@ParseClassName("QuizeRecordData")
public class QuizeRecordData extends ParseObject{

	public final static String COLUMN_QUIZE_ID="objectId";
	public final static String COLUMN_QUIZE_DATE="date";
	public final static String COLUMN_QUIZE_SCORE="score";
	public final static String COLUMN_QUIZE_USER="user";
	public final static String COLUMN_QUIZE_CREATED_AT="createAt";
	
	public String getQuizeRecordId(){
		return getObjectId();
	}
	
	public String getQuizeRecordDate(){
		return getString(COLUMN_QUIZE_DATE);
	}
	
	public int getQuizeRecordScore(){
		return getInt(COLUMN_QUIZE_SCORE);
	}
	
	public ParseUser getQuizeRecordUser(){
		return (ParseUser) get(COLUMN_QUIZE_USER);
	}
	
	
	public void putDate(String date){
		put(COLUMN_QUIZE_DATE, date);
	}
	
	public void putScore(int score){
		put(COLUMN_QUIZE_SCORE, score);
	}
	
	public void putUser(ParseUser user){
		put(COLUMN_QUIZE_USER, user);
	}
	

	
	
	
	public static void addRecordInParse(String date, int score, ParseUser user,SaveCallback callback ){
		
		QuizeRecordData recordData=new QuizeRecordData();
		
		recordData.putDate(date);
		recordData.putScore(score);
		recordData.putUser(user);
		
		recordData.saveInBackground(callback);
	}
	
	public static void findTopTenRecordsAndUser(FindTopTenAndUserRecordsCallback recordsCallback){
		
		
		RecordsTask recordsTask=new RecordsTask(recordsCallback);
		recordsTask.execute();
		
	}
	
	private static class RecordsTask extends AsyncTask<Void, Void, Integer>{

		final static int TOP_TEN_RECORDS=10;
		final static int TOP_USER_RECORD=1;
		
		FindTopTenAndUserRecordsCallback recordsCallback;
		ArrayList<QuizeRecordData> topTenRecordsList;
		QuizeRecordData userTopRecord;
		
		public RecordsTask(FindTopTenAndUserRecordsCallback recordsCallback) {
			this.recordsCallback=recordsCallback;
		}
		
		
		@Override
		protected Integer doInBackground(Void... params) {
		
			try {
				
				ParseQuery<QuizeRecordData> queryTopTen= ParseQuery.getQuery(QuizeRecordData.class);
				
				queryTopTen.setLimit(TOP_TEN_RECORDS);
				queryTopTen.orderByDescending(QuizeRecordData.COLUMN_QUIZE_SCORE);
				
				topTenRecordsList= (ArrayList<QuizeRecordData>) queryTopTen.find();
				
				
				ParseQuery<QuizeRecordData> queryTopUser= ParseQuery.getQuery(QuizeRecordData.class);
				
				queryTopUser.setLimit(TOP_USER_RECORD);
				queryTopUser.orderByAscending(QuizeRecordData.COLUMN_QUIZE_CREATED_AT);
				
				userTopRecord=queryTopTen.getFirst();
				
				return FindTopTenAndUserRecordsCallback.FIND_RESULT_OK;
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(GlobConst.LOG_TAG, "RecordsTask error: "+e.getMessage());
			}
			return FindTopTenAndUserRecordsCallback.FIND_RESULT_ERROR;

		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (FindTopTenAndUserRecordsCallback.FIND_RESULT_OK==result) {
				if (recordsCallback!=null) {
				recordsCallback.findTopTenAndUserRecordsCallback(topTenRecordsList, userTopRecord,
						FindTopTenAndUserRecordsCallback.FIND_RESULT_OK);
				}
			}else{
				if (recordsCallback!=null) {
					recordsCallback.findTopTenAndUserRecordsCallback(null, null,
							FindTopTenAndUserRecordsCallback.FIND_RESULT_ERROR);
					Log.d(GlobConst.LOG_TAG, "RecordsTask FIND_RESULT_ERROR");		
				}
			}
		}

		
	}
	
	
	
	
}
