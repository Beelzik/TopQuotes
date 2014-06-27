package com.beelzik.topquotes.db;

public class QuotesData {
	
	private String quote;
	private String serialName;
	private String numSeason;
	private String numSeries;
	private String userWut;
	int numLanguage;
	
	

	public QuotesData(String quote, String serialName, String numSeason,
			
			String numSeries, String userWut,int numLanguage) {
		this.quote = quote;
		this.serialName = serialName;
		this.numSeason = numSeason;
		this.numSeries=numSeries;
		this.userWut = userWut;
		this.numLanguage=numLanguage;
	}

	public String getQuote() {
		return quote;
	}


	public void setQuote(String quote) {
		this.quote = quote;
	}


	public String getSerialName() {
		return serialName;
	}


	public void setSerialName(String serialName) {
		this.serialName = serialName;
	}


	public String getNumSeason() {
		return numSeason;
	}


	public void setNumSeazone(String serialSeazone) {
		this.numSeason = serialSeazone;
	}

	public String getNumSeries() {
		return numSeries;
	}
	
	public void setNumSeries(String numSeries) {
		this.numSeries = numSeries;
	}

	public String getUserWut() {
		return userWut;
	}


	public void setUserWut(String userWut) {
		this.userWut = userWut;
	}

	public void setNumLanguage(int numLanguage) {
		this.numLanguage = numLanguage;
	}
	
	public int getNumLanguage() {
		return numLanguage;
	}
	
	

}
