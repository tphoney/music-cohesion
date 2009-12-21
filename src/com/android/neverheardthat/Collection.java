package com.android.neverheardthat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

public class Collection {
	
List <ArtistClass> def = new ArrayList<ArtistClass> ();

Collection (){
}

public void addArtist (String tempName, String tempYahooName, String tempYahooID, boolean tempOwned){
	if (tempName != null && tempYahooID != null){
		ArtistClass temp = new ArtistClass(tempName, tempYahooID, tempOwned);
		def.add(temp);
	}
}

public void addArtist (ArtistClass tempArtistClass){
	def.add(tempArtistClass);
}

public boolean doesArtistExist (String  tempName){
	for (int i = 0; i < this.sizeOfCollection(); i++) {
		for (int j = 0; j < this.sizeOfCollection(); j++) {
			if (this.getArtist(j).getArtistName().equals(tempName)){
				return true;
			}
		}
	}
	return false;
}
public ArtistClass getArtist (int i) {
	if (i < def.size()) 
		return (ArtistClass) def.get(i);
	return (ArtistClass) def.get(0);
}

public int sizeOfCollection (){
	return def.size();
}

public Collection incrementRecommendations(Collection tempCollection){
	boolean found = false;
	for (int i = 0; i < tempCollection.sizeOfCollection(); i++) {
		for (int j = 0; j < this.sizeOfCollection(); j++) {
			if (this.getArtist(j).getArtistYahooID().equals(tempCollection.getArtist(i).getArtistYahooID())){
				this.getArtist(j).incrementRecommendation();
				found = true;
			}
		}
		if  (!found){
			this.addArtist(tempCollection.getArtist(i));
		}
		found = false;
	}
	return this;
}

public Collection addNewRecommendedArtists(Collection tempCollection){
	boolean found = false;
	for (int i = 0; i < tempCollection.sizeOfCollection(); i++) {
		for (int j = 0; j < this.sizeOfCollection(); j++) {
			if (this.getArtist(j).getArtistYahooID().equals(tempCollection.getArtist(i).getArtistYahooID())){
				found = true;
			}
		}
		if  (!found){
			this.addArtist(tempCollection.getArtist(i));
		}
		found = false;
	}
	return this;
}

public void sortCollection(){
	Collections.sort(this.def, new CollectionSortByRecommendations() );
}

public void emptyCollection(){
	def.clear();
}
public class CollectionSortByRecommendations implements Comparator<ArtistClass>{

    public int compare(ArtistClass o1, ArtistClass o2) {
        return o2.getNumberOfRecommendations() - o1.getNumberOfRecommendations();
    }
}

}

