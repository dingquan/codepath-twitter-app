package com.codepath.apps.twitterclient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends Activity {
	private static final int REQUEST_CODE = 10;
	
	private TwitterClient twitterClient;
	private List<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private ListView lvTweets;
	
	private SharedPreferences prefs;
	
	private Long minId = Long.MAX_VALUE;
	private Long maxId = 1L;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		prefs = this.getSharedPreferences("com.codepath.twitterclient", Context.MODE_PRIVATE);
		twitterClient = TwitterApp.getRestClient();

		populateTimeline();
		saveLoginUserProfileData();
		
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
	
    private void saveLoginUserProfileData(){
		twitterClient.getUserProfile(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject json) {
				User user = User.fromJSON(json);
				prefs.edit().putString("userProfile", JsonUtil.toJson(user)).commit();
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				super.onFailure(e, s);
			}
			
		});
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_compose, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    public void composeTweet(MenuItem mi){
		Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
		startActivityForResult(i, REQUEST_CODE);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			String tweetStr = data.getExtras().getString("tweet");
			twitterClient.postTweet(tweetStr, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, JSONObject json) {
//					Toast.makeText(getBaseContext(), json.toString(), Toast.LENGTH_SHORT).show();
					Tweet tweet = Tweet.fromJSON(json);
					aTweets.insert(tweet, 0);
				}
				
				@Override
				public void onFailure(Throwable e, String s) {
					super.onFailure(e, s);
				}
			});
		}
	}
}
