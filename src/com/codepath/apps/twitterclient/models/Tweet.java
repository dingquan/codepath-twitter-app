package com.codepath.apps.twitterclient.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateUtils;

public class Tweet {
	private String body;
	private long uid;
	private String createdAt;
	private User user;

	public static Tweet fromJSON(JSONObject json){
		Tweet tweet = new Tweet();
		try{
			tweet.body = json.getString("text");
			tweet.uid = json.getLong("id");
			tweet.createdAt = json.getString("created_at");
			tweet.user = User.fromJSON(json.getJSONObject("user"));
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		return tweet;
	}
	
	public static List<Tweet> fromJSONArray(JSONArray json){
		List<Tweet> tweets = new ArrayList<Tweet>();
		for (int i = 0; i < json.length(); i++){
			JSONObject tweetJson = null;
			try{
				tweetJson = json.getJSONObject(i);
			}
			catch(JSONException e){
				e.printStackTrace();
				continue;
			}
			
			Tweet tweet = Tweet.fromJSON(tweetJson);
			if (tweet != null){
				tweets.add(tweet);
			}
		}
		return tweets;
	}
	
	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}
	
	public String toString(){
		return body;
	}

	// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
	public String getRelativeTimeAgo() {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
	 
		String relativeDate = "";
		try {
			long dateMillis = sf.parse(createdAt).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL).toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	 
		return relativeDate;
	}
}
