package com.beelzik.topquotes.logic.game.quize;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.parse.callback.FindRandomQuoteCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.parse.data.storage.TitleListStorage;

public class QuizeGame {

	
	private int lives;
	private int score;
	private QuoteData quote;
	private ArrayList<String> fourRandomTitle;
	private String correctTitle;

	int langFlag;
	Context ctx; 
	
	QuizeGameProgressListener gameProgressListener;
	
	int noTitlesInTitleList;
	
	final static int MIN_TITLE_FOR_GAME=4;
	final static int RANDOM_TITLE_COUNT=4;
	
	private final static int SCORE_STEP=100;
	ArrayList<String> titles;
	SharedPreferences sp;

	public QuizeGame(Context ctx,int lives) {
		this.lives = lives;
		noTitlesInTitleList=ctx.getResources().getStringArray(R.array.navigation_drawer_const_item).length;
		sp=PreferenceManager.getDefaultSharedPreferences(ctx);
		this.ctx=ctx;
	}
	
	public void setQuizeGameProgressListener(
			QuizeGameProgressListener gameProgressListener) {
		this.gameProgressListener = gameProgressListener;
	}
	
	
	public void startGame(){
		
		findQuote(checkCurrentLanguage());
		if (gameProgressListener!=null) {
			gameProgressListener.quizeGameStart(score, lives);
		}
	}
	
	private void findQuote(final int langFlag){
		QuoteData.findRandomQuote(ctx,langFlag, new FindRandomQuoteCallback() {
			
			@Override
			public void findRandomQuoteCallback(QuoteData quote, int resultCode) {
				if (FindRandomQuoteCallback.FIND_RESULT_OK==resultCode) {
					titles=TitleListStorage.getTitleList(langFlag);
					if (titles.size()>=(noTitlesInTitleList+MIN_TITLE_FOR_GAME)) {
						ArrayList<String> fourRandomTitles=getFourRandomTitles(quote.getTitle().getTitleName(),titles);
						Log.d(GlobConst.LOG_TAG, "random quote: "+quote.getQuote());
						QuizeGame.this.fourRandomTitle=fourRandomTitles;
						QuizeGame.this.quote=quote;
						QuizeGame.this.correctTitle=quote.getTitle().getTitleName();
						if (gameProgressListener!=null) {
							gameProgressListener.quizeGameProgress(score, lives, quote.getQuote(),fourRandomTitles);
						}
					}
				} else {

				}
			}
		});
	}
	
	private int checkCurrentLanguage(){
		return sp.getInt(GlobConst.SP_FLAG_WUT_LANG, 0);
	}
	
	public void guessingQuoteTitle(String title){
		boolean isCorrect;
		if(lives>0){
			if(title.equals(correctTitle)){
				score+=SCORE_STEP;
				isCorrect=true;
			}else{
					lives--;
					isCorrect=false;
				  }
			if (gameProgressListener!=null) {
				gameProgressListener.quizeGameAnswer(isCorrect, correctTitle);
			}
			gameContinue();
		}else{
			endGame();
			}
	}
	
	private void gameContinue(){
		if(lives>0){
			findQuote(checkCurrentLanguage());
		}else{
			endGame();
		}
	}
	
	private void endGame(){
		if (gameProgressListener!=null) {
			gameProgressListener.quizeGameEnd(score, lives);
		}
	}
	
	private ArrayList<String> getFourRandomTitles(String firstTitle,ArrayList<String> allTitles){
		ArrayList<String> allTitleCont=new ArrayList<String>();
		
	
		for (int i = noTitlesInTitleList; i < allTitles.size(); i++) {
			allTitleCont.add(allTitles.get(i));
		}
		
		ArrayList<String> fourTitles= new ArrayList<String>();
		
		Random random=new Random();
		int correctTitlePosition=random.nextInt(RANDOM_TITLE_COUNT);
		
		String correctTitleInRandomTitles=firstTitle;
		fourTitles.add(correctTitleInRandomTitles);
		
		String nextTitle=null;
		int nextTitlePosition;
		
				
					for ( ; fourTitles.size()<4; ) {
						if (allTitleCont.size()>1) {
							nextTitlePosition=random.nextInt(allTitleCont.size());
							nextTitle=allTitleCont.get(nextTitlePosition);
							boolean isFound=false;
							for (int fourTitlePosition = 0; fourTitlePosition < fourTitles.size(); fourTitlePosition++) {		
								isFound=nextTitle.equals(fourTitles.get(fourTitlePosition));
									if (isFound) {
										break;
									}
								}
							if(!isFound){
									fourTitles.add(nextTitle);
									allTitleCont.remove(nextTitlePosition);
							}
							
						}else{
							fourTitles.addAll(allTitleCont);
						}
					}
			
			String buffer=fourTitles.get(correctTitlePosition);
			fourTitles.set(correctTitlePosition, fourTitles.get(0));
			fourTitles.set(0, buffer);
	
		return fourTitles;
	}
	
}
