package com.codepath.apps.twitterclient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends Activity {

	private TwitterClient twitterClient;
	private List<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private ListView lvTweets;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		twitterClient = TwitterApp.getRestClient();

		populateTimeline();
//		twitterClient.getTweetById("480479298403729408", new JsonHttpResponseHandler(){
//			@Override
//			public void onSuccess(int arg0, JSONObject arg1) {
//				Toast.makeText(getBaseContext(), arg1.toString(), Toast.LENGTH_SHORT).show();
//				Log.d("debug", "Response: " + arg1.toString());
//				// TODO Auto-generated method stub
//				super.onSuccess(arg0, arg1);
//			}
//			
//			@Override
//			public void onFailure(Throwable e, String s) {
//				Log.d("debug", s);
//			}
//		});
		
		lvTweets = (ListView)findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
	}
	
	public void populateTimeline(){
		twitterClient.getHomeTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONArray json) {
//				Toast.makeText(getBaseContext(), "Success: " + json.toString(), Toast.LENGTH_SHORT).show();
				Log.e("debug", json.toString());
				aTweets.addAll(Tweet.fromJSONArray(json));
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
//				Toast.makeText(getBaseContext(), "Fail: " + s.toString(), Toast.LENGTH_SHORT).show();
				// TODO Auto-generated method stub
				Log.e("debug", e.toString());
				Log.e(s, e);
				super.onFailure(e, s);
			}
			
		});
	}
}
