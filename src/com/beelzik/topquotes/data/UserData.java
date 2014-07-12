package com.beelzik.topquotes.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;


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
	
}
