package com.android.neverheardthat;

import android.util.Log;
import java.io.InputStream;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.net.*;
import java.io.*;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class ArtistSearchXML {

	public String returnMostLikely(String searchURL)  {
		String  artistID = "";
    	try {
    		DefaultHttpClient client = new DefaultHttpClient();
	    	HttpGet get = new HttpGet(searchURL);	
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream in = entity.getContent();
				
				DocumentBuilderFactory dbf =  DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(in);
				Element elem = doc.getDocumentElement();
				NodeList nl = elem.getElementsByTagName("Artist");
				if (nl.getLength() > 0) 
				{
					Element artist = (Element)nl.item(0);
					artistID = artist.getAttribute("id");
				} else {
					artistID = "9999999";
				}
				in.close();
			}
    	} catch (MalformedURLException e) {
    		Log.e(this.toString(), "ArtistSearch::returnMostLikely MALFORMEDURL");
        } catch (IOException e) {
        	Log.e(this.toString(), "ArtistSearch::returnMostLikely IOEXCEPTION");
        } catch (SAXException e) {
        	Log.e(this.toString(), "ArtistSearch::returnMostLikely SAXEXCEPTION");
		} catch (ParserConfigurationException e) {
			Log.e(this.toString(), "ArtistSearch::returnMostLikely PARSERCONFIGURATIONEXCEPTION");
		}
        return artistID;
  }

	public Collection returnSimilarArtists(String searchURL)  {
		Collection similarArtists = new Collection();
    	try {
    		DefaultHttpClient client = new DefaultHttpClient();
	    	HttpGet get = new HttpGet(searchURL);	
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream in = entity.getContent();
				
				DocumentBuilderFactory dbf =  DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(in);
				Element elem = doc.getDocumentElement();
				NodeList nl = elem.getElementsByTagName("Artist");
				for (int i = 0; i < nl.getLength(); i++) 
				{
					Element artist = (Element)nl.item(i);
					similarArtists.addArtist(artist.getAttribute("name"), artist.getAttribute("name"), artist.getAttribute("id"), false);
				} 
				in.close();
			}
			//if (entity != null) {
			//	entity.consumeContent();
			//}
    	} catch (MalformedURLException e) {
    		Log.e(this.toString(), "ArtistSearch::returnSimilarArtists MALFORMEDURL");
        } catch (IOException e) {
        	Log.e(this.toString(), "ArtistSearch::returnSimilarArtists IOEXCEPTION");
        } catch (SAXException e) {
        	Log.e(this.toString(), "ArtistSearch::returnSimilarArtists SAXEXCEPTION");
		} catch (ParserConfigurationException e) {
			Log.e(this.toString(), "ArtistSearch::returnSimilarArtists PARSERCONFIGURATIONEXCEPTION");
		}
        return similarArtists;
  }
}
