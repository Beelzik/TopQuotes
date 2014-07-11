package com.beelzik.topquotes.ui.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.logic.game.quize.QuizeGame;
import com.beelzik.topquotes.logic.game.quize.QuizeGameProgressListener;
import com.beelzik.topquotes.parse.ParseQuoteDataManager;
import com.beelzik.topquotes.ui.activity.MainActivity;

public class QuizFragment extends Fragment implements OnClickListener, QuizeGameProgressListener{

	
	TextView tvQuizQuote;
	TextView tvQuizScore;
	TextView tvQuizLivesCurrent;
	TextView tvQuizLivesPeak;
	
	Button btnQuizPickOne;
	Button btnQuizPickTwo;
	Button btnQuizPickThree;
	Button btnQuizPickFour;
	
	QuizeGame quizeGame;
	ParseQuoteDataManager parseQuoteDataManager;
	
	 ArrayList<String> fourRandomTitles;
	
	
	public static QuizFragment newInstance(int sectionNumber) {
		QuizFragment fragment = new QuizFragment();
		Bundle args = new Bundle();
		args.putInt(GlobConst.ARG_SECTION_FRAGMENT_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView=inflater.inflate(R.layout.fragment_quiz, container,false);
		tvQuizLivesCurrent=(TextView) rootView.findViewById(R.id.tvQuizLivesCurrent);
		tvQuizLivesPeak=(TextView) rootView.findViewById(R.id.tvQuizLivesPeak);
		tvQuizQuote=(TextView) rootView.findViewById(R.id.tvQuizQuote);
		tvQuizScore=(TextView) rootView.findViewById(R.id.tvQuizScore);
		
		btnQuizPickOne=(Button) rootView.findViewById(R.id.btnQuizPickOne);
		btnQuizPickTwo=(Button) rootView.findViewById(R.id.btnQuizPickTwo);
		btnQuizPickThree=(Button) rootView.findViewById(R.id.btnQuizPickThree);
		btnQuizPickFour=(Button) rootView.findViewById(R.id.btnQuizePickFour);
		
		btnQuizPickOne.setOnClickListener(this);
		btnQuizPickTwo.setOnClickListener(this);
		btnQuizPickThree.setOnClickListener(this);
		btnQuizPickFour.setOnClickListener(this);
		
		return rootView;
		}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parseQuoteDataManager=((TopQuotesApplication) getActivity().
				getApplication()).getParseQuoteDataManager();
		quizeGame=new QuizeGame(getActivity(), 10, parseQuoteDataManager);
		quizeGame.startGame(5);
		quizeGame.setQuizeGameProgressListener(this);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				GlobConst.ARG_SECTION_FRAGMENT_NUMBER),null);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnQuizPickOne:
			quizeGame.guessingQuoteTitle(fourRandomTitles.get(0));
			break;
		case R.id.btnQuizPickTwo:
			quizeGame.guessingQuoteTitle(fourRandomTitles.get(1));
			break;
		case R.id.btnQuizPickThree:
			quizeGame.guessingQuoteTitle(fourRandomTitles.get(2));
			break;
		case R.id.btnQuizePickFour:
			quizeGame.guessingQuoteTitle(fourRandomTitles.get(3));
			break;
		default:
			break;
		}
		
	}

	@Override
	public void quizeGameStart(int score, int lives) {
		tvQuizLivesPeak.setText(lives+"");
		tvQuizLivesCurrent.setText(lives+"");
		tvQuizScore.setText(score+"");
		tvQuizQuote.setText("");
		
		
	}
	
	@Override
	public void quizeGameProgress(int score, int lives, String quote, ArrayList<String> fourRandomTitles) {
		tvQuizLivesCurrent.setText(lives+"");
		tvQuizScore.setText(score+"");
		tvQuizQuote.setText(quote);
		
		this.fourRandomTitles=fourRandomTitles;
		
		btnQuizPickOne.setText(fourRandomTitles.get(0));
		btnQuizPickTwo.setText(fourRandomTitles.get(1));
		btnQuizPickThree.setText(fourRandomTitles.get(2));
		btnQuizPickFour.setText(fourRandomTitles.get(3));
	}


	@Override
	public void quizeGameAnswer(boolean isCorrect, String titleName) {
		Toast.makeText(getActivity(),"is correct: "+isCorrect+" title: "+titleName, Toast.LENGTH_LONG).show();
		
	}


	@Override
	public void quizeGameEnd(int score, int lives) {
		tvQuizLivesCurrent.setText(lives+"");
		tvQuizScore.setText(score+"");
	}



}
