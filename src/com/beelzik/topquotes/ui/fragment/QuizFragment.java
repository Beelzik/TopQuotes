package com.beelzik.topquotes.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beelzik.topquotes.R;
import com.beelzik.topquotes.ui.activity.MainActivity;

public class QuizFragment extends Fragment implements OnClickListener{

	private static final String ARG_SECTION_NUMBER = "section_number";
	
	TextView tvQuizQuote;
	TextView tvQuizScore;
	TextView tvQuizLives;
	
	Button btnQuizPickOne;
	Button btnQuizPickTwo;
	Button btnQuizPickThree;
	Button btnQuizPickFour;
	
	
	
	public static QuizFragment newInstance(int sectionNumber) {
		QuizFragment fragment = new QuizFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView=inflater.inflate(R.layout.fragment_quiz, container,false);
		tvQuizLives=(TextView) rootView.findViewById(R.id.tvQuizLives);
		tvQuizQuote=(TextView) rootView.findViewById(R.id.tvQuizQuote);
		tvQuizScore=(TextView) rootView.findViewById(R.id.tvQuizScore);
		
		btnQuizPickOne=(Button) rootView.findViewById(R.id.btnQuizPickOne);
		btnQuizPickTwo=(Button) rootView.findViewById(R.id.btnQuizPickTwo);
		btnQuizPickThree=(Button) rootView.findViewById(R.id.btnQuizPickThree);
		btnQuizPickFour=(Button) rootView.findViewById(R.id.btnQuizePickFour);
		
		
		return rootView;
		}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER),null);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnQuizPickOne:
			break;
		case R.id.btnQuizPickTwo:
			break;
		case R.id.btnQuizPickThree:
			break;
		case R.id.btnQuizePickFour:
			break;
		default:
			break;
		}
		
	}
}
