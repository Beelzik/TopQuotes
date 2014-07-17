package com.beelzik.topquotes.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.logic.game.quize.QuizeGame;
import com.beelzik.topquotes.logic.game.quize.QuizeGameProgressListener;
import com.beelzik.topquotes.parse.callback.FindTopTenAndUserRecordsCallback;
import com.beelzik.topquotes.parse.data.QuizeRecordData;
import com.beelzik.topquotes.parse.data.UserData;

import com.beelzik.topquotes.ui.activity.MainActivity;
import com.beelzik.topquotes.ui.adapter.QuizeRecordAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

	
	 ArrayList<String> fourRandomTitles;
	 
	 int quizeGameDefaultLives=5;
	 
	 String congratulationsUrScoreNewRecord;
	 String dialBtnPosetiveRestartGame;
	 String dialogProgressTitle;
	 int userGameScore;
	 String userGameDateEnd;
	 
	 ConnectivityManager conMng;
	 
	 ProgressDialog downloadRecordsDialog;
	 
	 
	
	
	
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
		
		downloadRecordsDialog=createDownloadRecordsDialog();
		
		return rootView;
		}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		conMng= (ConnectivityManager) getActivity().getSystemService(Service.CONNECTIVITY_SERVICE);
		
		congratulationsUrScoreNewRecord=getActivity().getString(R.string.dialog_quize_congratulations);
		dialBtnPosetiveRestartGame=getActivity().getString(R.string.dialog_quize_posetive_btn_restart_game);
		dialogProgressTitle=getActivity().getString(R.string.dialog_quize_progress_title);
		
		refreshQuizeGame();
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				GlobConst.ARG_SECTION_FRAGMENT_NUMBER),null);
	}
	
	@Override
	public void onDetach() {
		quizeGame.setQuizeGameProgressListener(null);
		
		super.onDetach();
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
		
		userGameScore=score;
		
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
			
		userGameScore=score;
	}


	@Override
	public void quizeGameAnswer(boolean isCorrect, String titleName) {
		Toast.makeText(getActivity(),"is correct: "+isCorrect+" title: "+titleName, Toast.LENGTH_LONG).show();
		
	}
	
	private boolean checkNetConection(){
		 if (conMng.getActiveNetworkInfo() != null
                && conMng.getActiveNetworkInfo().isAvailable()
                && conMng.getActiveNetworkInfo().isConnected()) {
			 return true;
		 }else {
			 return false;
		 }
	}


	@Override
	public void quizeGameEnd(int score, int lives) {
		tvQuizLivesCurrent.setText(lives+"");
		tvQuizScore.setText(score+"");
		tvQuizQuote.setText("");
		
		ParseUser user=ParseUser.getCurrentUser();
		SimpleDateFormat format= new SimpleDateFormat("dd MMMM yyyy hh.mm");
		userGameDateEnd=format.format(System.currentTimeMillis());
		
		userGameScore=score;
		
		final boolean hasNetConnection=checkNetConection();
		
		disableButtons();
		downloadRecordsDialog.show();
		
		if (user!=null) {
			QuizeRecordData.addRecordInParse(userGameDateEnd, score, user,new SaveCallback() {
				
				@Override
				public void done(ParseException e) {
					if(e==null){
						if (hasNetConnection) {
							QuizeRecordData.findTopTenRecordsAndUser(new FindTopTenAndUserRecordsCallback() {
								
								@Override
								public void findTopTenAndUserRecordsCallback(
										ArrayList<QuizeRecordData> topTenRecordsList,
										QuizeRecordData userTopRecord, int resultCode) {
								if (resultCode==FindTopTenAndUserRecordsCallback.FIND_RESULT_OK) {
									if (isAdded()) {
										downloadRecordsDialog.cancel();
										createDialogProgressWithNet(topTenRecordsList, userTopRecord);
										enableButtons();
									
									}
									
								}	
								}
							});
						}
						
					}
				}
			});
		}
		
		if (!hasNetConnection) {
			if (isAdded()) {
				createDialogProgressWithoutNet();
				enableButtons();
				downloadRecordsDialog.cancel();
			}
		}
		
		
	
	}
	
	public ProgressDialog createDownloadRecordsDialog(){
		ProgressDialog downloadRecordsDialog=new ProgressDialog(getActivity());
		String downloadMsg=getActivity().getString(R.string.dialog_quize_download_records_message);
		downloadRecordsDialog.setMessage(downloadMsg);
		return downloadRecordsDialog;
	}
	
	public void enableButtons(){
		btnQuizPickOne.setEnabled(true);
		btnQuizPickTwo.setEnabled(true);	
		btnQuizPickThree.setEnabled(true);	
		btnQuizPickFour.setEnabled(true);	
	}
	
	public void disableButtons(){
		btnQuizPickOne.setEnabled(false);
		btnQuizPickTwo.setEnabled(false);	
		btnQuizPickThree.setEnabled(false);	
		btnQuizPickFour.setEnabled(false);	
	}
	
	public void createDialogProgressWithoutNet(){
		
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
	    adb.setTitle(dialogProgressTitle);
	  
	    View  view =  getActivity().getLayoutInflater()
	        .inflate(R.layout.dialog_quize_records_without_net, null);
	
	    TextView tvQuizeDialogCurrentDate=(TextView) view.findViewById(R.id.tvQuizeDialogUrrecordDate);
	    TextView tvQuizeDialogCurrentUserName=(TextView) view.findViewById(R.id.tvQuizeDialogUrrecordUserName);
	    TextView tvQuizeDialogCurrentScore=(TextView) view.findViewById(R.id.tvQuizeDialogUrrecordScore);
	    
	    tvQuizeDialogCurrentDate.setText(userGameDateEnd);
	    tvQuizeDialogCurrentUserName.setText(ParseUser.getCurrentUser().getString(UserData.COLUMN_USER_NAME_DISPLAY));
	    tvQuizeDialogCurrentScore.setText(userGameScore+"");
	    
	    adb.setView(view);
	    adb.setPositiveButton(dialBtnPosetiveRestartGame, new Dialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==Dialog.BUTTON_POSITIVE){
					refreshQuizeGame();
				}
			}
		});
		adb.show();
	    
	}
	
	
	public void createDialogProgressWithNet(ArrayList<QuizeRecordData> topTenRecordsList, QuizeRecordData userTopRecord){
		
		
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
	    adb.setTitle(dialogProgressTitle);
	  
	    View  view = (LinearLayout) getActivity().getLayoutInflater()
	        .inflate(R.layout.dialog_quize_records_with_net, null);
	
	    ListView lvQuizeDialogToptenPlayers=(ListView) view.findViewById(R.id.lvQuizeDialogToptenPlayers);
	    
	    TextView tvQuizeDialogUrrecordDate=(TextView) view.findViewById(R.id.tvQuizeDialogUrrecordDate);
	    TextView tvQuizeDialogUrrecordUserName=(TextView) view.findViewById(R.id.tvQuizeDialogUrrecordUserName);
	    TextView tvQuizeDialogUrrecordScore=(TextView) view.findViewById(R.id.tvQuizeDialogUrrecordScore);
	    
	    TextView tvQuizeDialogCurrentDate=(TextView) view.findViewById(R.id.tvQuizeDialogCurrentDate);
	    TextView tvQuizeDialogCurrentUserName=(TextView) view.findViewById(R.id.tvQuizeDialogCurrentUserName);
	    TextView tvQuizeDialogCurrentScore=(TextView) view.findViewById(R.id.tvQuizeDialogCurrentScore);
	    
	    TextView tvQuizeDialogCongratulations=(TextView) view.findViewById(R.id.tvQuizeDialogCongratulations);
	    
	    
	    tvQuizeDialogUrrecordDate.setText(userTopRecord.getQuizeRecordDate());
	    tvQuizeDialogUrrecordUserName.setText(userTopRecord.getQuizeRecordUser().getString(UserData.COLUMN_USER_NAME_DISPLAY));
	    tvQuizeDialogUrrecordScore.setText(userTopRecord.getQuizeRecordScore()+"");
	    
	    tvQuizeDialogCurrentDate.setText(userGameDateEnd);
	    tvQuizeDialogCurrentUserName.setText(ParseUser.getCurrentUser().getString(UserData.COLUMN_USER_NAME_DISPLAY));
	    tvQuizeDialogCurrentScore.setText(userGameScore+"");
	    
	    
	    QuizeRecordAdapter quizeRecordAdapter= new QuizeRecordAdapter(getActivity());
	    quizeRecordAdapter.addAll(topTenRecordsList);
	    lvQuizeDialogToptenPlayers.setAdapter(quizeRecordAdapter);
	    
	    
	    if (isCurrentScoreRecord(userGameScore,userTopRecord)) {
	    	tvQuizeDialogCongratulations.setText(congratulationsUrScoreNewRecord);
		}
	    
	    adb.setView(view);
	    adb.setPositiveButton(dialBtnPosetiveRestartGame, new Dialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==Dialog.BUTTON_POSITIVE){
					refreshQuizeGame();
				}
			}
		});
		adb.show();
	}
	
	private boolean isCurrentScoreRecord(int score,QuizeRecordData record){
		if (record.getQuizeRecordScore()<=score) {
			return true;
		}
		return false;
		
	}
	
	
	
	public void refreshQuizeGame(){
		
		quizeGame=new QuizeGame(getActivity(), quizeGameDefaultLives);
		quizeGame.setQuizeGameProgressListener(this);
		quizeGame.startGame();

	}



}
