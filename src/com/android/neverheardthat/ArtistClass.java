package com.android.neverheardthat;


public class ArtistClass {
private String artistsName;
private String artistsYahooID;
private boolean artistOwned;
private int numberOfRecommendations;

public ArtistClass(String tempName, String tempYahooID, boolean tempOwned){
	if ((tempName != null) && (tempYahooID != null)) {
		this.artistsName = tempName;
		this.artistsYahooID = tempYahooID;
		this.artistOwned = tempOwned;
		this.numberOfRecommendations = 0;
	}
}

public void setArtistsName (String tempName){
	if (tempName != null)
		this.artistsName = tempName;
}

public String getArtistName (){
	return this.artistsName;
}

public void setArtistsYahooID (String tempYahooID){
	if (tempYahooID != null)
		this.artistsYahooID = tempYahooID;
}

public String getArtistYahooID (){
	return this.artistsYahooID;
}

public int getNumberOfRecommendations (){
	return this.numberOfRecommendations;
}

public void incrementRecommendation (){
	this.numberOfRecommendations++;
}

public void decrementRecommendations (){
	if (this.numberOfRecommendations > 0)
		this.numberOfRecommendations--;
}

public void setArtistOwned (boolean tempOwned){
	this.artistOwned = tempOwned;
}

public boolean getArtistOwned (){
	return this.artistOwned;
}
}