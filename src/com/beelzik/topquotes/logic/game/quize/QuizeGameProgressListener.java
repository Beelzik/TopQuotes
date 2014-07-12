package com.beelzik.topquotes.logic.game.quize;

import java.util.ArrayList;

public interface QuizeGameProgressListener {

	
	public void quizeGameStart(int score, int lives);
	
	public void quizeGameProgress(int score, int lives, String quote, ArrayList<String> fourRandomTitles);
	
	public void quizeGameAnswer(boolean isCorrect, String titleName);
	
	public void quizeGameEnd(int score, int lives);
	
}
