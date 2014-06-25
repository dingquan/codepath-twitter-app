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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity {
	private static final int REQUEST_CODE = 10;
	
	private TwitterClient twitterClient;
	private List<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private PullToRefreshListView lvTweets;
	
	private SharedPreferences prefs;
	
	private Long minId = Long.MAX_VALUE;
	private Long maxId = 1L;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		prefs = this.getSharedPreferences("com.codepath.twitterclient", Context.MODE_PRIVATE);
		twitterClient = TwitterApp.getRestClient();
		
		lvTweets = (PullToRefreshListView)findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
		
		setupHandlers();
		saveLoginUserProfileData();
//		fetchingSavedTweets();
//		refreshTimeline();
	}
	
	private void setupHandlers(){
		lvTweets.setOnRefreshListener(new OnRefreshListener(){

			@Override
			public void onRefresh() {
				refreshTimeline();
			}
			
		});
		
		lvTweets.setOnScrollListener(new EndlessScrollListener(){

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				fetchMoreTimeline();
			}
			
		});

	}
	
	private void fetchingSavedTweets(){
		aTweets.addAll(Tweet.findAll());
	}
	
	public void refreshTimeline(){
		if (aTweets.getCount() > 0){
			minId = aTweets.getItem(aTweets.getCount()-1).getUid(); 
			maxId = aTweets.getItem(0).getUid();
		}
		else{
			minId = Long.MAX_VALUE;
			maxId = 1L;
		}
		twitterClient.getHomeTimeline(null, maxId, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONArray json) {
				List<Tweet> newTweets = Tweet.fromJSONArray(json);
				aTweets.addAll(newTweets);
//				updateTweetIdCounter(newTweets);
				Log.d("DEBUG", "maxId: " + newTweets.get(newTweets.size()-1).getUid());
				saveTweets();
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				super.onFailure(e, s);
			}
			
			@Override
			public void onFinish() {
                // ...the data has come back, finish populating listview...
                // Now we call onRefreshComplete to signify refresh has finished
                lvTweets.onRefreshComplete();
				super.onFinish();
			}
		});
	}

	public void fetchMoreTimeline(){
		if (aTweets.getCount() > 0){
			minId = aTweets.getItem(aTweets.getCount()-1).getUid(); 
			maxId = aTweets.getItem(0).getUid();
		}
		else{
			minId = Long.MAX_VALUE;
			maxId = 1L;
		}
		twitterClient.getHomeTimeline(minId, null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONArray json) {
				List<Tweet> newTweets = Tweet.fromJSONArray(json);
				aTweets.addAll(newTweets);
				Log.d("DEBUG", "maxId: " + newTweets.get(newTweets.size()-1).getUid());
				saveTweets();
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				super.onFailure(e, s);
			}
			
			@Override
			public void onFinish() {
                // ...the data has come back, finish populating listview...
                // Now we call onRefreshComplete to signify refresh has finished
                lvTweets.onRefreshComplete();
				super.onFinish();
			}
		});
	}
	
    private void saveLoginUserProfileData(){
		twitterClient.getUserProfile(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject json) {
				User user = User.fromJSON(json);
				prefs.edit().putLong("userId", user.getUid()).commit();
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
	
	private void saveTweets(){
		Tweet.saveAll(tweets);
		Toast.makeText(this, "Tweets saved", Toast.LENGTH_SHORT).show();
	}
	
	private List<Tweet> loadTweets(){
		return Tweet.findAll();
	}
}
