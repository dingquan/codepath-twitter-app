package com.codepath.apps.twitterclient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends Activity {

	private TwitterClient twitterClient;
	private List<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private ListView lvTweets;
	
	private Long minId = Long.MAX_VALUE;
	private Long maxId = 1L;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		twitterClient = TwitterApp.getRestClient();

		populateTimeline();
		
		lvTweets = (ListView)findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
		
		lvTweets.setOnScrollListener(new EndlessScrollListener(){

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				populateTimeline();
			}
			
		});
	}
	

	/*
	 * Process the tweet IDs from newTweets and update the min and max of the tweet Id
	 */
	public void updateTweetIdCounter(List<Tweet> newTweets){
		Long uid;
		for (Tweet t : newTweets){
			uid = t.getUid();
			if (uid < minId){
				minId = uid;
			}
			if (uid > maxId){
				maxId = uid;
			}
		}
	}
	
	public void populateTimeline(){
		twitterClient.getHomeTimeline(minId, maxId, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONArray json) {
//				Toast.makeText(getBaseContext(), json.toString(), Toast.LENGTH_SHORT).show();
				List<Tweet> newTweets = Tweet.fromJSONArray(json);
				aTweets.addAll(newTweets);
				updateTweetIdCounter(newTweets);
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				super.onFailure(e, s);
			}
			
		});
	}
}
