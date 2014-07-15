package com.beelzik.topquotes.util;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;

public class NetConnectionRespondent {

	static ConnectivityManager conMng;

	public static boolean checkNetConection(Context ctx){
		
		conMng=(ConnectivityManager) ctx.
				getSystemService(Service.CONNECTIVITY_SERVICE);
		 if (conMng.getActiveNetworkInfo() != null
                && conMng.getActiveNetworkInfo().isAvailable()
                && conMng.getActiveNetworkInfo().isConnected()) {
			 return true;
		 }else {
			 return false;
		 }
	}
	
}
