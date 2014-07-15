package com.beelzik.topquotes.parse.data;

import android.util.Log;

import com.beelzik.topquotes.GlobConst;
import com.beelzik.topquotes.ui.activity.listener.OnUserAuthListener;
import com.parse.LogInCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


@ParseClassName("UserData")
public class UserData extends ParseUser{

	
	public final String TABLE_USER_NAME="_User";
	
	public final static String COLUMN_USER_ID="objectId";
	public final static String COLUMN_USER_ACOUNT_NAME="username";
	public final static String COLUMN_USER_NAME_DISPLAY="name";
	public final static String COLUMN_USER_EMAIL="email";
	public final static String COLUMN_USER_WALLET="wallet";
	public final static String COLUMN_USER_AVATA_URL="userAvatarUrl";
	public final static String COLUMN_USER_RELATION="quoteLiked";
	
	public final static String DEFAULT_PARSE_USER_PASSWORD="0000";
	
	
	public String getUserId(){
		return super.getObjectId();
	}
	
	public String getUserNameDisplay(){
		return super.getString(COLUMN_USER_NAME_DISPLAY);
	}
	
	public String getEmail(){
		return super.getString(COLUMN_USER_EMAIL);
	}
	
	public WalletData getWallet(){
		return (WalletData) super.get(COLUMN_USER_WALLET);
	}
	
	public String getAvatarUrl(){
		return super.getString(COLUMN_USER_AVATA_URL);
	}
	
	public ParseRelation<ParseObject> getQuoteLiked(){
		return super.getRelation(COLUMN_USER_RELATION);
	}
	
	public void putAvatarUrl(String avatartUrl){
		super.put(COLUMN_USER_AVATA_URL, avatartUrl);
	}
	
	public static void addUser(String displayName,String acountName,String avatarUrl,String email,OnUserAuthListener userAuthListener){
		
		if (GlobConst.DEBUG) {
				Log.d(GlobConst.LOG_TAG, "  addUser()");
		}
		ParseUser user=new ParseUser();
		user.setUsername(acountName);
		user.put(UserData.COLUMN_USER_NAME_DISPLAY, displayName);
		user.setPassword(DEFAULT_PARSE_USER_PASSWORD);
		user.setEmail(email);
		user.put(UserData.COLUMN_USER_AVATA_URL,avatarUrl);
	
		doAuth(user,acountName,userAuthListener);
		
	}
	
	protected static void doAuth(final ParseUser user, final String acountName,final OnUserAuthListener userAuthListener) {
	
		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					doWallet(user, userAuthListener);		
				} 
				if(e!=null){
					signIn(acountName,userAuthListener); 
				}
			}
		});
	}

	protected static void doWallet(final ParseUser user,final OnUserAuthListener userAuthListener) {
	    final ParseObject wallet;
		wallet = new ParseObject("Wallet");
		wallet.put("total", 0);
		wallet.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException arg0) {
				user.put("wallet", wallet);
				user.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException e) {
						if (userAuthListener!=null) {
							userAuthListener.onUserAuth();
						}
					}
				});
			}
		});
	}

	protected static void signIn(String acountName,final OnUserAuthListener userAuthListener) {
		ParseUser.logInInBackground(acountName, DEFAULT_PARSE_USER_PASSWORD, new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException e) {
				if(e==null){
				
					if (userAuthListener!=null) {
						userAuthListener.onUserAuth();
					}
				}else{
					//Log.d(GlobConst.LOG_TAG, "logIn ERROR: "+e.getMessage());
				}
			}
		});
		
	}
	
}
