package com.android.neverheardthat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.util.Log;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.database.Cursor;

public class NeverHeardThat extends ListActivity {
	// some globals
	final static String yahooID = "%20Ru2iPSLV34HyJm.nRNqoDl5_eP1yJsfZlxyYNUf6AqaHQvXSC2eflGazGVE_XMeU%20";
	Collection myCollection = new Collection();
	private static final int SEARCH_DEVICE_ID = Menu.FIRST;
	private static final int WHAT_DO_I_OWN = Menu.FIRST + 1;
	private static final int NUMBER_OF_CONCURRENT_GETS = 3;
	private ProgressDialog pd;
	private boolean showOwned = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_screen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SEARCH_DEVICE_ID, 0, R.string.menu_search_device);
		menu.add(0, WHAT_DO_I_OWN, 0, R.string.menu_what_do_i_own);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SEARCH_DEVICE_ID:
			myCollection.emptyCollection();
			pd = new ProgressDialog(this);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("Searching Device");
			pd.setIndeterminate(false);
			pd.show();
			Thread thread = new Thread(new munkey());
			thread.start();
			showOwned = false;
			return true;
		case WHAT_DO_I_OWN:
			myCollection.emptyCollection();
			pd = new ProgressDialog(this);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("Searching Device");
			pd.setIndeterminate(false);
			pd.show();
			Thread thread2 = new Thread(new Cohesiveness());
			thread2.start();
			showOwned = true;
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private class Cohesiveness implements Runnable {
		public void run() {
			searchDeviceForArtists();
			updateProgress.sendMessage(Message.obtain(updateProgress,
					myCollection.sizeOfCollection(), "Searching for Artists"));
			ExecutorService executor = Executors
					.newFixedThreadPool(NUMBER_OF_CONCURRENT_GETS);
			for (int i = 0; i < myCollection.sizeOfCollection(); i++) {
				Runnable worker = new FindArtistInfo(i);
				executor.execute(worker);
			}
			// This will make the executor accept no new threads
			// and finish all existing threads in the queue
			executor.shutdown();
			// Wait until all threads are finish
			while (!executor.isTerminated()) {

			}
			updateProgress
					.sendMessage(Message.obtain(updateProgress, myCollection
							.sizeOfCollection(), "Getting Recommendations"));
			ExecutorService executor2 = Executors
					.newFixedThreadPool(NUMBER_OF_CONCURRENT_GETS);
			for (int i = 0; i < myCollection.sizeOfCollection(); i++) {
				Runnable worker = new FindRecommendedArtists(i);
				executor2.execute(worker);
			}
			// This will make the executor accept no new threads
			// and finish all existing threads in the queue
			executor2.shutdown();
			// Wait until all threads are finish
			while (!executor2.isTerminated()) {

			}
			updateProgress.sendMessage(Message.obtain(updateProgress, 0, ""));
		}
	}

	private class munkey implements Runnable {
		public void run() {
			searchDeviceForArtists();
			updateProgress.sendMessage(Message.obtain(updateProgress,
					myCollection.sizeOfCollection(), "Searching for Artists"));
			findArtistInfo2();
			updateProgress
					.sendMessage(Message.obtain(updateProgress, myCollection
							.sizeOfCollection(), "Getting Recommendations"));
			findRecommendedArtists();
			updateProgress.sendMessage(Message.obtain(updateProgress, 0, ""));
		}
	}

	private Handler updateProgress = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				pd.dismiss();
				if (showOwned) {
					displayOwnedRecomended();
				} else {
					displayNewRecomended();
				}
				break;
			case 1:
				pd.incrementProgressBy(1);
				break;
			default:
				pd.setProgress(0);
				pd.setMax(msg.what);
				pd.setMessage((CharSequence) msg.obj);
			}
		}
	};

	public void searchDeviceForArtists() {
		// search on the internal device and external media
		Uri mUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String[] mProjection = new String[] {
				android.provider.MediaStore.Audio.Media._ID,
				android.provider.MediaStore.Audio.Media.TITLE,
				android.provider.MediaStore.Audio.Media.DATA,
				android.provider.MediaStore.Audio.Media.ARTIST,
				android.provider.MediaStore.Audio.Media.ALBUM, };
		Cursor mCursor = managedQuery(mUri, mProjection, // Which columns to
				// return.
				null, // WHERE clause--we won't specify.
				null, null); // Order-by clause.
		if (mCursor.moveToFirst()) {
			do {
				// check to see if artist exists before adding::: specifically
				// we only care about different artists not the number of tracks
				if (!myCollection.doesArtistExist(mCursor.getString(3))) {
					myCollection.addArtist(mCursor.getString(3), mCursor
							.getString(3), "yahooidnono", true);
				}
			} while (mCursor.moveToNext());
		}
	}

	public void debugTest() {
		myCollection.addArtist("Radiohead", "RADIOHEAD", "crap1", true);
		myCollection.addArtist("Pulp", "PULP", "crap3", true);
		myCollection.addArtist("James", "JAMES", "crap4", true);
		myCollection.addArtist("Daft Punk", "DAFT PUNK", "crap4", true);
		myCollection.addArtist("mötorhead", "mötorhead", "crap4", true);
	}

	public void findArtistInfo2() {
		for (int i = 0; i < myCollection.sizeOfCollection(); i++) {
			String searchArtistURL = "http://us.music.yahooapis.com/artist/v1/list/search/artist/"
					+ normalizeURL(myCollection.getArtist(i).getArtistName())
					+ "?appid=[" + yahooID + "]";
			myCollection.getArtist(i).setArtistsYahooID(
					getArtistID(searchArtistURL));
			myCollection.getArtist(i).setArtistOwned(true);
			updateProgress.sendMessage(Message.obtain(updateProgress, 1, ""));
		}
	}

	class FindArtistInfo implements Runnable {
		private final int artistNumber;

		public FindArtistInfo(int inputNumber) {
			artistNumber = inputNumber;
		}

		public void run() {
			String searchArtistURL = "http://us.music.yahooapis.com/artist/v1/list/search/artist/"
					+ normalizeURL(myCollection.getArtist(artistNumber)
							.getArtistName()) + "?appid=[" + yahooID + "]";
			myCollection.getArtist(artistNumber).setArtistsYahooID(
					getArtistID(searchArtistURL));
			myCollection.getArtist(artistNumber).setArtistOwned(true);
			updateProgress.sendMessage(Message.obtain(updateProgress, 1, ""));
		}
	}

	class FindRecommendedArtists implements Runnable {
		private final int artistNumber;

		public FindRecommendedArtists(int inputNumber) {
			artistNumber = inputNumber;
		}

		public void run() {
			String similarArtistURL = "http://us.music.yahooapis.com/artist/v1/list/similar/"
					+ myCollection.getArtist(artistNumber).getArtistYahooID()
					+ "?appid=[" + yahooID + "]";
			Collection similarArtists = getSimilarArtists(similarArtistURL);
			// increment recommendations
			myCollection.incrementRecommendations(similarArtists);
			updateProgress.sendMessage(Message.obtain(updateProgress, 1, ""));
		}
	}

	public void findRecommendedArtists() {
		int tmpNumberOfOwnedArtists = myCollection.sizeOfCollection();
		for (int i = 0; i < tmpNumberOfOwnedArtists; i++) {
			String similarArtistURL = "http://us.music.yahooapis.com/artist/v1/list/similar/"
					+ myCollection.getArtist(i).getArtistYahooID()
					+ "?appid=["
					+ yahooID + "]";
			Collection similarArtists = getSimilarArtists(similarArtistURL);
			// increment recommendations
			myCollection.incrementRecommendations(similarArtists);
			updateProgress.sendMessage(Message.obtain(updateProgress, 1, ""));
		}
	}

	public void displayNewRecomended() {
		// lets display shit
		List<String> outputText = new ArrayList<String>();
		myCollection.sortCollection();

		outputText.add("Recommended");

		for (int i = 0; i < myCollection.sizeOfCollection(); i++) {
			if (!myCollection.getArtist(i).getArtistOwned())
				outputText.add("+"
						+ myCollection.getArtist(i)
								.getNumberOfRecommendations() + "\t "
						+ myCollection.getArtist(i).getArtistName());
		}

		ArrayAdapter<String> songList = new ArrayAdapter<String>(this,
				R.layout.start_screen_row, outputText);
		setListAdapter(songList);
	}

	public void displayOwnedRecomended() {
		int numberOfArtistsOwned = 0;
		// lets display shit
		List<String> outputText = new ArrayList<String>();
		myCollection.sortCollection();

		// lets display the owned section
		outputText.add("Owned");

		for (int i = 0; i < myCollection.sizeOfCollection(); i++) {
			if (myCollection.getArtist(i).getArtistOwned()) {
				outputText.add("+"
						+ myCollection.getArtist(i)
								.getNumberOfRecommendations() + "\t "
						+ myCollection.getArtist(i).getArtistName());
				numberOfArtistsOwned++;
			}
		}

		ArrayAdapter<String> songList = new ArrayAdapter<String>(this,
				R.layout.start_screen_row, outputText);
		setListAdapter(songList);
	}

	public String getArtistID(String searchArtistURL) {
		String temp = "";
		if (searchArtistURL != null) {
			ArtistSearchXML handler = new ArtistSearchXML();
			temp = handler.returnMostLikely(searchArtistURL);
		} else {
			Log.e(this.toString(), "getArtist noURL");
		}
		return temp;
	}

	public Collection getSimilarArtists(String similarArtistURL) {
		Collection arrayOfSimilarArtists = new Collection();
		if (similarArtistURL != null) {
			ArtistSearchXML handler = new ArtistSearchXML();
			arrayOfSimilarArtists = handler
					.returnSimilarArtists(similarArtistURL);
		} else {
			Log.e(this.toString(), "getSimilarArtists noURL");
		}
		return arrayOfSimilarArtists;
	}

	public String normalizeURL(String tempURL) {
		try {
			return URLEncoder.encode(tempURL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Log.e(this.toString(), "normalizing URL");
		}
		return tempURL;
	}
}