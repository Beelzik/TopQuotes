package com.beelzik.topquotes.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.R;
import com.beelzik.topquotes.parse.callback.FindTitlesNameCallback;
import com.beelzik.topquotes.parse.data.QuoteData;
import com.beelzik.topquotes.parse.data.TitleData;
import com.beelzik.topquotes.parse.data.UserData;
import com.parse.ParseUser;

public class AddQuoteActivity extends ActionBarActivity implements OnClickListener{
	
	ActionBar actionBar;
	
	EditText edAddQuoteTitleName;
	EditText edAddQuoteText;
	EditText edAddQuoteNumSeries;
	EditText edAddQuoteNumSeason;
	
	Spinner spAddQuoteLanguage;
	Spinner spAddQuoteTitleName;
	
	Button btnAddQuote;

    private ArrayAdapter<String> languageAdapter;
    private ArrayAdapter<String> titlesAdapter;
	
	String[] checkedLaguages;
	
	SharedPreferences sp;
	
	String spDefaultValue;
	
	String edHintTitleNameSelected;
	String edHintTitleNameEnter;
	
	int nonTitleItemsLangth;
	ArrayList<String> defTitleList;
	ArrayList<String> curTitleList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_quote);
		
		curTitleList=new ArrayList<String>();
		
		
		edHintTitleNameEnter=getString(R.string.add_quote_sp_title_Enter_title);
		edHintTitleNameSelected=getString(R.string.add_quote_sp_title_selected_title);
		
		checkedLaguages=getResources().getStringArray(R.array.check_languages);
		spDefaultValue=getString(R.string.add_quote_sp_title_Enter_title);
		nonTitleItemsLangth=getResources().getStringArray(R.array.navigation_drawer_const_item).length;
		
		defTitleList= new ArrayList<String>();
		defTitleList.add(spDefaultValue);
	        languageAdapter = new ArrayAdapter<String>(this,
	        		android.R.layout.simple_list_item_1,
	        		android.R.id.text1,
	        		checkedLaguages);
	        
	        titlesAdapter = new ArrayAdapter<String>(this,
	        		android.R.layout.simple_list_item_1,
	        		android.R.id.text1,
	        		defTitleList);
	      
	        
	        
		actionBar=getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		edAddQuoteTitleName=(EditText) findViewById(R.id.edAddQuoteTitleName);
		edAddQuoteText=(EditText) findViewById(R.id.edAddQuoteText);
		edAddQuoteNumSeason=(EditText) findViewById(R.id.edAddQuoteNumSeason);
		edAddQuoteNumSeries=(EditText) findViewById(R.id.edAddQuoteNumSeries);
		
		spAddQuoteTitleName=(Spinner) findViewById(R.id.spAddQuoteTitleName);
		spAddQuoteTitleName.setAdapter(titlesAdapter);
		spAddQuoteTitleName.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(position==0){
					edAddQuoteTitleName.setEnabled(true);
					edAddQuoteTitleName.setHint(edHintTitleNameEnter);
				}else{
					edAddQuoteTitleName.setEnabled(false);
					edAddQuoteTitleName.setHint(edHintTitleNameSelected+"  "+titlesAdapter.getItem(position));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		spAddQuoteLanguage=(Spinner) findViewById(R.id.spAddQuoteLanguages);
		spAddQuoteLanguage.setAdapter(languageAdapter);
		spAddQuoteLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(GlobConst.LOG_TAG,"setOnItemSelectedListener position: "
					+position);
				
			TitleData.findAllTitleName(AddQuoteActivity.this,position, new FindTitlesNameCallback() {
					
					@Override
					public void findTitleNameCallback(List<String> titleNameList, int resultCode) {
						
						titlesAdapter.clear();
						titlesAdapter.add(spDefaultValue);
						
						
						
						for (String titleName : titleNameList) {
							titlesAdapter.add(titleName);
						}	
						
						
						
						titlesAdapter.notifyDataSetChanged();
					}
				});
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
	
		
		btnAddQuote=(Button) findViewById(R.id.btnAddQuote);
		btnAddQuote.setOnClickListener(this);
		
		sp=PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnAddQuote:
			showConfirmDialog();
			
			break;
		default:
			break;
		}
	}

	
	public void showConfirmDialog(){
		
		
		String title;
		String message;
		String buttonCancel;
		String buttonOk;
		
		title=getString(R.string.add_quote_dlg_conf_title);
		message=getString(R.string.add_quote_dlg_conf_msg);
		buttonCancel=getString(R.string.add_quote_dlg_conf_btn_cancel);
		buttonOk=getString(R.string.add_quote_dlg_conf_btn_ok);  
		
		ConfDialogListener confDialogListener=new ConfDialogListener();
		AlertDialog.Builder adb=new AlertDialog.Builder(this);
		adb.setTitle(title);
		adb.setMessage(message);
		adb.setPositiveButton(buttonOk, confDialogListener);
		adb.setNegativeButton(buttonCancel, confDialogListener);
		adb.create();
		adb.show();
		
	}
	
	public void showThanksDialog(){
		String title;
		String message;
		String buttonCancel;
		String buttonOk;
		
		title=getString(R.string.add_quote_dlg_thanks_title);
		message=getString(R.string.add_quote_dlg_thanks_msg);
		buttonCancel=getString(R.string.add_quote_dlg_thanks_btn_cancel);
		buttonOk=getString(R.string.add_quote_dlg_thanks_btn_ok); 
		
		ThanksDialogListener thanksDialogListener=new ThanksDialogListener();
		AlertDialog.Builder adb=new AlertDialog.Builder(this);
		adb.setTitle(title);
		adb.setMessage(message);
		adb.setPositiveButton(buttonOk, thanksDialogListener);
		adb.setNegativeButton(buttonCancel, thanksDialogListener);
		adb.create();
		adb.show();
	}
	
	private class ConfDialogListener implements android.content.DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case DialogInterface.BUTTON_POSITIVE:
				
				String failInputMessage;
				failInputMessage=getString(R.string.add_quote_dlg_conf_fail_input);
				String titleName;
				if (spAddQuoteTitleName.getSelectedItemPosition()==0) {
					titleName=edAddQuoteTitleName.getText().toString();
				}else{
					titleName=(String) spAddQuoteTitleName.getSelectedItem();
				}
			
				String titleQuote=edAddQuoteText.getText().toString();
	
				
				int lang=spAddQuoteLanguage.getSelectedItemPosition();
				int episode;
				int season;
				ParseUser user=ParseUser.getCurrentUser();
				
				boolean isTitleNameFilled=!titleName.equals("");
				boolean isTitlQuoteFilled=!titleQuote.equals("");
				boolean isSeasonFilled=(!edAddQuoteNumSeason.getText().toString().equals(""));
				boolean isEpisodeFilled=(!edAddQuoteNumSeries.getText().toString().equals(""));
				
				boolean isUserNotNull=(user!=null);
				
				if (!isSeasonFilled) {
					season=-1;
				}else{
					season=Integer.parseInt(replaceUnnecessaryZerro(edAddQuoteNumSeason.getText().toString()));
				}
				
				if (!isEpisodeFilled) {
					episode=-1;
				}else{
					episode=Integer.parseInt(edAddQuoteNumSeries.getText().toString());
				}
				
				boolean canAdd=isTitleNameFilled && isTitlQuoteFilled && isUserNotNull;
				
				
				if(canAdd){
					
					
					String userDisplayName=user.getString(UserData.COLUMN_USER_NAME_DISPLAY);
					if (userDisplayName!=null) {
						
						QuoteData.addQuoteInParse(titleQuote, titleName, season, episode, user, lang);
						showThanksDialog();
					}
					
					
					
					
				}else{
					Toast.makeText(AddQuoteActivity.this,failInputMessage,
							Toast.LENGTH_LONG).show();
				}
				
				
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				break;
			}
		}
	}
	
	private String replaceUnnecessaryZerro(String num){
		num=num.replaceAll("^0*", "");
		return num;
	}
	
	private class ThanksDialogListener implements android.content.DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch(which){
			case DialogInterface.BUTTON_POSITIVE:	
				edAddQuoteText.setText("");
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				AddQuoteActivity.this.finish();
				break;
			}
		}
	}

}
