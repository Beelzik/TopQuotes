package com.beelzik.topquotes.ui.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.beelzik.topquotes.R;
import com.beelzik.topquotes.TopQuotesApplication;
import com.beelzik.topquotes.db.ParseQuoteDataManager;
import com.beelzik.topquotes.db.QuotesData;
import com.beelzik.topquotes.ui.actionbar.mpdel.SpinnerNavItem;

public class AddQuoteActivity extends ActionBarActivity implements OnClickListener{
	
	ActionBar actionBar;
	
	EditText edAddQuoteTitleName;
	EditText edAddQuoteText;
	EditText edAddQuoteNumSeries;
	EditText edAddQuoteNumSeason;
	
	Spinner spAddQuoteLanguage;
	
	Button btnAddQuote;

    private ArrayAdapter<String> languageAdapter;
	private ArrayList<SpinnerNavItem> navSpinner;
	
	String[] checkedLaguages;
	
	
	ParseQuoteDataManager parseQuoteDataManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_quote);
		
		
		checkedLaguages=getResources().getStringArray(R.array.check_languages);
		
	        languageAdapter = new ArrayAdapter<String>(this,
	        		android.R.layout.simple_list_item_1,
	        		android.R.id.text1,
	        		checkedLaguages);
	       
	      
	        
	        
		actionBar=getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		edAddQuoteTitleName=(EditText) findViewById(R.id.edAddQuoteTitleName);
		edAddQuoteText=(EditText) findViewById(R.id.edAddQuoteText);
		edAddQuoteNumSeason=(EditText) findViewById(R.id.edAddQuoteNumSeason);
		edAddQuoteNumSeries=(EditText) findViewById(R.id.edAddQuoteNumSeries);
		
		spAddQuoteLanguage=(Spinner) findViewById(R.id.spAddQuoteLanguages);
		spAddQuoteLanguage.setAdapter(languageAdapter);
		
		btnAddQuote=(Button) findViewById(R.id.btnAddQuote);
		btnAddQuote.setOnClickListener(this);
		
		parseQuoteDataManager=((TopQuotesApplication) getApplication())
				.getParseQuoteDataManager();
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
				
				String titleName=edAddQuoteTitleName.getText().toString();
				String titleQuote=edAddQuoteText.getText().toString();
				String numSeason=edAddQuoteNumSeason.getText().toString();
				String numSeries=edAddQuoteNumSeries.getText().toString();
				int numLanguage=spAddQuoteLanguage.getSelectedItemPosition();
				
				if(!titleName.equals("") && !titleQuote.equals("")){
					QuotesData quotesData=new QuotesData(titleQuote, titleName, 
							numSeason, numSeries, "9l", numLanguage);
					parseQuoteDataManager.addQuoteInParse(quotesData);
					
					showThanksDialog();
					
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
