package com.beelzik.topquotes.util;

import com.beelzik.topquotes.R;
import com.beelzik.topquotes.data.QuoteData;
import com.beelzik.topquotes.data.TitleData;

import android.content.Context;
import android.content.Intent;

public class ShareQuoteUtil {
	
	
	private static String formQuoteTextForShare(Context ctx,QuoteData quote){
		String title=ctx.getString(R.string.share_util_title);
		String season=ctx.getString(R.string.share_util_season);
		String series=ctx.getString(R.string.share_util_series);
		String published=ctx.getString(R.string.share_util_published_from_app);
		String appName=ctx.getString(R.string.app_name);
		
		StringBuilder sp=new StringBuilder();
		sp.append(quote.getQuote());
		sp.append("\n");
		sp.append(title);
		sp.append(quote.getTitle().getTitleName());
		sp.append("\t");
		sp.append(season);
		sp.append(quote.getSeason());
		sp.append(" ,");
		sp.append(series);
		sp.append(quote.getEpisode());
		sp.append("\n");
		sp.append("\n");
		sp.append(published);
		sp.append(appName);
		return sp.toString();
		
	}
	
	public static void shareQuote(Context ctx,QuoteData quote){
		String quoteText=formQuoteTextForShare(ctx, quote);
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, quoteText);
		sendIntent.setType("text/plain");
		ctx.startActivity(sendIntent);
	}

}
